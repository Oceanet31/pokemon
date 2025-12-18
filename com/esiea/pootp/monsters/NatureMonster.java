package com.esiea.pootp.monsters;

public abstract class NatureMonster extends Monster{

    public NatureMonster(String name, int hp, int defense, double attack, int speed){
        super(name, ElementType.NATURE, hp, defense, attack, speed);
    }
    
    @Override
    //Damages to this monster
    public double damages(Monster monster){
        double damage = 0;
        switch(monster.getElement()){
            case FIRE :
                //Faiblesse monster m'inflige 2*damage
                damage = 2*super.damages(monster);
                break;

            case EARTH :
                //Force monster m'inflige 0.5*damage
                damage = 0.5*super.damages(monster);
                break;

            default :
                damage = super.damages(monster);
                break;
        }

        return damage;
    }


    public void recoverHealth(Monster monster){
        if(monster instanceof WaterMonster){
            if(((WaterMonster) monster).isTerrainFlooded()){
                int healAmount = this.getHp()*(1/20);
                this.setHp(this.getHp() + healAmount);
            }
        }
    }
}