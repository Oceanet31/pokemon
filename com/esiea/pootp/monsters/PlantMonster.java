package com.esiea.pootp.monsters;

public class PlantMonster extends NatureMonster{

    public PlantMonster(String name, int hp, int defense, double attack, int speed){
        super(name, hp, defense, attack, speed);
    }
    
    public void attack(Monster target){
        this.recoverHealth(target);
        target.getAttacked(this);

        //a la fin de l'attaque
        if(Math.random() >= 0.8){
            this.setState(State.NORMAL);
        }
    }

    @Override
    public void getAttacked(Monster attacker){
        double damage = attacker.damages(this); //Calculate damage from attacker

        this.takeDamage(damage); //Apply damage to this monster
    }


}