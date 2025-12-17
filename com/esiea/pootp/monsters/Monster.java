package com.esiea.pootp.monsters;

import java.util.ArrayList;

public class Monster{
    private ElementType element;
    private String name;
    private int hp;
    private ArrayList<Attack> attacks;
    private int defense;
    private int speed;

    public Monster(String name){
        this.name = name;
    }

}