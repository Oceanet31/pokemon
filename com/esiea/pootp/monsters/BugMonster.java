package com.esiea.pootp.monsters;

import com.esiea.pootp.attacks.*;

public class BugMonster extends NatureMonster{

    public BugMonster(String name, int hp, int defense, double attack, int speed){
        super(name, hp, defense, attack, speed);
    }


    public void attack(Monster target, Attack attack){
        this.recoverHealth(target);

        if(Math.random() >= 0.66){
            target.setState(State.POISONED);
        }

        if(target instanceof WaterMonster){
            if(((WaterMonster) target).isTerrainFlooded() && target.getState() == State.POISONED){
                target.setState(State.NORMAL);
            }
        }
        target.applyStateEffects();
        target.getAttacked(this, attack);
    }


    public void getAttacked(Monster attacker, Attack attack){

        double damage = attacker.damages(this); //Calculate damage from attacker

        this.takeDamage(damage); //Apply damage to this monster
    }
}