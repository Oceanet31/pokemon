package com.esiea.pootp.monsters;

public class WaterMonster extends Monster{

    private double floodChance;
    private double fallChance;
    private int floodDuration;
    
    public WaterMonster(String name, int hp, int defense, double attack, int speed, double floodChance, double fallChance){
        super(name,ElementType.WATER,hp,defense,attack,speed);
        this.floodChance = floodChance;
        this.fallChance = fallChance;
        this.floodDuration = 0;
    }


    @Override
    //Damages to this monster
    public double damages(Monster monster){
        double damage = 0;
        switch(monster.getElement()){
            case LIGHTNING :
                //Faiblesse monster m'inflige 2*damage
                damage = 2*super.damages(monster);
                break;

            case FIRE :
                //Force monster m'inflige 0.5*damage
                damage = 0.5*super.damages(monster);
                break;

            default :
                damage = super.damages(monster);
                break;
        }

        return damage;
    }

    //Attack opponent monster
    public void attack(Monster enemy){

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

        enemy.getAttacked(this);
    }

    public void getAttacked(Monster attacker){

        double damage = attacker.damages(this); //Calculate damage from attacker

        //------Special Effects------
        if (isTerrainFlooded() && attacker.getElement() != ElementType.WATER){

           if(Math.random() < this.fallChance){                 //20% de chance de rater son attaque
                attacker.takeDamage(damage / 4);     //L'attaquant prend 25% des dégats qu'il voulait infliger
                damage = 0;  
           }
              floodDuration--;             
        }
        //---------------------------

        this.takeDamage(damage); //Apply damage to this monster
    }

    public boolean isTerrainFlooded() {
        return floodDuration > 0;
    }
}