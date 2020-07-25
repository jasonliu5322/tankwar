package tankwar;


import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.List;

public class GameClient extends JComponent {

    private Tank playerTank;
    private List<Tank> enemyTanks;


    public GameClient(){
        this.playerTank = new Tank(400, 100, Direction.DOWN);
        this.enemyTanks = new ArrayList<>(12);
        for(int i = 0; i < 3; i ++){
            for (int j = 0; j < 4 ; j++) {
                this.enemyTanks.add(new Tank(200 + 50 * j, 400 + 40 * i, true, Direction.UP ));
            }
        }

        this.setPreferredSize(new Dimension(800, 600));
    }

    @Override
    protected void paintComponent(Graphics g) {
        playerTank.draw(g);
        for(Tank tank: enemyTanks){
            tank.draw(g);
        }
    }

    public static void main(String[] args) {
        JFrame frame =  new JFrame();
        frame.setTitle("New Tankwar");
        frame.setIconImage(new ImageIcon("assets/images/icon.png").getImage());
        final GameClient client = new GameClient();
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
