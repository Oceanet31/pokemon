package com.esiea.pootp;
import com.esiea.pootp.attacks.Attack;
import com.esiea.pootp.dataBase.AttackDataBase;
import com.esiea.pootp.dataBase.MonsterDataBase;
import com.esiea.pootp.game.Player;
import com.esiea.pootp.gui.GameWindow;
import com.esiea.pootp.monsters.BugMonster;
import com.esiea.pootp.monsters.EarthMonster;
import com.esiea.pootp.monsters.FireMonster;
import com.esiea.pootp.monsters.LightningMonster;
import com.esiea.pootp.monsters.Monster;
import com.esiea.pootp.monsters.NatureMonster;
import com.esiea.pootp.monsters.PlantMonster;
import com.esiea.pootp.monsters.WaterMonster;
import com.esiea.pootp.objects.Item;
import com.esiea.pootp.game.GameEngine;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;
import com.esiea.pootp.gui.GameWindow;

import com.esiea.pootp.dataBase.ItemDataBase;


public class PokemonApp {

    static MonsterDataBase monsterDB = new MonsterDataBase();
    static AttackDataBase attackDB = new AttackDataBase();
    static ItemDataBase itemDB = new ItemDataBase();

    public static void main(String[] args) throws FileNotFoundException {

        try { // Chargement des bases de données
             
            monsterDB.loadMonstersFromFile(new File("com/esiea/pootp/dataBase/pokemons.txt"));
            attackDB.loadAttacksFromFile(new File("com/esiea/pootp/dataBase/attacks.txt"));
            itemDB.loadItemsFromFile(new File("com/esiea/pootp/dataBase/items.txt"));
            
            System.out.println(monsterDB.getMonsters().size() + " monstres chargés.");
            System.out.println(attackDB.getAttacks().size() + " attaques chargées.");
            System.out.println(itemDB.getItems().size() + " objets chargés.");

        } catch (Exception e) {
            System.err.println("Erreur de chargement des fichiers : " + e.getMessage());
            e.printStackTrace();
            return;
        }

        GameWindow window = new GameWindow();
        window.setVisible(true);

        Player Player = new Player("Sasha");
        
        // Le joueur choisit son monstre de départ
        Monster starter = monsterDB.getMonsters().get(0);
        
        if (starter != null) {
            Player.addMonsterToTeam(starter);
            System.out.println("\nVous commencez l'aventure avec " + starter.getName() + " !");
        }
        
        // On donne quelques items de départ
        if (itemDB.getItems().isEmpty() == false) {
            Player.addItemToInventory(itemDB.getItems().get(0)); // Donne le premier item de la liste
        }

        int round = 1;

        while (Player.hasLost() == false) { // Boucle principale du jeu (tant que le joueur n'a pas perdu)   


            // Préparation de l'adversaire
            Player enemy;
            boolean isWild = true;

            if (round % 5 == 0) {// Boss tous les 5 rounds
                
                System.out.println("!!! UN DRESSEUR DE LIGUE VOUS DÉFIE !!!");
                enemy = new Player("Maître de la Ligue");

                // Le boss a 2 monstres forts
                enemy.addMonsterToTeam(createRandomMonster());
                enemy.addMonsterToTeam(createRandomMonster());
                isWild = false;

            } else {// Combat sauvage classique
                
                enemy = new Player("Monstre Sauvage");
                Monster wild = createRandomMonster();
                System.out.println("Un " + wild.getName() + " sauvage apparaît !");
                enemy.addMonsterToTeam(wild);
            }

            // Lancement du combat
            GameEngine battle = new GameEngine(Player, enemy, isWild, window);
            battle.startBattle();

            // Vérification après combat
            if (Player.hasLost()) {
                System.out.println("\nGAME OVER... Vous avez atteint le round " + round);
                break;
            } else {
                System.out.println("\nVictoire !");
                
                // Mécanique Roguelike : Soin partiel automatique
                Monster active = Player.getActiveMonster();
                if(active != null) {
                    active.setHp(active.getHp() + 20); 
                    System.out.println(active.getName() + " récupère 20 PV.");
                }
                
                round++;
            }
        }
    }

