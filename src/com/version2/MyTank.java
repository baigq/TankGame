package com.version2;

import java.util.Vector;

/**
 * 玩家坦克类
 */
public class MyTank extends Tank {
    // 最大发射子弹数
    private int maxBulletNum = 5;
    public bullet b = null;

    Vector<bullet> bullets = new Vector<>();// 发射多颗子弹用

    public MyTank(int x, int y) {
        super(x, y);
    }

    /**
     * 子弹发射函数
     */
    public void shot(){
        if(bullets.size() < maxBulletNum){
            // 判断方向
            switch (getDirection()){
            case up:
                b = new bullet(getX() + 20,getY(),Direction.up);
                break;
            case down:
                b = new bullet(getX() + 20,getY() + 60,Direction.down);
                break;
            case left:
                b = new bullet(getX(),getY() + 20,Direction.left);
                break;
            case right:
                b = new bullet(getX() + 60,getY() + 20,Direction.right);
                break;
        }

            //bullets.add(b);// 可发射多颗子弹

            // 开启子弹线程
            new Thread(b).start();
        }
    }
}
