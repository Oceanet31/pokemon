package com.esiea.pootp.objects;

import com.esiea.pootp.monsters.Monster;
import com.esiea.pootp.monsters.State;

public class Medicament extends Item {
    private String name;

    public Medicament(String name) {
        this.name = name;
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
