package tankwar;


import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class GameClient extends JComponent {

    private static final GameClient INSTANCE = new GameClient();

    public static GameClient getInstance(){
        return INSTANCE;
    }

    private Tank playerTank;
    private List<Tank> enemyTanks;
    private List<Wall> wall;
    private List<Missile> missiles;
    private List<Explosion> explosions;

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

    public GameClient(){
        this.playerTank = new Tank(400, 100, Direction.DOWN);
        this.missiles = new ArrayList<>();
        this.explosions = new ArrayList<>();
        this.wall = Arrays.asList(
                new Wall(200, 140, true, 15),
                new Wall(200, 540, true, 15),
                new Wall(100, 80, false, 15),
                new Wall(700, 80, false, 15)
        );
        initEnemyTanks();
        this.setPreferredSize(new Dimension(800, 600));
    }

    private void initEnemyTanks() {
        this.enemyTanks = new ArrayList<>(12);
        for(int i = 0; i < 3; i ++){
            for (int j = 0; j < 4 ; j++) {
                this.enemyTanks.add(new Tank(200 + 120 * j, 400 + 40 * i, true, Direction.UP ));
            }
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        g.setColor(Color.BLACK);
        g.fillRect(0, 0, 800, 600);
        playerTank.draw(g);

        enemyTanks.removeIf(t -> !t.isLive());
        for(Tank tank: enemyTanks){
            tank.draw(g);
        }
        for(Wall walls: wall){
            walls.draw(g);
        }
        missiles.removeIf(m -> !m.isLive());
        if(enemyTanks.isEmpty()){
            this.initEnemyTanks();
        }
        for(Missile missile: missiles){
            missile.draw(g);
        }
        explosions.removeIf(e -> !e.isLive());
        for(Explosion explosion: explosions){
            explosion.draw(g);
        }
    }

    public static void main(String[] args) {
        com.sun.javafx.application.PlatformImpl.startup(()->{});
        JFrame frame =  new JFrame();
        frame.setTitle("New Tankwar");
        frame.setIconImage(new ImageIcon("assets/images/icon.png").getImage());
        final GameClient client = GameClient.getInstance();
        frame.add(client);
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

        while(true){
            client.repaint();
            try{
                Thread.sleep(50);
            }catch(InterruptedException e){
                e.printStackTrace();
            }
        }
    }


}
