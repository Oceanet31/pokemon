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

    

}