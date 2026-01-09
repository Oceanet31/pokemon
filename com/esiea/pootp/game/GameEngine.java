package com.esiea.pootp.game;

import com.esiea.pootp.attacks.Attack;
import com.esiea.pootp.monsters.ElementType;
import com.esiea.pootp.monsters.Monster;
import com.esiea.pootp.monsters.WaterMonster;
import com.esiea.pootp.monsters.State;
import com.esiea.pootp.objects.Item;
import com.esiea.pootp.gui.GameWindow;
import java.util.List;
import java.util.ArrayList;

/**
 * GameEngine : Le chef d'orchestre du combat.
 * ---------------------------------------------------------
 * Il ne gère pas l'affichage direct (c'est le rôle de BattlePanel),
 * mais il dit à la fenêtre (GameWindow) QUOI afficher et QUAND.
 */
public class GameEngine {
    private Player player;
    private Player enemy;
    /** Indique si le combat est un combat sauvage (capture possible) ou un duel de dresseurs */
    private boolean isWildBattle;
    private GameWindow window;
    
    /** Action à exécuter UNIQUEMENT si le joueur gagne (Donner XP, objets, etc.)
    * C'est PokemonApp qui définit cette action.
     */
    private Runnable onVictory;

    /**
     * Structure pour stocker le choix du joueur avant de l'exécuter.
     */
    private class ActionChoice {
        int type; // 1 = Attaque, 2 = Objet, 3 = Changement de Pokémon (Switch)
        Attack attack;   // Quelle attaque ? (si type 1)
        Item item;       // Quel objet ? (si type 2)
        int switchIndex; // Quel slot d'équipe ? (si type 3)
    }

    /**
     * Constructor for GameEngine
     * @param player the player
     * @param enemy the enemy player
     * @param isWildBattle true if it's a wild battle
     * @param window the game window
     */
    public GameEngine(Player player, Player enemy, boolean isWildBattle, GameWindow window) {
        this.player = player;
        this.enemy = enemy;
        this.isWildBattle = isWildBattle;
        this.window = window;
        this.window.setGameEngine(this); // On donne une référence du moteur à la fenêtre pour qu'elle puisse nous parler.
    }

    /**
     * Set the action to execute on victory
     * @param action the action to execute
     */
    public void setOnVictory(Runnable action) {
        this.onVictory = action;
    }

    /**
     * Démarre le combat.
     * Utilise des "Callbacks" imbriqués :
     * 1. Affiche "Un monstre apparait"
     * 2. QUAND c'est fini -> Affiche "Go Pikachu"
     * 3. QUAND c'est fini -> Affiche les boutons du menu.
     */
    public void startBattle() {
        window.showDialog("Un " + enemy.getActiveMonster().getName() + " sauvage apparaît !", () -> {
            window.showDialog("Go " + player.getActiveMonster().getName() + " !", () -> {
                updateGraphics(); // Met à jour les sprites à l'écran
                window.showMainMenuButtons(); // Rend la main au joueur
            });
        });
    }

    // =========================================================================
    //              MÉTHODES D'ENTRÉE (Appelées quand on clique sur un bouton)
    // =========================================================================

    /**
     * Handle player attack action
     * @param attack the attack chosen by the player
     */
    public void onPlayerAttack(Attack attack) {
        if (isBattleOver()) return; // Sécurité
        ActionChoice playerAction = new ActionChoice();
        playerAction.type = 1;
        playerAction.attack = attack;
        processTurn(playerAction); // On lance la résolution du tour
    }

    /**
     * Handle player use item action
     * @param item the item chosen by the player
     */
    public void onPlayerUseItem(Item item) {
        if (isBattleOver()) return;
        ActionChoice playerAction = new ActionChoice();
        playerAction.type = 2;
        playerAction.item = item;
        processTurn(playerAction);
    }

    /**
     * Handle player switch monster action
     * @param slotIndex the index of the monster to switch to
     */
    public void onPlayerSwitch(int slotIndex) {
        if (isBattleOver()) return;
        ActionChoice playerAction = new ActionChoice();
        playerAction.type = 3;
        playerAction.switchIndex = slotIndex;
        processTurn(playerAction);
    }

    // =========================================================================
    //                          LOGIQUE DU TOUR (COEUR DU JEU)
    // =========================================================================

