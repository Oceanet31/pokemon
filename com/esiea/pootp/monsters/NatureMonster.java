package com.esiea.pootp.monsters;

public abstract class NatureMonster extends Monster{

    public NatureMonster(String name, int hp, int defense, double attack, int speed){
        super(name, ElementType.NATURE, hp, defense, attack, speed);
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