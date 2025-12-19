package com.esiea.pootp.game;

import com.esiea.pootp.attacks.Attack;
import com.esiea.pootp.monsters.Monster;
import com.esiea.pootp.objects.Item;
import java.util.Scanner;
import java.util.List;
import java.util.ArrayList;
import com.esiea.pootp.gui.GameWindow;

public class GameEngine {
    private Player player;
    private Player enemy;
    private boolean isWildBattle;
    private Scanner scanner;
    private GameWindow window;

    private class ActionChoice { // Structure pour stocker le choix d'action
        int type; // 1: Attack, 2: Item, 3: Switch, 4: Flee
        Attack attack;
        Item item;
        int nextMonsterIndex;
    }

    public GameEngine(Player player, Player enemy, boolean isWildBattle, GameWindow window) {
        this.player = player;
        this.enemy = enemy;
        this.isWildBattle = isWildBattle;
        this.window = window;
    }

    public void startBattle() {
        window.addLog("A wild " + enemy.getActiveMonster().getName() + " appears!\n");
        window.addLog("Go " + player.getActiveMonster().getName() + "!\n");
        window.addLog("Battle Start!");
        updateGraphics();
        Battle();
    }

    public void Battle() {

        while (!player.hasLost() && !enemy.hasLost()) {
            playTurn();
        }

        if (player.hasLost()) {
            window.addLog("\n" + player.getName() + " n'a plus de monstres. " + enemy.getName() + " gagne !");
        } else {
            window.addLog("\n" + enemy.getName() + " est vaincu ! " + player.getName() + " gagne !");
        }
    }

    public void playTurn() {
        // 1. On récupère les choix
        ActionChoice playerAction = playerChoice();
        ActionChoice enemyAction = AIChoice();

        // 2. Priorité 1 : Fuite (Action immédiate)
        if (playerAction.type == 4) {
            window.addLog("You fled the battle!");
            System.exit(0);
        }

        // 3. Priorité 2 : Changements de monstres
        if (playerAction.type == 3) executeSwitch(player, playerAction.nextMonsterIndex);
        if (enemyAction.type == 3) executeSwitch(enemy, enemyAction.nextMonsterIndex);

        // 4. Priorité 3 : Utilisation d'objets
        if (playerAction.type == 2) player.useItem(playerAction.item, player.getActiveMonster());
        if (enemyAction.type == 2) enemy.useItem(enemyAction.item, enemy.getActiveMonster());

        // 5. Priorité 4 : Attaques (Gestion de la vitesse)
        resolveAttacks(playerAction, enemyAction);

        // 6. Vérification si un monstre est KO à la fin du tour
        handleKOSwitch(player);
        handleKOSwitch(enemy);
    }

    private ActionChoice playerChoice() {

        ActionChoice action = new ActionChoice();
        window.addLog("\n--- Tour de " + player.getName() + " ---");
        window.addLog("1. Attack | 2. Use Item | 3. Switch Monster | 4. Flee");
        int choice = scanner.nextInt();

        switch (choice) { 
            case 1:             // Attaque
                action.type = 1;
                action.attack = selectAttackMenu(player.getActiveMonster());
                break;
            case 2:            // Utilisation d'objet
                action.type = 2;
                action.item = selectItemMenu(player);
                if (action.item == null) return playerChoice();
                break;
            case 3:           // Changement de monstre
                action.type = 3;
                action.nextMonsterIndex = selectMonsterMenu(player);
                break;
            case 4:          // Fuite
                action.type = 4;
                break;
            default:
                window.addLog("Invalid choice.");
                return playerChoice();
        }
        return action;
    }

    private ActionChoice AIChoice() {
        ActionChoice action = new ActionChoice();
        Monster active = enemy.getActiveMonster();
        
        // IA: Si HP < 20% et a des potions, elle soigne, sinon attaque
        if (!isWildBattle && active.getHp() < 30 && !enemy.getInventory().isEmpty()) {
            action.type = 2;
            action.item = enemy.getInventory().keySet().iterator().next();
        } else {
            action.type = 1;
            List<Attack> attacks = active.getAttacks();
            if (!attacks.isEmpty()) {
                action.attack = attacks.get((int) (Math.random() * attacks.size()));
            }
        }
        return action;
    }

    // ------------------------- Méthodes de résolution ------------------------- //

