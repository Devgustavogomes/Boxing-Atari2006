package br.com.mvbos.lgj.boxing;

import java.awt.Canvas;

import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.image.BufferStrategy;

import javax.swing.JFrame;
import javax.swing.JOptionPane;

public class Main {

    public static void main(String[] args) {
        JFrame frame = new JFrame("Boxing");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        
        int width = 640;
        int height = 480;
        
        Canvas canvas = new Canvas();
        canvas.setPreferredSize(new Dimension(width, height));
        frame.add(canvas);
        frame.pack();
        frame.setResizable(false);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
        
        canvas.createBufferStrategy(2);
        BufferStrategy bs = canvas.getBufferStrategy();
        
        BoxingGame game = new BoxingGame(width, height);
        RankingManager rankingManager = new RankingManager();
        game.setRankingManager(rankingManager);
        game.carregar();
        
        canvas.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                game.teclaPressionada(e.getKeyCode());
                
                if (game.isGameOver() && e.getKeyCode() == KeyEvent.VK_ENTER) {
                    String name = JOptionPane.showInputDialog(frame, "Game Over! Digite seu nome:");
                    if (name != null && !name.trim().isEmpty()) {
                        rankingManager.addScore(name, game.getPlayerScore());
                        game.setShowRanking(true);
                    }
                }
            }

            @Override
            public void keyReleased(KeyEvent e) {
                game.teclaSolta(e.getKeyCode());
            }
        });
        
        canvas.requestFocus();

        long lastTime = System.nanoTime();
        double amountOfTicks = 60.0;
        double ns = 1000000000 / amountOfTicks;
        double delta = 0;
        
        while (true) {
            long now = System.nanoTime();
            delta += (now - lastTime) / ns;
            lastTime = now;
            
            while (delta >= 1) {
                game.atualizar();
                delta--;
            }
            
            Graphics2D g = (Graphics2D) bs.getDrawGraphics();
            g.clearRect(0, 0, width, height);
            
            game.desenhar(g);
            
            g.dispose();
            bs.show();
            
            try {
                Thread.sleep(1);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
