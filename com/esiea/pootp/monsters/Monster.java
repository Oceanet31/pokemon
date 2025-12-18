package com.esiea.pootp.monsters;

import com.esiea.pootp.attacks.*;
import java.util.ArrayList;

public abstract class Monster{
    private ElementType element;
    private String name;
    private int hp;
    private ArrayList<Attack> attacks;
    private int defense;
    private double attack;
    private int speed;
    private State state;
    private int round;
    private int effectDuration;

    public Monster(String name, ElementType element, int hp, int defense, double attack, int speed){
        this.name = name;
        this.element = element;
        this.state = State.NORMAL;
        this.attacks = new ArrayList<Attack>();
        this.hp = hp;
        this.defense = defense;
        this.attack = attack;
        this.speed = speed;
        this.effectDuration = 0;
    }

    //Getters and setters
    public ElementType getElement(){
        return this.element;
    }

    public State getState(){
        return this.state;
    }

    public void setState(State newState){
        this.state = newState;
    }

    public int getEffectDuration(){
        return this.effectDuration;
    }

    public void setEffectDuration(int value){
        this.effectDuration = value;
    }

    public int getDefense(){
        return this.defense;
    }

    public void setDefense(int value){
        this.defense = value;
    }

    //Override in all monster classes
    public double damages(Monster monster){
        double coef = 0.85 + (0.15)*Math.random();

        double damage = 20*(this.attack/monster.defense)*coef;

        return damage;
    }

    //Monster takes damage
    public void takeDamage(double amount) {
        this.hp -= (int) amount;
        if (this.hp < 0) this.hp = 0;
    }


    //Monster has been attacked
    public abstract void getAttacked(Monster monster);


    //opponent monster has been attacked
    public abstract void attack(Monster monster);

}