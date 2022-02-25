package com.version2;

/**
 * 爆炸点类
 */
public class Bomb {
    int x,y;
    int life = 12;
    boolean isLive = true;

    public Bomb(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public void explode(){
        if(life > 0)
            life--;
        else
            isLive = false;
    }
}