    private void resolveAttacks(ActionChoice playerAction, ActionChoice enemyAction) {
        Monster playMonster = player.getActiveMonster();
        Monster enemyMonster = enemy.getActiveMonster();

        if (playerAction.type == 1 && enemyAction.type == 1) {

            // Comparaison de vitesse pour l'initiative
            if (playMonster.getSpeed() >= enemyMonster.getSpeed()) { // Le joueur attaque en premier

                executeAttack(playMonster, enemyMonster, playerAction.attack);

                if (enemyMonster.getHp() > 0) { // Vérifie si le monstre de l'ennemi est toujours en vie

                        executeAttack(enemyMonster, playMonster, enemyAction.attack); // Attaque de l'ennemi
                } 
            } else { // L'ennemi attaque en premier

                executeAttack(enemyMonster, playMonster, enemyAction.attack);

                if (playMonster.getHp() > 0) { // Vérifie si le monstre du joueur est toujours en vie

                    executeAttack(playMonster, enemyMonster, playerAction.attack); // Attaque du joueur
                }
            }
        } else if (playerAction.type == 1) { // Seulement le joueur attaque

            executeAttack(playMonster, enemyMonster, playerAction.attack);

        } else if (enemyAction.type == 1) { // Seulement l'ennemi attaque

            executeAttack(enemyMonster, playMonster, enemyAction.attack);

        }
    }
    private void executeAttack(Monster attacker, Monster defender, Attack attack) {
        if (attacker.getHp() <= 0) return;

        window.addLog(attacker.getName() + " utilise " + (attack != null ? attack.getName() : "une attaque de base") + " !");

        // On demande au monstre d'exécuter son attaque complète (dégâts + effets)
        attacker.attack(defender, attack);
    }

    private void executeSwitch(Player player, int index) { // Changement de monstre

        Monster monster = player.getTeam().get(index);
        player.getTeam().remove(index);
        player.getTeam().add(0, monster); // Place le monstre choisi en première position (actif)
        window.addLog(player.getName() + " switches to " + monster.getName() + "!");
    }

   private void handleKOSwitch(Player player) { 
        
        Monster active = player.getActiveMonster();//On récupère le monstre actif

        if (active == null) {
            return;
        }

        // On vérifie s'il est KO
        if (active.getHp() <= 0) { 
            
            window.addLog(active.getName() + " est KO !");
            active.setState(com.esiea.pootp.monsters.State.DEAD);// On s'assure qu'il est bien marqué mort

            if (player == this.enemy) {
            // On récupère le tueur (le monstre actif du joueur)
            Monster killer = this.player.getActiveMonster();
            
            if (killer != null) {
                // Formule simple : 50 XP de base + (Niveau ennemi * 10)
                // Comme les ennemis n'ont pas encore de niveau défini, on donne 100 XP fixe pour l'instant
                int xpReward = 100; 
                killer.gainXp(xpReward);
            }
        }

            if (!player.hasLost()) {
                if (player == this.player) { // Si c'est l'humain
                    int next = selectMonsterMenu(player);
                    executeSwitch(player, next);
                } else {
                    // L'IA prend le premier disponible (qui n'est pas mort)
                    window.addLog(player.getName() + " envoie un autre monstre !");
                }
            }
        }
    }

    // ---------------------- Menus de sélection -------------------------- //

    private Attack selectAttackMenu(Monster monster) { // Sélection de l'attaque
        List<Attack> attacks = monster.getAttacks();

        if (attacks.isEmpty())
        {
            window.addLog(monster.getName() + " has no attacks, he will fight bare-handed!");
            return null;
        }

        for (int i = 0; i < attacks.size(); i++) {

            window.addLog((i + 1) + ". " + attacks.get(i).getName());
        }
        return attacks.get(scanner.nextInt() - 1);
    }

    private Item selectItemMenu(Player p) { // Sélection de l'objet

        if (p.getInventory().isEmpty()) // Vérifie si l'inventaire est vide
        {
            return null;
        }

        List<Item> items = new ArrayList<>(p.getInventory().keySet()); // Récupère les objets disponibles

        for (int i = 0; i < items.size(); i++) { // Affiche les objets

            window.addLog(i + ". " + items.get(i).getName());

        }

        return items.get(scanner.nextInt()); // Retourne l'objet sélectionné
    }

    private int selectMonsterMenu(Player p) { // Sélection du monstre

        window.addLog("Select a monster:");

        for (int i = 0; i < p.getTeam().size(); i++) { // Affiche les monstres disponibles

            Monster m = p.getTeam().get(i);
            if (m.getHp() > 0) window.addLog(i + ". " + m.getName() + " (" + m.getHp() + " HP)"); // Affiche uniquement les monstres vivants

        }

        return scanner.nextInt();
    }

    //------------------------- Mise à jour graphique ------------------------- //
    public void updateGraphics() 
    {
        window.getBattlePanel().updateMonsters(player.getActiveMonster(), enemy.getActiveMonster());
    }
}