    /**
     * Calcule qui joue en premier et enchaîne les actions.
     * C'est ici que la complexité asynchrone est gérée.
     * @param playerAction the action chosen by the player
     */
    private void processTurn(ActionChoice playerAction) {

        // On applique les effets temporels (Brûlure, Poison, flood, etc.)
        player.getActiveMonster().onStartTurn(enemy.getActiveMonster());
        enemy.getActiveMonster().onStartTurn(player.getActiveMonster());

        String EffectTypeP = "";
        String EffectTypeE = "";
        EffectTypeP = player.getActiveMonster().getState().toString();
        EffectTypeE = enemy.getActiveMonster().getState().toString();
        window.showDialog(player.getActiveMonster().getName() + " subit des effets de " + EffectTypeP + " !", () -> {
            updateGraphics();
        });
        window.showDialog(enemy.getActiveMonster().getName() + " subit des effets de " + EffectTypeE + " !", () -> {
            updateGraphics();
        });

        if (isBattleOver() || player.getActiveMonster().getHp() <= 0 || enemy.getActiveMonster().getHp() <= 0) {
             updateGraphics();
             checkBattleEndAndContinue(() -> endTurn());
             return;
        }


        ActionChoice enemyAction = AIChoice();
        
        Monster pMonsterStart = player.getActiveMonster();
        Monster eMonsterStart = enemy.getActiveMonster();

        
        
        // Comparaison de vitesse (Speed tie = avantage joueur)
        boolean playerFirst = pMonsterStart.getSpeed() >= eMonsterStart.getSpeed();
        if (playerAction.type == 2 || playerAction.type == 3) playerFirst = true;

        if (playerFirst) {
            // --- CAS A : JOUEUR RAPIDE ---
            executeAction(player, playerAction, pMonsterStart, eMonsterStart, () -> {
                
                // 1. Vérification Victoire/Défaite globale
                if (isBattleOver()) {
                    checkBattleEndAndContinue(() -> {}); 
                    return;
                }

                // 2. ANNULATION : Si l'ennemi est mort avant de pouvoir jouer
                if (enemy.getTeam().get(0).getHp() <= 0) {
                    // On gère le KO, et on force la FIN DU TOUR (endTurn) au lieu de l'attaque suivante
                    handleKOSwitch(enemy, () -> endTurn());
                    return;
                }

                // 3. Sinon, le combat continue normalement
                checkBattleEndAndContinue(() -> {
                    Monster currentTarget = player.getActiveMonster(); 
                    Monster currentAttacker = enemy.getActiveMonster();
                    
                    executeAction(enemy, enemyAction, currentAttacker, currentTarget, () -> {
                        checkBattleEndAndContinue(() -> endTurn());
                    });
                });
            });
        } else {
            // --- CAS B : ENNEMI RAPIDE ---
            executeAction(enemy, enemyAction, eMonsterStart, pMonsterStart, () -> {
                
                // 1. Vérification Victoire/Défaite globale
                if (isBattleOver()) {
                    checkBattleEndAndContinue(() -> {});
                    return;
                }

                // 2. ANNULATION : Si le joueur est mort avant de pouvoir jouer
                if (player.getTeam().get(0).getHp() <= 0) {
                    handleKOSwitch(player, () -> endTurn());
                    return;
                }

                // 3. Sinon, le combat continue
                checkBattleEndAndContinue(() -> {
                    Monster currentTarget = enemy.getActiveMonster();
                    Monster currentAttacker = player.getActiveMonster();
                    
                    executeAction(player, playerAction, currentAttacker, currentTarget, () -> {
                        checkBattleEndAndContinue(() -> endTurn());
                    });
                });
            });
        }
    }

