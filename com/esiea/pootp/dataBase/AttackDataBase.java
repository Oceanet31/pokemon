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

    /**
     * Constructor for AttackDataBase
     */
    public AttackDataBase(){
        this.attacks = new ArrayList<Attack>();
    }


    /**
     * Load attacks from a file and populate the attacks list
     * @param file the file containing attack data
     * @throws FileNotFoundException if the file is not found
     */
    public void loadAttacksFromFile(File file) throws FileNotFoundException {

        Scanner scanner = new Scanner(file); // Scanner pour lire le fichier
        scanner.useLocale(java.util.Locale.US); // Pour lire les nombres à virgule avec un point
        
        String name = "";
        ElementType type = null;
        int power = 0;
        int nbUse = 0;
        double failProbability = 0.0;

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

                case "Power":

                    power = scanner.nextInt();
                    break;

                case "NbUse":

                    nbUse = scanner.nextInt();
                    break;

                case "Fail":

                    failProbability = scanner.nextDouble();
                    break;

                case "EndAttack":

                    attacks.add(new Attack(name, type, nbUse, power, failProbability));// Création de l'attaque une fois le bloc fini
                    break;

                default:
                    // Ignore "Attack" et les autres mots inconnus
                    break;
            }
        }
        scanner.close();
    }

    /**
     * Get the list of attacks
     * @return list of attacks
     */
    public ArrayList<Attack> getAttacks(){
        return this.attacks;
    }
}