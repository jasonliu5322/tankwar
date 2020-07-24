package tankwar;

import javax.swing.*;
import java.awt.*;

public class GameClient extends JComponent {

    public GameClient(){
        this.setPreferredSize(new Dimension(800, 600));
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        g.drawImage(new ImageIcon("assets/images/tankD.gif").getImage(),400,100,null);
    }

    public static void main(String[] args) {
        JFrame frame =  new JFrame();
        frame.setTitle("New Tankwar");
        frame.setIconImage(new ImageIcon("assets/images/icon.png").getImage());
        GameClient client = new GameClient();
        frame.add(client);
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        frame.pack();//make the frame size normal
        frame.setLocationRelativeTo(null);//put the frame in the middle of screen
        frame.setVisible(true);
    }
}
