package com.esiea.pootp.objects;

import com.esiea.pootp.monsters.Monster;

public class Pokeball extends Item {
    @Override
    public String getName() {
        return "Pokéball";
    }

    @Override
    public void use(Monster monster) {
        // La logique est gérée par le GameEngine car elle affecte l'équipe
        System.out.println("Lancer de Pokéball !");
    }
}