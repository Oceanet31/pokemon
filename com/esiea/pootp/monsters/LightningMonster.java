package com.esiea.pootp.monsters;

public class LightningMonster extends Monster{
    
    private double paralizedChance;

    public LightningMonster(String name, int hp, int defense, double attack, int speed){
        super(name,ElementType.LIGHTNING,hp,defense,attack,speed);
        this.paralizedChance = 0; //TODO
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

    public void attack(Monster monster){

    }

    public void getAttacked(Monster monster){
        //if monster is paralized he has 1/4 chance to success
        //Fabric a 4 element tab with 1 1
        //Pick a number if its 1 success

        
    }


}