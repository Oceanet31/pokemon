package com.esiea.pootp.objects;

import com.esiea.pootp.monsters.Monster;
import com.esiea.pootp.monsters.State;

public class Medicament extends Item {
    private String name;
    private State curedState;

    /**
     * Constructor for Medicament
     * @param name Name of the medicament
     * @param curedState State that the medicament cures
     */
    public Medicament(String name, State curedState) {
        this.name = name;
        this.curedState = curedState;
    }

    @Override
    /**
     * Get the name of the medicament
     * @return name of the medicament
     */
    public String getName() {
        return this.name;
    }


    @Override
    /**
     * Use the medicament on a monster
     * @param monster Monster to use the medicament on
     */
    public void use(Monster monster) {
        monster.setState(State.NORMAL);
        System.out.println(monster.getName() + " is now in NORMAL state.");
    }
}
