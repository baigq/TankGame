package com.version2;

import java.util.Vector;

/**
 * BOT坦克类
 */
public class BotTank extends Tank implements Runnable{
    private int minBulletNum = 1;
    private bullet b = null;
    // 子弹集合
    // 子弹与坦克分离，防止子弹跟着坦克消失而消失
    public Vector<bullet> myBullets = null;
    // 记录其他BOT信息,判断重叠用
    public Vector<BotTank> bots = new Vector<>();

    public void setBotBullets(Vector<bullet> botBullets) {
        this.myBullets = botBullets;
    }

    public void setBots(Vector<BotTank> bots) {
        this.bots = bots;
    }

    public BotTank(int x, int y) {
        super(x, y);
    }

    /**
     * 坦克运行线程
     */
    @Override
    public void run() {
        while(true){
            // 发射子弹，且数目维持在一定水平
            if(isLive && myBullets.size() < minBulletNum){

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

                // 开启子弹线程
                myBullets.add(b);
                new Thread(b).start();
            }

            // 先沿原方向走
            switch (getDirection()){
                case up:
                    for(int i = 0;i < 30;i++) {
                        if(getY() > 0 && !isTouch())
                            moveUp();

                        try {
                            Thread.sleep(50);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                    break;
                case down:
                    for(int i = 0;i < 30;i++) {
                        if(getY() + 60 < 600 && !isTouch())
                            moveDown();

                        try {
                            Thread.sleep(50);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                    break;
                case left:
                    for(int i = 0;i < 30;i++) {
                        if(getX() > 0 && !isTouch())
                            moveLeft();

                        try {
                            Thread.sleep(50);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                    break;
                case right:
                    for(int i = 0;i < 30;i++) {
                        if(getX() + 60 < 800 && !isTouch())
                            moveRight();

                        try {
                            Thread.sleep(50);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                    break;
            }

            // 再随机改变方向
            setDirection(Direction.values()[(int)(Math.random() * 4)]);

            // 牢记关闭线程
            if(!isLive)
                break;
        }
    }

    /**
     * 判断坦克是否重叠
     * @return 坦克是否重叠
     */
    public boolean isTouch(){
        switch (getDirection()){
            case up:
                for (int i = 0; i < bots.size(); i++) {
                    BotTank bot = bots.get(i);
                    if (bot != this) {

                        //如果敌人坦克是 上/下
                        if (bot.getDirection() == Direction.up || bot.getDirection() == Direction.down) {
                            // 当前坦克左上角
                            if (this.getX() >= bot.getX()
                                    && this.getX() <= bot.getX() + 40
                                    && this.getY() >= bot.getY()
                                    && this.getY() <= bot.getY() + 60) {
                                return true;
                            }
                            // 当前坦克右上角
                            if (this.getX() + 40 >= bot.getX()
                                    && this.getX() + 40 <= bot.getX() + 40
                                    && this.getY() >= bot.getY()
                                    && this.getY() <= bot.getY() + 60) {
                                return true;
                            }
                        }

                        //如果敌人坦克是 右/左
                        if (bot.getDirection() == Direction.left || bot.getDirection() == Direction.right) {
                            // 当前坦克 左上角
                            if (this.getX() >= bot.getX()
                                    && this.getX() <= bot.getX() + 60
                                    && this.getY() >= bot.getY()
                                    && this.getY() <= bot.getY() + 40) {
                                return true;
                            }
                            // 当前坦克 右上角
                            if (this.getX() + 40 >= bot.getX()
                                    && this.getX() + 40 <= bot.getX() + 60
                                    && this.getY() >= bot.getY()
                                    && this.getY() <= bot.getY() + 40) {
                                return true;
                            }
                        }
                    }
                }
                break;
            case down:
                for (int i = 0; i < bots.size(); i++) {
                    BotTank bot = bots.get(i);
                    if (bot != this) {

                        //如果敌人坦克是 上/下
                        if (bot.getDirection() == Direction.up || bot.getDirection() == Direction.down) {
                            // 当前坦克左下角
                            if (this.getX() >= bot.getX()
                                    && this.getX() <= bot.getX() + 40
                                    && this.getY() + 60 >= bot.getY()
                                    && this.getY() + 60 <= bot.getY() + 60) {
                                return true;
                            }
                            // 当前坦克右下角
                            if (this.getX() + 40 >= bot.getX()
                                    && this.getX() + 40 <= bot.getX() + 40
                                    && this.getY() + 60 >= bot.getY()
                                    && this.getY() + 60 <= bot.getY() + 60) {
                                return true;
                            }
                        }

                        //如果敌人坦克是 右/左
                        if (bot.getDirection() == Direction.left || bot.getDirection() == Direction.right) {
                            // 当前坦克 左下角
                            if (this.getX() >= bot.getX()
                                    && this.getX() <= bot.getX() + 60
                                    && this.getY() + 60 >= bot.getY()
                                    && this.getY() + 60 <= bot.getY() + 40) {
                                return true;
                            }
                            // 当前坦克 右下角
                            if (this.getX() + 40 >= bot.getX()
                                    && this.getX() + 40 <= bot.getX() + 60
                                    && this.getY() + 60 >= bot.getY()
                                    && this.getY() + 60 <= bot.getY() + 40) {
                                return true;
                            }
                        }
                    }
                }
                break;
            case left:
                for (int i = 0; i < bots.size(); i++) {
                    BotTank bot = bots.get(i);
                    if (bot != this) {

                        //如果敌人坦克是 上/下
                        if (bot.getDirection() == Direction.up || bot.getDirection() == Direction.down) {
                            // 当前坦克左上角
                            if (this.getX() >= bot.getX()
                                    && this.getX() <= bot.getX() + 40
                                    && this.getY() >= bot.getY()
                                    && this.getY() <= bot.getY() + 60) {
                                return true;
                            }
                            // 当前坦克左下角
                            if (this.getX() >= bot.getX()
                                    && this.getX() <= bot.getX() + 40
                                    && this.getY() + 40 >= bot.getY()
                                    && this.getY() + 40 <= bot.getY() + 60) {
                                return true;
                            }
                        }

                        //如果敌人坦克是 右/左
                        if (bot.getDirection() == Direction.left || bot.getDirection() == Direction.right) {
                            // 当前坦克 左上角
                            if (this.getX() >= bot.getX()
                                    && this.getX() <= bot.getX() + 60
                                    && this.getY() >= bot.getY()
                                    && this.getY() <= bot.getY() + 40) {
                                return true;
                            }
                            // 当前坦克 左下角
                            if (this.getX() >= bot.getX()
                                    && this.getX() <= bot.getX() + 60
                                    && this.getY() + 40 >= bot.getY()
                                    && this.getY() + 40 <= bot.getY() + 40) {
                                return true;
                            }
                        }
                    }
                }
                break;
            case right:
                for (int i = 0; i < bots.size(); i++) {
                    BotTank bot = bots.get(i);
                    if (bot != this) {

                        //如果敌人坦克是 上/下
                        if (bot.getDirection() == Direction.up || bot.getDirection() == Direction.down) {
                            // 当前坦克右上角
                            if (this.getX() + 60 >= bot.getX()
                                    && this.getX() + 60 <= bot.getX() + 40
                                    && this.getY() >= bot.getY()
                                    && this.getY() <= bot.getY() + 60) {
                                return true;
                            }
                            // 当前坦克右下角
                            if (this.getX() + 60 >= bot.getX()
                                    && this.getX() + 60 <= bot.getX() + 40
                                    && this.getY() + 40 >= bot.getY()
                                    && this.getY() + 40 <= bot.getY() + 60) {
                                return true;
                            }
                        }

                        //如果敌人坦克是 右/左
                        if (bot.getDirection() == Direction.left || bot.getDirection() == Direction.right) {
                            // 当前坦克 右上角
                            if (this.getX() + 60 >= bot.getX()
                                    && this.getX() + 60 <= bot.getX() + 60
                                    && this.getY() >= bot.getY()
                                    && this.getY() <= bot.getY() + 40) {
                                return true;
                            }
                            // 当前坦克 右下角
                            if (this.getX() + 60 >= bot.getX()
                                    && this.getX() + 60 <= bot.getX() + 60
                                    && this.getY() + 40 >= bot.getY()
                                    && this.getY() + 40 <= bot.getY() + 40) {
                                return true;
                            }
                        }
                    }
                }
                break;
        }
        return false;
    }
}
