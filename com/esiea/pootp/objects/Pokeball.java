package com.esiea.pootp.objects;

import com.esiea.pootp.monsters.Monster;

public class Pokeball extends Item {
    
    /**
     * Get the name of the Pokéball
     * @return name of the Pokéball
     */
    @Override
    public String getName() {
        return "Pokéball";
    }

    /**
     * Use the Pokéball on a monster
     * @param monster Monster to use the Pokéball on
     */
    @Override
    public void use(Monster monster) {
        // La logique est gérée par le GameEngine car elle affecte l'équipe
        System.out.println("Lancer de Pokéball !");
    }
}