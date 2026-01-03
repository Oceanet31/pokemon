package com.esiea.pootp.gui;

import java.awt.image.BufferedImage;
import java.util.List;

public class Animation {
    private List<BufferedImage> frames;
    private int currentFrameIndex;
    private long lastFrameTime;

    /** Temps en ms entre chaque image */
    private long frameDelay; 

    /**
     * Constructor for Animation
     * @param frames List of frames for the animation
     * @param fps Frames per second
     */
    public Animation(List<BufferedImage> frames, int fps) {
        this.frames = frames;
        this.frameDelay = 1000 / fps;
        this.currentFrameIndex = 0;
        this.lastFrameTime = System.currentTimeMillis();
    }

    /**
     * Update the animation to the next frame
     */
    public void update() { // Met à jour l'animation
        if (frames.isEmpty()) return;

        long currentTime = System.currentTimeMillis();
        if (currentTime - lastFrameTime > frameDelay) {
            currentFrameIndex++;
            if (currentFrameIndex >= frames.size()) {
                currentFrameIndex = 0; // On boucle au début
            }
            lastFrameTime = currentTime;
        }
    }

    /** Get the current sprite 
     * @return current sprite image
    */
    public BufferedImage getSprite() { // Récupère l'image courante
        if (frames.isEmpty()) return null;
        return frames.get(currentFrameIndex);
    }
}