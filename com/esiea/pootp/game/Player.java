package com.esiea.pootp.game;

import java.util.ArrayList;
import java.util.HashMap;

import com.esiea.pootp.monsters.Monster;
import com.esiea.pootp.objects.Item;

public class Player {

    private String name;
    private ArrayList<Monster> team;
    private HashMap<Item,Integer> inventory;
    
    public Player(String name) {
        this.name = name;
        this.team = new ArrayList<Monster>();
        this.inventory = new HashMap<Item,Integer>();
    }

    public String getName() {
        return this.name;
    }

    public ArrayList<Monster> getTeam() {
        return this.team;
    }

    public HashMap<Item,Integer> getInventory() {
        return this.inventory;
    }

    public void addMonsterToTeam(Monster monster) {
        this.team.add(monster);
    }

    public void addItemToInventory(Item item) {
        this.inventory.put(item, this.inventory.getOrDefault(item, 0) + 1);
    }

    public void useItem(Item item, Monster monster) {
        if(this.inventory.containsKey(item) && this.inventory.get(item) > 0) {
            System.out.println(this.name + " used " + item.getName() + " on " + monster.getName() + ".");
            item.use(monster);
            this.inventory.put(item, this.inventory.get(item) - 1);
        } else {
            System.out.println("Item not available in inventory.");
        }
    }

    public boolean hasLost() {
        for(Monster monster : this.team) {
            if(monster.getState() != com.esiea.pootp.monsters.State.DEAD) {
                return false;
            }
        }
        return true;
    }

    public Monster getActiveMonster() {
        for(Monster monster : this.team) {
            if(monster.getState() != com.esiea.pootp.monsters.State.DEAD) {
                return monster;
            }
        }
        return null; // All monsters are dead
    }
}