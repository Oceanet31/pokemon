package com.esiea.pootp.gui;

import com.esiea.pootp.game.Player;
import com.esiea.pootp.monsters.Monster;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.util.List;
import java.util.function.Consumer;

/**
 * Panneau qui affiche l'équipe du joueur.
 * Permet de sélectionner un Pokémon pour le changer.
 */
public class TeamPanel extends JPanel {

    private Player player;
    private Runnable onClose;       // Action quand on clique sur "Sortir"
    
    // Action quand on choisit un Pokémon.
    // Consumer<Integer> veut dire : Une fonction qui prend un nombre (l'index) et ne renvoie rien.
    private Consumer<Integer> onSwitch; 

    // États internes
    private int selectedSlot = -1; // Quel slot est cliqué ? (-1 = aucun)
    private ContextMenu contextMenu = null; // Le petit menu "Envoyer/Retour"

    // Animation
    private Timer animTimer;
    private int iconOffsetY = 0; // Pour faire rebondir l'icône du Pokémon sélectionné
    private final int SCALE = 4; // Zoom global pour le pixel art

    // Assets
    private BufferedImage background, hpBarBg, lblPv, lblLv, cursorImage, windowFrame;
    private Font pixelFont;

    // Chemins de fichiers (pour éviter les répétitions)
    private final String PATH_TEAM = "com/esiea/pootp/resources/team/";
    private final String PATH_UI = "com/esiea/pootp/resources/ui/";
    private String mainSlotJson = PATH_TEAM + "party_slot_main.json";
    private String mainSlotImg  = PATH_TEAM + "party_slot_main.png";
    private String subSlotJson  = PATH_TEAM + "party_slot.json";
    private String subSlotImg   = PATH_TEAM + "party_slot.png";
    private String hpJson       = PATH_TEAM + "party_slot_hp_overlay.json";
    private String hpImg        = PATH_TEAM + "party_slot_hp_overlay.png";


