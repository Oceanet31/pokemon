package com.esiea.pootp.attacks;
import com.esiea.pootp.monsters.ElementType;

public class Attack{

    private String name;
    private ElementType type;
    private int nbUse;
    private int power;
    private double failProbability;

    public Attack(String name, ElementType type, int nbUse, int power, double failProbability) {
        this.name = name;
        this.type = type;
        this.nbUse = nbUse;
        this.power = power;
        this.failProbability = failProbability;
    }

    public String getName(){
        return this.name;
    }

    public ElementType getType(){
        return this.type;
    }

    public int getNbUse(){
        return this.nbUse;
    }

    public int getPower(){
        return this.power;
    }

    public double getFailProbability(){
        return this.failProbability;
    }

    public void AttackUsed(){
        if(this.nbUse > 0){
            this.nbUse -= 1;
        }
    }

}