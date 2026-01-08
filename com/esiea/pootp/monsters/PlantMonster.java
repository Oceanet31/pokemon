package com.esiea.pootp.monsters;

import com.esiea.pootp.attacks.Attack;

public class PlantMonster extends NatureMonster{

    /**
     * Constructor for PlantMonster
     * @param name Name of the monster
     * @param hp Health points of the monster
     * @param defense Defense stat of the monster
     * @param attack Attack stat of the monster
     * @param speed Speed stat of the monster
     */
    public PlantMonster(String name, int hp, int defense, double attack, int speed){
        super(name, hp, defense, attack, speed);
    }
    
    @Override
    /**
     * Perform an attack on a target monster
     * @param target Target monster to attack
     * @param attack Attack to use
     */
    public void attack(Monster target, Attack attack){
        // Soin passif (PV) lié au type Nature (Seulement si terrain inondé)
        this.recoverHealth(target);
        
        // L'attaque inflige des dégâts
        target.getAttacked(this, attack);

        // Effet Spécial PLANTE (Guérison Statut)
        // Condition : Attaque de type NATURE
        if (attack != null && attack.getType() == ElementType.NATURE) {
            
            // 20% de chance de guérir les altérations (Poison, Brûlure, Paralysie)
            if (Math.random() < 0.2) { // 20% de chance
                if (this.getState() != State.NORMAL && this.getState() != State.DEAD) {
                    this.setState(State.NORMAL);
                    System.out.println(this.getName() + " se soigne de ses problèmes de statut !");
                }
            }
        }
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
            case FIRE :
                //Faiblesse monster m'inflige 2*damage
                damage = 2*super.damages(this);
                break;

            case EARTH :
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


}