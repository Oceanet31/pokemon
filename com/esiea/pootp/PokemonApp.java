package com.esiea.pootp;

import com.esiea.pootp.attacks.Attack;
import com.esiea.pootp.dataBase.AttackDataBase;
import com.esiea.pootp.dataBase.MonsterDataBase;
import com.esiea.pootp.dataBase.ItemDataBase;
import com.esiea.pootp.game.Player;
import com.esiea.pootp.game.GameEngine;
import com.esiea.pootp.gui.GameWindow;
import com.esiea.pootp.monsters.*;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;

public class PokemonApp {

    static MonsterDataBase monsterDB = new MonsterDataBase();
    static AttackDataBase attackDB = new AttackDataBase();
    static ItemDataBase itemDB = new ItemDataBase();

    public static void main(String[] args) throws FileNotFoundException {

        try { 
            monsterDB.loadMonstersFromFile(new File("com/esiea/pootp/dataBase/pokemons.txt"));
            attackDB.loadAttacksFromFile(new File("com/esiea/pootp/dataBase/attacks.txt"));
            itemDB.loadItemsFromFile(new File("com/esiea/pootp/dataBase/items.txt"));
        } catch (Exception e) {
            e.printStackTrace();
            return;
        }

        Player player = new Player("Sasha");
        
        Monster starter = monsterDB.getMonsters().get(0);
        if (starter != null) {
            assignRandomAttacks(starter);
            player.addMonsterToTeam(starter);
        }
        
        if (!itemDB.getItems().isEmpty()) {
            player.addItemToInventory(itemDB.getItems().get(0)); 
        }

        GameWindow window = new GameWindow(player);
        window.setVisible(true);

        // Lance la chaîne de combats
        startNextBattle(player, window, 1);
    }

    public static void startNextBattle(Player player, GameWindow window, int round) {
        
        if (player.hasLost()) {
            return;
        }

        Player enemy;
        boolean isWild = true;

        if (round % 5 == 0) {
            enemy = new Player("Maître de la Ligue");
            enemy.addMonsterToTeam(createRandomMonster());
            enemy.addMonsterToTeam(createRandomMonster());
            isWild = false;
        } else {
            enemy = new Player("Monstre Sauvage");
            Monster wild = createRandomMonster();
            enemy.addMonsterToTeam(wild);
        }

        GameEngine battle = new GameEngine(player, enemy, isWild, window);
        
        // Callback : Quand on gagne, on soigne et on lance le prochain round
        battle.setOnVictory(() -> {
            Monster active = player.getActiveMonster();
            if(active != null) {
                active.setHp(active.getHp() + 20); 
                window.showDialog(active.getName() + " récupère 20 PV.", () -> {
                    startNextBattle(player, window, round + 1);
                });
            } else {
                startNextBattle(player, window, round + 1);
            }
        });

        battle.startBattle();
    }

    public static Monster createRandomMonster() {
        ArrayList<Monster> templates = monsterDB.getMonsters();
        if (templates.isEmpty()) return null;
        Random r = new Random();
        Monster template = templates.get(r.nextInt(templates.size()));
        Monster newMonster = cloneMonster(template);
        if (newMonster != null) assignRandomAttacks(newMonster);
        return newMonster;
    }

    public static void assignRandomAttacks(Monster m) {
        ArrayList<Attack> allAttacks = attackDB.getAttacks(); 
        ArrayList<Attack> compatible = new ArrayList<>();
        for (Attack a : allAttacks) {
            if (a.getType() == m.getElement() || a.getType() == com.esiea.pootp.monsters.ElementType.NATURE) {
                compatible.add(a);
            }
        }
        Collections.shuffle(compatible);
        for (int i = 0; i < Math.min(4, compatible.size()); i++) {
            m.getAttacks().add(compatible.get(i));
        }
    }

    public static Monster cloneMonster(Monster template) {
        if (template == null) return null;
        Monster newMonster = null;
        
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