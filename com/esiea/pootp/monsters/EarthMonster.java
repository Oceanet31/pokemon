package com.esiea.pootp.monsters;

import java.util.Random;

import com.esiea.pootp.attacks.Attack;

public class EarthMonster extends Monster{
    
    private double fleeingChance;
    private int fleeingDuration;

    /**
     * Constructor for EarthMonster
     * @param name Name of the monster
     * @param hp Health points of the monster
     * @param defense Defense stat of the monster
     * @param attack Attack stat of the monster
     * @param speed Speed stat of the monster
     * @param fleeingChance Chance to flee
     */
    public EarthMonster(String name, int hp, int defense, double attack, int speed, double fleeingChance){
        super(name,ElementType.EARTH,hp,defense,attack,speed);
        this.fleeingChance = fleeingChance;
        this.fleeingDuration = 0;
    }

    /**
     * Perform an attack on a target monster
     * @param monster Target monster to attack
     * @param attack Attack to use
     */
    @Override
    public void attack(Monster monster, Attack attack){
        //peut paraliser s'il utilise une attaque "anormale"
        double damageBase = this.damages(monster);

        if (!isProtected()){

            if(Math.random() < this.fleeingChance){          //si non, 30% de chance d'inonder le terrain

                double Rand = Math.random();

                if(0.0 <= Rand && Rand < 0.33){           //10% de chance d'inonder pour 1 tour
                    this.fleeingDuration = 1;
                } else if (0.33 <= Rand && Rand < 0.66){  //10% de chance d'inonder pour 2 tours
                    this.fleeingDuration = 2; 
                } else if (0.66 <= Rand && Rand < 1){     //10% de chance d'inonder pour 3 tours
                    this.fleeingDuration = 3;
                }

            }
        }


        monster.getAttacked(this, attack);
    }

    /**
     * Handle being attacked by another monster
     * @param attacker Attacking monster
     * @param attack Attack used
     */
    @Override
    public void getAttacked(Monster attacker, Attack attack){
        double damage = attacker.damages(this); //Calculate damage from attacker

        if (this.isProtected()) {
        damage = damage / 2;
        System.out.println(this.getName() + " est sous terre et réduit les dégâts !");
        }
        //------Special Effects------
         switch(attacker.getElement()){
            case NATURE :
                //Faiblesse monster m'inflige 2*damage
                damage = 2*super.damages(this);
                break;

            case LIGHTNING :
                //Force monster m'inflige 0.5*damage
                damage = 0.5*super.damages(this);
                break;

            default :
                damage = super.damages(this);
                break;
        }
        //---------------------------

        this.takeDamage(damage); //Apply damage to this monster
        
        if(this.getHp() <= 0){
            this.setState(State.DEAD);
        }
    }


    public boolean isProtected() {
        return fleeingDuration > 0;
    }

    public double getFleeingChance() {
    return this.fleeingChance;
    }
}