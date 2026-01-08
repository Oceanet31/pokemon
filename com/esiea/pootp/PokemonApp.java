package com.esiea.pootp;

import com.esiea.pootp.attacks.Attack;
import com.esiea.pootp.dataBase.AttackDataBase;
import com.esiea.pootp.dataBase.MonsterDataBase;
import com.esiea.pootp.dataBase.ItemDataBase;
import com.esiea.pootp.game.Player;
import com.esiea.pootp.game.GameEngine;
import com.esiea.pootp.gui.GameWindow;
import com.esiea.pootp.monsters.*;
// Ajout de Scanner pour la lecture console
import java.util.Scanner; 

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;

public class PokemonApp {

    static MonsterDataBase monsterDB = new MonsterDataBase();
    public static AttackDataBase attackDB = new AttackDataBase();
    static ItemDataBase itemDB = new ItemDataBase();

    public static void main(String[] args) throws FileNotFoundException {

        // 1. Chargement des données
        try { 
            monsterDB.loadMonstersFromFile(new File("com/esiea/pootp/dataBase/pokemons.txt"));
            attackDB.loadAttacksFromFile(new File("com/esiea/pootp/dataBase/attacks.txt"));
            itemDB.loadItemsFromFile(new File("com/esiea/pootp/dataBase/items.txt"));
        } catch (Exception e) {
            e.printStackTrace();
            return;
        }

        Player player = new Player("Sasha");
        Scanner scanner = new Scanner(System.in);

        // =================================================================================
        // 2. PHASE DE SÉLECTION DU JOUEUR (CONSOLE)
        // =================================================================================
        System.out.println("========================================");
        System.out.println("   BIENVENUE DANS NOT POKEMON JAVA");
        System.out.println("========================================");
        System.out.println("Veuillez constituer votre équipe de 3 monstres.");
        
        // On affiche la liste des monstres disponibles
        ArrayList<Monster> availableMonsters = monsterDB.getMonsters();
        for (int i = 0; i < availableMonsters.size(); i++) {
            System.out.println("[" + i + "] " + availableMonsters.get(i).getName() 
                + " (HP: " + availableMonsters.get(i).getHp() + ")");
        }

        // Boucle pour choisir 3 monstres
        while (player.getTeam().size() < 3) {
            System.out.print("\nChoisissez le monstre n°" + (player.getTeam().size() + 1) + " (entrez le numéro) : ");
            
            try {
                String input = scanner.nextLine();
                int choice = Integer.parseInt(input);

                if (choice >= 0 && choice < availableMonsters.size()) {
                    // IMPORTANT : On clone le monstre pour ne pas modifier l'original dans la DB
                    Monster template = availableMonsters.get(choice);
                    Monster myMonster = cloneMonster(template);
                    
                    if (myMonster != null) {
                        myMonster.assignAttacks(); // On lui donne ses attaques par défaut
                        player.addMonsterToTeam(myMonster);
                        System.out.println("-> " + myMonster.getName() + " ajouté à l'équipe !");
                    }
                } else {
                    System.out.println("Numéro invalide. Essayez encore.");
                }
            } catch (NumberFormatException e) {
                System.out.println("Veuillez entrer un nombre valide.");
            }
        }
        
        System.out.println("\nVotre équipe est prête !");
        
        // Ajout de quelques objets pour le combat
        if (!itemDB.getItems().isEmpty()) {
            // On donne 3 Potions (ou premier item de la liste)
            for(int i=0; i<3; i++) player.addItemToInventory(itemDB.getItems().get(0));
        }

        // =================================================================================
        // 3. CRÉATION DE L'ADVERSAIRE (IA)
        // =================================================================================
        Player enemy = new Player("Rival IA");
        System.out.println("L'IA choisit ses monstres...");
        
        for (int i = 0; i < 3; i++) {
            Monster randomMon = createRandomMonster();
            enemy.addMonsterToTeam(randomMon);
            System.out.println("L'IA a choisi : " + randomMon.getName());
        }

        // =================================================================================
        // 4. LANCEMENT DU COMBAT GRAPHIQUE
        // =================================================================================
        System.out.println("\nLancement du combat graphique...");
        
        // On crée la fenêtre APRES avoir choisi les pokemons, sinon elle serait vide au début
        GameWindow window = new GameWindow(player);
        window.setVisible(true);

        // false car ce n'est pas un combat sauvage (c'est un duel 3v3)
        GameEngine battle = new GameEngine(player, enemy, false, window);
        
        // Définition de ce qui se passe quand le joueur gagne
        battle.setOnVictory(() -> {
            window.showDialog("Félicitations ! Vous avez vaincu l'IA !", () -> {
                System.out.println("Victoire du joueur. Fin du programme.");
                System.exit(0); // On ferme le jeu
            });
        });

        // Note : La défaite est déjà gérée dans GameEngine (player.hasLost() -> System.exit(0))
        battle.startBattle();
        
        scanner.close();
    }

    // --- OUTILS UTILITAIRES (Gardés tels quels ou adaptés) ---

    public static Monster createRandomMonster() {
        ArrayList<Monster> templates = monsterDB.getMonsters();
        if (templates.isEmpty()) return null;
        Random r = new Random();
        Monster template = templates.get(r.nextInt(templates.size()));
        Monster newMonster = cloneMonster(template);
        if (newMonster != null) assignRandomAttacks(newMonster); // IA a des attaques aléatoires
        return newMonster;
    }

    public static void assignRandomAttacks(Monster m) {
        ArrayList<Attack> allAttacks = attackDB.getAttacks(); 
        ArrayList<Attack> compatible = new ArrayList<>();
        // On filtre les attaques compatibles avec le type du monstre
        for (Attack a : allAttacks) {
            if (a.getType() == m.getElement() || a.getType() == com.esiea.pootp.monsters.ElementType.NATURE 
                || a.getType() == com.esiea.pootp.monsters.ElementType.NORMAL) {
                compatible.add(a);
            }
        }
        Collections.shuffle(compatible);
        // On en donne jusqu'à 4
        for (int i = 0; i < Math.min(4, compatible.size()); i++) {
            m.getAttacks().add(compatible.get(i));
        }
        // Sécurité : si aucune attaque compatible, on met une attaque par défaut (Lutte simulée)
        if (m.getAttacks().isEmpty() && !allAttacks.isEmpty()) {
             m.getAttacks().add(allAttacks.get(0));
        }
    }

    public static Monster cloneMonster(Monster template) {
        if (template == null) return null;
        Monster newMonster = null;
        
        // Copie des stats de base selon le type
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
            NatureMonster t = (NatureMonster) template;
            newMonster = new PlantMonster(t.getName(), t.getHp(), t.getDefense(), t.getAttack(), t.getSpeed());
        }

        if (newMonster != null && template.getEvolutionName() != null) {
            newMonster.setEvolutionName(template.getEvolutionName());
        }
        return newMonster;
    }
}