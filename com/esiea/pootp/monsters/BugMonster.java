package com.esiea.pootp.monsters;

import com.esiea.pootp.attacks.Attack;

public class BugMonster extends NatureMonster{

    private int attackCount;

    /**
     * Constructor for BugMonster
     * @param name Name of the monster
     * @param hp Health points of the monster
     * @param defense Defense stat of the monster
     * @param attack Attack stat of the monster
     * @param speed Speed stat of the monster
     */
    public BugMonster(String name, int hp, int defense, double attack, int speed){
        super(name, hp, defense, attack, speed);
        this.attackCount = 0;
    }


    /**
     * Perform an attack on a target monster
     * @param target Target monster to attack
     * @param attack Attack to use
     */
    @Override
    public void attack(Monster target, Attack attack){
        this.recoverHealth(target);

        if (attack != null && attack.getType() == ElementType.NATURE) {
            this.attackCount++;
            
            // Si c'est la 3ème attaque, on empoisonne à coup sûr
            if (this.attackCount % 3 == 0) {

                target.setState(State.POISONED);
                System.out.println(this.getName() + " empoisonne " + target.getName() + " !");
                
            }
        }

        // Gestion de l'empoisonnement sur terrain inondé
        if (target.getState() == State.POISONED && target instanceof WaterMonster) {

            if (((WaterMonster) target).isTerrainFlooded()) {

                target.setState(State.NORMAL);
                System.out.println("L'eau lave le poison de " + target.getName() + " !");

            }
        }    
        target.applyStateEffects();
        target.getAttacked(this, attack);
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