   public static Monster createRandomMonster() {

        // 1. Récupérer la liste des templates chargés depuis le fichier
        ArrayList<Monster> templates = monsterDB.getMonsters();
        if (templates.isEmpty()) return null;

        // 2. Choisir un template au hasard
        Random r = new Random();
        Monster template = templates.get(r.nextInt(templates.size()));
        
        Monster newMonster = null;

        // 3. Cloner le monstre en fonction de son type spécifique
        if (template instanceof FireMonster) {
            FireMonster t = (FireMonster) template;
            newMonster = new FireMonster(t.getName(), t.getHp(), t.getDefense(), t.getAttack(), t.getSpeed(), t.getBurningChance());
        
        } else if (template instanceof WaterMonster) {
            WaterMonster t = (WaterMonster) template;
            newMonster = new WaterMonster(t.getName(), t.getHp(), t.getDefense(), t.getAttack(), t.getSpeed(), t.getFloodChance(), t.getFallChance());

        } else if (template instanceof EarthMonster) {
            EarthMonster t = (EarthMonster) template;
            newMonster = new EarthMonster(t.getName(), t.getHp(), t.getDefense(), t.getAttack(), t.getSpeed(), t.getFleeingChance());

        } else if (template instanceof LightningMonster) {
            LightningMonster t = (LightningMonster) template;
            newMonster = new LightningMonster(t.getName(), t.getHp(), t.getDefense(), t.getAttack(), t.getSpeed(), t.getParalizedChance());
        
        } else if (template instanceof NatureMonster) {
            // Cas particulier : Le fichier TXT dit "Nature", mais le code a "Plant" et "Bug".
            // On convertit le template Nature générique en l'un des deux au hasard pour varier le jeu.
            NatureMonster t = (NatureMonster) template;
            
            if (Math.random() < 0.5) {
                newMonster = new PlantMonster(t.getName(), t.getHp(), t.getDefense(), t.getAttack(), t.getSpeed());
            } else {
                newMonster = new BugMonster(t.getName(), t.getHp(), t.getDefense(), t.getAttack(), t.getSpeed());
            }
        }

        // 4. Assigner des attaques et retourner
        if (newMonster != null) {
            assignRandomAttacks(newMonster);
        }
        
        return newMonster;
    }

    public static void assignRandomAttacks(Monster m) {
        ArrayList<Attack> allAttacks = attackDB.getAttacks(); 
        ArrayList<Attack> compatible = new ArrayList<>();

        for (Attack a : allAttacks) {
            // Le monstre peut apprendre les attaques de son type
            if (a.getType() == m.getElement()) {
                compatible.add(a);
            }
        }
        
        // Mélange et prend jusqu'à 4 attaques
        Collections.shuffle(compatible);
        for (int i = 0; i < Math.min(4, compatible.size()); i++) {
            m.getAttacks().add(compatible.get(i));
        }
    }

    public static void checkEvolution(Player player) {
        Monster current = player.getActiveMonster();
        
        // Si le monstre est null ou n'a pas d'évolution définie on arrête
        if (current == null || current.getEvolutionName() == null) 
        {
            return;
        }

        System.out.println("Quoi ? " + current.getName() + " évolue !");

        Monster evolutionTemplate = null; // On cherche le template d'évolution dans la base de données
        for (Monster Monster : monsterDB.getMonsters()) {
            if (Monster.getName().equalsIgnoreCase(current.getEvolutionName())) {
                evolutionTemplate = Monster;
                break;
            }
        }

        if (evolutionTemplate != null) {
            
            Monster newMonster = cloneMonster(evolutionTemplate); // Voir fonction cloneMonster plus bas
            
            if (newMonster != null) {
                // On garde peut-être le % de vie actuel ? Ou on soigne l'évolution ?
                // Ici, on soigne l'évolution (gratification)
                
                //On remplace dans l'équipe
                int index = player.getTeam().indexOf(current);
                player.getTeam().set(index, newMonster);
                
                
                newMonster.getAttacks().addAll(current.getAttacks());// Garde les attaques apprises
               
                
                System.out.println("Félicitations ! Votre " + current.getName() + " a évolué en " + newMonster.getName() + " !");
            }
        }
    }

    public static Monster cloneMonster(Monster template) {
        if (template == null) return null;

        Monster newMonster = null;

        // On vérifie le type exact de l'instance pour appeler le bon constructeur
        if (template instanceof FireMonster) {
            FireMonster t = (FireMonster) template;
            newMonster = new FireMonster(t.getName(), t.getHp(), t.getDefense(), t.getAttack(), t.getSpeed(), t.getBurningChance());
        
        } else if (template instanceof WaterMonster) {
            WaterMonster t = (WaterMonster) template;
            newMonster = new WaterMonster(t.getName(), t.getHp(), t.getDefense(), t.getAttack(), t.getSpeed(), t.getFloodChance(), t.getFallChance());

        } else if (template instanceof EarthMonster) {
            EarthMonster t = (EarthMonster) template;
            newMonster = new EarthMonster(t.getName(), t.getHp(), t.getDefense(), t.getAttack(), t.getSpeed(), t.getFleeingChance());

        } else if (template instanceof LightningMonster) {
            LightningMonster t = (LightningMonster) template;
            newMonster = new LightningMonster(t.getName(), t.getHp(), t.getDefense(), t.getAttack(), t.getSpeed(), t.getParalizedChance());
        
        } else if (template instanceof PlantMonster) {
            PlantMonster t = (PlantMonster) template;
            newMonster = new PlantMonster(t.getName(), t.getHp(), t.getDefense(), t.getAttack(), t.getSpeed());

        } else if (template instanceof BugMonster) {
            BugMonster t = (BugMonster) template;
            newMonster = new BugMonster(t.getName(), t.getHp(), t.getDefense(), t.getAttack(), t.getSpeed());
        
        } else if (template instanceof NatureMonster) {
            // Cas générique si jamais le template est resté NatureMonster
            NatureMonster t = (NatureMonster) template;
            newMonster = new PlantMonster(t.getName(), t.getHp(), t.getDefense(), t.getAttack(), t.getSpeed());
        }

        // Copie de l'attribut évolution
        if (newMonster != null && template.getEvolutionName() != null) {
            newMonster.setEvolutionName(template.getEvolutionName());
        }

        return newMonster;
    }
}