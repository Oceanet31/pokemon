package com.esiea.pootp;
import com.esiea.pootp.dataBase.AttackDataBase;
import java.io.File;
import java.io.FileNotFoundException;


public class PokemonApp {
    public static void main(String[] args) throws FileNotFoundException {

        File ADB = new File("com/esiea/pootp/dataBase/attacks.csv");

        AttackDataBase adb = new AttackDataBase();

        adb.loadAttacksFromFile(ADB);
        adb.getAttacks().forEach(attack -> {
            System.out.println("Attack loaded: " + attack);
        });

    }
}