package br.com.mvbos.lgj.boxing;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.event.KeyEvent;
import java.util.Random;

import br.com.mvbos.lgj.base.CenarioPadrao;
import br.com.mvbos.lgj.base.Texto;
import br.com.mvbos.lgj.base.Util;

public class BoxingGame extends CenarioPadrao {

    private Player p1;
    private Player cpu;
    private Texto txtScoreP1;
    private Texto txtScoreCpu;
    private Texto txtTime;
    private Texto txtInfo;
    private RankingManager rankingManager;
    private boolean showRanking = false;
    
    private int gameTime = 60 * 60;
    private boolean gameOver = false;
    private String gameResult = "";
    private Random rand = new Random();

    public BoxingGame(int largura, int altura) {
        super(largura, altura);
    }

    @Override
    public void carregar() {
        p1 = new Player(largura / 4, altura / 2);
        p1.setCor(Color.WHITE);
        p1.setDirection(Player.DIR_RIGHT);

        cpu = new Player(3 * largura / 4, altura / 2);
        cpu.setCor(Color.BLACK);
        cpu.setDirection(Player.DIR_LEFT);

        txtScoreP1 = new Texto();
        txtScoreP1.setPx(20);
        txtScoreP1.setPy(30);
        
        txtScoreCpu = new Texto();
        txtScoreCpu.setPx(largura - 100);
        txtScoreCpu.setPy(30);
        
        txtTime = new Texto();
        Util.centraliza(txtTime, largura, 0);
        txtTime.setPy(30);
        
        txtInfo = new Texto();
        Util.centraliza(txtInfo, largura, altura);
    }

    @Override
    public void descarregar() {
        p1 = null;
        cpu = null;
    }

    @Override
    public void atualizar() {
        if (gameOver) return;

        p1.atualiza();
        Util.corrigePosicao(p1, largura, altura);

        updateAI();
        cpu.atualiza();
        Util.corrigePosicao(cpu, largura, altura);

        checkCollisions();

        if (gameTime > 0) {
            gameTime--;
        } else {
            gameOver = true;
            determineWinner("TEMPO ACABOU!");
        }

        if (p1.getHealth() <= 0) {
            gameOver = true;
            determineWinner("KO! VOCÊ PERDEU");
        } else if (cpu.getHealth() <= 0) {
            gameOver = true;
            determineWinner("KO! VOCÊ GANHOU");
        }
    }
    
    private void determineWinner(String reason) {
        if (p1.getScore() > cpu.getScore()) {
            gameResult = reason + " - GANHADOR: P1";
        } else if (cpu.getScore() > p1.getScore()) {
            gameResult = reason + " - GANHADOR: CPU";
        } else {
            gameResult = reason + " - EMPATE";
        }
    }
    
    private void updateAI() {
        int dx = p1.getPx() - cpu.getPx();
        int dy = p1.getPy() - cpu.getPy();

        cpu.setLeft(false);
        cpu.setRight(false);
        cpu.setUp(false);
        cpu.setDown(false);
        cpu.setPunchKey(false);

        if (Math.abs(dx) > 40) {
            if (dx > 0) {
                cpu.setRight(true);
                cpu.setDirection(Player.DIR_RIGHT);
            } else {
                cpu.setLeft(true);
                cpu.setDirection(Player.DIR_LEFT);
            }
        }
        
        if (Math.abs(dy) > 10) {
            if (dy > 0) {
                cpu.setDown(true);
                cpu.setDirection(Player.DIR_DOWN);
            } else {
                cpu.setUp(true);
                cpu.setDirection(Player.DIR_UP);
            }
        }

        if (Math.abs(dx) < 50 && Math.abs(dy) < 30) {
            if (rand.nextInt(100) < 5) {
                if (Math.abs(dx) > Math.abs(dy)) {
                     cpu.setDirection(dx > 0 ? Player.DIR_RIGHT : Player.DIR_LEFT);
                } else {
                     cpu.setDirection(dy > 0 ? Player.DIR_DOWN : Player.DIR_UP);
                }
                cpu.setPunchKey(true);
            }
        }
    }

    private void checkCollisions() {
        if (p1.isPunching() && checkPunchCollision(p1, cpu)) {
             p1.addScore(1);
             cpu.takeDamage(1);
             java.awt.Toolkit.getDefaultToolkit().beep();
        }

        if (cpu.isPunching() && checkPunchCollision(cpu, p1)) {
             cpu.addScore(1);
             p1.takeDamage(1);
             java.awt.Toolkit.getDefaultToolkit().beep();
        }
    }

