package com.esiea.pootp.gui;

import com.esiea.pootp.game.Player;
import com.esiea.pootp.game.GameEngine; 
import com.esiea.pootp.objects.Item;
import com.esiea.pootp.attacks.Attack;
import com.esiea.pootp.monsters.Monster;
import com.esiea.pootp.monsters.ElementType;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.util.Map;
import java.util.List;

public class GameWindow extends JFrame {

    private BattlePanel battlePanel;
    private JPanel bottomPanel, leftContainer, buttonPanel;
    private JTextArea logArea;        
    private BagOverlay bagOverlay;
    private TeamPanel teamOverlay;
    
    private Player player;
    private GameEngine gameEngine;

    // Assets graphiques
    private BufferedImage overlayBg, windowFrame, cursorImage, typesAtlas, categoriesAtlas;
    private Font pixelFont;

    // Gestion du texte "Machine à écrire"
    private Timer textTimer;
    private String textToDisplay;
    private int textIndex;
    private Runnable onTextFinished; // Ce qu'on fait quand le texte est fini
    private boolean isTyping = false;

    /** Constructor for GameWindow
     * @param player Player instance
    */
    public GameWindow(Player player) {
        this.player = player;
        this.setTitle("Pokémon Java Edition");
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setSize(1200, 800);
        
        getLayeredPane().setLayout(null); 
        JPanel mainContent = new JPanel(new BorderLayout());
        mainContent.setBounds(0, 0, 1200, 800); 
        this.setContentPane(mainContent);
        //Utilite de les declerer ici
        // Chargement Assets
        this.overlayBg = SpriteManager.loadImage("com/esiea/pootp/resources/ui/overlay_message.png");
        this.windowFrame = SpriteManager.loadImage("com/esiea/pootp/resources/ui/window_1.png");
        this.cursorImage = SpriteManager.loadImage("com/esiea/pootp/resources/ui/cursor.png");
        this.typesAtlas = SpriteManager.loadImage("com/esiea/pootp/resources/ui/types_fr.png");
        this.categoriesAtlas = SpriteManager.loadImage("com/esiea/pootp/resources/ui/categories.png");
        this.pixelFont = UIUtils.loadPixelFont(32f);

        battlePanel = new BattlePanel();
        mainContent.add(battlePanel, BorderLayout.CENTER);

        initBottomPanel(mainContent);
        showMainMenuButtons(); 

       // Gestion du redimensionnement (pour que les menus flottants suivent la taille)
       this.addComponentListener(new java.awt.event.ComponentAdapter() {
            public void componentResized(java.awt.event.ComponentEvent e) {
                mainContent.setBounds(0, 0, getWidth(), getHeight());
                if(bagOverlay != null) reposBagOverlay(); 
                if(teamOverlay != null) teamOverlay.setBounds(0, 0, getWidth(), getHeight());
            }
        });
        
        textTimer = new Timer(30, e -> updateTextTypewriter());
        this.setVisible(true);
    }

    /**
     * Show a dialog with typewriter effect
     * @param text Text to display
     * @param nextAction Action to perform after text is displayed
     */
    public void showDialog(String text, Runnable nextAction) {
        // On s'assure que la zone de log est visible
        if (leftContainer.getComponent(0) != logArea) {
            leftContainer.removeAll(); leftContainer.add(logArea, BorderLayout.CENTER);
            leftContainer.revalidate(); leftContainer.repaint();
            buttonPanel.removeAll(); buttonPanel.revalidate(); buttonPanel.repaint();
        }
        this.textToDisplay = text;
        this.onTextFinished = nextAction;
        this.textIndex = 0;
        this.logArea.setText(""); 
        this.isTyping = true;
        this.textTimer.start();
    }

