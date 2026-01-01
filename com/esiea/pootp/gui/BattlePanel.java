package com.esiea.pootp.gui;

import com.esiea.pootp.monsters.Monster;
import javax.swing.JPanel;
import javax.swing.Timer;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;

/**
 * BattlePanel : La zone de dessin du combat.
 * ---------------------------------------------------------
 * Cette classe hérite de JPanel et redéfinit paintComponent(Graphics g)
 * pour dessiner manuellement tout le jeu (Pixel Art).
 */
public class BattlePanel extends JPanel {

    // Références vers les monstres actuels pour savoir quoi dessiner
    private Monster playerMonster;
    private Monster enemyMonster;
    
    // --- IMAGES (Assets chargés en mémoire) ---
    private BufferedImage background; // Le fond (herbe)
    private BufferedImage podPlayer;  // La plateforme sous le joueur
    private BufferedImage podEnemy;   // La plateforme sous l'ennemi
    
    // --- UI (Interface Utilisateur) ---
    private BufferedImage hudPlayerBg;    // Cadre d'info du joueur
    private BufferedImage hudEnemyBg;     // Cadre d'info de l'ennemi
    private BufferedImage hpBarAtlas;     // Image contenant les couleurs de la barre de vie (Vert/Orange/Rouge)
    private BufferedImage expBarTexture;  // Texture bleue pour la barre d'expérience
    
    private BufferedImage lblPV;          // Petit badge "PV"
    private BufferedImage lblEXP;         // Petit badge "EXP"
    private BufferedImage lblLv;          // Symbole "N." (Niveau)

    // --- MOTEUR D'ANIMATION ---
    private Animation playerAnimation;    // Gère le GIF/Animation du joueur (si dispo)
    private Animation enemyAnimation;     // Gère l'animation de l'ennemi (si dispo)
    private Timer gameLoop;               // Boucle infinie qui redessine l'écran 10 fois par seconde
    private Font pixelFont;               // Police d'écriture style rétro

    public BattlePanel() {
        this.setBackground(Color.BLACK);
        
        // =========================================================
        // 1. CHARGEMENT DES RESSOURCES (Au démarrage)
        // =========================================================
        
        // Chargement des décors
        this.background  = SpriteManager.loadImage("com/esiea/pootp/resources/background/grass_bg.png"); 
        this.podPlayer   = SpriteManager.loadImage("com/esiea/pootp/resources/background/grass_a.png"); 
        this.podEnemy    = SpriteManager.loadImage("com/esiea/pootp/resources/background/grass_b.png"); 

        // Chargement des éléments d'interface (HUD)
        this.hudPlayerBg   = SpriteManager.loadImage("com/esiea/pootp/resources/ui/pbinfo_player.png");
        this.hudEnemyBg    = SpriteManager.loadImage("com/esiea/pootp/resources/ui/pbinfo_enemy.png");
        this.hpBarAtlas    = SpriteManager.loadImage("com/esiea/pootp/resources/ui/overlay_hp.png");
        this.expBarTexture = SpriteManager.loadImage("com/esiea/pootp/resources/ui/overlay_exp.png");
        this.lblPV         = SpriteManager.loadImage("com/esiea/pootp/resources/ui/overlay_hp_label_fr.png");
        this.lblEXP        = SpriteManager.loadImage("com/esiea/pootp/resources/ui/overlay_exp_label_fr.png");
        this.lblLv         = SpriteManager.loadImage("com/esiea/pootp/resources/ui/overlay_lv_fr.png");

        // Chargement de la police (ou Arial par défaut si échec)
        this.pixelFont = UIUtils.loadPixelFont(32f); 
        
        // =========================================================
        // 2. BOUCLE DE RENDU (Game Loop)
        // =========================================================
        // Timer réglé à 100ms (10 FPS)
        this.gameLoop = new Timer(100, e -> {
            // À chaque "tic" du timer :
            if (playerAnimation != null) playerAnimation.update(); // On avance l'animation du joueur
            if (enemyAnimation != null) enemyAnimation.update(); // On avance l'animation de l'ennemi
            repaint(); // On demande à Java de redessiner tout l'écran (appelle paintComponent)
        });
        this.gameLoop.start();
    }

    /**
     * Appelé par le GameEngine quand les monstres changent (Switch, KO, Début de combat).
     */
    public void updateMonsters(Monster player, Monster enemy) {
        // Mise à jour du joueur
        if (this.playerMonster != player) {
            this.playerMonster = player;
            // On essaie de charger une animation JSON, sinon ce sera null (image fixe)
            this.playerAnimation = (player != null) ? loadMonsterAnimation(player, true) : null;
        }

        // Mise à jour de l'ennemi
        if (this.enemyMonster != enemy) {
            this.enemyMonster = enemy;
            this.enemyAnimation = (enemy != null) ? loadMonsterAnimation(enemy, false) : null;
        }
        this.repaint(); // Force un redessin immédiat
    }

