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
    public String use(Monster monster){
        switch(this.type){
            case HEAL:
                monster.healFullHP();
                return monster.getName() + " a tous ses PV !";

            case REVIVE:
                monster.healFullHP();
                monster.setState(State.NORMAL);
                return monster.getName() + " est réanimé !";

            case CURE:
                monster.setHp(monster.getHp() + this.power);
                return monster.getName() + " récupère " + this.power + " PV.";

            case BOOST_ATTACK:
                monster.setAttack(monster.getAttack() + this.power);
                return monster.getName() + " gagne " + this.power + " Attaque.";

            case BOOST_DEFENSE:
                monster.setDefense(monster.getDefense() + this.power);
                return monster.getName() + " gagne " + this.power + " Défense.";

            case BOOST_SPEED:
                monster.setSpeed(monster.getSpeed() + this.power);
                return monster.getName() + " gagne " + this.power + " Vitesse.";

            default:
                return "Rien ne se passe.";
        }
    }
}