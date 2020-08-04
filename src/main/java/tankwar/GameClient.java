package tankwar;


import com.alibaba.fastjson.JSON;
import org.apache.commons.io.FileUtils;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

public class GameClient extends JComponent {

    private static final GameClient INSTANCE = new GameClient();
    public static final String GAME_SAVE = "game.sav";

    public static GameClient getInstance(){
        return INSTANCE;
    }

    private Tank playerTank;
    private List<Tank> enemyTanks;
    private final AtomicInteger enemyKilled = new AtomicInteger(0);
    private List<Wall> wall;
    private List<Missile> missiles;
    private List<Explosion> explosions;
    private Blood blood;

    void addExplosion(Explosion explosion){
        explosions.add(explosion);
    }
    public void removeMissile(Missile missile) {
        missiles.remove(missile);
    }

    public List<Missile> getMissiles() {
        return missiles;
    }

    public List<Tank> getEnemyTanks() {
        return enemyTanks;
    }

    public List<Wall> getWall() {
        return wall;
    }

    public Tank getPlayerTank() {
        return playerTank;
    }

    public Blood getBlood() {
        return blood;
    }

    public GameClient(){
        this.playerTank = new Tank(400, 100, Direction.DOWN);
        this.missiles = new CopyOnWriteArrayList<>();
        this.explosions = new ArrayList<>();
        this.blood = new Blood(400, 250);
        this.wall = Arrays.asList(
                new Wall(280, 140, true, 12),
                new Wall(280, 540, true, 12),
                new Wall(100, 160, false, 12),
                new Wall(700, 160, false, 12)
        );
        initEnemyTanks();
        this.setPreferredSize(new Dimension(800, 600));
    }

    private void initEnemyTanks() {
        this.enemyTanks = new CopyOnWriteArrayList<>();
        for(int i = 0; i < 3; i ++){
            for (int j = 0; j < 4 ; j++) {
                this.enemyTanks.add(new Tank(200 + 120 * j, 400 + 40 * i, true, Direction.UP ));
            }
        }
    }
    private final static Random random = new Random();
    @Override
    protected void paintComponent(Graphics g) {
        g.setColor(Color.BLACK);
        g.fillRect(0, 0, 800, 600);
        if (!playerTank.isLive()) {
            g.setColor(Color.RED);
            g.setFont(new Font(null, Font.BOLD, 100));
            g.drawString("GAME OVER", 100, 200);
            g.setFont(new Font(null, Font.BOLD, 40));
            g.drawString("PRESS F2 TO RESTART", 170, 360);
        } else {
            g.setColor(Color.WHITE);
            g.setFont(new Font(null, Font.BOLD, 16));
            g.drawString("Missiles: " + missiles.size(), 10, 50);
            g.drawString("Explosions: " + explosions.size(), 10, 70);
            g.drawString("Player Tank HP: " + playerTank.getHp(), 10, 90);
            g.drawString("Enemy Left: " + enemyTanks.size(), 10, 110);
            g.drawString("Enemy Killed: " + enemyKilled.get(), 10, 130);
            g.drawImage(Tools.getImage("tree.png"), 720, 10, null);
            g.drawImage(Tools.getImage("tree.png"), 10, 520, null);

            playerTank.draw(g);

            if(playerTank.isDying() && random.nextInt(50) == 2){
                blood.setLive(true);
            }
            if(blood.isLive()) {
                blood.draw(g);
            }
            int count = enemyTanks.size();
            enemyTanks.removeIf(t -> !t.isLive());
            enemyKilled.addAndGet(count - enemyTanks.size());
            for (Tank tank : enemyTanks) {
                tank.draw(g);
            }
            for (Wall walls : wall) {
                walls.draw(g);
            }
            missiles.removeIf(m -> !m.isLive());
            if (enemyTanks.isEmpty()) {
                this.initEnemyTanks();
            }
            for (Missile missile : missiles) {
                missile.draw(g);
            }
            explosions.removeIf(e -> !e.isLive());
            for (Explosion explosion : explosions) {
                explosion.draw(g);
            }
        }
    }

    public static void main(String[] args) {
        com.sun.javafx.application.PlatformImpl.startup(()->{});
        JFrame frame =  new JFrame();
        frame.setTitle("New Tankwar");
        frame.setIconImage(new ImageIcon("assets/images/icon.png").getImage());
        final GameClient client = GameClient.getInstance();
        frame.add(client);
        frame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                try {
                    client.save();
                    System.exit(0);
                } catch (IOException ioException) {
                    JOptionPane.showMessageDialog(null, "Failed to save current game!",
                            "Oops! Error Occurred", JOptionPane.ERROR_MESSAGE);
                }
                System.exit(4);
            }
        });
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        frame.pack();//make the frame size normal
        frame.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                client.playerTank.keyPressed(e);
            }

            @Override
            public void keyReleased(KeyEvent e) {
                client.playerTank.keyReleased(e);

            }
        });
        frame.setLocationRelativeTo(null);//put the frame in the middle of screen
        frame.setVisible(true);

        try {
            client.load();
        } catch (IOException e) {
            JOptionPane.showMessageDialog(null, "Failed to previous game!",
                    "Oops! Error Occurred", JOptionPane.ERROR_MESSAGE);
        }

        while(true){
            try{
                client.repaint();
                if(client.playerTank.isLive()){
                    for(Tank tank : client.enemyTanks){
                        tank.actRandomly();
                    }
                }
                Thread.sleep(50);
            }catch(Exception e){
                e.printStackTrace();
            }
        }
    }

    private void load() throws IOException {
        File file = new File(GAME_SAVE);
        if(file.exists() && file.isFile()) {
            String json = FileUtils.readFileToString(file, StandardCharsets.UTF_8);
            Save save = JSON.parseObject(json, Save.class);
            if(save.isGameContinued()){
                this.playerTank = new Tank(save.getPlayerPosition(),false);

                this.enemyTanks.clear();
                List<Save.Position> enemyPosition = save.getEnemyPosition();
                if(enemyPosition != null && !enemyPosition.isEmpty()){
                    for(Save.Position position : enemyPosition){
                        this.enemyTanks.add(new Tank(position, true));
                    }
                }

            }
        }
    }

    void save(String destination) throws IOException {
        Save save = new Save(playerTank.isLive(), playerTank.getPosition(),
                enemyTanks.stream().filter(Tank::isLive)
                        .map(Tank::getPosition).collect(Collectors.toList()));
        FileUtils.write(new File(destination), JSON.toJSONString(save, true), StandardCharsets.UTF_8);
    }

    void save() throws IOException {
        this.save(GAME_SAVE);
    }

    public void restart() {
        if(!playerTank.isLive()){
            this.playerTank = new Tank(400, 100, Direction.DOWN);
        }
        this.initEnemyTanks();
    }
}