    private boolean checkPunchCollision(Player attacker, Player victim) {
        int reach = 15;
        int punchX = attacker.getPx();
        int punchY = attacker.getPy();
        int punchW = 0;
        int punchH = 0;
        
        switch (attacker.getDirection()) {
            case Player.DIR_RIGHT:
                punchX += attacker.getLargura();
                punchY += 10;
                punchW = reach;
                punchH = 10;
                break;
            case Player.DIR_LEFT:
                punchX -= reach;
                punchY += 10;
                punchW = reach;
                punchH = 10;
                break;
            case Player.DIR_UP:
                punchX += 10;
                punchY -= reach;
                punchW = 10;
                punchH = reach;
                break;
            case Player.DIR_DOWN:
                punchX += 10;
                punchY += attacker.getAltura();
                punchW = 10;
                punchH = reach;
                break;
        }

        int vX = victim.getPx();
        int vY = victim.getPy();
        int vW = victim.getLargura();
        int vH = victim.getAltura();

        return (punchX < vX + vW &&
                punchX + punchW > vX &&
                punchY < vY + vH &&
                punchY + punchH > vY);
    }

    @Override
    public void desenhar(Graphics2D g) {
        g.setColor(new Color(200, 200, 255));
        g.fillRect(0, 0, largura, altura);

        g.setColor(Color.RED);
        g.drawRect(10, 10, largura - 20, altura - 20);

        p1.desenha(g);
        cpu.desenha(g);

        txtScoreP1.desenha(g, "P1: " + p1.getScore());
        txtScoreCpu.desenha(g, "CPU: " + cpu.getScore());
        txtTime.desenha(g, "Tempo: " + (gameTime / 60));
        
        if (gameOver) {
            g.setColor(new Color(0, 0, 0, 150));
            g.fillRect(0, 0, largura, altura);
            
            if (showRanking && rankingManager != null) {
                txtInfo.desenha(g, "TOP 10 RANKING", largura / 2 - 60, 50);
                int y = 80;
                for (PlayerScore ps : rankingManager.getTopScores()) {
                    txtInfo.desenha(g, ps.getName() + " - " + ps.getScore(), largura / 2 - 80, y);
                    y += 30;
                }
                txtInfo.desenha(g, "Press R to Restart", largura / 2 - 70, y + 30);
            } else {
                txtInfo.desenha(g, gameResult, largura / 2 - 150, altura / 2 - 20);
                txtInfo.desenha(g, "Press ENTER to Save Ranking", largura / 2 - 150, altura / 2 + 20);
            }
        }
    }
    
    public void setRankingManager(RankingManager rm) {
        this.rankingManager = rm;
    }
    
    public void setShowRanking(boolean show) {
        this.showRanking = show;
    }
    
    public void restart() {
        p1.reset();
        cpu.reset();
        gameTime = 60 * 60;
        gameOver = false;
        showRanking = false;
        p1.setPx(largura / 4);
        p1.setPy(altura / 2);
        cpu.setPx(3 * largura / 4);
        cpu.setPy(altura / 2);
    }
    
    public void teclaPressionada(int keyCode) {
        if (keyCode == KeyEvent.VK_R && showRanking) {
            restart();
            return;
        }

        if (gameOver) return;
        
        switch (keyCode) {
            case KeyEvent.VK_UP: p1.setUp(true); break;
            case KeyEvent.VK_DOWN: p1.setDown(true); break;
            case KeyEvent.VK_LEFT: p1.setLeft(true); break;
            case KeyEvent.VK_RIGHT: p1.setRight(true); break;

            case KeyEvent.VK_W: 
                p1.setDirection(Player.DIR_UP);
                p1.setPunchKey(true); 
                break;
            case KeyEvent.VK_S: 
                p1.setDirection(Player.DIR_DOWN);
                p1.setPunchKey(true); 
                break;
            case KeyEvent.VK_A: 
                p1.setDirection(Player.DIR_LEFT);
                p1.setPunchKey(true); 
                break;
            case KeyEvent.VK_D: 
                p1.setDirection(Player.DIR_RIGHT);
                p1.setPunchKey(true); 
                break;
        }
    }

    public void teclaSolta(int keyCode) {
        switch (keyCode) {
            case KeyEvent.VK_UP: p1.setUp(false); break;
            case KeyEvent.VK_DOWN: p1.setDown(false); break;
            case KeyEvent.VK_LEFT: p1.setLeft(false); break;
            case KeyEvent.VK_RIGHT: p1.setRight(false); break;
            
            case KeyEvent.VK_W: 
            case KeyEvent.VK_S: 
            case KeyEvent.VK_A: 
            case KeyEvent.VK_D: 
                p1.setPunchKey(false); 
                break;
        }
    }
    
    public boolean isGameOver() {
        return gameOver;
    }
    
    public int getPlayerScore() {
        return p1.getScore();
    }
}
