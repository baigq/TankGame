package com.version2;

/**
 * 存档点类
 */
public class Node {
    private int x,y;
    private Direction d;

    public Node(int x, int y, Direction d) {
        this.x = x;
        this.y = y;
        this.d = d;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public Direction getD() {
        return d;
    }
}