    /**
     * Exécute une action unique (Switch, Objet ou Attaque).
     * @param actor Le joueur qui agit.
     * @param action L'action choisie par ce joueur.
     * @param attacker Le monstre qui agit (avant action).
     * @param defender Le monstre cible (avant action).
     * @param nextStep Le code à lancer quand tout est fini (affiché).
     */
    private void executeAction(Player actor, ActionChoice action, Monster attacker, Monster defender, Runnable nextStep) {
        
        // --- TYPE 3 : SWITCH (Changement de Pokémon) ---
        if (action.type == 3) {
            if (actor == player) { // Pour l'instant, seul le joueur switch manuellement

                Monster oldMon = actor.getTeam().get(0);
                
                if (oldMon instanceof WaterMonster) {
                    ((WaterMonster) oldMon).resetFlood();
                }

                Monster newMon = actor.getTeam().get(action.switchIndex);
                
                // Dialogue 1 : "On retire..."
                window.showDialog(actor.getName() + " retire " + attacker.getName() + "...", () -> {
                     
                     // ACTION RÉELLE : On échange les positions dans la liste.
                     // L'index 0 est toujours le monstre actif.
                     java.util.Collections.swap(actor.getTeam(), 0, action.switchIndex);
                     
                     // Dialogue 2 : "Go..."
                     window.showDialog("Go " + newMon.getName() + " !", () -> {
                         updateGraphics(); // Met à jour l'image
                         nextStep.run();   // Passe à la suite (Tour ennemi)
                     });
                });
            } else {
                nextStep.run(); // Si l'IA devait switcher, ce serait ici.
            }
            return; // On arrête là, car un switch remplace toute autre action.
        }

        // Si l'attaquant est mort avant de jouer (ex: tué par le coup de l'autre), il ne fait rien.
        if (attacker.getHp() <= 0) {
            nextStep.run();
            return;
        }

        // --- TYPE 2 : OBJET ---
        if (action.type == 2) { 
            // Cas Classique : Potion / Médicament
            window.showDialog(actor.getName() + " utilise " + action.item.getName() + " !", () -> {
                actor.useItem(action.item, attacker); // Applique l'effet (Soins, etc.)
                updateGraphics();
                nextStep.run();
            });
        } 
        // --- TYPE 1 : ATTAQUE ---
        else if (action.type == 1) { 
            String atkName = (action.attack != null) ? action.attack.getName() : "Lutte"; // "Lutte" si plus de PP/Attaques
            window.showDialog(attacker.getName() + " utilise " + atkName + " !", () -> {
                
                // On décrémente les PP (Points de Pouvoir)
                if (action.attack != null) {
                    action.attack.setNbUse(action.attack.getNbUse() - 1);
                }
                
                // Calcul des dégâts et application
                attacker.attack(defender, action.attack);
                updateGraphics();
                nextStep.run();
            });
        }
    }

    // =========================================================================
    //                  GESTION FIN DE COMBAT & KO (ASYNCHRONE)
    // =========================================================================

    /**
     * Vérifie si le combat doit s'arrêter ou si un Pokémon est KO.
     * Si tout va bien, appelle 'onContinue'.
     * @param onContinue le code à exécuter si le combat continue
     */
    private void checkBattleEndAndContinue(Runnable onContinue) {
        // 1. Victoire du Joueur
        if (enemy.hasLost()) {
            window.showDialog(player.getName() + " a gagné !", () -> {
                window.dispose(); // Ferme la fenêtre
                System.exit(0);   // Arrête le programme
            });
            return;
        } 
        // 2. Victoire de l'Ennemi (Défaite du joueur)
        else if (player.hasLost()) {
            window.showDialog(enemy.getName() + " a gagné !", () -> {
                window.dispose(); // Ferme la fenêtre
                System.exit(0);   // Arrête le programme
            });
            return;
        }
        
        // 3. Gestion des KO individuels
        if (enemy.getTeam().get(0).getHp() <= 0) {
             handleKOSwitch(enemy, onContinue); 
             return;
        }
        if (player.getTeam().get(0).getHp() <= 0) {
             handleKOSwitch(player, onContinue); 
             return;
        }

        // 4. Si personne n'est mort et combat pas fini -> on continue
        onContinue.run();
    }

    /**
     * Gère le remplacement forcé d'un Pokémon KO.
     * @param p Le joueur dont le Pokémon est KO.
     * @param onComplete Le code à exécuter une fois le remplacement fait.
     */
    private void handleKOSwitch(Player p, Runnable onComplete) {
        Monster deadMon = p.getTeam().get(0);
        deadMon.setState(com.esiea.pootp.monsters.State.DEAD);

        // 1. EFFET VISUEL : On efface le Pokémon mort de l'écran immédiatement
        // On force le BattlePanel à afficher "null" à la place du monstre KO
        if (p == player) {
            // Si c'est le joueur, on cache son monstre, mais on garde l'ennemi visible
             window.getBattlePanel().updateMonsters(null, enemy.getActiveMonster());
        } else {
            // Si c'est l'ennemi, on cache son monstre, on garde le nôtre
             window.getBattlePanel().updateMonsters(player.getActiveMonster(), null);
        }
        window.getBattlePanel().repaint(); // On force le redessin pour voir le vide

        window.showDialog(deadMon.getName() + " est K.O. !", () -> {
            
            // 2. DÉLAI : On laisse le terrain vide pendant 0.5 secondes
            javax.swing.Timer waitTimer = new javax.swing.Timer(500, e -> {
                ((javax.swing.Timer)e.getSource()).stop();

                // 3. LOGIQUE DE REMPLACEMENT
                int swapIndex = -1;
                for (int i = 0; i < p.getTeam().size(); i++) {
                    if (p.getTeam().get(i).getHp() > 0) {
                        swapIndex = i;
                        break;
                    }
                }

                if (swapIndex != -1) {
                    Monster newMon = p.getTeam().get(swapIndex);
                    java.util.Collections.swap(p.getTeam(), 0, swapIndex);
                    
                    String msg = (p == player) ? "Go " + newMon.getName() + " !" : p.getName() + " envoie " + newMon.getName() + " !";
                    
                    window.showDialog(msg, () -> {
                        // 4. RÉAPPARITION : On met à jour les graphismes avec le nouveau monstre
                        updateGraphics(); 
                        onComplete.run(); 
                    });
                } else {
                    onComplete.run();
                }
            });
            waitTimer.setRepeats(false);
            waitTimer.start();
        });
    }