    /**
     * Tente de charger le fichier .json d'animation associé au monstre.
     * isBack = true si c'est le monstre du joueur (vu de dos).
     */
    private Animation loadMonsterAnimation(Monster m, boolean isBack) {
        String folder = isBack ? "com/esiea/pootp/resources/pokemon/back/" : "com/esiea/pootp/resources/pokemon/front/";
        String name = m.getName().toLowerCase();
        
        String jsonPath = folder + name + ".json";
        String pngPath = folder + name + ".png"; 

        File f = new File(jsonPath);
        if (f.exists()) {
            return SpriteManager.loadAnimation(jsonPath, pngPath);
        }
        return null; // Pas d'animation trouvée
    }

    // =========================================================
    //              MÉTHODE PRINCIPALE DE DESSIN
    // =========================================================
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;

        // Astuce : Désactive le lissage (Anti-aliasing) pour garder les pixels bien nets (Retro Style)
        g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR);
        g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_OFF);

        // --- 1. DESSIN DU FOND ---
        if (background != null) {
            g2d.drawImage(background, 0, 0, getWidth(), getHeight(), null);
        } else {
            g2d.setColor(new Color(110, 190, 110)); // Fond vert si image manquante
            g2d.fillRect(0, 0, getWidth(), getHeight());
        }

        int groundY = getHeight() - 180; // Niveau du sol visuel (au-dessus du menu du bas)

        // --- 2. DESSIN DES PODS (Plateformes) ---
        if (podEnemy != null) {
            int w = 1000; int h = 500;
            g2d.drawImage(podEnemy, 200, getHeight() - h - 50, w, h, null);
        }
        if (podPlayer != null) {
            int w = 1000; int h = 500;
            g2d.drawImage(podPlayer, getWidth() - w - 200, getHeight() - h, w, h, null);
        }

        // --- 3. DESSIN DE L'ENNEMI ---
        if (enemyMonster != null) {
            BufferedImage spriteToDraw = null;

            // On prend l'image de l'animation SI elle existe, sinon l'image fixe
            if (enemyAnimation != null) {
                spriteToDraw = enemyAnimation.getSprite();
            } else {
                spriteToDraw = SpriteManager.getPokemonSprite(enemyMonster.getName(), false);
            }

            if (spriteToDraw != null) {
                int scale = 4; // Zoom pour l'ennemi (il est loin)
                int w = spriteToDraw.getWidth() * scale;
                int h = spriteToDraw.getHeight() * scale;
                
                // Positionnement calculé
                int x = getWidth() - 370; 
                int y = 250 - h + 60;
                
                g2d.drawImage(spriteToDraw, x, y, w, h, null);
            }
            
            // Dessine l'interface (Nom + PV) de l'ennemi
            drawEnemyHUD(g2d, enemyMonster, 40, 40, 450, 125);
        }

        // --- 4. DESSIN DU JOUEUR ---
        if (playerMonster != null) {
            BufferedImage spriteToDraw = null;

            if (playerAnimation != null) {
                spriteToDraw = playerAnimation.getSprite();
            } else {
                spriteToDraw = SpriteManager.getPokemonSprite(playerMonster.getName(), true);
            }

            if (spriteToDraw != null) {
                int scale = 5; // Zoom plus gros car le joueur est au premier plan
                int w = spriteToDraw.getWidth() * scale;
                int h = spriteToDraw.getHeight() * scale;
                
                int x = 175; 
                int y = groundY - h + 220; 
                
                g2d.drawImage(spriteToDraw, x, y, w, h, null);
            }
            
            // Calcul position HUD Joueur (Bas Droite)
            int hudW = 520; 
            int hudH = 160;
            int hudX = getWidth() - hudW - 20; 
            int hudY = getHeight() - 180 - hudH + 170; 
            
            drawPlayerHUD(g2d, playerMonster, hudX, hudY, hudW, hudH);
        }
    }

    // =========================================================
    //                  MÉTHODES D'INTERFACE (HUD)
    // =========================================================

    /**
     * Dessine la barre de vie et le nom de l'ennemi.
     */
    private void drawEnemyHUD(Graphics2D g, Monster m, int x, int y, int w, int h) {
        // Fond du cadre
        if (hudEnemyBg != null) {
            g.drawImage(hudEnemyBg, x, y, w, h, null);
        }

        // Nom du monstre avec ombre portée pour lisibilité
        g.setColor(Color.WHITE);
        g.setFont(pixelFont.deriveFont(50f));
        g.setColor(new Color(0,0,0,100)); // Ombre noire transparente
        g.drawString(m.getName(), x + 54, y + 59); 
        g.setColor(Color.WHITE);          // Texte blanc par-dessus
        g.drawString(m.getName(), x + 50, y + 55);

        // Niveau
        if (lblLv != null) {
            g.drawImage(lblLv, x + w - 180, y + 20, 35, 35, null);
            g.setColor(Color.WHITE);
            g.drawString("" + m.getLevel(), x + w - 140, y + 53);
        }

        // Calcul et dessin de la barre de vie
        int barX = x + 204; 
        int barY = y + 81; 
        int barW = 166;  
        int barH = 8;      

        if (lblPV != null) {
            g.drawImage(lblPV, barX - 58, barY - 10, 52, 28, null);
        }

        // Ratio PV (entre 0.0 et 1.0)
        double ratio = (double) m.getHp() / m.getStartingHp();
        if (ratio < 0) ratio = 0; else if (ratio > 1) ratio = 1;
        int currentW = (int) (barW * ratio); // Largeur en pixels

        // Choix de la couleur dans l'atlas (Vert / Orange / Rouge)
        int srcY = 0; 
        if (ratio <= 0.2) srcY = 4;      
        else if (ratio <= 0.5) srcY = 2; 
        
        if (hpBarAtlas != null && currentW > 0) {
            // getSubimage découpe juste la bande de couleur nécessaire
            BufferedImage barColor = hpBarAtlas.getSubimage(0, srcY, hpBarAtlas.getWidth(), 2);
            g.drawImage(barColor, barX, barY, currentW, barH, null);
        }
    }

    /**
     * Dessine l'interface du joueur (plus détaillée : affiche les PV exacts et l'EXP).
     */
    private void drawPlayerHUD(Graphics2D g, Monster m, int x, int y, int w, int h) {
        // Fond
        if (hudPlayerBg != null) {
            g.drawImage(hudPlayerBg, x, y, w, h, null);
        }

        // Nom
        g.setColor(Color.WHITE);
        g.setFont(pixelFont.deriveFont(50F));
        g.setColor(new Color(0,0,0,100)); 
        g.drawString(m.getName(), x + 79, y + 64); 
        g.setColor(Color.WHITE);
        g.drawString(m.getName(), x + 75, y + 60);

        // Niveau
        if (lblLv != null) {
            g.drawImage(lblLv, x + w - 150, y + 25, 35, 35, null);
            g.setColor(Color.WHITE);
            g.drawString("" + m.getLevel(), x + w - 110, y + 55);
        }

        // Barre PV (similaire à l'ennemi mais dimensions différentes)
        int barX = x + 276; 
        int barY = y + 76; 
        int barW = 192;  
        int barH = 8;      

        if (lblPV != null) {
            g.drawImage(lblPV, barX - 58, barY - 10, 52, 28, null);
        }

        double ratio = (double) m.getHp() / m.getStartingHp();
        if (ratio < 0) ratio = 0; else if (ratio > 1) ratio = 1;
        int currentW = (int) (barW * ratio);

        int srcY = 0;
        if (ratio <= 0.2) srcY = 4; 
        else if (ratio <= 0.5) srcY = 2; 
        
        if (hpBarAtlas != null && currentW > 0) {
            BufferedImage barColor = hpBarAtlas.getSubimage(0, srcY, hpBarAtlas.getWidth(), 2);
            g.drawImage(barColor, barX, barY, currentW, barH, null);
        }

        // Texte PV détaillé - Spécifique au joueur
        g.setColor(Color.WHITE);
        g.setFont(pixelFont.deriveFont(24f));
        String hpTxt = m.getHp() + " / " + m.getStartingHp();
        int txtW = g.getFontMetrics().stringWidth(hpTxt);
        g.drawString(hpTxt, x + w - txtW - 40, y + 72);
        
        // --- BARRE EXPÉRIENCE ---
        int expBarX = x + 128; 
        int expBarY = y + 148; 
        int expW = 340;     
        int expH = 8;         
        
        if (lblEXP != null) {
            g.drawImage(lblEXP, x + 100, expBarY - 20, 64, 28, null); 
        }

        // Calcul du ratio d'XP pour le niveau suivant
        double expRatio = (double) m.getXp() / m.getXpToNextLevel();
        if (expRatio < 0) expRatio = 0; 
        if (expRatio > 1) expRatio = 1;

        int currentExpW = (int) (expW * expRatio);
        
        // Dessin de la barre bleue
        if (expBarTexture != null && currentExpW > 0) {
            g.drawImage(expBarTexture, expBarX, expBarY, currentExpW, expH, null);
        } else if (currentExpW > 0) {
            g.setColor(Color.BLUE);
            g.fillRect(expBarX, expBarY, currentExpW, expH);
        }
    }
}