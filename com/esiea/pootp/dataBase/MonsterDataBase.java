package com.esiea.pootp.dataBase;

import com.esiea.pootp.monsters.*;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Scanner;

public class MonsterDataBase {

    private ArrayList<Monster> monsters;

    public MonsterDataBase() {
        this.monsters = new ArrayList<Monster>();
    }

    // Méthode pour générer un entier entre les deux bornes données
    private int randomStat(int min, int max) {
        return (int) (Math.random() * (max - min + 1)) + min;
    }

    public void loadMonstersFromFile(File file) throws FileNotFoundException {

        Scanner scanner = new Scanner(file);  // Scanner pour lire le fichier
        scanner.useLocale(java.util.Locale.US); // Pour lire les nombres à virgule avec un point

        String name = "";
        ElementType type = null;
        int hp = 0, speed = 0, defense = 0;
        double attack = 0;

        double paralizedChance = 0, floodChance = 0, fallChance = 0, burningChance = 0, fleeingChance = 0;

        while (scanner.hasNext()) { // Tant qu'il y a des lignes à lire

            String word = scanner.next(); // Lire le prochain mot

            switch (word) { // Traiter en fonction du mot lu

                case "Name":

                    name = scanner.next();
                    break;

                case "Type":

                    String typeStr = scanner.next().toUpperCase();

                      if (typeStr.equals("ELECTRIC")) {

                        type = ElementType.LIGHTNING;

                    } else if (typeStr.equals("NATURE")) {

                        type = ElementType.NATURE; 

                    } else if (typeStr.equals("EARTH")) {

                        type = ElementType.EARTH; 

                    } else if (typeStr.equals("WATER")) {

                        type = ElementType.WATER; 

                    } else if (typeStr.equals("FIRE")) {

                        type = ElementType.FIRE; 

                    }
                    break;

                case "HP":

                    hp = randomStat(scanner.nextInt(), scanner.nextInt());
                    break;

                case "Speed":

                    speed = randomStat(scanner.nextInt(), scanner.nextInt());
                    break;

                case "Attack":

                    attack = randomStat(scanner.nextInt(), scanner.nextInt());
                    break;

                case "Defense":

                    defense = randomStat(scanner.nextInt(), scanner.nextInt());
                    break;

                case "Paralysis": //EFFET SPECIAUX POUR LIGHTNING

                    paralizedChance = scanner.nextDouble(); 
                    break;

                case "Flood": //EFFET SPECIAUX POUR WATER

                    floodChance = scanner.nextDouble();
                    break;

                case "Fall": //EFFET SPECIAUX POUR WATER

                    fallChance = scanner.nextDouble();
                    break;

                case "EndMonster":

                    // Instanciation selon le type pour gérer l'héritage
                    if (type == ElementType.LIGHTNING) {
                        monsters.add(new LightningMonster(name, hp, defense, attack, speed, paralizedChance));
                    } else if (type == ElementType.WATER) {
                        monsters.add(new WaterMonster(name, hp, defense, attack, speed, floodChance, fallChance));
                    } else if (type == ElementType.FIRE) {
                        monsters.add(new FireMonster(name, hp, defense, attack, speed, burningChance));
                    } else if (type == ElementType.EARTH) {
                        monsters.add(new EarthMonster(name, hp, defense, attack, speed, fleeingChance));
                    }
                                        
                    paralizedChance = 0; floodChance = 0; fallChance = 0; burningChance = 0; fleeingChance = 0; // Reset des effets spéciaux
                    break;
                }
        }
        scanner.close();
    }

    public ArrayList<Monster> getMonsters() {
        return this.monsters;
    }
}