package com.esiea.pootp.objects;

import com.esiea.pootp.monsters.Monster;
import com.esiea.pootp.monsters.State;

public class Potion extends Item{
    private String name;
    private int power;
    private PotionType type;

    public Potion(String name, int power, PotionType type){
        this.name = name;
        this.power = power;
        this.type = type;
    }

    public int getPower(){
        return this.power;
    }

    public PotionType getType(){
        return this.type;
    }

    public void usePotion(Monster monster){
        switch(this.type){
            case HEAL:
                monster.healFullHP();
                break;

            case REVIVE:
                monster.healFullHP();
                monster.setState(State.NORMAL);
                break;

            case CURE:
                monster.setHp(monster.getHp() + this.power);
                break;

            case BOOST_ATTACK:
                monster.setAttack(monster.getAttack() + this.power);
                break;

            case BOOST_DEFENSE:
                monster.setDefense(monster.getDefense() + this.power);
                break;

            case BOOST_SPEED:
                monster.setSpeed(monster.getSpeed() + this.power);
                break;

            default:
                break;
        }
    }
}