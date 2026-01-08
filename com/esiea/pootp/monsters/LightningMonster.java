package com.esiea.pootp.monsters;

import java.util.Random;

import com.esiea.pootp.attacks.Attack;

public class LightningMonster extends Monster{
    
    private double paralizedChance;
    private int paralizedDuration;

    /**
     * Constructor for LightningMonster
     * @param name Name of the monster
     * @param hp Health points of the monster
     * @param defense Defense stat of the monster
     * @param attack Attack stat of the monster
     * @param speed Speed stat of the monster
     * @param paralizedChance Chance to paralyze the opponent
     */
    public LightningMonster(String name, int hp, int defense, double attack, int speed, double paralizedChance){
        super(name,ElementType.LIGHTNING,hp,defense,attack,speed);
        this.paralizedChance = paralizedChance;
        this.paralizedDuration = paralizedDuration;
    }

  
    /**
     * Perform an attack on a target monster
     * @param monster Target monster to attack
     * @param attack Attack to use
     */
    @Override
    public void attack(Monster monster, Attack attack){
        double damageBase = this.damages(monster);

        if (attack != null && attack.getType() == ElementType.LIGHTNING) {
            
            // Si déjà paralysé, on prolonge
            if(monster.getState() == State.PARALIZED){
                if(Math.random() >= getParalizedChance()){
                     monster.setEffectDuration(monster.getEffectDuration() + 1);
                }
            } 
            else {
                if(Math.random() < getParalizedChance()) {
                    monster.setState(State.PARALIZED);
                    monster.setEffectDuration(2); // Durée initiale de paralysie
                    System.out.println(this.getName() + " paralyse " + monster.getName() + " !");
                }
            }
        }

        monster.getAttacked(this, attack);
    }


    /**
     * Handle being attacked by another monster
     * @param monster Attacking monster
     * @param attack Attack used
     */
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

    /**
     * Get the paralized chance of the monster
     * @return paralized chance
     */
    public double getParalizedChance() {
        return this.paralizedChance;
    }
    
    /**
     * Set the paralized chance of the monster
     * @param paralizedChance New paralized chance
     */
    @Override
    public void onStartTurn(Monster opponent) {
        super.onStartTurn(opponent); // Appelle la logique de base (brûlure, etc.)

        // Gestion de la guérison de la Paralysie
        if (this.getState() == State.PARALIZED) {
            // effectDuration est incrémenté dans super.onStartTurn
            double chanceToCure = (double)this.getEffectDuration() / 6.0;
            
            if (Math.random() < chanceToCure) {
                this.setState(State.NORMAL);
                this.setEffectDuration(0);
                System.out.println(this.getName() + " n'est plus paralysé !");
            }
        }
    }
}