package com.version2;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.File;
import java.io.IOException;
import java.util.Vector;

// 方向枚举
enum Direction {
    up,down,left,right
}

/**
 * 主线程
 * 为了实现重绘子弹，JPanel需要实现Runnable
 */
public class Ground extends JPanel implements KeyListener,Runnable {
    /**
     * 玩家坦克
     */
    private MyTank me = null;
    /**
     * BOT坦克
     */
    private Vector<BotTank> bots = new Vector<>();
    /**
     * BOT数量
     */
    private int botNum = 5;
    /**
     * 子弹集合
     */
    private Vector<Vector<bullet>> bullets = new Vector<>();
    /**
     * 爆炸点
     */
    private Vector<Bomb> bombs = new Vector<>();
    /**
     * 存档
     */
    private Vector<Node> nodes = null;

    /**
     * 爆炸渲染素材
     */
    Image img1 = null;
    Image img2 = null;
    Image img3 = null;

    /**
     * 主线程初始化
     * @param mode 功能选择
     */
    public Ground(String mode){
        // 文件存档读取
        File f = new File(Recorder.getPath());
        if(f.exists()) {
            nodes = Recorder.recovery();
        }else {
            // 处理无文件异常
            System.out.println("Error: No Save Files");
            mode = "0";
        }

        // 玩家坦克初始化
        me = new MyTank(150,150);
        me.setSpeed(5);

        // 用于记录坦克位置信息，便于存档
        Recorder.setBots(bots);

        switch (mode){
            case "0":
                for(int i = 0;i < botNum;i++){
                    // 重置比分
                    Recorder.setScore(0);

                    // BOT初始化
                    BotTank bot = new BotTank(100 * (i + 1), 50);
                    bot.setDirection(Direction.down);
                    // 每个BOT记录其他BOT位置,以判断重叠
                    bot.setBots(bots);
                    // BOT子弹初始化
                    Vector<bullet> myBullets = new Vector<>();
                    bot.setBotBullets(myBullets);
                    // BOT线程启动
                    new Thread(bot).start();

                    // 加入全局
                    bullets.add(myBullets);
                    bots.add(bot);
                }
                break;

            case "1":
                for(int i = 0;i < nodes.size();i++){
                    // 读取存档节点
                    Node node = nodes.get(i);

                    // 初始化BOT
                    BotTank bot = new BotTank(node.getX(),node.getY());
                    bot.setDirection(node.getD());
                    bot.setBots(bots);
                    Vector<bullet> myBullets = new Vector<>();
                    bot.setBotBullets(myBullets);
                    new Thread(bot).start();

                    // 加入全局
                    bullets.add(myBullets);
                    bots.add(bot);
                }
                break;

            default:
                System.out.println("Invalid Input!");
        }

        // 爆炸素材导入
        try {
            img1 = ImageIO.read(new File("src/bomb_1.gif"));
            img2 = ImageIO.read(new File("src/bomb_2.gif"));
            img3 = ImageIO.read(new File("src/bomb_3.gif"));
        } catch (IOException e) {
            e.printStackTrace();
        }

        // new AePlayWave("src\\bgm.wav").start();
    }

