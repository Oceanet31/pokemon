package com.esiea.pootp.monsters;

import java.util.Random;

public class LightningMonster extends Monster{
    
    private double paralizedChance;
    private int paralizedDuration;

    public LightningMonster(String name, int hp, int defense, double attack, int speed, int paralizedChance, int paralizedDuration){
        super(name,ElementType.LIGHTNING,hp,defense,attack,speed);
        this.paralizedChance = paralizedChance;
        this.paralizedDuration = paralizedDuration;
    }

    @Override
    //Damages to this monster
    public double damages(Monster monster){
        double damage = 0;
        switch(monster.getElement()){
            case EARTH :
                //Faiblesse monster m'inflige 2*damage
                damage = 2*super.damages(monster);
                break;

            case WATER :
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
    public void attack(Monster monster){
        //peut paraliser si il utilise une attaque "anormale"
        double damageBase = this.damages(monster);

        //Re paralise le monstre en lui ajoutant a nouveau le temps
        if(monster.getState() == State.PARALIZED){
            if(Math.random() >= paralizedChance){
                monster.setEffectDuration(monster.getEffectDuration()+paralizedDuration);
            }
        }
        
        monster.getAttacked(this);
    }


    @Override
    public void getAttacked(Monster monster){
        //Chances to get unparalized
        //TODO

        if(monster.getState() == State.PARALIZED){
            //if monster is paralized he has 1/4 chance to success
            Random r = new Random();
            int chance = r.nextInt(4);
            if(chance != 3){
                //Fail
                return;
            }
        }
        double damage = monster.damages(this); //Calculate damage from attacker
        this.takeDamage(damage); //Apply damage to this monster
    }


}