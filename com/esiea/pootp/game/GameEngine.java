package com.esiea.pootp.game;

import com.esiea.pootp.attacks.Attack;
import com.esiea.pootp.monsters.Monster;
import java.util.Scanner;
import java.util.List;
import java.util.ArrayList;

public class GameEngine {
    private Monster playerMonster;
    private Monster enemyMonster;

    public GameEngine(Monster playerMonster, Monster enemyMonster){
        this.playerMonster = playerMonster;
        this.enemyMonster = enemyMonster;
    }

    public void startBattle(){
        Scanner scanner = new Scanner(System.in);
        System.out.println("A wild " + enemyMonster.getName() + " appears!");
        System.out.println("Go " + playerMonster.getName() + "!");

        System.out.println("Battle Start!");

        while(true){
            System.out.println("Choose your action:");
            System.out.println("1. Attack");
            System.out.println("2. Inventory");
            System.out.println("3. Pokemon");
            System.out.println("4. Flee");
            System.out.print("Enter choice: ");
            int choice = scanner.nextInt();

            if(choice == 1){

                List<Attack> attacks = playerMonster.getAttacks();//initialisation de la liste des attaques du monstre du joueur
                System.out.println("Choose an attack:");

                if(attacks.isEmpty()){
                   
                    System.out.println("No attacks available!");
                    System.out.println(playerMonster.getName() + " has no attacks, he will fight bare-handed!");
                    playerMonster.attack(enemyMonster, null);

                    
                } else{    
                    for (Attack attackElement : attacks){ //affichage des attaques disponibles
                        System.out.println((attacks.indexOf(attackElement) + 1) + ". " + attackElement.getName());
                    }
                    int attackChoice = scanner.nextInt() - 1;                  
                    playerMonster.attack(enemyMonster, attacks.get(attackChoice));                     
                }   

                int attackChoice = scanner.nextInt() - 1;                  
                
                playerMonster.attack(enemyMonster, attacks.get(attackChoice)); //le monstre du joueur attaque le monstre adverse avec l'attaque choisie

            } else if(choice == 2){
                System.out.println("You have no items!");                
            } else if(choice == 3){
                System.out.println("You have no other Pokemon!");
            } else if(choice == 4){
                System.out.println("You fled the battle!");
            }
        } 
    }
}