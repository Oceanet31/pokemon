package com.esiea.pootp.game;

import com.esiea.pootp.attacks.Attack;
import com.esiea.pootp.monsters.Monster;
import com.esiea.pootp.objects.Item;
import com.esiea.pootp.gui.GameWindow;
import java.util.List;
import java.util.ArrayList;

public class GameEngine {
    private Player player;
    private Player enemy;
    private boolean isWildBattle;
    private GameWindow window;
    
    // Callback pour prévenir l'App que le combat est gagné
    private Runnable onVictory;

    private class ActionChoice {
        int type; // 1: Attack, 2: Item
        Attack attack;
        Item item;
    }

    public GameEngine(Player player, Player enemy, boolean isWildBattle, GameWindow window) {
        this.player = player;
        this.enemy = enemy;
        this.isWildBattle = isWildBattle;
        this.window = window;
        this.window.setGameEngine(this);
    }

    public void setOnVictory(Runnable action) {
        this.onVictory = action;
    }

    public void startBattle() {
        window.showDialog("Un " + enemy.getActiveMonster().getName() + " sauvage apparaît !", () -> {
            window.showDialog("Go " + player.getActiveMonster().getName() + " !", () -> {
                updateGraphics();
                window.showMainMenuButtons();
            });
        });
    }

    public void onPlayerAttack(Attack attack) {
        if (isBattleOver()) return;
        ActionChoice playerAction = new ActionChoice();
        playerAction.type = 1;
        playerAction.attack = attack;
        processTurn(playerAction);
    }

    public void onPlayerUseItem(Item item) {
        if (isBattleOver()) return;
        ActionChoice playerAction = new ActionChoice();
        playerAction.type = 2;
        playerAction.item = item;
        processTurn(playerAction);
    }

    private void processTurn(ActionChoice playerAction) {
        ActionChoice enemyAction = AIChoice();
        Monster pMonster = player.getActiveMonster();
        Monster eMonster = enemy.getActiveMonster();
        
        boolean playerFirst = pMonster.getSpeed() >= eMonster.getSpeed();
        if (playerAction.type == 2) playerFirst = true;

        // CHAÎNE D'ACTIONS : A -> B -> Fin
        if (playerFirst) {
            executeAction(player, playerAction, pMonster, eMonster, () -> {
                if (!checkBattleEnd()) {
                    executeAction(enemy, enemyAction, eMonster, pMonster, () -> endTurn());
                }
            });
        } else {
            executeAction(enemy, enemyAction, eMonster, pMonster, () -> {
                if (!checkBattleEnd()) {
                    executeAction(player, playerAction, pMonster, eMonster, () -> endTurn());
                }
            });
        }
    }

    private void executeAction(Player actor, ActionChoice action, Monster attacker, Monster defender, Runnable nextStep) {
        if (attacker.getHp() <= 0) {
            nextStep.run();
            return;
        }

        if (action.type == 2) { 
            window.showDialog(actor.getName() + " utilise " + action.item.getName() + " !", () -> {
                actor.useItem(action.item, attacker);
                updateGraphics();
                nextStep.run();
            });
        } 
        else if (action.type == 1) { 
            String atkName = (action.attack != null) ? action.attack.getName() : "Lutte";
            // 1. Affiche le texte
            window.showDialog(attacker.getName() + " utilise " + atkName + " !", () -> {
                // 2. Applique les dégâts après le texte
                attacker.attack(defender, action.attack);
                updateGraphics();
                nextStep.run();
            });
        }
    }

    private void endTurn() {
        if (!checkBattleEnd()) {
            window.showMainMenuButtons();
        }
    }

    private boolean checkBattleEnd() {
        if (enemy.hasLost()) {
            window.showDialog(enemy.getActiveMonster().getName() + " est KO !\nVictoire !", () -> {
                if (onVictory != null) onVictory.run(); // LANCE LE PROCHAIN ROUND
            });
            return true;
        } 
        else if (player.hasLost()) {
            window.showDialog(player.getActiveMonster().getName() + " est KO...\nVous avez perdu.", () -> {
                // Game Over
            });
            return true;
        }
        
        if (enemy.getActiveMonster().getHp() <= 0) {
             handleKOSwitch(enemy);
             updateGraphics();
             return false;
        }
        if (player.getActiveMonster().getHp() <= 0) {
             handleKOSwitch(player);
             updateGraphics();
             return false;
        }

        return false;
    }

    private void handleKOSwitch(Player p) {
        Monster active = p.getActiveMonster();
        if (active != null && active.getHp() <= 0) {
            active.setState(com.esiea.pootp.monsters.State.DEAD);
            for (Monster m : p.getTeam()) {
                if (m.getHp() > 0) {
                    p.getTeam().remove(m);
                    p.getTeam().add(0, m); 
                    return;
                }
            }
        }
    }

    private ActionChoice AIChoice() {
        ActionChoice action = new ActionChoice();
        Monster active = enemy.getActiveMonster();
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
    
    private boolean isBattleOver() {
        return player.hasLost() || enemy.hasLost();
    }

    public void updateGraphics() {
        window.getBattlePanel().updateMonsters(player.getActiveMonster(), enemy.getActiveMonster());
        window.getBattlePanel().repaint();
    }
}