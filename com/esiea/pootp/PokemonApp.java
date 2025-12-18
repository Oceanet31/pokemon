package com.esiea.pootp;
import com.esiea.pootp.dataBase.AttackDataBase;
import com.esiea.pootp.dataBase.MonsterDataBase;
import com.esiea.pootp.game.Player;
import com.esiea.pootp.game.GameEngine;
import java.io.File;
import java.io.FileNotFoundException;


public class PokemonApp {
    public static void main(String[] args) throws FileNotFoundException {

        File ADB = new File("com/esiea/pootp/dataBase/attacks.txt");
        File PDB = new File("com/esiea/pootp/dataBase/pokemons.txt");

        AttackDataBase adb = new AttackDataBase();
        MonsterDataBase mdb = new MonsterDataBase();

        adb.loadAttacksFromFile(ADB);
        mdb.loadMonstersFromFile(PDB);
        adb.getAttacks().forEach(attack -> { System.out.println("Attack loaded: " + attack.getName()); });
        mdb.getMonsters().forEach(monster -> { System.out.println("Monster loaded: " + monster.getName()); });

        // Assign all attacks to each monster for demo purposes (replace with logic as needed)
        mdb.getMonsters().forEach(monster -> {
            try {
                java.lang.reflect.Field attacksField = monster.getClass().getSuperclass().getDeclaredField("attacks");
                attacksField.setAccessible(true);
                attacksField.set(monster, new java.util.ArrayList<>(adb.getAttacks()));
            } catch (Exception e) {
                e.printStackTrace();
            }
        });

        Player player1 = new Player("Ash");
        Player player2 = new Player("Misty");

        GameEngine game = new GameEngine(mdb.getMonsters().get(0), mdb.getMonsters().get(1));
        game.startBattle();
    }
}