package com.esiea.pootp.monsters;

import java.util.Random;

import com.esiea.pootp.attacks.Attack;

public class LightningMonster extends Monster{
    
    private double paralizedChance;
    private int paralizedDuration;

    public LightningMonster(String name, int hp, int defense, double attack, int speed, double paralizedChance, int paralizedDuration){
        super(name,ElementType.LIGHTNING,hp,defense,attack,speed);
        this.paralizedChance = paralizedChance;
        this.paralizedDuration = paralizedDuration;
    }

  
    @Override
    public void attack(Monster monster, Attack attack){
        //peut paraliser s'il utilise une attaque "anormale"
        double damageBase = this.damages(monster);

        //Re paralise le monstre en lui ajoutant a nouveau le temps
        if(monster.getState() == State.PARALIZED){
            if(Math.random() >= paralizedChance){
                monster.setEffectDuration(monster.getEffectDuration()+paralizedDuration);
            }
        }

        monster.getAttacked(this, attack);
    }


    @Override
    public void getAttacked(Monster monster, Attack attack){

        double damage = monster.damages(this); //Calculate damage from attacker

        //Chances to get unparalized
        switch(monster.getElement()){
            case EARTH :
                //Faiblesse monster m'inflige 2*damage
                damage = 2*super.damages(this);
                break;

            case WATER :
                //Force monster m'inflige 0.5*damage
                damage = 0.5*super.damages(this);
                break;

            default :
                damage = super.damages(this);
                break;
        }

        if(monster.getState() == State.PARALIZED){
            //if monster is paralized he has 1/4 chance to success
            Random r = new Random();
            int chance = r.nextInt(4);
            if(chance != 3){
                return;
            }
        }

        this.takeDamage(damage); //Apply damage to this monster
        if(this.getHp() <= 0){
            this.setState(State.DEAD);
        }
    }


}