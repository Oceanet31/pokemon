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

    /**
     * Constructor for Monster
     * @param name Name of the monster
     * @param element Element type of the monster
     * @param hp Health points of the monster
     * @param defense Defense stat of the monster
     * @param attack Attack stat of the monster
     * @param speed Speed stat of the monster
     */
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
    /**
     * Get the element type of the monster
     * @return element type
     */
    public ElementType getElement(){
        return this.element;
    }

    /**
     * Get the current state of the monster
     * @return current state
     */
    public State getState(){
        return this.state;
    }


    /**
     * Set the state of the monster
     * @param newState New state to set
     */
    public void setState(State newState){
        this.state = newState;
    }

    /**
     * Get the list of attacks of the monster
     * @return list of attacks
     */
    public ArrayList<Attack> getAttacks(){
        return this.attacks;
    }

    /**
     * Get the effect duration of the monster
     * @return effect duration
     */
    public int getEffectDuration(){
        return this.effectDuration;
    }

    /**
     * Set the effect duration of the monster
     * @param value New effect duration
     */
    public void setEffectDuration(int value){
        this.effectDuration = value;
    }


    /**
     * Heal the monster to full HP
     */
    public void healFullHP(){
        this.hp = this.startingHp;
    }

    /**
     * Get the defense stat of the monster
     * @return defense stat
     */
    public int getDefense(){
        return this.defense;
    }


    /**
     * Get the name of the monster
     * @return name of the monster
     */
    public String getName(){
        return this.name;
    }

    /**
     * Get the speed stat of the monster
     * @return speed stat
     */
    public int getSpeed(){
        return this.speed;
    }


    /**
     * Get the attack stat of the monster
     * @return attack stat
     */
    public double getAttack(){
        return this.attack;
    }


    /**
     * Get the level of the monster
     * @return level of the monster
     */
    public int getLevel(){ 
        return level; 
    }


    /**
     * Get the experience points of the monster
     * @return experience points
     */
    public int getXp(){ 
        return xp; 
    }


    /**
     * Get the experience points needed for the next level
     * @return experience points to next level
     */
    public int getXpToNextLevel(){ 
        return xpToNextLevel; 
    }


    /**
     * Set the attack stat of the monster
     * @param value New attack stat
     */
    public void setAttack(double value){
        this.attack = value;
    }


    /**
     * Set the speed stat of the monster
     * @param value New speed stat
     */
    public void setSpeed(int value){
        this.speed = value;
    }


    /**
     * Set the defense stat of the monster
     * @param value New defense stat
     */
    public void setDefense(int value){
        this.defense = value;
    }


    /**
     * Get the current health points of the monster
     * @return current health points
     */
    public int getHp(){
        return this.hp;
    }


    /**
     * Set the health points of the monster
     * @param value New health points
     */
    public void setHp(int value){
        if(value >= this.startingHp) {
            this.hp = this.startingHp;
        } else {
            this.hp = value;
        }
    }


    /**
     * Get the starting health points of the monster
     * @return starting health points
     */
    public int getStartingHp() {
        return this.startingHp;
    }


    /**
     * Set the evolution name of the monster
     * @param name Evolution name
     */
    public void setEvolutionName(String name) {
        this.evolutionName = name;
    }


    /**
     * Get the evolution name of the monster
     * @return evolution name
     */
    public String getEvolutionName() {
        return this.evolutionName;
    }

    /**
     * Calculate damage dealt to another monster
     * @param monster Target monster
     */
    public double damages(Monster monster){
        double coef = 0.85 + (0.15)*Math.random();
        double damage = 20*(this.attack/monster.defense)*coef;

        return damage;
    }


    /**
     * Calculate damage dealt to another monster with a specific attack
     * @param monster Target monster
     * @param power Attack used
     */
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

    /**
     * Monster takes damage
     * @param amount Amount of damage taken
     */
    public void takeDamage(double amount) {
        this.hp -= (int) amount;
        if (this.hp < 0) this.hp = 0;
    }

    
    /**
     * Apply state effects at the start of the turn
     */
    public void applyStateEffects() {
        if (this.state == State.BURNED || this.state == State.POISONED) {
    
            double burnDamage = this.attack / 10;
            this.takeDamage(burnDamage);
            if(this.state == State.BURNED) System.out.println(this.name + " souffre de sa brûlure...");

            if(this.state == State.POISONED) System.out.println(this.name + " souffre de son empoissonnement...");

        }
    }

    /**
     * Gain experience points
     * @param amount Amount of experience points gained
     */
    public void gainXp(int amount) { //Méthode pour gagner de l'expérience
        this.xp += amount;
        
        // Boucle while au cas où on gagne assez d'XP pour passer plusieurs niveaux d'un coup
        while (this.xp >= this.xpToNextLevel) {
            levelUp();
        }
    }


    /**
     * Level up the monster
     */
    private void levelUp() {
        this.level++;
        this.xp -= this.xpToNextLevel; // On garde le surplus d'XP
        
        this.xpToNextLevel = (int)(this.xpToNextLevel * 1.2);

        System.out.println("\n " + this.name + " passe au niveau " + this.level + " !");

        // Augmentation des statistiques (+10% par niveau)
        increaseStats(1.10);
        
        // Apprentissage d'une nouvelle attaque
        learnNewAttack();
        
        // Soin complet gratuit au passage de niveau
        this.healFullHP();
    }

    /**
     * Assign 4 attacks to the monster from the attack database
     */
    public void assignAttacks(){
        for(int i=0; i<4; i++){
            this.learnNewAttack();
        }
    }
    
    /**
     * Learn a new attack
     */
    public void learnNewAttack() {
        ArrayList<Attack> allAttacks = PokemonApp.attackDB.getAttacks();
        ArrayList<Attack> learnableAttacks = new ArrayList<>();

        for (Attack a : allAttacks) {
            boolean isCompatible = (a.getType() == this.element || a.getType() == ElementType.NORMAL);
            boolean isKnown = this.attacks.contains(a);
            
            if (isCompatible && !isKnown) {
                learnableAttacks.add(a);
            }
        }

        
        if (!learnableAttacks.isEmpty()) {
            Random r = new Random();
            Attack newAttack = learnableAttacks.get(r.nextInt(learnableAttacks.size()));

            if (this.attacks.size() < 4) {
                this.attacks.add(newAttack);
                System.out.println(this.name + " apprend l'attaque " + newAttack.getName() + " !");
            } else {
                Attack forgotten = this.attacks.get(0);
                this.attacks.remove(0);
                this.attacks.add(newAttack);
                System.out.println(this.name + " oublie " + forgotten.getName() + " et apprend " + newAttack.getName() + " !");
            }
        }
    }

    /**
     * Increase monster stats by a multiplier
     * @param multiplier Multiplier to increase stats
     */
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

    /**
     * Handle effects at the start of the turn
     * @param opponent Opponent monster
     */
    public void onStartTurn(Monster opponent) {
        // 1. Incrémenter la durée des effets
        this.effectDuration++;

        // 2. Gestion Brûlure / Poison (Dégâts)
        if (this.state == State.BURNED || this.state == State.POISONED) {
            // Vérification Terrain Inondé pour guérir
            boolean isFlooded = (opponent instanceof WaterMonster && ((WaterMonster)opponent).isTerrainFlooded()) || (this instanceof WaterMonster && ((WaterMonster)this).isTerrainFlooded());
            
            if (isFlooded) {
                this.state = State.NORMAL;
                System.out.println(this.name + " est guéri par l'eau du terrain !");
            } else {
                // Application des dégâts
                int dmg = (int)(this.getAttack() / 10); // 1/10eme de son attaque
                this.takeDamage(dmg);
                System.out.println(this.name + " souffre (" + dmg + " dmg).");
            }
        }
    }


    //Monster has been attacked
    /**
     * Handle being attacked by another monster
     * @param monster Attacking monster
     * @param attack Attack used
     */
    public abstract void getAttacked(Monster monster, Attack attack);


    //opponent monster has been attacked
    /**
     * Perform an attack on a target monster
     * @param monster Target monster to attack
     * @param attack Attack to use
     */
    public abstract void attack(Monster monster, Attack attack);

}