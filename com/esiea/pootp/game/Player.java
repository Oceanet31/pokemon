package com.esiea.pootp.game;

import java.util.ArrayList;

import com.esiea.pootp.monsters.Monster;

public class Player {

    private String name;
    private ArrayList<Monster> team;
    
    public Player(String name) {
        this.name = name;
        this.team = new ArrayList<>();
    }
    
}
