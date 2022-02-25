package com.version2;

import java.io.*;
import java.util.Vector;

/**
 * 信息记录类
 */
public class Recorder {
    private static int score = 0;

    private static BufferedWriter bw = null;
    private static BufferedReader br = null;
    /**
     * 存档路径
     */
    private static String path = "src/myRecord.txt";

    private static Vector<BotTank> bots = null;

    private static Vector<Node> nodes = new Vector<>();

    public static String getPath() {
        return path;
    }

    public static void setScore(int score) {
        Recorder.score = score;
    }

    public static void setBots(Vector<BotTank> bots) {
        Recorder.bots = bots;
    }

    public static int getScore() {
        return score;
    }

    public static void addScore(){
        score++;
    }

    /**
     * 存档方法
     */
    public static void save(){
        try {
            bw = new BufferedWriter(new FileWriter(path));
            bw.write(score + "\r\n");

            for(int i = 0; i < bots.size();i++){
                BotTank bot = bots.get(i);
                if(bot.isLive) {
                    String record = bot.getX() + " " + bot.getY() + " " + bot.getDirection();
                    bw.write(record + "\r\n");
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            if(bw != null) {
                try {
                    bw.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * 读档
     * @return 返回存档点
     */
    public static Vector<Node> recovery(){
        try {
            br = new BufferedReader(new FileReader(path));
            score = Integer.parseInt(br.readLine());
            String line = "";
            while((line = br.readLine())!=null){
                String[] xyd = line.split(" ");
                Node node = new Node(Integer.parseInt(xyd[0]),Integer.parseInt(xyd[1]),Direction.valueOf(xyd[2]));
                nodes.add(node);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            if(br!=null) {
                try {
                    br.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return nodes;
    }
}
