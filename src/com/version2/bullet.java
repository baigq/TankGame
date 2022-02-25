package com.version2;

/**
 * 子弹类
 */
public class bullet implements Runnable{
    // 子弹横坐标
    private int x;
    // 子弹纵坐标
    private int y;
    // 子弹方向
    private Direction d = Direction.up;
    // 子弹速度
    private int speed = 5;
    // 子弹存活标志
    public boolean isLive = true;

    public bullet(int x, int y, Direction d) {
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

    /**
     * 子弹线程函数
     */
    @Override
    public void run() {
        while(true) {
            // 子弹移动
            switch (d) {
                case up:
                    y -= speed;
                    break;
                case down:
                    y += speed;
                    break;
                case left:
                    x -= speed;
                    break;
                case right:
                    x += speed;
                    break;
            }

            // 子弹结束线程
            if (!(x >= 0 && x <= 800 && y >= 0 && y <= 600 && isLive)){
                    isLive = false;
                    break;
            }

            //System.out.println(x + " " + y);

            // 子弹线程休眠，避免过快影响观察
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
