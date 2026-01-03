package com.esiea.pootp.game;

import java.util.ArrayList;
import java.util.HashMap;

import com.esiea.pootp.monsters.Monster;
import com.esiea.pootp.objects.Item;

public class Player {

    private String name;
    private ArrayList<Monster> team;
    private HashMap<Item,Integer> inventory;
    
    /**
     * Constructor for Player
     * @param name Name of the player
     */
    public Player(String name) {
        this.name = name;
        this.team = new ArrayList<Monster>();
        this.inventory = new HashMap<Item,Integer>();
    }


    /**
     * Get the name of the player
     * @return name of the player
     */
    public String getName() {
        return this.name;
    }


    /**
     * Get the team of monsters
     * @return team of monsters
     */
    public ArrayList<Monster> getTeam() {
        return this.team;
    }


    /**
     * Get the inventory of items
     * @return inventory of items
     */
    public HashMap<Item,Integer> getInventory() {
        return this.inventory;
    }


    /**
     * Add a monster to the player's team
     * @param monster Monster to add
     */
    public void addMonsterToTeam(Monster monster) {
        this.team.add(monster);
    }


    /**
     * Add an item to the player's inventory
     * @param item Item to add
     */
    public void addItemToInventory(Item item) {
        this.inventory.put(item, this.inventory.getOrDefault(item, 0) + 1);
    }


    /**
     * Use an item from the inventory on a monster
     * @param item Item to use
     * @param monster Monster to use the item on
     */
    public void useItem(Item item, Monster monster) {
        if(this.inventory.containsKey(item) && this.inventory.get(item) > 0) {
            System.out.println(this.name + " used " + item.getName() + " on " + monster.getName() + ".");
            item.use(monster);
            this.inventory.put(item, this.inventory.get(item) - 1);
        } else {
            System.out.println("Item not available in inventory.");
        }
    }


    /**
     * Check if the player has lost (all monsters are dead)
     * @return true if the player has lost, false otherwise
     */
    public boolean hasLost() {
        for(Monster monster : this.team) {
            if(monster.getState() != com.esiea.pootp.monsters.State.DEAD) {
                return false;
            }
        }
        return true;
    }


    /**
     * Get the active monster (the first non-dead monster in the team)
     * @return active monster
     */
    public Monster getActiveMonster() {
        for(Monster monster : this.team) {
            if(monster.getState() != com.esiea.pootp.monsters.State.DEAD) {
                return monster;
            }
        }
        return null; // All monsters are dead
    }
}