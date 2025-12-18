package com.esiea.pootp.dataBase;
import java.util.ArrayList;
import com.esiea.pootp.attacks.Attack;
import com.esiea.pootp.monsters.ElementType;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

public class AttackDataBase {

    private double failProbability;
    private int power;
    private int nbUse;
    private String name;
    private ElementType type;
    private ArrayList<Attack> attacks;

    public AttackDataBase(){
        this.attacks = new ArrayList<Attack>();
    }

    public void loadAttacksFromFile(File file) throws FileNotFoundException{

        Scanner scanner = new Scanner(file);

        while(scanner.hasNextLine()) {
            String line = scanner.nextLine();
            String[] parts = line.split(",");

            String name = parts[0];
            ElementType type = ElementType.valueOf(parts[1].toUpperCase());
            int nbUse = Integer.parseInt(parts[2]);
            int power = Integer.parseInt(parts[3]);
            double failProbability = Double.parseDouble(parts[4]);

            Attack attack = new Attack(name, type, nbUse, power, failProbability);
            attacks.add(attack);
        }
        scanner.close();
    }

    public ArrayList<Attack> getAttacks(){
        return this.attacks;
    }
}