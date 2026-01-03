package com.esiea.pootp.monsters;

import com.esiea.pootp.attacks.Attack;

public class WaterMonster extends Monster{

    private double floodChance;
    private double fallChance;
    private int floodDuration;
    
    /**
     * Constructor for WaterMonster
     * @param name Name of the monster
     * @param hp Health points of the monster
     * @param defense Defense stat of the monster
     * @param attack Attack stat of the monster
     * @param speed Speed stat of the monster
     * @param floodChance Chance to flood the terrain
     * @param fallChance Chance for attacker to fall on flooded terrain
     */
    public WaterMonster(String name, int hp, int defense, double attack, int speed, double floodChance, double fallChance){
        super(name,ElementType.WATER,hp,defense,attack,speed);
        this.floodChance = floodChance;
        this.fallChance = fallChance;
        this.floodDuration = 0;
    }

    @Override
    /**
     * Perform an attack on a target monster
     * @param enemy Target monster to attack
     * @param attack Attack to use
     */
    public void attack(Monster enemy, Attack attack){

        double damageBase = this.damages(enemy);

        //check si le terrain est inondé
        if (!isTerrainFlooded()){

            if(Math.random() < this.floodChance){          //si non, 30% de chance d'inonder le terrain

                double Rand = Math.random();

                if(0.0 <= Rand && Rand < 0.33){           //10% de chance d'inonder pour 1 tour
                    this.floodDuration = 1;
                } else if (0.33 <= Rand && Rand < 0.66){  //10% de chance d'inonder pour 2 tours
                    this.floodDuration = 2; 
                } else if (0.66 <= Rand && Rand < 1){     //10% de chance d'inonder pour 3 tours
                    this.floodDuration = 3;
                }

            }
        }

        enemy.getAttacked(this, attack);
    }


    /**
     * Handle being attacked by another monster
     * @param attacker Attacking monster
     * @param attack Attack used
     */
    @Override
    public void getAttacked(Monster attacker, Attack attack){

        double damage = attacker.damages(this); //Calculate damage from attacker

        switch(attacker.getElement()){
            case LIGHTNING :
                //Faiblesse monster m'inflige 2*damage
                damage = 2*super.damages(this);
                break;

            case FIRE :
                //Force monster m'inflige 0.5*damage
                damage = 0.5*super.damages(this);
                break;

            default :
                damage = super.damages(this);
                break;
        }

        //------Special Effects------
        if (isTerrainFlooded() && attacker.getElement() != ElementType.WATER){

           if(Math.random() < this.fallChance){                 //20% de chance de rater son attaque
                attacker.takeDamage(attacker.getAttack() / 4);     //L'attaquant prend 25% des dégats qu'il voulait infliger
                damage = 0;  
           }
              floodDuration--;             
        }
        //---------------------------

        this.takeDamage(damage); //Apply damage to this monster

        if(this.getHp() <= 0){
            this.setState(State.DEAD);
        }
    }

    /**
     * Check if the terrain is flooded
     * @return true if flooded, false otherwise
     */
    public boolean isTerrainFlooded() {
        return floodDuration > 0;
    }

    /**
     * Get the flood chance of the monster
     * @return flood chance
     */
    public double getFloodChance() {
        return this.floodChance;
    }

    /**
     * Get the fall chance of the monster
     * @return fall chance
     */
    public double getFallChance() {
        return this.fallChance;
    }
    
}