    /**
     * Update the text area with typewriter effect
     */
    private void updateTextTypewriter() {
        if (textToDisplay == null) return;
        if (textIndex < textToDisplay.length()) {
            logArea.append(String.valueOf(textToDisplay.charAt(textIndex)));
            textIndex++;
        } else {
            textTimer.stop(); isTyping = false;
            // Pause courte à la fin du texte avant d'exécuter la suite
            Timer pause = new Timer(800, e -> {
                ((Timer)e.getSource()).stop();
                if (onTextFinished != null) onTextFinished.run(); 
            });
            pause.setRepeats(false); pause.start();
        }
    }

    /**
     * Add log text directly (without typewriter effect)
     * @param text Text to display
     */
    public void addLog(String text) { 
        if (leftContainer.getComponent(0) != logArea) {
             leftContainer.removeAll(); leftContainer.add(logArea, BorderLayout.CENTER);
             leftContainer.revalidate(); leftContainer.repaint();
        }
        logArea.setText(text); 
    }

    /** Set the game engine 
     * @param engine GameEngine instance
    */
    public void setGameEngine(GameEngine engine) { 
        this.gameEngine = engine; 
    }

    /** Return the battle panel
     * @return battle panel
     */
    public BattlePanel getBattlePanel() { 
        return battlePanel; 
    }

    /**
     * Initialize the bottom panel with log area and buttons
     * @param mainContent Main content panel
     */
    private void initBottomPanel(JPanel mainContent) {
        bottomPanel = new JPanel() {
            @Override protected void paintComponent(Graphics g) {
                if (overlayBg != null) {
                    Graphics2D g2 = (Graphics2D) g.create();
                    g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR);
                    g2.scale(4.0, 4.0);
                    g2.drawImage(overlayBg, 0, 0, getWidth()/4, getHeight()/4, null);
                    g2.dispose();
                } else { g.setColor(Color.DARK_GRAY); g.fillRect(0, 0, getWidth(), getHeight()); }
            }
        };

        bottomPanel.setPreferredSize(new Dimension(800, 180));
        bottomPanel.setLayout(new BorderLayout());
        mainContent.add(bottomPanel, BorderLayout.SOUTH);

        leftContainer = new JPanel(new BorderLayout());
        leftContainer.setOpaque(false);
        leftContainer.setBorder(new EmptyBorder(25, 45, 25, 10)); 
        
        logArea = new JTextArea();
        logArea.setEditable(false); logArea.setOpaque(false);
        logArea.setForeground(Color.WHITE); logArea.setFont(pixelFont.deriveFont(48f));
        logArea.setLineWrap(true); logArea.setWrapStyleWord(true);
        leftContainer.add(logArea, BorderLayout.CENTER);
        bottomPanel.add(leftContainer, BorderLayout.CENTER);

        //Partie droite avec les boutons
        JPanel rightContainer = new JPanel(new BorderLayout()); 
        rightContainer.setOpaque(false);

