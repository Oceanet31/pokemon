package com.esiea.pootp.gui;

import com.esiea.pootp.monsters.Monster;
import javax.swing.JPanel;
import javax.swing.Timer;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;

public class BattlePanel extends JPanel {

    // --- DONNÉES DU COMBAT ---
    private Monster playerMonster;
    private Monster enemyMonster;
    
    // --- IMAGES DE DÉCORS ---
    private BufferedImage background;
    private BufferedImage podPlayer;
    private BufferedImage podEnemy;
    // --- ASSETS HUD (Interface) ---
    private BufferedImage hudPlayerBg;    // Cadre Joueur (pbinfo_player.png)
    private BufferedImage hudEnemyBg;     // Cadre Ennemi (pbinfo_enemy.png)
    
    private BufferedImage hpBarAtlas;     // Couleurs HP (overlay_hp.png)
    private BufferedImage expBarTexture;  // Barre EXP (overlay_exp.png)
    
    private BufferedImage lblPV;          //  "PV"
    private BufferedImage lblEXP;         //  "EXP"
    private BufferedImage lblLv;          //  "N."

    // --- ANIMATION & MOTEUR ---
    private Animation playerAnimation;    // Gestion animation Joueur
    private Animation enemyAnimation;     // Gestion animation Ennemi
    private Timer gameLoop;               // Boucle de rafraîchissement
    private Font pixelFont;

    public BattlePanel() {
        this.setBackground(Color.BLACK);
        
        // =========================================================
        // 1. CHARGEMENT DES RESSOURCES
        // =========================================================
        
        // Décors
        this.background  = SpriteManager.loadImage("com/esiea/pootp/resources/background/grass_bg.png"); 
        this.podPlayer   = SpriteManager.loadImage("com/esiea/pootp/resources/background/grass_a.png"); 
        this.podEnemy    = SpriteManager.loadImage("com/esiea/pootp/resources/background/grass_b.png"); 

        // Interface (HUD)
        this.hudPlayerBg   = SpriteManager.loadImage("com/esiea/pootp/resources/ui/pbinfo_player.png");
        this.hudEnemyBg    = SpriteManager.loadImage("com/esiea/pootp/resources/ui/pbinfo_enemy.png");
        this.hpBarAtlas    = SpriteManager.loadImage("com/esiea/pootp/resources/ui/overlay_hp.png");
        this.expBarTexture = SpriteManager.loadImage("com/esiea/pootp/resources/ui/overlay_exp.png");
        this.lblPV         = SpriteManager.loadImage("com/esiea/pootp/resources/ui/overlay_hp_label_fr.png");
        this.lblEXP        = SpriteManager.loadImage("com/esiea/pootp/resources/ui/overlay_exp_label_fr.png");
        this.lblLv         = SpriteManager.loadImage("com/esiea/pootp/resources/ui/overlay_lv_fr.png");

        // Police d'écriture
        this.pixelFont = UIUtils.loadPixelFont(32f); 
        
        // =========================================================
        // 2. BOUCLE DE JEU
        // =========================================================
        // Timer réglé à 100ms (10 FPS)
        this.gameLoop = new Timer(100, e -> {
            if (playerAnimation != null) playerAnimation.update();
            if (enemyAnimation != null) enemyAnimation.update();
            repaint(); // Redessine le panneau entier
        });
        this.gameLoop.start();
    }

    /**
     * Met à jour les monstres affichés dans le panneau de combat.
     */
    public void updateMonsters(Monster player, Monster enemy) {
        if (this.playerMonster != player) {
            this.playerMonster = player;
            this.playerAnimation = (player != null) ? loadMonsterAnimation(player, true) : null;
        }

        if (this.enemyMonster != enemy) {
            this.enemyMonster = enemy;
            this.enemyAnimation = (enemy != null) ? loadMonsterAnimation(enemy, false) : null;
        }
        this.repaint();
    }