    // =========================================================================
    //                          OUTILS ET IA
    // =========================================================================

    /**
     * Fin du tour : réaffiche le menu principal.
     */
    private void endTurn() {
        window.showMainMenuButtons(); // Le tour est fini, on réaffiche le menu
    }

    // IA basique pour l'ennemi
    /**
     * AI chooses an action for the enemy
     * @return the action chosen by the AI
     */
   private ActionChoice AIChoice() {
        ActionChoice action = new ActionChoice();
        Monster aiMonster = enemy.getActiveMonster();
        Monster playerMonster = player.getActiveMonster();

        // =================================================================
        // 1. LOGIQUE DE SWITCH 
        // =================================================================
        // On récupère le type qui bat notre monstre actuel (ex: EAU bat FEU)
        ElementType myWeakness = aiMonster.getWeakType();
        
        // Si on a une faiblesse (pas null) ET que l'adversaire EST de ce type
        if (myWeakness != null && myWeakness == playerMonster.getElement()) {
            
            int bestIndex = -1;
            
            // On cherche un remplaçant dans l'équipe
            for (int i = 1; i < enemy.getTeam().size(); i++) {
                Monster candidate = enemy.getTeam().get(i);
                
                // Si le candidat est vivant
                if (candidate.getHp() > 0) {
                    // Est-ce que ce candidat est AUSSI faible contre le joueur ?
                    ElementType candidateWeakness = candidate.getWeakType();
                    boolean isCandidateWeak = (candidateWeakness != null && candidateWeakness == playerMonster.getElement());
                    
                    // Si le candidat n'est PAS faible, c'est une bonne option
                    if (!isCandidateWeak) {
                        bestIndex = i;
                        
                        // Est-ce qu'il est carrément FORT contre le joueur ?
                        ElementType candidateStrength = candidate.getStrongType();
                        if (candidateStrength != null && candidateStrength == playerMonster.getElement()) {
                            break; 
                        }
                    }
                }
            }

            // Si on a trouvé quelqu'un de mieux, on switch !
            if (bestIndex != -1) {
                action.type = 3;
                action.switchIndex = bestIndex;
                return action;
            }
        }

        // =================================================================
        // 2. LOGIQUE DE SOIN (Si PV < 30%)
        // =================================================================
        if (!isWildBattle && aiMonster.getHp() < aiMonster.getMaxHp() * 0.3 && !enemy.getInventory().isEmpty()) {
            for (Item item : enemy.getInventory().keySet()) {
                if (item.isHealingItem() && enemy.getInventory().get(item) > 0) { 
                    action.type = 2;
                    action.item = item;
                    return action;
                }
            }
        }

        // =================================================================
        // 3. LOGIQUE D'ATTAQUE
        // =================================================================
        List<Attack> attacks = aiMonster.getAttacks();
        Attack bestAttack = null;
        double maxDamage = -1.0;

        for (Attack atk : attacks) {
            if (atk.getNbUse() > 0) {
                double predictedDamage = aiMonster.damages(playerMonster, atk);
                
                if (predictedDamage > maxDamage) {
                    maxDamage = predictedDamage;
                    bestAttack = atk;
                }
            }
        }

        if (bestAttack != null) {
            action.type = 1;
            action.attack = bestAttack;
        } else {
            // Plus de PP ou aucune attaque -> Lutte
            action.type = 1;
            action.attack = null; 
        }

        return action;
    }
    

    /**
     * Check if the battle is over
     * @return true if the battle is over, false otherwise
     */
    private boolean isBattleOver() {
        return player.hasLost() || enemy.hasLost();
    }

    /**
     * Update the graphics on the screen
     */
    public void updateGraphics() {
        // Demande au panneau graphique de se mettre à jour
        window.getBattlePanel().updateMonsters(player.getActiveMonster(), enemy.getActiveMonster());
        window.getBattlePanel().repaint();
    }
}