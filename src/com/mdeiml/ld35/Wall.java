package com.mdeiml.ld35;

import java.awt.*;
import java.awt.image.*;

public class Wall {

    public int x;
    public int y;
    public int width;
    public int height;

    public Wall(int x, int y, int w, int h) {
        this.x = x;
        this.y = y;
        this.width = w;
        this.height = h;
    }

    public boolean intersect(Wall w) {
        return intersect(w.x,w.y,w.width,w.height);
    }

    public boolean intersect(int x1, int y1, int w, int h) {
        boolean intersectX = x < x1 + w && x+width > x1;
        boolean intersectY = y < y1 + h && y+height > y1;
        return intersectX && intersectY;
    }

    public void render(Graphics g, BufferedImage layer) {
        g.drawImage(layer,x,y,x+width,y+height,x,y,x+width,y+height,null);
    }

}