    /**
     * Cherche le fichier .json correspondant au monstre pour charger son animation.
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
        return null; // Pas d'animation, on utilisera l'image fixe
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;

        //on enlève le lissage pour un rendu pixel art
        g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR);
        g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_OFF);

        // --- 1. DESSIN DU FOND ---
        if (background != null) {
            g2d.drawImage(background, 0, 0, getWidth(), getHeight(), null);
        } else {
            g2d.setColor(new Color(110, 190, 110));
            g2d.fillRect(0, 0, getWidth(), getHeight());
        }

        int groundY = getHeight() - 180; // Hauteur du sol (au-dessus du menu du bas)

        // --- 2. DESSIN DES PODS ---
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

            // Choix : Animation ou Image Fixe ?
            if (enemyAnimation != null) {
                spriteToDraw = enemyAnimation.getSprite();
            } else {
                spriteToDraw = SpriteManager.getPokemonSprite(enemyMonster.getName(), false);
            }

            if (spriteToDraw != null) {
                int scale = 4; // Zoom pour l'ennemi
                int w = spriteToDraw.getWidth() * scale;
                int h = spriteToDraw.getHeight() * scale;
                
                int x = getWidth() - 370; 
                int y = 250 - h + 60;
                
                g2d.drawImage(spriteToDraw, x, y, w, h, null);
            }
            
            // HUD ENNEMI (Position et Taille Large)
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
                int scale = 5; // Zoom pour le joueur (plus grand car devant)
                int w = spriteToDraw.getWidth() * scale;
                int h = spriteToDraw.getHeight() * scale;
                
                int x = 175; 
                int y = groundY - h + 220; // Ajustement sur le pod
                
                g2d.drawImage(spriteToDraw, x, y, w, h, null);
            }
            
            // HUD JOUEUR
            int hudW = 520; 
            int hudH = 160;
            
            // Calcul X : Droite de l'écran avec marge
            int hudX = getWidth() - hudW - 20; 
            
            // Calcul Y : Bas de l'écran, remonté de 170px par rapport au menu
            int hudY = getHeight() - 180 - hudH + 170; 
            
            drawPlayerHUD(g2d, playerMonster, hudX, hudY, hudW, hudH);
        }
    }

    // =========================================================
    //                  GESTION DU HUD
    // =========================================================

    /**
     * Affiche l'interface de l'adversaire
     */
    private void drawEnemyHUD(Graphics2D g, Monster m, int x, int y, int w, int h) {
        // Fond
        if (hudEnemyBg != null) {
            g.drawImage(hudEnemyBg, x, y, w, h, null);
        }

        // Nom
        g.setColor(Color.WHITE);
        g.setFont(pixelFont.deriveFont(50f));
        g.setColor(new Color(0,0,0,100)); // Ombre
        g.drawString(m.getName(), x + 54, y + 59); 
        g.setColor(Color.WHITE);          // Texte
        g.drawString(m.getName(), x + 50, y + 55);

        // Niveau
        if (lblLv != null) {
            g.drawImage(lblLv, x + w - 180, y + 20, 35, 35, null);
            g.setColor(Color.WHITE);
            g.drawString("" + m.getLevel(), x + w - 140, y + 53);
        }

        // Barre PV
        int barX = x + 204; 
        int barY = y + 81; 
        int barW = 166;  
        int barH = 8;      

        if (lblPV != null) {
            g.drawImage(lblPV, barX - 58, barY - 10, 52, 28, null);
        }

        double ratio = (double) m.getHp() / m.getStartingHp();
        if (ratio < 0) ratio = 0; else if (ratio > 1) ratio = 1;
        int currentW = (int) (barW * ratio);

        // Couleur dynamique (Vert -> Orange -> Rouge)
        int srcY = 0; 
        if (ratio <= 0.2) srcY = 4;      
        else if (ratio <= 0.5) srcY = 2; 
        
        if (hpBarAtlas != null && currentW > 0) {
            BufferedImage barColor = hpBarAtlas.getSubimage(0, srcY, hpBarAtlas.getWidth(), 2);
            g.drawImage(barColor, barX, barY, currentW, barH, null);
        }
    }

    /**
     * Affiche l'interface du joueur
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

        // Barre PV
        int barX = x + 276; 
        int barY = y + 76; 
        int barW = 192;  
        int barH = 8;      

        if (lblPV != null) {
            g.drawImage(lblPV, barX - 58, barY - 10, 52, 28, null);
        }

        double ratio = (double) m.getHp() / m.getStartingHp(); // Calcul ratio PV
        if (ratio < 0) ratio = 0; else if (ratio > 1) ratio = 1;
        int currentW = (int) (barW * ratio);

        int srcY = 0;  // Couleur dynamique (Vert -> Orange -> Rouge)
        if (ratio <= 0.2) srcY = 4; 
        else if (ratio <= 0.5) srcY = 2; 
        
        if (hpBarAtlas != null && currentW > 0) { // Dessin de la barre HP
            BufferedImage barColor = hpBarAtlas.getSubimage(0, srcY, hpBarAtlas.getWidth(), 2);
            g.drawImage(barColor, barX, barY, currentW, barH, null);
        }

        // Texte PV (ex: 120 / 120)
        g.setColor(Color.WHITE);
        g.setFont(pixelFont.deriveFont(24f));
        String hpTxt = m.getHp() + " / " + m.getStartingHp();
        int txtW = g.getFontMetrics().stringWidth(hpTxt);
        g.drawString(hpTxt, x + w - txtW - 40, y + 72);
        
        // Barre EXP
        int expBarX = x + 128; 
        int expBarY = y + 148; 
        int expW = 340;     
        int expH = 8;         
        
        if (lblEXP != null) {
            g.drawImage(lblEXP, x + 100, expBarY - 20, 64, 28, null); 
        }

        int currentExpW = (int) (expW * 1.0); // 100% visuel pour l'instant
        
        if (expBarTexture != null && currentExpW > 0) {
            g.drawImage(expBarTexture, expBarX, expBarY, currentExpW, expH, null);
        } else if (currentExpW > 0) {
            g.setColor(Color.BLUE);
            g.fillRect(expBarX, expBarY, currentExpW, expH);
        }
    }
}