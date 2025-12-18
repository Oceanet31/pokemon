package com.esiea.pootp.monsters;

import com.esiea.pootp.attacks.*;
import java.util.ArrayList;
import java.util.List;

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

     public List<Attack> getAttacks(){
        return this.attacks;
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

    public String getName(){
        return this.name;
    }

    public void setDefense(int value){
        this.defense = value;
    }

    public int getHp(){
        return this.hp;
    }

    public void setHp(int value){
        this.hp = value;
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

    // Monster applies state effects at the start of its turn
    public void applyStateEffects() {
    if (this.state == State.BURNED && this.state == State.POISONED) {
 
        double burnDamage = this.attack / 10;
        this.takeDamage(burnDamage);
        if(this.state == State.BURNED) System.out.println(this.name + " souffre de sa brûlure...");

        if(this.state == State.POISONED) System.out.println(this.name + " souffre de son empoissonnement...");

    }
}


    //Monster has been attacked
    public abstract void getAttacked(Monster monster, Attack attack);


    //opponent monster has been attacked
    public abstract void attack(Monster monster, Attack attack);

}