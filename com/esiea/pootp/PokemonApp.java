package com.esiea.pootp;
import com.esiea.pootp.dataBase.AttackDataBase;
import com.esiea.pootp.dataBase.MonsterDataBase;
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

        
    }
}