    /**
     * Constructor for TeamPanel
     * @param player Player whose team is displayed
     * @param onClose Action to perform on closing the panel
     * @param onSwitch Action to perform when switching Pokémon (index of selected slot)
     */
    public TeamPanel(Player player, Runnable onClose, Consumer<Integer> onSwitch) {
        this.player = player;
        this.onClose = onClose;
        this.onSwitch = onSwitch;
        
        this.setLayout(null);
        this.setOpaque(false); // Transparent pour voir le jeu derrière si besoin
        this.setFocusable(true);
        this.requestFocusInWindow(); // Important pour capter les touches clavier

        // Chargement des images via SpriteManager
        this.background = SpriteManager.loadImage(PATH_TEAM + "party_bg.png");
        this.hpBarBg = SpriteManager.loadImage(PATH_TEAM + "party_slot_hp_bar.png");
        this.lblPv = SpriteManager.loadImage(PATH_TEAM + "party_slot_overlay_hp_fr.png");
        this.lblLv = SpriteManager.loadImage(PATH_TEAM + "party_slot_overlay_lv_fr.png");
        this.cursorImage = SpriteManager.loadImage(PATH_UI + "cursor.png");
        this.windowFrame = SpriteManager.loadImage(PATH_UI + "window_1.png");
        this.pixelFont = UIUtils.loadPixelFont(32f);

        // --- Écouteur CLAVIER ---
        this.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    if (selectedSlot != -1 && contextMenu == null) openContextMenu();
                }
            }
        });

        // --- Écouteur SOURIS ---
        this.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                // Si le menu contextuel est ouvert et qu'on clique ailleurs, on le ferme
                if (contextMenu != null) {
                    if (!contextMenu.getBounds().contains(e.getPoint())) closeContextMenu();
                    return;
                }
                requestFocusInWindow();
                // Détection du slot cliqué
                handleSlotClick(e.getX(), e.getY());
            }
        });

        startAnimation();
    }


    /**
     * Start the icon bounce animation
     */
    private void startAnimation() {
        animTimer = new Timer(30, e -> {
            long now = System.currentTimeMillis();
            // Petit rebond cyclique tous les 650ms
            if (now % 650 < 150) iconOffsetY = -10; else iconOffsetY = 0;   
            repaint(); 
        });
        animTimer.start();
    }

    /**
     * Stop the icon bounce animation
     */
    private void stopAnimation() {
        if (animTimer != null && animTimer.isRunning()) animTimer.stop();
    }

    // Ouvre le menu "Envoyer / Retour" au dessus du slot sélectionné
    /**
     * Open the context menu
     */
    private void openContextMenu() {
        if (contextMenu != null) return;
        contextMenu = new ContextMenu();
        contextMenu.setBounds(800, 480, 350, 180);
        this.add(contextMenu);
        this.setComponentZOrder(contextMenu, 0); // Met le menu au premier plan
        this.revalidate(); 
        this.repaint();
    }

    /**
     * Close the context menu
     */
    private void closeContextMenu() {
        if (contextMenu != null) {
            this.remove(contextMenu);
            contextMenu = null;
            this.revalidate(); this.repaint();
        }
    }

    // Vérifie si le clic est dans les coordonnées d'un slot
    /**
     * Handle click on a slot
     * @param x X coordinate of the click
     * @param y Y coordinate of the click
     */
    private void handleSlotClick(int x, int y) {
        List<Monster> team = player.getTeam();
        
        // Slot Principal (celui en haut à gauche, index 0)
        if (x >= 40 && x <= 480 && y >= 140 && y <= 340) {
            if (selectedSlot == 0) openContextMenu(); else selectedSlot = 0;
            repaint(); return;
        }
        
        // Slots Secondaires (la liste à droite, index 1+)
        for (int i = 1; i < team.size(); i++) {
            int slotY = 50 + (i - 1) * 120;
            if (x >= 500 && x <= 1100 && y >= slotY && y <= slotY + 96) {
                if (selectedSlot == i) openContextMenu(); else selectedSlot = i;
                repaint(); return;
            }
        }
        selectedSlot = -1; repaint();
    }

    @Override
    /**
     * Paint the TeamPanel
     * @param g Graphics context
     */
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;
        
        // Activation du rendu pixelisé
        g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR);
        g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_OFF);

        // Dessin du fond
        if (background != null) g2.drawImage(background, 0, 0, getWidth(), getHeight(), null);
        else { g2.setColor(new Color(20, 30, 50)); g2.fillRect(0, 0, getWidth(), getHeight()); }

        List<Monster> team = player.getTeam();
        if (team.isEmpty()) return;

        // Dessin du Slot Principal (index 0)
        drawMainSlot(g2, team.get(0), 40, 140, selectedSlot == 0);
        
        // Dessin des Slots Secondaires (index 1 à N)
        for (int i = 1; i < team.size(); i++) {
            Monster m = team.get(i);
            int y = 50 + (i - 1) * 124;
            drawSubSlot(g2, m, 544, y+17, selectedSlot == i);
        }

        // Dessin de la barre de texte en bas
        drawBottomBar(g2);
    }

    /**
     * Draw the bottom text bar
     * @param g2 Graphics2D context
     */
    private void drawBottomBar(Graphics2D g2) {
        int boxX = 0; 
        int boxY = getHeight() - 170; 
        int boxW = getWidth() - 70; 
        int boxH = 70;

        if (windowFrame != null) {
            Graphics2D gFrame = (Graphics2D) g2.create();
            gFrame.translate(boxX, boxY); 
            gFrame.scale(4.0, 4.0);
            UIUtils.draw9Slice(gFrame, windowFrame, 0, 0, boxW / 4, boxH / 4);
            gFrame.dispose();
        }
        
        g2.setFont(pixelFont.deriveFont(44f));
        String msg = (contextMenu != null) ? "Que faire avec ce Pokémon ?" : "Sélectionnez un Pokémon.";

        //Placement du texte dans le cadre
        drawShadowText(g2, msg, boxX + 40, boxY + 50);

        // Bouton "Sortir"
        TeamMenuButton btnExit = new TeamMenuButton("Sortir");
        btnExit.setBounds(880,  boxY + 5 , 200, 60); 
        btnExit.addActionListener(e -> {
            if (contextMenu == null) {
                stopAnimation();
                onClose.run(); // Appelle la fermeture gérée par GameWindow
            }
        });
        this.add(btnExit);
    }


    /**
     * Draw text with shadow
     * @param g2 Graphics2D context
     * @param text Text to draw
     * @param x X coordinate
     * @param y Y coordinate
     */
    private void drawShadowText(Graphics2D g2, String text, int x, int y) {
        g2.setColor(new Color(95, 85, 105)); g2.drawString(text, x + 2, y + 2); 
        g2.setColor(Color.WHITE); g2.drawString(text, x, y);
    }


    /**
     * Draw the main slot
     * @param g2 Graphics2D context
     * @param m Monster to draw
     * @param x X coordinate
     * @param y Y coordinate
     * @param isSelected True if the slot is selected
     */
    private void drawMainSlot(Graphics2D g2, Monster m, int x, int y, boolean isSelected) {
        String frame = (m.getHp() > 0) ? "party_slot_main" : "party_slot_main_fnt";
        if (isSelected) frame += "_sel";
        BufferedImage slotImg = SpriteManager.getFrameFromAtlas(mainSlotJson, mainSlotImg, frame);
        if (slotImg != null) g2.drawImage(slotImg, x+1, y+7, slotImg.getWidth() * SCALE-40, slotImg.getHeight() * SCALE+5, null);
        
        g2.setFont(pixelFont.deriveFont(52f));
        drawShadowText(g2, m.getName(), x + 120, y + 75);
        if(lblLv != null) g2.drawImage(lblLv, x + 120, y + 95, lblLv.getWidth()*4, lblLv.getHeight()*4, null);
        drawShadowText(g2, "" + m.getLevel(), x + 160, y + 125);
        
        drawHPBar(g2, m, x + 93, y + 150, true);
        
        BufferedImage icon = SpriteManager.getPokemonIcon(m.getName());
        if (icon != null) g2.drawImage(icon, x, y + iconOffsetY, icon.getWidth() * 3, icon.getHeight() * 3, null);
    }



    // Dessin des petits slots
    /**
     * Draw a sub slot
     * @param g2 Graphics2D context
     * @param m Monster to draw
     * @param x X coordinate
     * @param y Y coordinate
     * @param isSelected True if the slot is selected
     */
    private void drawSubSlot(Graphics2D g2, Monster m, int x, int y, boolean isSelected) {
        String frame = (m.getHp() > 0) ? "party_slot" : "party_slot_fnt";
        if (isSelected) frame += "_sel";
        BufferedImage slotImg = SpriteManager.getFrameFromAtlas(subSlotJson, subSlotImg, frame);
        if (slotImg != null) g2.drawImage(slotImg, x, y, slotImg.getWidth() * SCALE-58, slotImg.getHeight() * SCALE-7, null);
        
        g2.setFont(pixelFont.deriveFont(37f));
        drawShadowText(g2, m.getName(), x + 100, y + 43);
        if(lblLv != null) g2.drawImage(lblLv, x + 130, y + 50, lblLv.getWidth()*3, lblLv.getHeight()*3, null);
        g2.setFont(pixelFont.deriveFont(30f));
        drawShadowText(g2, "" + m.getLevel(), x + 160, y + 74);
        
        drawHPBar(g2, m, x + 300, y + 40, false);
        
        g2.setFont(pixelFont.deriveFont(37f));
        drawShadowText(g2, m.getHp() + "/" + m.getStartingHp(), x + 450, y + 70);
        BufferedImage icon = SpriteManager.getPokemonIcon(m.getName());
        if (icon != null) g2.drawImage(icon, x - 30, y - 30 + iconOffsetY, icon.getWidth() * 3, icon.getHeight() * 3, null);
    }


    /**
     * DESSIN DE LA BARRE DE VIE
     * Cette méthode calcule la largeur de la jauge colorée en fonction des PV.
     * @param g2 Graphics2D context
     * @param m Monster whose HP bar is drawn
     * @param x X coordinate
     * @param y Y coordinate
     * @param showText True to show HP text, false otherwise
     */
    private void drawHPBar(Graphics2D g2, Monster m, int x, int y, boolean showText) {
        //Fond de la barre
        if (hpBarBg != null) {
            g2.drawImage(hpBarBg, x - 75, y - 20, hpBarBg.getWidth() * SCALE - 30, hpBarBg.getHeight() * SCALE, null);
        }
        if (lblPv != null) {
            g2.drawImage(lblPv, x-75, y-20, lblPv.getWidth()*SCALE, lblPv.getHeight()*SCALE, null);
        }

        //Calcul du ratio
        float ratio = (float) m.getHp() / m.getStartingHp();
        if (ratio < 0) ratio = 0; else if (ratio > 1) ratio = 1;

        //Couleur de la barre
        String colorName = "hight";
        if (ratio <= 0.2) colorName = "low";
        else if (ratio <= 0.5) colorName = "medium";
        
        BufferedImage colorImg = SpriteManager.getFrameFromAtlas(hpJson, hpImg, colorName);
        
        //Dessin de la couleur
        if (colorImg != null && hpBarBg != null) {
            // Largeur max
            int maxBarWidth = (hpBarBg.getWidth() * SCALE) - 104; 
            
            // Largeur réelle à afficher
            int currentW = (int) (maxBarWidth * ratio);
            
            // On ne dessine que si la largeur est positive
            if (currentW > 0) {
                g2.drawImage(colorImg, x - 16, y - 12, currentW, colorImg.getHeight() * SCALE, null);
            }
        }

        //Texte des HP
        if (showText) {
            drawShadowText(g2, m.getHp() + "/" + m.getStartingHp(), x + 150, y + 40);
        }
    }



    /**
     * Menu contextuel "Envoyer / Retour"
     */
    private class ContextMenu extends JPanel {

        /**
         * Constructor for ContextMenu
         */
        public ContextMenu() {
            setOpaque(false);
            setLayout(new GridLayout(2, 1, 0, -10)); 
            setBorder(new EmptyBorder(35, 40, 35, 40)); 

            TeamMenuButton btnSend = new TeamMenuButton("Envoyer");
            btnSend.addActionListener(e -> {
                // ACTION : On appelle le 'Consumer' défini dans le constructeur.
                // Cela dit au moteur : "Le joueur veut échanger avec le slot X".
                if (selectedSlot > 0 && selectedSlot < player.getTeam().size()) {
                    if (onSwitch != null) onSwitch.accept(selectedSlot);
                }
                // Note: On ne ferme pas le menu ici, c'est le GameEngine/GameWindow qui fermera le TeamPanel.
            });
            this.add(btnSend);

            TeamMenuButton btnBack = new TeamMenuButton("Retour");
            btnBack.addActionListener(e -> closeContextMenu());
            this.add(btnBack);
        }
        
        @Override 
        /**
         * Paint the ContextMenu
         * @param g Graphics context
         */
        protected void paintComponent(Graphics g) {
            // Dessine un cadre style fenêtre
            if (windowFrame != null) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR);
                g2.scale(4.0, 4.0);
                UIUtils.draw9Slice(g2, windowFrame, 0, 0, getWidth() / 4, getHeight() / 4);
                g2.dispose();
            }
        }
    }

    // Classe Bouton Standard
    /**
     * TeamMenuButton is a custom JButton for the team menu
     */
    private class TeamMenuButton extends JButton {

        private boolean isHovered = false;

        /**
         * Constructor for TeamMenuButton
         * @param text Button text
         */
        public TeamMenuButton(String text) {

            super(text);
            this.setFont(pixelFont.deriveFont(48f)); 
            this.setForeground(Color.WHITE);
            this.setFocusPainted(false); this.setContentAreaFilled(false);
            this.setBorderPainted(false); this.setHorizontalAlignment(SwingConstants.LEFT);
            this.setBorder(new EmptyBorder(0, 40, 0, 0)); 
            this.setCursor(new Cursor(Cursor.HAND_CURSOR));

            addMouseListener(new MouseAdapter() {
                @Override public void mouseEntered(MouseEvent e) { isHovered = true; repaint(); }
                @Override public void mouseExited(MouseEvent e) { isHovered = false; repaint(); }
            });
        }

        @Override 
        /**
         * Paint the TeamMenuButton
         * @param g Graphics context
         */
        protected void paintComponent(Graphics g) {
            Graphics2D g2d = (Graphics2D) g;
            g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_OFF);
            g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR);
            if (hasFocus() || isHovered) {
                if (cursorImage != null) {
                    int scale = 4;
                    g2d.drawImage(cursorImage, 0, (getHeight() - cursorImage.getHeight()*scale) / 2, cursorImage.getWidth()*scale, cursorImage.getHeight()*scale, null);
                }
            }
            g2d.setFont(getFont());
            g2d.setColor(new Color(95, 85, 105)); g2d.drawString(getText(), getInsets().left + 2, (getHeight() / 2) + 12);
            g2d.setColor(getForeground()); g2d.drawString(getText(), getInsets().left, (getHeight() / 2) + 10);
        }
    }
}