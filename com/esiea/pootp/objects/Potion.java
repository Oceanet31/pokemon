package com.esiea.pootp.objects;

import com.esiea.pootp.monsters.Monster;
import com.esiea.pootp.monsters.State;

public class Potion extends Item{
    private String name;
    private int power;
    private PotionType type;

    /**
     * Constructor for Potion
     * @param name Name of the potion
     * @param power Power of the potion
     * @param type Type of the potion
     */
    public Potion(String name, int power, PotionType type){
        this.name = name;
        this.power = power;
        this.type = type;
    }


    /**
     * Get the name of the potion
     * @return name of the potion
     */
    @Override
    public String getName(){
        return this.name;
    }

    /**
     * Get the power of the potion
     * @return power of the potion
     */
    public int getPower(){
        return this.power;
    }

    /**
     * Get the type of the potion
     * @return type of the potion
     */
    public PotionType getType(){
        return this.type;
    }


    /**
     * Use the potion on a monster
     * @param monster Monster to use the potion on
     */
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