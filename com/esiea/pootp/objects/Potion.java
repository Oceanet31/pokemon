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

    @Override
    public String getName(){
        return this.name;
    }

    public int getPower(){
        return this.power;
    }

    public PotionType getType(){
        return this.type;
    }

    @Override
    public void use(Monster monster){
        switch(this.type){
            case HEAL:
                monster.healFullHP();
                System.out.println(monster.getName() + " has been fully healed.");
                break;

            case REVIVE:
                monster.healFullHP();
                monster.setState(State.NORMAL);
                System.out.println(monster.getName() + " has been revived to full health and is now in NORMAL state.");
                break;

            case CURE:
                monster.setHp(monster.getHp() + this.power);
                System.out.println(monster.getName() + " has been cured with " + this.power + " HP.");
                break;

            case BOOST_ATTACK:
                monster.setAttack(monster.getAttack() + this.power);
                System.out.println(monster.getName() + " has been boosted with " + this.power + " attack.");
                break;

            case BOOST_DEFENSE:
                monster.setDefense(monster.getDefense() + this.power);
                System.out.println(monster.getName() + " has been boosted with " + this.power + " defense.");
                break;

            case BOOST_SPEED:
                monster.setSpeed(monster.getSpeed() + this.power);
                System.out.println(monster.getName() + " has been boosted with " + this.power + " speed.");
                break;

            default:
                break;
        }
    }
}