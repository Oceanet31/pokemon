package com.esiea.pootp.attacks;
import com.esiea.pootp.monsters.ElementType;

public class Attack{

    private String name;
    private ElementType type;
    private int nbUse;
    private int power;
    private double failProbability;
    private int nbUseMax;

    /* Constructor for Attack */
    public Attack(String name, ElementType type, int nbUse, int power, double failProbability) {
        this.name = name;
        this.type = type;
        this.nbUse = nbUse;
        this.power = power;
        this.failProbability = failProbability;
        this.nbUseMax = nbUse;
    }

    /**
     * Constructeur de COPIE (Crée une nouvelle attaque identique à l'originale)
     * @param other L'attaque modèle à copier
     */
    public Attack(Attack other) {
        this.name = other.name;
        this.type = other.type;
        this.nbUse = other.nbUse;
        this.power = other.power;
        this.failProbability = other.failProbability;
        this.nbUseMax = nbUse;
    }

    /**
     * Get the name of the attack
     * @return name of the attack
     */
    public String getName(){
        return this.name;
    }

    /**
     * Get the type of the attack
     * @return type of the attack
     */
    public ElementType getType(){
        return this.type;
    }


    /**
     * Get the number of uses left for the attack
     * @return number of uses left
     */
    public int getNbUse(){
        return this.nbUse;
    }

    public int getMaxUse(){
        return this.nbUseMax;
    }


    /**
     * Set the number of uses left for the attack
     * @param nbUse number of uses left
     */
    public void setNbUse(int nbUse){
        this.nbUse = nbUse;
    }


    /**
     * Get the power of the attack
     * @return power of the attack
     */
    public int getPower(){
        return this.power;
    }


    /**
     * Get the fail probability of the attack
     * @return fail probability of the attack
     */
    public double getFailProbability(){
        return this.failProbability;
    }


    /**
     * Decrease the number of uses left for the attack by 1
     */
    public void attackUsed(){
        if(this.nbUse > 0){
            this.nbUse -= 1;
        }
    }

    /**
     * Get the accuracy of the attack
     * @return accuracy of the attack
     */
    public double getAccuracy(){
        return 1.0 - this.failProbability;
    }

}