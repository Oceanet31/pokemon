package com.esiea.pootp.monsters;

public class FireMonster extends Monster{

    private double burningChance;
    
    public FireMonster(String name, int hp, int defense, double attack, int speed, double burningChance){
        super(name,ElementType.FIRE,hp,defense,attack,speed);
        this.burningChance = burningChance;
    }

    @Override
    //Damages to this monster
    public double damages(Monster monster){
        double damage = 0;
        switch(monster.getElement()){
            case WATER :
                //Faiblesse monster m'inflige 2*damage
                damage = 2*super.damages(monster);
                break;

            case NATURE :
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

        //check si enemy est en train de brûler
        if (enemy.getState() == State.NORMAL) 
        {
            if (Math.random() < this.burningChance) 
            {
                enemy.setState(State.BURNED); // Appliquer l'état BRÛLÉ
            }
        }
        enemy.getAttacked(this);
    }

    public void getAttacked(Monster attacker){

        double damage = attacker.damages(this); //Calculate damage from attacker

        //------Special Effects------

        //---------------------------

        this.takeDamage(damage); //Apply damage to this monster
    }
}