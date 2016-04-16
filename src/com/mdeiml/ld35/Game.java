package com.mdeiml.ld35;

import java.awt.*;
import java.awt.event.*;
import java.awt.image.*;

public class Game extends Frame {

    public static final int SCREEN_WIDTH = 200;
    public static final int SCREEN_HEIGHT = 150;
    public static final int SCREEN_SCALE = 4;
    public static final int FPS = 60;
    private static final float BACKTRACK = 1f;

    private boolean running;
    private BufferedImage buffer;
    private InputManager input;
    private int[] swarmXs;
    private int[] swarmYs;
    private int swarmIndex;
    private Graphics bg;

    public Game() {
        running = false;
        buffer = new BufferedImage(SCREEN_WIDTH, SCREEN_HEIGHT, BufferedImage.TYPE_INT_RGB);
        bg = buffer.getGraphics();

        int swarmSize = (int)(BACKTRACK * FPS);
        swarmXs = new int[swarmSize];
        swarmYs = new int[swarmSize];
        for(int i = 0; i < swarmSize; i++) {
            swarmXs[i] = SCREEN_WIDTH / 2;
            swarmYs[i] = SCREEN_HEIGHT / 2;
        }
        swarmIndex = 0;

        setSize(SCREEN_WIDTH * SCREEN_SCALE, SCREEN_HEIGHT * SCREEN_SCALE);
        setResizable(false);
        setTitle("Ludum Dare 35");
        input = new InputManager();
        addMouseListener(input);
        addMouseMotionListener(input);
        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent ev) {
                setRunning(false);
            }
        });
        setVisible(true);
    }

    public void start() {
        if(isRunning())
            return;
        setRunning(true);
        run();
    }

    public void run() {
        long unprocessed = 0;
        long lastTime = System.nanoTime();
        while(running) {
            boolean render = false;
            long now = System.nanoTime();
            unprocessed += now - lastTime;
            while(unprocessed >= 1000000000/FPS) {
                tick();
                unprocessed -= 1000000000/FPS;
                render = true;
            }
            if(render) {
                render();
            }
            lastTime = now;
        }
    }

    public void tick() {
        input.tick();
        if(input.button(0)) {
            int lastSwarmIndex = (swarmIndex - 1 + swarmXs.length) % swarmXs.length;
            int vx = input.mouseX() - swarmXs[lastSwarmIndex];
            int vy = input.mouseY() - swarmYs[lastSwarmIndex];
            double length = Math.sqrt(vx*vx+vy*vy);
            length = Math.max(1, length);
            swarmXs[swarmIndex] = swarmXs[lastSwarmIndex] + (int)(vx * 3 / length);
            swarmYs[swarmIndex] = swarmYs[lastSwarmIndex] + (int)(vy * 3 / length);
            swarmIndex = (swarmIndex+1) % swarmXs.length;
        }
        if(input.button(2)) {
            int sumX = 0;
            int sumY = 0;
            for(int i = 0; i < swarmXs.length; i++) {
                sumX += swarmXs[i];
                sumY += swarmYs[i];
            }
            sumX /= swarmXs.length;
            sumY /= swarmYs.length;
            int vx = input.mouseX() - sumX;
            int vy = input.mouseY() - sumY;
            double length = Math.sqrt(vx*vx+vy*vy);
            length = Math.max(1, length);
            vx *= 2 / length;
            vy *= 2 / length;
            for(int i = 0; i < swarmXs.length; i++) {
                swarmXs[i] += vx;
                swarmYs[i] += vy;
            }
        }
    }

    public void render() {
        bg.setColor(Color.WHITE);
        bg.drawPolyline(swarmXs, swarmYs, swarmXs.length);
        repaint();
    }

    public void paint(Graphics g) {
        update(g);
    }

    public void update(Graphics g) {
        g.drawImage(buffer, 0, 0, SCREEN_WIDTH * SCREEN_SCALE, SCREEN_HEIGHT * SCREEN_SCALE, 0, 0, SCREEN_WIDTH, SCREEN_HEIGHT, null);
        bg.setColor(Color.BLACK);
        bg.fillRect(0, 0, SCREEN_WIDTH, SCREEN_HEIGHT);
    }

    public synchronized void setRunning(boolean r) {
        running = r;
    }

    public synchronized boolean isRunning() {
        return running;
    }

}