        buttonPanel = new JPanel() {
            @Override 
            protected void paintComponent(Graphics g) {
                if (windowFrame != null) {
                    Graphics2D g2 = (Graphics2D) g.create();
                    g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR);
                    g2.scale(4.0, 4.0);
                    UIUtils.draw9Slice(g2, windowFrame, 0, 0, getWidth()/4, getHeight()/4);
                    g2.dispose();
                }
            }
        };
        buttonPanel.setOpaque(false);
        buttonPanel.setPreferredSize(new Dimension(500, 180)); 
        buttonPanel.setBorder(new EmptyBorder(20, 20, 20, 20));
        buttonPanel.setLayout(new GridLayout(2, 2, 0, 0));
        rightContainer.add(buttonPanel, BorderLayout.CENTER);
        bottomPanel.add(rightContainer, BorderLayout.EAST);
    }


    /**
     * Show main menu buttons
     */
    public void showMainMenuButtons() {
        buttonPanel.setPreferredSize(new Dimension(500, 180)); 
        leftContainer.removeAll(); leftContainer.add(logArea, BorderLayout.CENTER);
        leftContainer.revalidate(); leftContainer.repaint();
        buttonPanel.removeAll(); buttonPanel.setLayout(new GridLayout(2, 2, 0, 0)); 
        
        buttonPanel.add(createMenuButton("Attaque", e -> showAttackMenu())); 
        buttonPanel.add(createMenuButton("Sac", e -> toggleBagMenu())); 
        buttonPanel.add(createMenuButton("Pokémon", e -> toggleTeamMenu()));
        buttonPanel.add(createMenuButton("Fuite", e -> addLog("Impossible !")));
        buttonPanel.revalidate(); buttonPanel.repaint();
    }

    /**
     * Show attack menu
     */
    public void showAttackMenu() {
        Monster active = player.getActiveMonster();
        if (active == null) return;
        List<Attack> attacks = active.getAttacks();

        if (attacks.isEmpty()) {
            showDialog("Pas d'attaque ! " + active.getName() + " utilise Lutte.", () -> {
                if (gameEngine != null) gameEngine.onPlayerAttack(null); 
            });
            return;
        }

        buttonPanel.setPreferredSize(new Dimension(340, 180)); 
        AttackInfoPanel infoPanel = new AttackInfoPanel(); // Panneau d'info à droite
        buttonPanel.removeAll(); buttonPanel.setLayout(new BorderLayout());
        buttonPanel.add(infoPanel, BorderLayout.CENTER);

        JPanel attacksWrapper = new JPanel(new BorderLayout()); attacksWrapper.setOpaque(false);
        JPanel attacksGrid = new JPanel(new GridLayout(2, 2, 2, 2)); attacksGrid.setOpaque(false);

        for (Attack atk : attacks) {
            MenuButton btn = new MenuButton(atk.getName());
            btn.setFont(pixelFont.deriveFont(52f)); 
            btn.addActionListener(e -> {
                if (gameEngine != null) gameEngine.onPlayerAttack(atk);
            });
            // Survol souris -> Mise à jour info
            btn.addMouseListener(new MouseAdapter() {
                @Override public void mouseEntered(MouseEvent e) { infoPanel.updateInfo(atk); }
            });
            attacksGrid.add(btn);
        }
        for (int i = attacks.size(); i < 4; i++) attacksGrid.add(new JLabel("")); 

        MenuButton btnBack = new MenuButton("Retour");
        btnBack.setFont(pixelFont.deriveFont(52f)); 
        btnBack.addActionListener(e -> showMainMenuButtons());
        
        JPanel backContainer = new JPanel(new GridBagLayout());
        backContainer.setOpaque(false); backContainer.setBorder(new EmptyBorder(0, 10, 0, 0));
        backContainer.add(btnBack);

        attacksWrapper.add(attacksGrid, BorderLayout.CENTER);
        attacksWrapper.add(backContainer, BorderLayout.EAST);

        leftContainer.removeAll(); leftContainer.add(attacksWrapper, BorderLayout.CENTER);
        if (!attacks.isEmpty()) infoPanel.updateInfo(attacks.get(0)); // Init avec la 1ère attaque
        leftContainer.revalidate(); leftContainer.repaint();
        buttonPanel.revalidate(); buttonPanel.repaint();
    }


    /**     
     * Create a menu button with given text and action
     * @param text Button text
     * @param action Action listener
     * @return created MenuButton
     */
    private MenuButton createMenuButton(String text, java.awt.event.ActionListener action) {
        MenuButton btn = new MenuButton(text);
        btn.addActionListener(action);
        return btn;
    }

    
    /**
     * Panneau d'information sur l'attaque sélectionnée
     */
    private class AttackInfoPanel extends JPanel {
        private Attack currentAttack;

        /**
         * Constructor for AttackInfoPanel
         */
        public AttackInfoPanel() { 
            setOpaque(false); 
        }

        /**
         * Update the displayed attack info
         * @param atk Attack to display
         */
        public void updateInfo(Attack atk) { 
            this.currentAttack = atk; 
            this.repaint(); 
        }
        
        @Override 
        /**
         * Paint the attack info panel
         * @param g Graphics context
         */
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            if (currentAttack == null) return;
            Graphics2D g2 = (Graphics2D) g;
            g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR);
            g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_OFF);

            int leftX = 20; int rightX = 150; int topY = 8;
            int typeScale = 3; int catScale = 4;
            
            // Affichage du Type avec mapping corrigé (selon ton fichier JSON)
            if (typesAtlas != null) {
                int iconW = 32; int iconH = 14; int srcY = 0;
                switch(currentAttack.getType()) {
                    case FIRE:      srcY = 98;  break;
                    case WATER:     srcY = 252; break;
                    case LIGHTNING: srcY = 56;  break;
                    case EARTH:     srcY = 154; break;
                    case NATURE:    srcY = 140; break; 
                    case NORMAL:    srcY = 182; break;
                    default:        srcY = 0;   break;
                }
                g2.drawImage(typesAtlas, leftX, topY, leftX + iconW*typeScale, topY + iconH*typeScale, 0, srcY, iconW, srcY + iconH, null);
            }

            g2.setFont(pixelFont.deriveFont(33f));
            int line1Y = 75; int line2Y = 105; int line3Y = 135;
            
            // Affichage dynamique des PP
            drawShadowText(g2, "PP", leftX + 10, line1Y);
            drawShadowText(g2, "" + currentAttack.getNbUse(), rightX + 10, line1Y);

            drawShadowText(g2, "Puissance", leftX + 10, line2Y);
            drawShadowText(g2, "" + (int)currentAttack.getPower(), rightX + 10, line2Y);
            
            drawShadowText(g2, "Précision", leftX + 10, line3Y);
            drawShadowText(g2, "" + (int)(currentAttack.getAccuracy()*100), rightX + 10, line3Y);
        }

        /** Draw text with shadow effect
         * @param g2 Graphics2D context
         * @param text Text to draw
         * @param x X position
         * @param y Y position
         */
        private void drawShadowText(Graphics2D g2, String text, int x, int y) {
            g2.setColor(new Color(95, 85, 105)); g2.drawString(text, x + 2, y + 2);
            g2.setColor(Color.WHITE); g2.drawString(text, x, y);
        }
    }
    
    /**
     * Toggle bag menu
     */
    private void toggleBagMenu() {
        if (bagOverlay == null) {
            bagOverlay = new BagOverlay();
            JLayeredPane lp = getLayeredPane();
            lp.add(bagOverlay, JLayeredPane.POPUP_LAYER); lp.moveToFront(bagOverlay);
            reposBagOverlay(); lp.revalidate(); lp.repaint(); 
            addLog("Quel objet utiliser ?");
        } else closeBagMenu();
    }

    /**
     *  Close bag menu
     */
    private void closeBagMenu() {
        if (bagOverlay != null) { 
            getLayeredPane().remove(bagOverlay); 
            bagOverlay = null; 
            getLayeredPane().repaint(); 
            addLog("Retour au combat."); 
        }
    }

    /**
     * Reposition bag overlay
     */
    private void reposBagOverlay() {
        if(bagOverlay != null) bagOverlay.setBounds(getLayeredPane().getWidth()-500, getLayeredPane().getHeight()-180-320-15, 500, 320);
    }

    /**
     * Bag overlay panel
     */
    private class BagOverlay extends JPanel {

        /**
         * Constructor for BagOverlay
         */
        public BagOverlay() {
            setOpaque(false); setLayout(new BorderLayout()); setBorder(new EmptyBorder(30, 45, 30, 30));
            JPanel listPanel = new JPanel(new GridLayout(5, 1)); listPanel.setOpaque(false);
            
            int count = 0;
            for (Map.Entry<Item, Integer> entry : player.getInventory().entrySet()) {
                if(count >= 4) break; 
                Item item = entry.getKey();
                MenuButton btn = new MenuButton("x" + entry.getValue() + "   " + item.getName());
                btn.setFont(pixelFont.deriveFont(52f)); 
                btn.addActionListener(e -> {
                    // Clic sur objet -> Appel au moteur
                    if(gameEngine != null) gameEngine.onPlayerUseItem(item); 
                    closeBagMenu();
                });
                listPanel.add(btn); count++;
            }

            MenuButton btnBack = new MenuButton("Retour");
            btnBack.setFont(pixelFont.deriveFont(52f));
            btnBack.addActionListener(e -> closeBagMenu());
            listPanel.add(btnBack);
            add(listPanel, BorderLayout.CENTER);
        }

        @Override 
        /**
         * Paint the bag overlay panel
         * @param g Graphics context
         */
        protected void paintComponent(Graphics g) {
            if (windowFrame != null) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR);
                g2.scale(4.0, 4.0);
                UIUtils.draw9Slice(g2, windowFrame, 0, 0, getWidth()/4, getHeight()/4);
                g2.dispose();
            }
        }
    }

    /**
     * Toggle team menu
     */
    private void toggleTeamMenu() {
        if (teamOverlay == null) {
            // Création avec Callback pour le switch.
            // Quand le joueur choisit un pokémon, on ferme le menu PUIS on prévient le moteur.
            teamOverlay = new TeamPanel(player, 
                () -> closeTeamMenu(), 
                (slotIndex) -> {       
                    closeTeamMenu();   
                    if (gameEngine != null) gameEngine.onPlayerSwitch(slotIndex); 
                }
            );
            teamOverlay.setBounds(0, 0, getWidth(), getHeight());
            JLayeredPane lp = getLayeredPane();
            lp.add(teamOverlay, JLayeredPane.MODAL_LAYER); lp.moveToFront(teamOverlay);
            lp.revalidate(); lp.repaint();
        } else closeTeamMenu();
    }

    /**
     * Close team menu
     */
    private void closeTeamMenu() {
        if (teamOverlay != null) { getLayeredPane().remove(teamOverlay); teamOverlay = null; getLayeredPane().repaint(); }
    }

    /**
     * Menu button with pixel art style
     */
    private class MenuButton extends JButton {
        // Bouton stylisé Pixel Art (même code que TeamMenuButton en gros)
        private boolean isHovered = false;

        /**
         *  Constructor for MenuButton
         * @param text Button text
         */
        public MenuButton(String text) {
            super(text); setFont(pixelFont.deriveFont(52f)); setForeground(Color.WHITE);
            setFocusPainted(false); setContentAreaFilled(false); setBorderPainted(false);
            setHorizontalAlignment(SwingConstants.LEFT); setBorder(new EmptyBorder(0, 40, 0, 0)); 
            setCursor(new Cursor(Cursor.HAND_CURSOR));
            addMouseListener(new MouseAdapter() {
                @Override public void mouseEntered(MouseEvent e) { isHovered = true; repaint(); }
                @Override public void mouseExited(MouseEvent e) { isHovered = false; repaint(); }
            });
        }


        @Override 
        /**
         * Paint the menu button
         * @param g Graphics context
         */
        protected void paintComponent(Graphics g) {
            Graphics2D g2d = (Graphics2D) g;
            g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_OFF);
            g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR);
            
            if (hasFocus() || isHovered) {
                if (cursorImage != null) g2d.drawImage(cursorImage, 10, (getHeight()-cursorImage.getHeight()*4)/2, cursorImage.getWidth()*4, cursorImage.getHeight()*4, null);
            }

            g2d.setFont(getFont());
            g2d.setColor(new Color(95, 85, 105)); g2d.drawString(getText(), getInsets().left + 2, (getHeight()/2)+12);
            g2d.setColor(getForeground()); g2d.drawString(getText(), getInsets().left, (getHeight()/2)+10);
        }
    }
}