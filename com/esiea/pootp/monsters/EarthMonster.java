package com.esiea.pootp.monsters;

import java.util.Random;

import com.esiea.pootp.attacks.Attack;

public class EarthMonster extends Monster{
    
    private double fleeingChance;
    private int fleeingDuration;

    public EarthMonster(String name, int hp, int defense, double attack, int speed, double fleeingChance){
        super(name,ElementType.EARTH,hp,defense,attack,speed);
        this.fleeingChance = fleeingChance;
        this.fleeingDuration = 0;
    }

    @Override
    //Damages to this monster
    public double damages(Monster monster){
        double damage = 0;
        switch(monster.getElement()){
            case NATURE :
                //Faiblesse monster m'inflige 2*damage
                damage = 2*super.damages(monster);
                break;

            case LIGHTNING :
                //Force monster m'inflige 0.5*damage
                damage = 0.5*super.damages(monster);
                break;

            default :
                damage = super.damages(monster);
                break;
        }

        return damage;
    }

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


    @Override
    public void getAttacked(Monster attacker, Attack attack){
        double damage = attacker.damages(this); //Calculate damage from attacker


        //------Special Effects------
        if(isProtected()){
          damage = damage / 2;
        }
        //---------------------------

        this.takeDamage(damage); //Apply damage to this monster
    }


    public boolean isProtected() {
        return fleeingDuration > 0;
    }
}