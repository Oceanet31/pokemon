package com.esiea.pootp.dataBase;

import java.util.ArrayList;
import com.esiea.pootp.objects.*;
import com.esiea.pootp.monsters.State;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;



public class ItemDataBase {
    private ArrayList<Item> items;

    public ItemDataBase(){
        this.items = new ArrayList<Item>();
    }

    public void loadItemsFromFile(File file) throws FileNotFoundException {
        Scanner scanner = new Scanner(file); // Scanner pour lire le fichier
        scanner.useLocale(java.util.Locale.US); // Pour lire les nombres à virgule avec un point
        
        String name = "";
        PotionType type = null;
        int power = 0;
        State curedState = null;

        while (scanner.hasNext()) { // Tant qu'il y a des lignes à lire

            String word = scanner.next(); // Lire le prochain mot

            //On recupere toutes infos de l'item en fonction de sa nature
            switch (word) { 

                case "Medicament":
                    word = scanner.next();
                    //Fabrication du medicament
                    while (!word.equals("EndMedicament")) {
                        switch (word) {
                            case "Name":
                                name = scanner.next();
                                break;
                            case "Type":
                                String stateStr = scanner.next().toUpperCase();
                                curedState = State.valueOf(stateStr);
                                break;
                        }
                    }
                    Medicament medicament = new Medicament(name, curedState);
                    this.items.add(medicament);
                    break;

                case "Potion":
                    //Fabrication de la potion
                    while (!word.equals("EndPotion")) {
                        switch (word) {
                            case "Name":
                                name = scanner.next();
                                break;
                            case "Type":
                                String typeStr = scanner.next().toUpperCase();
                                type = PotionType.valueOf(typeStr);
                                break;

                            case "Power":
                                power = scanner.nextInt();
                                break;
                        }
                    }
                    Potion potion = new Potion(name, power, type);
                    this.items.add(potion);
                    break;
            }
                word = scanner.next();
        }
        scanner.close();
    }

    public ArrayList<Item> getItems(){
        return this.items;
    }
}