package com.esiea.pootp.dataBase;

import java.util.ArrayList;
import com.esiea.pootp.objects.*;
import com.esiea.pootp.monsters.State;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

public class ItemDataBase {
    private ArrayList<Item> items;

    /**
     * Constructor for ItemDataBase
     */
    public ItemDataBase(){
        this.items = new ArrayList<Item>();
    }

    /**
     * Load items from a file and populate the items list
     * @param file the file containing item data
     * @throws FileNotFoundException if the file is not found
     */
    public void loadItemsFromFile(File file) throws FileNotFoundException {
        Scanner scanner = new Scanner(file); 
        scanner.useLocale(java.util.Locale.US); 
        
        // Variables temporaires
        String name = "";
        PotionType type = null;
        int power = 0;
        State curedState = null;

        while (scanner.hasNext()) { // Tant qu'il y a des données
            String section = scanner.next(); // Lit le type d'objet (Medicament ou Potion)

            switch (section) { 

                case "Medicament":
                    String tokenMed = scanner.next();
                    
                    while (!tokenMed.equals("EndMedicament")) {
                        switch (tokenMed) {
                            case "Name":
                                name = scanner.next();
                                break;
                            case "Type":
                                String stateStr = scanner.next().toUpperCase();
                                curedState = State.valueOf(stateStr);
                                break;
                        }
                        tokenMed = scanner.next();
                    }
                    
                    // On crée l'objet une fois sorti de la boucle de lecture des attributs
                    Medicament medicament = new Medicament(name, curedState);
                    this.items.add(medicament);
                    break;

                case "Potion":
                    String tokenPot = scanner.next();
                    
                    while (!tokenPot.equals("EndPotion")) {
                        switch (tokenPot) {
                            case "Name":
                                name = scanner.next(); 

                                if(name.equals("Berry")) {
                                    String suite = scanner.next(); 
                                    if(suite.equals("Juice")) name = "Berry Juice";
                                }

                                break;
                            case "Type":
                                String typeStr = scanner.next().toUpperCase();
                                type = PotionType.valueOf(typeStr);
                                break;

                            case "Power":
                                power = scanner.nextInt();
                                break;
                        }
                        // Avance au mot suivant
                        tokenPot = scanner.next();
                    }
                    
                    Potion potion = new Potion(name, power, type);
                    this.items.add(potion);
                    break;
            }
        }
        scanner.close();
    }

    /**
     * Get the list of items
     * @return list of items
     */
    public ArrayList<Item> getItems(){
        return this.items;
    }
}