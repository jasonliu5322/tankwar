package tankwar;

import java.awt.*;

class Explosion {
    private int x;
    private int y;
    private int step = 0;
    private boolean live = true;

    public Explosion(int x, int y) {
        this.x = x;
        this.y = y;
    }

    boolean isLive() {
        return live;
    }

    void setLive(boolean live) {
        this.live = live;
    }

    void draw(Graphics g){
        if (step > 10) {
            this.setLive(false);
            return;
        }
        g.drawImage(Tools.getImage(step++ + ".gif"), x, y, null);
    }
}
