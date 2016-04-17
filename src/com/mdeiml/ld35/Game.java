package com.mdeiml.ld35;

import java.awt.*;
import java.awt.event.*;
import java.awt.image.*;
import java.util.ArrayList;

public class Game extends Frame {

    public static final int SCREEN_WIDTH = 200;
    public static final int SCREEN_HEIGHT = 150;
    public static final int SCREEN_SCALE = 4;
    public static final int FPS = 30;
    private static final float BACKTRACK = 1f;

    private boolean running;
    private BufferedImage buffer;
    private InputManager input;
    private int[] swarmXs;
    private int[] swarmYs;
    private int swarmIndex;
    private Graphics bg;
    private ArrayList<Wall> walls;
    private ArrayList<Projectile> projectiles;

    private BufferedImage background, wall, player, projectile;

    private int difficulty;

    public Game() {
        difficulty = 1;

        running = false;
        buffer = new BufferedImage(SCREEN_WIDTH, SCREEN_HEIGHT, BufferedImage.TYPE_INT_RGB);
        bg = buffer.getGraphics();

        background = new BufferedImage(SCREEN_WIDTH,SCREEN_HEIGHT, BufferedImage.TYPE_INT_RGB);
        player = new BufferedImage(SCREEN_WIDTH,SCREEN_HEIGHT, BufferedImage.TYPE_INT_RGB);
        wall = new BufferedImage(SCREEN_WIDTH,SCREEN_HEIGHT, BufferedImage.TYPE_INT_RGB);
        projectile = new BufferedImage(SCREEN_WIDTH,SCREEN_HEIGHT, BufferedImage.TYPE_INT_RGB);
        Graphics g1 = background.getGraphics();
        Graphics g2 = player.getGraphics();
        Graphics g3 = wall.getGraphics();
        Graphics g4 = projectile.getGraphics();
        for(int x = 0; x < SCREEN_WIDTH; x++) {
            for(int y = 0; y < SCREEN_WIDTH; y++) {
                int r1 = (int)(255-Math.random()*20);
                g1.setColor(new Color(r1,r1,r1));
                g1.fillRect(x,y,1,1);
                int r2 = (int)(255-Math.random()*128);
                g2.setColor(new Color(0,0,r2));
                g2.fillRect(x,y,1,1);
                int r3 = (int)(128-Math.random()*20);
                g3.setColor(new Color(r3, r3, r3));
                g3.fillRect(x,y,1,1);
                int r4 = (int)(255-Math.random()*128);
                g4.setColor(new Color(r4, 0, 0));
                g4.fillRect(x, y, 1, 1);
            }
        }

        walls = new ArrayList<Wall>();
        walls.add(new Wall(0,0,200,5));
        walls.add(new Wall(0,0,5,150));
        walls.add(new Wall(0,145,200,5));
        walls.add(new Wall(195,0,5,150));

        projectiles = new ArrayList<Projectile>();

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
        if(Math.random() < 0.03) {
            difficulty++;
            difficulty = Math.min(difficulty, 20);
        }
        if(Math.random() < 0.01 * difficulty) {
            int x,y,vx = 0,vy = 0;
            if(Math.random() > 0.5) {
                boolean r = Math.random() > 0.5;
                x = r ? 0 : SCREEN_WIDTH-3;
                vx = r ? 1 : -1;
                y = (int)(Math.random() * (SCREEN_HEIGHT-3));
            }else {
                boolean r = Math.random() > 0.5;
                y = r ? 0 : SCREEN_HEIGHT-3;
                vy = r ? 1 : -1;
                x = (int)(Math.random() * (SCREEN_WIDTH-3));
            }
            Projectile p = new Projectile(x, y, 3, 3, vx, vy);
            projectiles.add(p);
        }
        if(input.button(0)) {
            int lastSwarmIndex = (swarmIndex - 1 + swarmXs.length) % swarmXs.length;
            int vx = input.mouseX() - swarmXs[lastSwarmIndex];
            int vy = input.mouseY() - swarmYs[lastSwarmIndex];
            double length = Math.sqrt(vx*vx+vy*vy);
            length = Math.max(1, length);
            int[] pos = move(swarmXs[lastSwarmIndex]-1, swarmYs[lastSwarmIndex]-1,3,3,(int)(vx*3/length), (int)(vy*3/length));
            swarmXs[swarmIndex] = pos[0] + 1;
            swarmYs[swarmIndex] = pos[1] + 1;
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
            vx *= 3 / length;
            vy *= 3 / length;
            for(int i = 0; i < swarmXs.length; i++) {
                int[] pos = move(swarmXs[i]-1, swarmYs[i]-1, 3, 3, vx, vy);
                swarmXs[i] = pos[0] + 1;
                swarmYs[i] = pos[1] + 1;
            }
        }
        boolean lost = false;
        for(Projectile p : projectiles) {
            p.tick();
            for(int i = 0; i < swarmXs.length; i++) {
                if(p.intersect(swarmXs[i]-1, swarmYs[i]-1, 3, 3)) {
                    lost = true;
                }
            }
        }
        if(lost) {
            projectiles.clear();
            difficulty = 1;
        }
    }

    public void render() {
        bg.drawImage(background, 0, 0, null);
        bg.setColor(Color.WHITE);
        for(int i = 0; i < swarmXs.length; i++) {
            int x = swarmXs[i], y = swarmYs[i];
            bg.drawImage(player,x-1,y-1,x+2,y+2,x-1,y-1,x+2,y+2,null);
        }
        // bg.drawPolyline(swarmXs, swarmYs, swarmXs.length);
        for(Wall w : walls) {
            w.render(bg, wall);
        }
        for(Projectile p : projectiles) {
            p.render(bg, projectile);
        }
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

    public boolean intersect(int x, int y, int w, int h) {
        for(Wall wa : walls) {
            if(wa.intersect(x,y,w,h)) {
                return true;
            }
        }
        return false;
    }

    public int[] move(int x, int y, int w, int h, int vx, int vy) {
        int x1 = x, y1 = y;
        int xstart = x;
        int total = Math.abs(vx) + Math.abs(vy);
        for(int i = 0; i < total; i++) {
            if(Math.abs(vx) > Math.abs(vy)) {
                x = vx > 0 ? x+1 : x-1;
                vx = vx > 0 ? vx-1 : vx+1;
            }else {
                y = vy > 0 ? y+1 : y-1;
                vy = vy > 0 ? vy-1 : vy+1;
            }
            if(intersect(x,y,w,h)) {
                return new int[] {x1, y1};
            }
            x1 = x;
            y1 = y;
        }
        return new int[] {x, y};
    }

}
