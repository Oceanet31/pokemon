package com.esiea.pootp.gui;

import com.esiea.pootp.monsters.Monster;
import javax.swing.JPanel;
import java.awt.*;
import java.awt.image.BufferedImage;

public class BattlePanel extends JPanel {

    private Monster playerMonster;
    private Monster enemyMonster;
    private BufferedImage background;
    
    public BattlePanel() {// Constructeur
        // Charger le fond de la bataille
        this.background = SpriteManager.loadImage("com/esiea/pootp/resources/background/grass_bg.png"); 
        this.setPreferredSize(new Dimension(800, 600));
    }

    public void updateMonsters(Monster player, Monster enemy) {
        this.playerMonster = player;
        this.enemyMonster = enemy;
        this.repaint(); // Ordonne de redessiner l'écran
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;

        //DESSINER LE FOND
        if (background != null) {
            g2d.drawImage(background, 0, 0, getWidth(), getHeight(), null);
        } else {
            g2d.setColor(Color.DARK_GRAY);
            g2d.fillRect(0, 0, getWidth(), getHeight());
        }

        // --- Positions ---
        int groundY = getHeight() - 150;
        int playerX = 150;
        int enemyX = getWidth() - 250;
        int enemyY = 100;

        //DESSINER LES MONSTRES
        if (playerMonster != null) {
            BufferedImage sprite = SpriteManager.getPokemonSprite(playerMonster.getName(), true); // Dos
            if (sprite != null) {
                // On grossit l'image x3 pour le style pixel art
                g2d.drawImage(sprite, playerX, groundY - 100, sprite.getWidth() * 3, sprite.getHeight() * 3, null);
            }
            drawHealthBar(g2d, playerMonster, playerX + 200, groundY);
        }

        if (enemyMonster != null) {
            BufferedImage sprite = SpriteManager.getPokemonSprite(enemyMonster.getName(), false); // Face
            if (sprite != null) {
                g2d.drawImage(sprite, enemyX, enemyY, sprite.getWidth() * 3, sprite.getHeight() * 3, null);
            }
            drawHealthBar(g2d, enemyMonster, enemyX - 150, enemyY);
        }
    }

    
    private void drawHealthBar(Graphics2D g, Monster monster, int x, int y) { // Dessine la barre de vie d'un monstre à la position (x, y)
        // Fond de la barre (Gris)
        g.setColor(Color.DARK_GRAY);
        g.fillRoundRect(x, y, 150, 50, 10, 10);
        g.setColor(Color.WHITE);
        g.drawRoundRect(x, y, 150, 50, 10, 10);

        // Nom et Niveau
        g.setFont(new Font("Arial", Font.BOLD, 14));
        g.setColor(Color.WHITE);
        g.drawString(monster.getName() + " N." + monster.getLevel(), x + 10, y + 20);

        // Barre PV (Rouge fond)
        g.setColor(Color.RED);
        g.fillRect(x + 10, y + 30, 130, 10);

        // Barre PV (Vert actuel)
        double hpRatio = (double) monster.getHp() / monster.getStartingHp();
        if(hpRatio > 0.5) g.setColor(Color.GREEN);
        else if(hpRatio > 0.2) g.setColor(Color.ORANGE);
        else g.setColor(Color.RED);
        
        g.fillRect(x + 10, y + 30, (int)(130 * hpRatio), 10);
        
        // Texte PV
        g.setColor(Color.WHITE);
        g.setFont(new Font("Arial", Font.PLAIN, 10));
        g.drawString(monster.getHp() + " / " + monster.getStartingHp(), x + 40, y + 45);
    }
}