package com.esiea.pootp.objects;

import com.esiea.pootp.monsters.Monster;

public abstract class Item{
    /**
     * Use the item on a monster
     * @param monster Monster to use the item on
     */
    abstract public void use(Monster monster);

    /**
     * Get the name of the item
     * @return name of the item
     */
    abstract public String getName();

    public boolean equals(Item item) {
        return this.getName().equals(item.getName());
    }

    public boolean isHealingItem() {
        return this instanceof Potion;
    }
}