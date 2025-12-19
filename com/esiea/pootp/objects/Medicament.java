package com.esiea.pootp.objects;

import com.esiea.pootp.monsters.Monster;
import com.esiea.pootp.monsters.State;

public class Medicament extends Item {
    private String name;
    private State curedState;

    public Medicament(String name, State curedState) {
        this.name = name;
        this.curedState = curedState;
    }

    @Override
    public String getName() {
        return this.name;
    }


    @Override
    public void use(Monster monster) {
        monster.setState(State.NORMAL);
        System.out.println(monster.getName() + " is now in NORMAL state.");
    }
}
