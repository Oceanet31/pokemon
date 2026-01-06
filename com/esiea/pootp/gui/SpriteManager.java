package com.esiea.pootp.gui;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SpriteManager {
    
    private static Map<String, BufferedImage> cache = new HashMap<>();

    /**
     * Load an image with caching
     * @param path Path to the image file
     * @return Loaded BufferedImage
     */
    public static BufferedImage loadImage(String path) {
        if (cache.containsKey(path)) {
            return cache.get(path);
        }
        try {
            File file = new File(path);
            if (!file.exists()) {
                System.err.println("Image introuvable : " + path);
                return null;
            }
            BufferedImage img = ImageIO.read(file);
            cache.put(path, img);
            return img;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }


    /**
     * Get the sprite of a Pokémon
     * @param name Name of the Pokémon
     * @param isBack True for back sprite, false for front sprite
     * @return BufferedImage of the Pokémon sprite
     */
    public static BufferedImage getPokemonSprite(String name, boolean isBack) { // Récupère le sprite d'un Pokémon
        String folder = isBack ? "com/esiea/pootp/resources/pokemon/back/" : "com/esiea/pootp/resources/pokemon/front/";
        return loadImage(folder + name.toLowerCase() + ".png");
    }

    /**
     * Charge une animation depuis un JSON et un PNG
     * @param jsonPath Chemin vers le fichier JSON
     * @param imagePath Chemin vers le fichier PNG
     * @return Animation chargée
     */
    public static Animation loadAnimation(String jsonPath, String imagePath) {
        BufferedImage spriteSheet = loadImage(imagePath);
        if (spriteSheet == null) return null;

        File jsonFile = new File(jsonPath);
        if (!jsonFile.exists()) {
            System.err.println("JSON introuvable : " + jsonPath);
            return null;
        }

        List<FrameData> frameList = new ArrayList<>();

        try {
            String content = new String(Files.readAllBytes(jsonFile.toPath()));
            
            // Regex pour TexturePacker
            Pattern pattern = Pattern.compile("\"filename\":\\s*\"(.*?)\".*?\"frame\":\\s*\\{\\s*\"x\":\\s*(\\d+),\\s*\"y\":\\s*(\\d+),\\s*\"w\":\\s*(\\d+),\\s*\"h\":\\s*(\\d+)", Pattern.DOTALL);
            Matcher matcher = pattern.matcher(content);

            while (matcher.find()) {
                String name = matcher.group(1);
                int x = Integer.parseInt(matcher.group(2));
                int y = Integer.parseInt(matcher.group(3));
                int w = Integer.parseInt(matcher.group(4));
                int h = Integer.parseInt(matcher.group(5));

                if (w > 0 && h > 0) {
                    BufferedImage frameImg = spriteSheet.getSubimage(x, y, w, h);
                    frameList.add(new FrameData(name, frameImg));
                }
            }

            frameList.sort(Comparator.comparing(f -> f.name));

            List<BufferedImage> finalFrames = new ArrayList<>();
            for (FrameData fd : frameList) {
                finalFrames.add(fd.img);
            }

            return new Animation(finalFrames, 10); 

        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }


    /**
     * Récupère une frame spécifique depuis un atlas TexturePacker
     * @param jsonPath Chemin vers le fichier JSON
     * @param imagePath Chemin vers le fichier PNG
     * @param frameName Nom de la frame à récupérer
     * @return BufferedImage de la frame demandée
     */
    public static BufferedImage getFrameFromAtlas(String jsonPath, String imagePath, String frameName) {
        BufferedImage spriteSheet = loadImage(imagePath);
        if (spriteSheet == null) return null;

        java.io.File jsonFile = new java.io.File(jsonPath);
        if (!jsonFile.exists()) {
            // Fallback pour essayer de charger depuis le classpath si le fichier direct échoue
            java.net.URL url = SpriteManager.class.getClassLoader().getResource(jsonPath);
            if (url == null) return null;
            jsonFile = new java.io.File(url.getFile());
        }

        try {
            String content = new String(java.nio.file.Files.readAllBytes(jsonFile.toPath()));
            
            // Regex pour trouver les coordonnées de la frame demandée dans le JSON TexturePacker
            String quoteName = java.util.regex.Pattern.quote(frameName);
            java.util.regex.Pattern pattern = java.util.regex.Pattern.compile("\"filename\":\\s*\"" + quoteName + "\".*?\"frame\":\\s*\\{\\s*\"x\":\\s*(\\d+),\\s*\"y\":\\s*(\\d+),\\s*\"w\":\\s*(\\d+),\\s*\"h\":\\s*(\\d+)", java.util.regex.Pattern.DOTALL);
            java.util.regex.Matcher matcher = pattern.matcher(content);

            if (matcher.find()) {
                int x = Integer.parseInt(matcher.group(1));
                int y = Integer.parseInt(matcher.group(2));
                int w = Integer.parseInt(matcher.group(3));
                int h = Integer.parseInt(matcher.group(4));
                
                return spriteSheet.getSubimage(x, y, w, h);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
    
    // Helper pour les icônes
    /**
     * Get the icon of a Pokémon
     * @param name Name of the Pokémon
     * @return BufferedImage of the Pokémon icon
     */
    public static BufferedImage getPokemonIcon(String name) {
        return loadImage("com/esiea/pootp/resources/icons/" + name.toLowerCase() + ".png");
    }

    /**
     * Helper class to store frame data
     */
    private static class FrameData {
        String name;
        BufferedImage img;
        public FrameData(String name, BufferedImage img) { this.name = name; this.img = img; }
    }
}