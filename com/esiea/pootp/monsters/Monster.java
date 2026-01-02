package com.esiea.pootp.monsters;

import com.esiea.pootp.PokemonApp; // IMPORTANT : Pour accéder à la base d'attaques
import com.esiea.pootp.attacks.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public abstract class Monster{
    private ElementType element;
    private String name;
    private int hp;
    private ArrayList<Attack> attacks;
    private int defense;
    private double attack;
    private int speed;
    private State state;
    private int round;
    private int effectDuration;
    private int startingHp;
    private String evolutionName;
    private int level = 1;
    private int xp = 0;
    private int xpToNextLevel = 100;

    public Monster(String name, ElementType element, int hp, int defense, double attack, int speed){
        this.name = name;
        this.element = element;
        this.state = State.NORMAL;
        this.attacks = new ArrayList<Attack>();
        this.hp = hp;
        this.defense = defense;
        this.attack = attack;
        this.speed = speed;
        this.effectDuration = 0;
        this.startingHp = hp;
    }

    //Getters and setters
    public ElementType getElement(){
        return this.element;
    }

    public State getState(){
        return this.state;
    }

    public void setState(State newState){
        this.state = newState;
    }

    public ArrayList<Attack> getAttacks(){
        return this.attacks;
    }

    public int getEffectDuration(){
        return this.effectDuration;
    }

    public void setEffectDuration(int value){
        this.effectDuration = value;
    }

    public void healFullHP(){
        this.hp = this.startingHp;
    }

    public int getDefense(){
        return this.defense;
    }

    public String getName(){
        return this.name;
    }

    public int getSpeed(){
        return this.speed;
    }

    public double getAttack(){
        return this.attack;
    }

    public int getLevel(){ 
        return level; 
    }

    public int getXp(){ 
        return xp; 
    }

    public int getXpToNextLevel(){ 
        return xpToNextLevel; 
    }

    public void setAttack(double value){
        this.attack = value;
    }

    public void setSpeed(int value){
        this.speed = value;
    }

    public void setDefense(int value){
        this.defense = value;
    }

    public int getHp(){
        return this.hp;
    }

    public void setHp(int value){
        if(value >= this.startingHp) {
            this.hp = this.startingHp;
        } else {
            this.hp = value;
        }
    }

    public int getStartingHp() {
        return this.startingHp;
    }

    public void setEvolutionName(String name) {
    this.evolutionName = name;
    }

    public String getEvolutionName() {
        return this.evolutionName;
    }

    //Override in all monster classes
    public double damages(Monster monster){
        double coef = 0.85 + (0.15)*Math.random();
        double damage = 20*(this.attack/monster.defense)*coef;

        return damage;
    }

     public double damages(Monster monster, Attack power){
        double coef = 0.85 + (0.15)*Math.random();
        double damage = 0;

        if(power == null){
            damage = 20*(this.attack/monster.defense)*coef;
        } else if (power != null){
            damage = (((11*this.attack*power.getPower())/(25*monster.defense))+2)*coef;
        }
            
        return damage;

    }

    //Monster takes damage
    public void takeDamage(double amount) {
        this.hp -= (int) amount;
        if (this.hp < 0) this.hp = 0;
    }

    // Monster applies state effects at the start of its turn
    public void applyStateEffects() {
        if (this.state == State.BURNED || this.state == State.POISONED) {
    
            double burnDamage = this.attack / 10;
            this.takeDamage(burnDamage);
            if(this.state == State.BURNED) System.out.println(this.name + " souffre de sa brûlure...");

            if(this.state == State.POISONED) System.out.println(this.name + " souffre de son empoissonnement...");

        }
    }

    public void gainXp(int amount) { //Méthode pour gagner de l'expérience
        this.xp += amount;
        
        // Boucle while au cas où on gagne assez d'XP pour passer plusieurs niveaux d'un coup
        while (this.xp >= this.xpToNextLevel) {
            levelUp();
        }
    }

    private void levelUp() { //Méthode pour gérer le passage au niveau supérieur
        this.level++;
        this.xp -= this.xpToNextLevel; // On garde le surplus d'XP
        
        // La courbe d'XP devient plus dure (prochain niveau demande +20% d'XP)
        this.xpToNextLevel = (int)(this.xpToNextLevel * 1.2);

        System.out.println("\n " + this.name + " passe au niveau " + this.level + " !");

        // Augmentation des statistiques (+10% par niveau)
        increaseStats(1.10);
        
        // Apprentissage d'une nouvelle attaque
        learnNewAttack();
        
        // Soin complet gratuit au passage de niveau
        this.healFullHP();
    }

    // --- NOUVELLE MÉTHODE : APPRENTISSAGE D'ATTAQUE ---
    private void learnNewAttack() {
        // 1. Récupérer toutes les attaques disponibles via PokemonApp
        ArrayList<Attack> allAttacks = PokemonApp.attackDB.getAttacks();
        ArrayList<Attack> learnableAttacks = new ArrayList<>();

        // 2. Filtrer les attaques compatibles (Même type ou NATURE) et pas encore connues
        for (Attack a : allAttacks) {
            boolean isCompatible = (a.getType() == this.element || a.getType() == ElementType.NORMAL);
            boolean isKnown = this.attacks.contains(a);
            
            if (isCompatible && !isKnown) {
                learnableAttacks.add(a);
            }
        }

        // 3. Si des attaques sont disponibles, en apprendre une au hasard
        if (!learnableAttacks.isEmpty()) {
            Random r = new Random();
            Attack newAttack = learnableAttacks.get(r.nextInt(learnableAttacks.size()));

            if (this.attacks.size() < 4) {
                this.attacks.add(newAttack);
                System.out.println(this.name + " apprend l'attaque " + newAttack.getName() + " !");
            } else {
                // Si déjà 4 attaques, on oublie la première (la plus vieille)
                Attack forgotten = this.attacks.get(0);
                this.attacks.remove(0);
                this.attacks.add(newAttack);
                System.out.println(this.name + " oublie " + forgotten.getName() + " et apprend " + newAttack.getName() + " !");
            }
        }
    }

    private void increaseStats(double multiplier) { //Méthode pour augmenter les stats du monstre
        // PV Max augmentent
        int oldMaxHp = this.startingHp;
        this.startingHp = (int)(this.startingHp * multiplier);
        
        // Attaque augmente
        this.attack = this.attack * multiplier;
        
        // Défense augmente
        this.defense = (int)(this.defense * multiplier);
        
        // Vitesse augmente
        this.speed = (int)(this.speed * multiplier);

        System.out.println("   PV Max: " + oldMaxHp + " -> " + this.startingHp);
        System.out.println("   Attaque: " + String.format("%.1f", this.attack));
        System.out.println("   Défense: " + this.defense);
    }


    //Monster has been attacked
    public abstract void getAttacked(Monster monster, Attack attack);


    //opponent monster has been attacked
    public abstract void attack(Monster monster, Attack attack);

}