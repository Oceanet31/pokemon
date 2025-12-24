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
    
    // --- Composants ---
    private JPanel bottomPanel;
    private JPanel leftContainer;
    private JTextArea logArea;
    private JPanel buttonPanel;
    private BagOverlay bagOverlay;
    
    // --- Données ---
    private Player player;
    private GameEngine gameEngine;

    // --- Ressources UI ---
    private BufferedImage overlayBg;
    private BufferedImage windowFrame;
    private BufferedImage cursorImage;
    private Font pixelFont;

    // --- Ressources Attaques ---
    private BufferedImage typesAtlas;
    private BufferedImage categoriesAtlas;

    // --- GESTION DU TEXTE DÉFILANT ---
    private Timer textTimer;
    private String textToDisplay;
    private int textIndex;
    private Runnable onTextFinished; 
    private boolean isTyping = false;

    public GameWindow(Player player) {
        this.player = player;
        this.setTitle("Pokémon Java Edition");
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setSize(1200, 800);
        
        getLayeredPane().setLayout(null); 
        
        JPanel mainContent = new JPanel(new BorderLayout());
        mainContent.setBounds(0, 0, 1200, 800); 
        this.setContentPane(mainContent);

        // --- CHARGEMENT ---
        this.overlayBg = SpriteManager.loadImage("com/esiea/pootp/resources/ui/overlay_message.png");
        this.windowFrame = SpriteManager.loadImage("com/esiea/pootp/resources/ui/window_1.png");
        this.cursorImage = SpriteManager.loadImage("com/esiea/pootp/resources/ui/cursor.png");
        this.typesAtlas = SpriteManager.loadImage("com/esiea/pootp/resources/ui/types_fr.png");
        this.categoriesAtlas = SpriteManager.loadImage("com/esiea/pootp/resources/ui/categories.png");
        this.pixelFont = UIUtils.loadPixelFont(32f);

        // --- UI ---
        battlePanel = new BattlePanel();
        mainContent.add(battlePanel, BorderLayout.CENTER);

        initBottomPanel(mainContent);
        showMainMenuButtons(); 

        this.addComponentListener(new java.awt.event.ComponentAdapter() {
            public void componentResized(java.awt.event.ComponentEvent e) {
                mainContent.setBounds(0, 0, getWidth(), getHeight());
                if(bagOverlay != null) reposBagOverlay(); 
            }
        });
        
        // Timer pour le texte (Vitesse : 30ms)
        textTimer = new Timer(30, e -> updateTextTypewriter());

        this.setVisible(true);
    }

    // --- MÉTHODE D'AFFICHAGE TEXTE ---
    public void showDialog(String text, Runnable nextAction) {
        // Force l'affichage du panel de texte
        if (leftContainer.getComponent(0) != logArea) {
            leftContainer.removeAll();
            leftContainer.add(logArea, BorderLayout.CENTER);
            leftContainer.revalidate();
            leftContainer.repaint();
            
            // Cache les boutons
            buttonPanel.removeAll();
            buttonPanel.revalidate();
            buttonPanel.repaint();
        }

        this.textToDisplay = text;
        this.onTextFinished = nextAction;
        this.textIndex = 0;
        this.logArea.setText(""); 
        this.isTyping = true;
        this.textTimer.start();
    }

    private void updateTextTypewriter() {
        if (textToDisplay == null) return;

        if (textIndex < textToDisplay.length()) {
            logArea.append(String.valueOf(textToDisplay.charAt(textIndex)));
            textIndex++;
        } else {
            // Fin du texte
            textTimer.stop();
            isTyping = false;
            
            // Pause de lecture (800ms) avant l'action suivante
            Timer pause = new Timer(800, e -> {
                ((Timer)e.getSource()).stop();
                if (onTextFinished != null) {
                    onTextFinished.run(); 
                }
            });
            pause.setRepeats(false);
            pause.start();
        }
    }

    // Gardé pour compatibilité, mais showDialog est préférable
    public void addLog(String text) { 
        if (leftContainer.getComponent(0) != logArea) {
             leftContainer.removeAll();
             leftContainer.add(logArea, BorderLayout.CENTER);
             leftContainer.revalidate(); leftContainer.repaint();
        }
        logArea.setText(text); 
    }

    public void setGameEngine(GameEngine engine) { this.gameEngine = engine; }
    public BattlePanel getBattlePanel() { return battlePanel; }

    private void initBottomPanel(JPanel mainContent) {
        bottomPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                if (overlayBg != null) {
                    Graphics2D g2 = (Graphics2D) g.create();
                    g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR);
                    double scale = 4.0;
                    g2.scale(scale, scale);
                    g2.drawImage(overlayBg, 0, 0, (int)(getWidth()/scale), (int)(getHeight()/scale), null);
                    g2.dispose();
                } else {
                    g.setColor(Color.DARK_GRAY);
                    g.fillRect(0, 0, getWidth(), getHeight());
                }
            }
        };
        bottomPanel.setPreferredSize(new Dimension(800, 180));
        bottomPanel.setLayout(new BorderLayout());
        mainContent.add(bottomPanel, BorderLayout.SOUTH);

        leftContainer = new JPanel(new BorderLayout());
        leftContainer.setOpaque(false);
        leftContainer.setBorder(new EmptyBorder(25, 45, 25, 10)); 
        
        logArea = new JTextArea();
        logArea.setEditable(false);
        logArea.setOpaque(false);
        logArea.setForeground(Color.WHITE);
        logArea.setFont(pixelFont.deriveFont(48f));
        logArea.setLineWrap(true);
        logArea.setWrapStyleWord(true);
        
        logArea.setUI(new javax.swing.plaf.basic.BasicTextAreaUI() {
            protected void paintSafely(Graphics g) {
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_OFF);
                super.paintSafely(g);
            }
        });

        leftContainer.add(logArea, BorderLayout.CENTER);
        bottomPanel.add(leftContainer, BorderLayout.CENTER);

        JPanel rightContainer = new JPanel(new BorderLayout());
        rightContainer.setOpaque(false);
        
        buttonPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                if (windowFrame != null) {
                    Graphics2D g2 = (Graphics2D) g.create();
                    g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR);
                    double scale = 4.0;
                    g2.scale(scale, scale);
                    UIUtils.draw9Slice(g2, windowFrame, 0, 0, (int)(getWidth()/scale), (int)(getHeight()/scale));
                    g2.dispose();
                }
            }
        };
        buttonPanel.setOpaque(false);
        buttonPanel.setPreferredSize(new Dimension(500, 180));
        buttonPanel.setBorder(new EmptyBorder(25, 45, 25, 30));
        buttonPanel.setLayout(new GridLayout(2, 2, 0, 0));

        rightContainer.add(buttonPanel, BorderLayout.CENTER);
        bottomPanel.add(rightContainer, BorderLayout.EAST);
    }

    public void showMainMenuButtons() {
        leftContainer.removeAll();
        leftContainer.add(logArea, BorderLayout.CENTER);
        leftContainer.revalidate(); leftContainer.repaint();

        buttonPanel.removeAll();
        buttonPanel.setLayout(new GridLayout(2, 2, 0, 0)); 
        
        buttonPanel.add(createMenuButton("Attaque", e -> showAttackMenu())); 
        buttonPanel.add(createMenuButton("Sac", e -> toggleBagMenu())); 
        buttonPanel.add(createMenuButton("Pokémon", e -> addLog("Équipe...")));
        buttonPanel.add(createMenuButton("Fuite", e -> addLog("Impossible !")));
        
        buttonPanel.revalidate(); buttonPanel.repaint();
    }

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

        AttackInfoPanel infoPanel = new AttackInfoPanel();
        buttonPanel.removeAll();
        buttonPanel.setLayout(new BorderLayout());
        buttonPanel.add(infoPanel, BorderLayout.CENTER);

        JPanel attacksGrid = new JPanel(new GridLayout(2, 2, 10, 5)); 
        attacksGrid.setOpaque(false);

        for (Attack atk : attacks) {
            MenuButton btn = new MenuButton(atk.getName());
            btn.setFont(pixelFont.deriveFont(36f)); 
            
            btn.addActionListener(e -> {
                // On passe la main au moteur
                if (gameEngine != null) {
                    gameEngine.onPlayerAttack(atk);
                }
            });

            btn.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseEntered(MouseEvent e) {
                    infoPanel.updateInfo(atk);
                }
            });
            
            btn.addMouseListener(new MouseAdapter() {
                public void mouseClicked(MouseEvent e) {
                    if (SwingUtilities.isRightMouseButton(e)) showMainMenuButtons();
                }
            });

            attacksGrid.add(btn);
        }

        for (int i = attacks.size(); i < 4; i++) {
            attacksGrid.add(new JLabel(""));
        }

        leftContainer.removeAll();
        leftContainer.add(attacksGrid, BorderLayout.CENTER);
        
        if (!attacks.isEmpty()) infoPanel.updateInfo(attacks.get(0));

        leftContainer.revalidate(); leftContainer.repaint();
        buttonPanel.revalidate(); buttonPanel.repaint();
    }

    private void toggleBagMenu() {
        if (bagOverlay == null) {
            bagOverlay = new BagOverlay();
            getLayeredPane().add(bagOverlay, JLayeredPane.POPUP_LAYER);
            reposBagOverlay(); 
            addLog("Quel objet utiliser ?");
        } else {
            closeBagMenu();
        }
    }

    private void closeBagMenu() {
        if (bagOverlay != null) {
            getLayeredPane().remove(bagOverlay);
            bagOverlay = null;
            getLayeredPane().repaint();
            addLog("Retour au combat.");
        }
    }

    private void reposBagOverlay() {
        if(bagOverlay == null) return;
        int w = 500; int h = 320; 
        int x = getLayeredPane().getWidth() - w; 
        int y = getLayeredPane().getHeight() - 180 - h - 15; 
        bagOverlay.setBounds(x, y, w, h);
    }
    
    private MenuButton createMenuButton(String text, java.awt.event.ActionListener action) {
        MenuButton btn = new MenuButton(text);
        btn.addActionListener(action);
        return btn;
    }

    private class AttackInfoPanel extends JPanel {
        private Attack currentAttack;
        public AttackInfoPanel() { setOpaque(false); }
        public void updateInfo(Attack atk) {
            this.currentAttack = atk;
            this.repaint(); 
        }
        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            if (currentAttack == null) return;
            Graphics2D g2 = (Graphics2D) g;
            g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR);
            g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_OFF);

            if (typesAtlas != null) {
                int iconW = 32; int iconH = 14; int srcY = 0; 
                ElementType type = currentAttack.getType();
                switch(type) {
                    case FIRE:      srcY = 98;  break; 
                    case WATER:     srcY = 252; break; 
                    case LIGHTNING: srcY = 56;  break; 
                    case EARTH:     srcY = 154; break; 
                    case NATURE:    srcY = 182; break; 
                    default:        srcY = 0;   break; 
                }
                int scale = 3;
                g2.drawImage(typesAtlas, 10, 10, 10 + iconW*scale, 10 + iconH*scale, 0, srcY, iconW, srcY + iconH, null);
            }

            if (categoriesAtlas != null) {
                int catW = 28; int catH = 11; int srcX = 0;
                boolean isSpecial = (currentAttack.getType() == ElementType.WATER || currentAttack.getType() == ElementType.FIRE || currentAttack.getType() == ElementType.LIGHTNING);
                if (isSpecial) srcX = 28; else srcX = 0;
                int scale = 3;
                g2.drawImage(categoriesAtlas, 150, 10, 150 + catW*scale, 10 + catH*scale, srcX, 0, srcX + catW, catH, null);
            }

            g2.setFont(pixelFont.deriveFont(28f));
            g2.setColor(Color.WHITE);
            g2.drawString("PP", 20, 80);
            g2.drawString("15/15", 150, 80);
            g2.drawString("PUISS", 20, 110);
            String powerTxt = (currentAttack.getPower() > 0) ? "" + (int)currentAttack.getPower() : "---";
            g2.drawString(powerTxt, 150, 110);
            g2.drawString("PRÉC", 20, 140);
            String accTxt = (currentAttack.getAccuracy() > 0) ? "" + (int)(currentAttack.getAccuracy()*100) : "---";
            g2.drawString(accTxt, 150, 140);
        }
    }
    
    private class BagOverlay extends JPanel {
        private JPanel listPanel;
        public BagOverlay() {
            setOpaque(false);
            setLayout(new BorderLayout());
            setBorder(new EmptyBorder(30, 45, 30, 30));
            listPanel = new JPanel(new GridLayout(5, 1)); 
            listPanel.setOpaque(false);
            Map<Item, Integer> inventory = player.getInventory();
            int count = 0;
            for (Map.Entry<Item, Integer> entry : inventory.entrySet()) {
                if(count >= 4) break; 
                Item item = entry.getKey();
                String txt = "x" + entry.getValue() + "   " + item.getName();
                MenuButton btn = new MenuButton(txt);
                btn.setFont(pixelFont.deriveFont(36f)); 
                btn.addActionListener(e -> {
                    addLog("Utilisation de : " + item.getName());
                    if(gameEngine != null) gameEngine.onPlayerUseItem(item); 
                    closeBagMenu();
                });
                btn.addMouseListener(new MouseAdapter() {
                    @Override
                    public void mouseEntered(MouseEvent e) {
                       logArea.setText(item.getName() + " :\nObjet utile pour l'aventure.");
                    }
                });
                listPanel.add(btn);
                count++;
            }
            MenuButton btnBack = new MenuButton("Retour");
            btnBack.addActionListener(e -> closeBagMenu());
            listPanel.add(btnBack);
            add(listPanel, BorderLayout.CENTER);
        }
        @Override
        protected void paintComponent(Graphics g) {
            if (windowFrame != null) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR);
                double scale = 4.0;
                g2.scale(scale, scale);
                UIUtils.draw9Slice(g2, windowFrame, 0, 0, (int)(getWidth()/scale), (int)(getHeight()/scale));
                g2.dispose();
            }
        }
    }

    private class MenuButton extends JButton {
        private boolean isHovered = false;
        public MenuButton(String text) {
            super(text);
            this.setFont(pixelFont.deriveFont(42f));
            this.setForeground(Color.WHITE);
            this.setFocusPainted(false);
            this.setContentAreaFilled(false); 
            this.setBorderPainted(false);
            this.setHorizontalAlignment(SwingConstants.LEFT);
            this.setBorder(new EmptyBorder(0, 40, 0, 0)); 
            this.setCursor(new Cursor(Cursor.HAND_CURSOR));
            addMouseListener(new MouseAdapter() {
                @Override
                public void mouseEntered(MouseEvent e) { isHovered = true; repaint(); }
                @Override
                public void mouseExited(MouseEvent e) { isHovered = false; repaint(); }
            });
        }
        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2d = (Graphics2D) g;
            g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_OFF);
            g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR);
            super.paintComponent(g2d);
            if (hasFocus() || isHovered) {
                if (cursorImage != null) {
                    int scale = 3;
                    int cw = cursorImage.getWidth() * scale;
                    int ch = cursorImage.getHeight() * scale;
                    int cursorY = (getHeight() - ch) / 2;
                    g2d.drawImage(cursorImage, 10, cursorY, cw, ch, null);
                }
            }
        }
    }
}