package com.esiea.pootp.game;

import com.esiea.pootp.monsters.Monster;
import java.util.Scanner;

public class GameEngine {
    private Monster playerMonster;
    private Monster enemyMonster;

    public GameEngine(Monster playerMonster, Monster enemyMonster){
        this.playerMonster = playerMonster;
        this.enemyMonster = enemyMonster;
    }

    public void startBattle(){
        Scanner scanner = new Scanner(System.in);
        System.out.println("A wild " + enemyMonster.getName() + " appeared!");
        //Battle loop

        while(playerMonster.getState() != com.esiea.pootp.monsters.State.FELL && enemyMonster.getState() != com.esiea.pootp.monsters.State.FELL){

            System.out.println("Your turn! Choose an action:");
            System.out.println("1. Attack");
            System.out.println("2. Run");

            int choice = scanner.nextInt();

            if(choice == 1){

                playerMonster.attack(enemyMonster);

                if(enemyMonster.getState() != com.esiea.pootp.monsters.State.FELL){

                    enemyMonster.attack(playerMonster);

                } else {

                    System.out.println("You defeated the " + enemyMonster.getName() + "!");

                }
            } else if(choice == 2){

                System.out.println("You ran away!");
                break;
            }
        }
        scanner.close();
    }
}