    /**
     * 主线程方法
     */
    @Override
    public void run() {
        while(true){
            try {
                Thread.sleep(50);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            // BOT击中判断
            if(me.b != null && me.b.isLive)
                for(int i = 0;i < bots.size();i++)
                    hit(me.b,bots.get(i));

            //isHit_mul();
            // 玩家击中判断
            isHitMe();

            // 重绘
            this.repaint();
        }
    }

    /**
     * 【绘制】（总） - 主方法
     * @param g JPanel画笔
     * 实现了Runnable接口后，此处被重复调用（repaint最终就是调用paint）
     */
    @Override
    public void paint(Graphics g){
        super.paint(g);
        // 背景
        g.fillRect(0,0,800,600);
        // 得分信息
        showInfo(g);

        // 绘制玩家坦克
        if(me.isLive)
            drawTank(me.getX(),me.getY(),me.getDirection(),0,g);

        // 绘制玩家子弹
        if(me.b != null && me.b.isLive){
            g.setColor(Color.WHITE);
            g.draw3DRect(me.b.getX(),me.b.getY(),2,2,false);
            //在没有实现Runnable接口时，paint方法只运行一次，子弹也就只绘制一次 (但子弹确实在移动)
        }

        // 绘制多颗玩家子弹
/*      for(int i = 0;i < me.bullets.size();i++){
            bullet b = me.bullets.get(i);
            if(b != null && b.isLive)
                g.fill3DRect(b.getX(),b.getY(),2,2,false);
            else
                me.bullets.remove(b);
        }
*/

        // 绘制爆炸效果
        for(int i = 0;i < bombs.size();i++){
            Bomb bomb = bombs.get(i);
            // 模拟动态爆炸效果
            if(bomb.life > 8)
                g.drawImage(img1,bomb.x,bomb.y,60,60,this);
            else if(bomb.life > 4)
                g.drawImage(img2,bomb.x,bomb.y,60,60,this);
            else
                g.drawImage(img3,bomb.x,bomb.y,60,60,this);

            bomb.explode();

            if(!bomb.isLive)
                bombs.remove(bomb);
        }

        // 绘制敌方坦克
        for(int i = 0;i < bots.size();i++){
            BotTank bot = bots.get(i);

            if(bot.isLive) {
                drawTank(bot.getX(), bot.getY(), bot.getDirection(), 1, g);
            }
        }

        // 绘制BOT子弹【独立于BotTank类】
        // System.out.println(bots.size() + "-----" + bullets.get(0).size());
        for(int i = 0;i < bullets.size();i++) {
            Vector<bullet> myBullets = bullets.get(i);

            for (int j = 0; j < myBullets.size(); j++) {
                bullet b = myBullets.get(j);
                // 判断子弹是否存活
                if (b.isLive) {
                    g.setColor(Color.WHITE);
                    g.draw3DRect(b.getX(), b.getY(), 2, 2, false);
                }
                else
                    myBullets.remove(b);
            }
        }
    }

    /**
     * 【绘制】静止坦克
     * @param x 坦克横坐标
     * @param y 坦克纵坐标
     * @param direction 坦克朝向
     * @param type 坦克类型(玩家 / BOT)
     * @param g 画笔
     */
    public void drawTank(int x,int y,Direction direction,int type,Graphics g){
        // 敌我区分
        switch (type){
            case 0:
                g.setColor(Color.CYAN);
                break;
            case 1:
                g.setColor(Color.GREEN);
                break;
        }

        // 方向区分
        switch (direction){
            case up:
                g.fill3DRect(x,y,10,60,false);
                g.fill3DRect(x + 30,y,10,60,false);
                g.fill3DRect(x + 10,y + 10,20,40,false);
                g.fillOval(x + 10,y + 20,20,20);
                g.drawLine(x + 20,y + 30,x + 20,y);
                break;
            case down:
                g.fill3DRect(x,y,10,60,false);
                g.fill3DRect(x + 30,y,10,60,false);
                g.fill3DRect(x + 10,y + 10,20,40,false);
                g.fillOval(x + 10,y + 20,20,20);
                g.drawLine(x + 20,y + 30,x + 20,y + 60);
                break;
            case left:
                g.fill3DRect(x,y,60,10,false);
                g.fill3DRect(x,y + 30,60,10,false);
                g.fill3DRect(x + 10,y + 10,40,20,false);
                g.fillOval(x + 20,y + 10,20,20);
                g.drawLine(x + 30,y + 20,x,y + 20);
                break;
            case right:
                g.fill3DRect(x,y,60,10,false);
                g.fill3DRect(x,y + 30,60,10,false);
                g.fill3DRect(x + 10,y + 10,40,20,false);
                g.fillOval(x + 20,y + 10,20,20);
                g.drawLine(x + 30,y + 20,x + 60,y + 20);
                break;
        }
    }

    @Override
    public void keyTyped(KeyEvent e) {

    }

    /**
     * 监听按键按下
     * @param e 按键事件
     */
    @Override
    public void keyPressed(KeyEvent e) {
        switch (e.getExtendedKeyCode()){
            case KeyEvent.VK_W:
                me.setDirection(Direction.up);
                if(me.getY() > 0)
                    me.moveUp();
                break;
            case KeyEvent.VK_S:
                me.setDirection(Direction.down);
                if(me.getY() + 60 < 600)
                    me.moveDown();
                break;
            case KeyEvent.VK_A:
                me.setDirection(Direction.left);
                if(me.getX() > 0)
                    me.moveLeft();
                break;
            case KeyEvent.VK_D:
                me.setDirection(Direction.right);
                if(me.getX() + 60 < 800)
                    me.moveRight();
                break;
            case KeyEvent.VK_J:
                // 每次只能打一发
                if(me.b == null || !me.b.isLive)
                    me.shot();

                break;
        }

        //每次按键后都会重绘，营造动态效果
        this.repaint();
    }

    @Override
    public void keyReleased(KeyEvent e) {

    }

    /**
     * 子弹击中BOT
     * @param b 子弹
     * @param bot BOT坦克
     * 此处 bot 不更名 因为要将就 【bots】 无法改名
     */
    public void hit(bullet b,Tank bot){
        switch(bot.getDirection()){
            case up:
            case down:
                if(b.getX() > bot.getX() && b.getX() < bot.getX() + 40 &&
                        b.getY() > bot.getY() && b.getY() < bot.getY() + 60){
                    // 击中物体的子弹 及时销毁
                    b.isLive = false;
                    bot.isLive = false;

                    bots.remove(bot);

                    if(bot instanceof BotTank)
                        Recorder.addScore();

                    Bomb bomb = new Bomb(bot.getX(),bot.getY());
                    bombs.add(bomb);
                }
                break;
            case left:
            case right:
                if(b.getX() > bot.getX() && b.getX() < bot.getX() + 60 &&
                        b.getY() > bot.getY() && b.getY() < bot.getY() + 40){
                    // 击中物体的子弹 及时销毁
                    b.isLive = false;
                    bot.isLive = false;

                    bots.remove(bot);

                    if(bot instanceof BotTank)
                        Recorder.addScore();

                    Bomb bomb = new Bomb(bot.getX(),bot.getY());
                    bombs.add(bomb);
                }
                break;
        }
    }

    // 【玩家多个子弹】情况判断用
    public void isHit_mul(){
        for(int i = 0;i < me.bullets.size();i++){
            bullet b = me.bullets.get(i);
            if(b != null && b.isLive)
                for(int j = 0;j < bots.size();j++){
                    hit(b,bots.get(j));
                }
        }
    }

    /**
     * 玩家被击中判断
     */
    public void isHitMe(){
        for(int i = 0;i < bullets.size();i++){
            Vector<bullet> myBullets = bullets.get(i);
            for(int j = 0;j < myBullets.size();j++){
                bullet b = myBullets.get(j);
                if(me.isLive && b.isLive)
                    hit(b,me);
            }
        }
    }

    /**
     * 绘制提示信息
     * @param g 画笔
     */
    public void showInfo(Graphics g){
        g.setColor(Color.BLACK);
        Font font = new Font("宋体",Font.BOLD,25);
        g.setFont(font);

        g.drawString("Score : ",820,30);
        drawTank(820,60,Direction.up,1,g);
        g.setColor(Color.BLACK);
        g.drawString(Recorder.getScore() + "",880,100);
    }
}
