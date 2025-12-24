package com.esiea.pootp.gui;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class UIUtils {

    /**
     * Dessine une image en 9-slice scaling.
     * Utile pour les fenêtres/redimensionnables.
     */
    public static void draw9Slice(Graphics2D g2, BufferedImage img, int x, int y, int width, int height) {
        if (img == null) return;

        // On découpe l'image source en une grille de 3x3
        int sliceWidth = img.getWidth() / 3;
        int sliceHeight = img.getHeight() / 3;

        // --- 1. LES 4 COINS (Ne sont pas étirés) ---
        // Haut-Gauche
        g2.drawImage(img, x, y, x + sliceWidth, y + sliceHeight, 
                     0, 0, sliceWidth, sliceHeight, null);
        // Haut-Droit
        g2.drawImage(img, x + width - sliceWidth, y, x + width, y + sliceHeight, 
                     img.getWidth() - sliceWidth, 0, img.getWidth(), sliceHeight, null);
        // Bas-Gauche
        g2.drawImage(img, x, y + height - sliceHeight, x + sliceWidth, y + height, 
                     0, img.getHeight() - sliceHeight, sliceWidth, img.getHeight(), null);
        // Bas-Droit
        g2.drawImage(img, x + width - sliceWidth, y + height - sliceHeight, x + width, y + height, 
                     img.getWidth() - sliceWidth, img.getHeight() - sliceHeight, img.getWidth(), img.getHeight(), null);

        // --- 2. LES BORDS (Sont étirés dans une seule direction) ---
        // Haut
        g2.drawImage(img, x + sliceWidth, y, x + width - sliceWidth, y + sliceHeight, 
                     sliceWidth, 0, img.getWidth() - sliceWidth, sliceHeight, null);
        // Bas
        g2.drawImage(img, x + sliceWidth, y + height - sliceHeight, x + width - sliceWidth, y + height, 
                     sliceWidth, img.getHeight() - sliceHeight, img.getWidth() - sliceWidth, img.getHeight(), null);
        // Gauche
        g2.drawImage(img, x, y + sliceHeight, x + sliceWidth, y + height - sliceHeight, 
                     0, sliceHeight, sliceWidth, img.getHeight() - sliceHeight, null);
        // Droite
        g2.drawImage(img, x + width - sliceWidth, y + sliceHeight, x + width, y + height - sliceHeight, 
                     img.getWidth() - sliceWidth, sliceHeight, img.getWidth(), img.getHeight() - sliceHeight, null);

        // --- 3. LE CENTRE (Est étiré dans les deux directions) ---
        g2.drawImage(img, x + sliceWidth, y + sliceHeight, x + width - sliceWidth, y + height - sliceHeight, 
                     sliceWidth, sliceHeight, img.getWidth() - sliceWidth, img.getHeight() - sliceHeight, null);
    }
    
    /**
     * Charge une police personnalisée (ex: .ttf).
     */
    public static Font loadPixelFont(float size) {
        try {
            File fontFile = new File("com/esiea/pootp/resources/fonts/pokemon.ttf");
            if (!fontFile.exists()) {
                System.err.println("Police introuvable : " + fontFile.getAbsolutePath());
                return new Font("Monospaced", Font.BOLD, (int)size);
            }
            Font font = Font.createFont(Font.TRUETYPE_FONT, fontFile);
            return font.deriveFont(size);
        } catch (FontFormatException | IOException e) {
            e.printStackTrace();
            return new Font("SansSerif", Font.BOLD, (int)size); // Fallback
        }
    }
}