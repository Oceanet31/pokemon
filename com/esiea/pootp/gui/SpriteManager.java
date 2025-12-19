package com.esiea.pootp.gui;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class SpriteManager {
    
    private static Map<String, BufferedImage> cache = new HashMap<>();

    public static BufferedImage loadImage(String path) {
        if (cache.containsKey(path)) {
            return cache.get(path);
        }

        try {// chargement de l'image depuis le fichier
           
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

    //on charge les sprites des monstres
    public static BufferedImage getPokemonSprite(String name, boolean isBack) {
        String folder = isBack ? "pokemon/back/" : "pokemon/front/";
        return loadImage(folder + name.toLowerCase() + ".png");
    }
}