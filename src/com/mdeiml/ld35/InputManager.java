package com.mdeiml.ld35;

import java.awt.event.*;

public class InputManager implements MouseListener, MouseMotionListener {

    private static final int NUM_BUTTONS = 3;

    private boolean[] buttons;
    private boolean[] nextButtons;
    private boolean[] lastButtons;

    private int mouseX, mouseY;
    private int nextMouseX, nextMouseY;

    public void mouseClicked(MouseEvent e) {}
    public void mouseEntered(MouseEvent e) {}
    public void mouseExited(MouseEvent e) {}

    public InputManager() {
        buttons = new boolean[NUM_BUTTONS];
        nextButtons = new boolean[NUM_BUTTONS];
        lastButtons = new boolean[NUM_BUTTONS];
        for(int i = 0; i < NUM_BUTTONS; i++) {
            buttons[i] = false;
            nextButtons[i] = false;
            lastButtons[i] = false;
        }
        mouseX = 0;
        mouseY = 0;
        nextMouseX = 0;
        nextMouseY = 0;
    }

    public synchronized void mousePressed(MouseEvent e) {
        if(e.getButton() > 0 && e.getButton() <= NUM_BUTTONS) {
            nextButtons[e.getButton()-1] = true;
        }
        nextMouseX = e.getX();
        nextMouseY = e.getY();
    }

    public synchronized void mouseReleased(MouseEvent e) {
        if(e.getButton() > 0 && e.getButton() <= NUM_BUTTONS) {
            nextButtons[e.getButton()-1] = false;
        }
        nextMouseX = e.getX();
        nextMouseY = e.getY();
    }

    public synchronized void mouseMoved(MouseEvent e) {
        nextMouseX = e.getX();
        nextMouseY = e.getY();
    }

    public synchronized void mouseDragged(MouseEvent e) {
        nextMouseX = e.getX();
        nextMouseY = e.getY();
    }

    public boolean button(int i) {
        return buttons[i];
    }

    public boolean buttonPressed(int i) {
        return buttons[i] && !lastButtons[i];
    }

    public boolean buttonReleased(int i) {
        return lastButtons[i] && !buttons[i];
    }

    public int mouseX() {
        return mouseX;
    }

    public int mouseY() {
        return mouseY;
    }

    public synchronized void tick() {
        for(int i = 0; i < NUM_BUTTONS; i++) {
            lastButtons[i] = buttons[i];
            buttons[i] = nextButtons[i];
        }
        mouseX = nextMouseX / Game.SCREEN_SCALE;
        mouseY = nextMouseY / Game.SCREEN_SCALE;
    }

}
