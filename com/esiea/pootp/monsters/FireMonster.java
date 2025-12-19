package com.esiea.pootp.monsters;

import com.esiea.pootp.attacks.Attack;

public class FireMonster extends Monster{

    private double burningChance;
    
    public FireMonster(String name, int hp, int defense, double attack, int speed, double burningChance){
        super(name,ElementType.FIRE,hp,defense,attack,speed);
        this.burningChance = burningChance;
    }
  

    //Attack opponent monster
    public void attack(Monster enemy, Attack attack){

        double damageBase = this.damages(enemy);

        //check si enemy est en train de brûler
        if (enemy.getState() == State.NORMAL) 
        {
            if (Math.random() < this.burningChance) 
            {
                enemy.setState(State.BURNED); // Appliquer l'état BRÛLÉ
            }
        }
        enemy.getAttacked(this, attack);
    }

    public void getAttacked(Monster attacker, Attack attack){

        double damage = attacker.damages(this); //Calculate damage from attacker

          switch(attacker.getElement()){
            case WATER :
                //Faiblesse monster m'inflige 2*damage
                damage = 2*super.damages(this);
                break;

            case NATURE :
                //Force monster m'inflige 0.5*damage
                damage = 0.5*super.damages(this);
                break;

            default :
                damage = super.damages(this);
                break;
        }


        this.takeDamage(damage); //Apply damage to this monster

        if(this.getHp() <= 0){
            this.setState(State.DEAD);
        }
    }

    public double getBurningChance() {
    return this.burningChance;
}
}