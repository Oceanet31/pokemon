package com.esiea.pootp.objects;



public class Potion extends Item{
    private String name;
    private double power;
    private PotionType type;

    public Potion(String name, double power, PotionType type){
        super(name);
        this.power = power;
        this.type = type;
    }

    public int getPower(){
        return this.power;
    }

    public PotionType getType(){
        return this.type;
    }

    public void usePotion(Monster monster){
        switch(this.type){
            case HEAL:
                monster.heal(this.power);
                break;
            case REVIVE:
                monster.revive(this.power);
                break;
            default:
                break;
        }
    }
}