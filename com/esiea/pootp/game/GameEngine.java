package com.esiea.pootp.game;

import com.esiea.pootp.attacks.Attack;
import com.esiea.pootp.monsters.Monster;
import com.esiea.pootp.objects.Item;
import com.esiea.pootp.objects.Pokeball; 
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
    //                          LOGIQUE DU TOUR (CŒUR DU JEU)
    // =========================================================================

    /**
     * Calcule qui joue en premier et enchaîne les actions.
     * C'est ici que la complexité asynchrone est gérée.
     * @param playerAction the action chosen by the player
     */
    private void processTurn(ActionChoice playerAction) {
        ActionChoice enemyAction = AIChoice(); // L'ennemi choisit son coup (IA simple)
        
        // CRUCIAL : On sauvegarde l'état des monstres AU DÉBUT du tour.
        // Si on utilise player.getActiveMonster() plus tard, il pourrait avoir changé (si on switch).
        // Pour comparer les vitesses, il faut les combattants initiaux.
        Monster pMonsterStart = player.getActiveMonster();
        Monster eMonsterStart = enemy.getActiveMonster();
        
        // Comparaison de vitesse
        boolean playerFirst = pMonsterStart.getSpeed() >= eMonsterStart.getSpeed();
        
        // RÈGLE : Les Objets (2) et Switchs (3) sont toujours prioritaires sur les Attaques (1)
        if (playerAction.type == 2 || playerAction.type == 3) playerFirst = true;

        if (playerFirst) {
            // --- CAS A : JOUEUR RAPIDE ---
            
            // 1. Le joueur agit.
            executeAction(player, playerAction, pMonsterStart, eMonsterStart, () -> {
                
                // Ce code s'exécute UNE FOIS l'action du joueur terminée (dialogues finis).
                
                // 2. On vérifie si le combat continue (Personne n'est mort ?)
                checkBattleEndAndContinue(() -> {
                    
                    // Si on est ici, le combat continue.
                    // On met à jour les cibles : Si le joueur a switché, l'ennemi doit taper le NOUVEAU monstre.
                    Monster currentTarget = player.getActiveMonster(); 
                    Monster currentAttacker = enemy.getActiveMonster();
                    
                    // 3. L'ennemi riposte
                    executeAction(enemy, enemyAction, currentAttacker, currentTarget, () -> {
                        // 4. Fin du tour (Vérif KO finale)
                        checkBattleEndAndContinue(() -> endTurn());
                    });
                });
            });
        } else {
            // --- CAS B : ENNEMI RAPIDE ---
            
            // 1. L'ennemi agit
            executeAction(enemy, enemyAction, eMonsterStart, pMonsterStart, () -> {
                
                checkBattleEndAndContinue(() -> {
                    // Mise à jour des cibles (au cas où l'ennemi se serait tué tout seul, sait-on jamais)
                    Monster currentTarget = enemy.getActiveMonster();
                    Monster currentAttacker = player.getActiveMonster();
                    
                    // 2. Le joueur riposte
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
            // Cas Spécial : POKÉBALL (Capture)
            if (action.item instanceof Pokeball) {
                if (actor == player && isWildBattle) {
                    window.showDialog(actor.getName() + " lance une Pokéball !", () -> {
                        // On retire 1 Pokéball de l'inventaire
                        actor.getInventory().put(action.item, actor.getInventory().get(action.item) - 1);
                        tryCapture(defender, nextStep); // Logique de capture
                    });
                } else {
                    window.showDialog("Impossible de capturer en duel !", nextStep);
                }
                return;
            }

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
        // 1. Victoire (Ennemi n'a plus de pokémon)
        if (enemy.hasLost()) {
            window.showDialog("Victoire ! L'adversaire n'a plus de Pokémon !", () -> {
                if (onVictory != null) onVictory.run(); // Déclenche le gain d'XP
            });
            return;
        } 
        // 2. Défaite (Joueur n'a plus de pokémon)
        else if (player.hasLost()) {
            window.showDialog("Vous n'avez plus de Pokémon en forme...", () -> {
                window.showDialog("Vous avez perdu le combat.", () -> System.exit(0)); // Fin du jeu
            });
            return;
        }
        
        // 3. Gestion des KO individuels
        // On vérifie l'index 0 car getActiveMonster() renvoie null si mort, ce qui peut buguer.
        if (enemy.getTeam().get(0).getHp() <= 0) {
             handleKOSwitch(enemy, onContinue); // L'ennemi change de monstre
             return;
        }
        if (player.getTeam().get(0).getHp() <= 0) {
             handleKOSwitch(player, onContinue); // Le joueur change de monstre
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
        Monster deadMon = p.getTeam().get(0); // Le mort est forcément celui actif (0)
        deadMon.setState(com.esiea.pootp.monsters.State.DEAD);

        window.showDialog(deadMon.getName() + " est K.O. !", () -> {
            
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
                    updateGraphics();
                    onComplete.run(); // On reprend le tour là où il s'était arrêté
                });
            } else {
                
                onComplete.run();
            }
        });
    }

    // =========================================================================
    //                          OUTILS ET IA
    // =========================================================================

    // Tente de capturer un monstre
    /**
     * Try to capture a wild monster
     * @param wildMonster the wild monster to capture
     * @param failCallback the callback to execute if capture fails
     */
    private void tryCapture(Monster wildMonster, Runnable failCallback) {
        int maxHp = wildMonster.getStartingHp();
        int currentHp = wildMonster.getHp();
        
        // Formule : Moins il a de PV, plus chance est proche de 1.0 (100%)
        double chance = 1.0 - ((double)currentHp / (double)(maxHp * 1.5));
        
        if (Math.random() < chance) {
            window.showDialog("Hop ! " + wildMonster.getName() + " attrapé !", () -> {
                // Gestion équipe pleine (Limite à 6)
                if (player.getTeam().size() >= 6) {
                     // Simplification : On remplace le monstre actif
                     Monster toReplace = player.getActiveMonster();
                     player.getTeam().remove(toReplace);
                     player.addMonsterToTeam(wildMonster);
                     window.showDialog("Équipe pleine ! " + toReplace.getName() + " est relâché.", () -> {
                         if (onVictory != null) onVictory.run(); 
                     });
                } else {
                    player.addMonsterToTeam(wildMonster);
                    if (onVictory != null) onVictory.run(); 
                }
            });
        } else {
            window.showDialog("Zut ! Il s'est échappé...", failCallback);
        }
    }

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
        Monster active = enemy.getActiveMonster();
        // Si PV < 30 et possède objets -> Soin
        if (!isWildBattle && active.getHp() < 30 && !enemy.getInventory().isEmpty()) {
            action.type = 2;
            action.item = enemy.getInventory().keySet().iterator().next();
        } else {
            // Sinon -> Attaque aléatoire
            action.type = 1;
            List<Attack> attacks = active.getAttacks();
            if (!attacks.isEmpty()) {
                action.attack = attacks.get((int) (Math.random() * attacks.size()));
            }
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