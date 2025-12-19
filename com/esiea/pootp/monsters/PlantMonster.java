package com.esiea.pootp.monsters;

import com.esiea.pootp.attacks.Attack;

public class PlantMonster extends NatureMonster{

    public PlantMonster(String name, int hp, int defense, double attack, int speed){
        super(name, hp, defense, attack, speed);
    }
    
    public void attack(Monster target, Attack attack){
        this.recoverHealth(target);
        target.getAttacked(this, attack);

        //a la fin de l'attaque
        if(Math.random() >= 0.8){
            if (this.getState() != State.NORMAL && this.getState() != State.DEAD) {
                this.setState(State.NORMAL);
            }
        }
    }

    @Override
    public void getAttacked(Monster attacker, Attack attack){
        double damage = attacker.damages(this); //Calculate damage from attacker

        switch(attacker.getElement()){
            case FIRE :
                //Faiblesse monster m'inflige 2*damage
                damage = 2*super.damages(this);
                break;

            case EARTH :
                //Force monster m'inflige 0.5*damage
                damage = 0.5*super.damages(this);
                break;

            default :
                damage = super.damages(this);
                break;
        }

        this.takeDamage(damage); //Apply damage to this monster
        
        if(this.getHp() <= 0){
            this.setState(State.DEAD);
        }
    }


}