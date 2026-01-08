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
       boolean isFlooded = false;
        
        if (monster instanceof WaterMonster && ((WaterMonster) monster).isTerrainFlooded()) {
            isFlooded = true;
        }

        if (isFlooded) {
            int healAmount = this.getStartingHp() / 20; // 5% des PV Max
            if (healAmount < 1) healAmount = 1; // Au moins 1 PV

            this.setHp(this.getHp() + healAmount);
            System.out.println(this.getName() + " récupère " + healAmount + " PV grâce à la nature !");
        }
    }

    @Override
    public void onStartTurn(Monster opponent) {
        super.onStartTurn(opponent);

        // Règle : Récupère 1/20 PV si terrain inondé
        // Le terrain peut être inondé par l'ennemi
        boolean terrainFlooded = (opponent instanceof WaterMonster && ((WaterMonster)opponent).isTerrainFlooded());
        
        if (terrainFlooded) {
            int heal = this.getStartingHp() / 20;
            this.setHp(this.getHp() + heal);
            System.out.println(this.getName() + " récupère " + heal + " PV grâce à la nature !");
        }
    }
}