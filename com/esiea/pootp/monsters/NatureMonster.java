package com.esiea.pootp.monsters;

public abstract class NatureMonster extends Monster{

    /**
     * Constructor for NatureMonster
     * @param name Name of the monster
     * @param hp Health points of the monster
     * @param defense Defense stat of the monster
     * @param attack Attack stat of the monster
     * @param speed Speed stat of the monster
     */
    public NatureMonster(String name, int hp, int defense, double attack, int speed){
        super(name, ElementType.NATURE, hp, defense, attack, speed);
    }
  

    /**
     * Recover health if on flooded terrain
     * @param monster Monster to check terrain status
     */
    public void recoverHealth(Monster monster){
        if(monster instanceof WaterMonster){
            if(((WaterMonster) monster).isTerrainFlooded()){
                int healAmount = this.getHp()*(1/20);
                this.setHp(this.getHp() + healAmount);
            }
        }
    }
}