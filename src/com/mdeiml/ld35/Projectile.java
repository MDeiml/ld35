package com.mdeiml.ld35;

public class Projectile extends Wall {

    private int vx;
    private int vy;

    public Projectile(int x, int y, int w, int h, int vx, int vy) {
        super(x,y,w,h);
        this.vx = vx;
        this.vy = vy;
    }

    public void tick() {
        x += vx;
        y += vy;
    }
}
