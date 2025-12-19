package com.esiea.pootp.gui;

import javax.swing.*;
import java.awt.*;

public class GameWindow extends JFrame {
    
    private BattlePanel battlePanel;
    private JTextArea logArea;
    private JPanel buttonPanel;

    public GameWindow() {
        this.setTitle("TP pas pokémon");
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setSize(800, 750);
        this.setLayout(new BorderLayout());

        // Zone de combat (Centre)
        battlePanel = new BattlePanel();
        this.add(battlePanel, BorderLayout.CENTER);

        // Zone de texte (Logs)
        logArea = new JTextArea(5, 40);
        logArea.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(logArea);
        
        // Panneau bas (Boutons + Logs)
        JPanel bottomPanel = new JPanel();
        bottomPanel.setLayout(new BorderLayout());
        bottomPanel.add(scrollPane, BorderLayout.CENTER);
        
        // Boutons d'action
        buttonPanel = new JPanel();
        buttonPanel.setLayout(new GridLayout(2, 2));
        
        JButton atkBtn = new JButton("Attaque");
        JButton bagBtn = new JButton("Sac");
        JButton pkmnBtn = new JButton("Pokémon");
        JButton runBtn = new JButton("Fuite");
        
        // Style simple
        atkBtn.setBackground(new Color(255, 100, 100));
        
        buttonPanel.add(atkBtn);
        buttonPanel.add(bagBtn);
        buttonPanel.add(pkmnBtn);
        buttonPanel.add(runBtn);
        
        bottomPanel.add(buttonPanel, BorderLayout.EAST);
        
        this.add(bottomPanel, BorderLayout.SOUTH);

        this.setVisible(true);
    }

    public BattlePanel getBattlePanel() {
        return battlePanel;
    }
    
    public void addLog(String text) {
        logArea.append(text + "\n");
        logArea.setCaretPosition(logArea.getDocument().getLength()); // Auto-scroll
    }
}