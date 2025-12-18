package com.esiea.pootp.objects;

import com.esiea.pootp.monsters.Monster;

public abstract class Item{
    abstract public void use(Monster monster);

    abstract public String getName();
}