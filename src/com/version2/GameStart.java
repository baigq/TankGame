package com.version2;

import javax.swing.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.Scanner;

/**
 * 主窗口类
 */
public class GameStart extends JFrame {
    private Ground ground = null;

    public GameStart(){
        System.out.println("0: New Game 1: Continue");
        String mode = new Scanner(System.in).next();

        // 游戏主线程
        ground = new Ground(mode);
        //主线程启动
        new Thread(ground).start();
        this.add(ground);

        // 窗口相关设置
        this.setSize(945,640);
        this.setVisible(true);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // 按键监听
        this.addKeyListener(ground);

        // 窗口关闭存档
        this.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                Recorder.save();
                System.exit(0);
            }
        });
    }

    public static void main(String[] args) {
        new GameStart();
    }
}
