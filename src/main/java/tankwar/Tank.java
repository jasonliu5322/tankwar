package tankwar;


import java.awt.*;
import java.awt.event.KeyEvent;
import java.util.Random;

public class Tank {
    Save.Position getPosition(){
        return new Save.Position(x, y, direction);
    }

    private static final int SPEED = 5;
    private int x;
    private int y;
    private boolean live = true;
    private static final int MAX_HP = 100;
    private int hp = MAX_HP;
    private final boolean enemy;
    private Direction direction;

    public Tank(int x, int y, Direction direction) {
        this(x, y, false, direction);
    }

    Tank(Save.Position position, boolean enemy){
        this(position.getX(), position.getY(), enemy, position.getDirection());
    }

    public Tank(int x, int y, boolean enemy, Direction direction) {
        this.x = x;
        this.y = y;
        this.enemy = enemy;
        this.direction = direction;
    }

    public int getHp() {
        return hp;
    }

    public void setHp(int hp) {
        this.hp = hp;
    }

    boolean isLive() {
        return live;
    }

    void setLive(boolean live) {
        this.live = live;
    }

    boolean isEnemy() {
        return enemy;
    }

    void move(){
        if(stopped){
            return;
        }
        x += direction.xFactor * SPEED;
        y += direction.yFactor * SPEED;

    }

    Image getImage(){
        String prefix = enemy? "e" : "";
        return direction.getImage(prefix + "tank");

    }

    boolean isDying(){
        return this.hp <= MAX_HP * 0.2;
    }

    void draw(Graphics g){
        int oldX = x, oldY = y;
        if(!this.enemy) {
            this.determineDirection();
        }
        this.move();
        if(x < 0){
             x = 0;
        }else if(x > 800 - this.getImage().getWidth(null)){
            x = 800 - this.getImage().getWidth(null);
        }
        if(y < 0){
            y = 0;
        }else if(y > 600 - this.getImage().getHeight(null)){
            y = 600 - this.getImage().getHeight(null);
        }

        Rectangle rec = this.getRectangle();
        for(Wall wall: GameClient.getInstance().getWall()){
            if(rec.intersects(wall.getRectangle())){
                x = oldX;
                y = oldY;
                break;
            }
        }
        for(Tank tank : GameClient.getInstance().getEnemyTanks()){
            if(tank != this && rec.intersects(tank.getRectangle())){
                x = oldX;
                y = oldY;
                break;
            }
        }
        if(this.enemy && rec.intersects(GameClient
                .getInstance().getPlayerTank().getRectangle())){
            x = oldX;
            y = oldY;
        }

        if(!enemy){
            Blood blood = GameClient.getInstance().getBlood();
            if(blood.isLive() && rec.intersects(blood.getRectangle())){
                    this.hp = MAX_HP;
                    Tools.playAudio("revive.wav");
                    blood.setLive(false);
            }
            g.setColor(Color.WHITE);
            g.fillRect(x, y - 10, this.getImage().getWidth(null), 10);
            g.setColor(Color.RED);
            int width = hp * this.getImage().getWidth(null) / MAX_HP;
            g.fillRect(x, y - 10, width, 10);

            Image petImage = Tools.getImage("pet-camel.gif");
            g.drawImage(petImage, this.x - petImage.getWidth(null) - DISTANCE_TO_PET, this.y,null);
        }

        g.drawImage(this.getImage(), this.x, this.y, null);
    }

    private static final int DISTANCE_TO_PET = 4;
    public Rectangle getRectangle(){
        if(enemy) {
            return new Rectangle(x, y, getImage().getWidth(null), getImage().getHeight(null));
        }else{
            Image petImage = Tools.getImage("pet-camel.gif");
            int delta = petImage.getWidth(null) + DISTANCE_TO_PET;
            return new Rectangle(x - delta, y, getImage().getWidth(null) + delta, getImage().getHeight(null));
            }
        }
    public Rectangle getRectangleForHitDetection(){
        return new Rectangle(x, y, getImage().getWidth(null), getImage().getHeight(null));
    }

    public void keyPressed(KeyEvent e) {
        switch (e.getKeyCode()) {
            case KeyEvent.VK_UP:
                code |= Direction.UP.code;//https://www.w3schools.com/java/java_operators.asp
                break;          // |(OR) - Sets each bit to 1 if any of the two bits is 1
            case KeyEvent.VK_DOWN:
                code |= Direction.DOWN.code;
                break;
            case KeyEvent.VK_LEFT:
                code |= Direction.LEFT.code;
                break;
            case KeyEvent.VK_RIGHT:
                code |= Direction.RIGHT.code;
                break;
            case KeyEvent.VK_SPACE:
                fire();
                break;
            case KeyEvent.VK_A:
                superFire();
                break;
            case KeyEvent.VK_F2:
                GameClient.getInstance().restart();
                break;
        }
    }

    private void superFire() {
        for(Direction direction : Direction.values()) {
            Missile missile = new Missile(x + getImage().getWidth(null) / 2 - 6,
                    y + getImage().getHeight(null) / 2 - 6, enemy, direction);
            GameClient.getInstance().getMissiles().add(missile);
        }

        String audioFile = new Random().nextBoolean()?"supershoot.aiff":"supershoot.wav";
        Tools.playAudio(audioFile);
    }

    private void fire() {
        Missile missile = new Missile(x + getImage().getWidth(null)/2 - 6,
                y + getImage().getHeight(null)/2 - 6, enemy, direction);
        GameClient.getInstance().getMissiles().add(missile);
        Tools.playAudio("shoot.wav");
    }

    private boolean stopped;
    private int code;
    private void determineDirection() {
        Direction newDirection = Direction.get(code);
        if(newDirection == null){
            this.stopped = true;
        }else{
            this.direction = newDirection;
            this.stopped = false;
        }
    }

    public void keyReleased(KeyEvent e) {
        switch (e.getKeyCode()) {
            case KeyEvent.VK_UP:
                code ^= Direction.UP.code; //Java Bitwise Operators
                break;                     //^(XOR) - Sets each bit to 1 if only one of the two bits is 1
            case KeyEvent.VK_DOWN:
                code ^= Direction.DOWN.code;
                break;
            case KeyEvent.VK_LEFT:
                code ^= Direction.LEFT.code;
                break;
            case KeyEvent.VK_RIGHT:
                code ^= Direction.RIGHT.code;
                break;
        }
    }
    private final Random random = new Random();
    private int step = random.nextInt(12) + 3;
    void actRandomly() {
        Direction[] dirs = Direction.values();
        if(step == 0){
            step = random.nextInt(12) + 3;
            this.direction = dirs[random.nextInt(dirs.length)];
            if(random.nextBoolean()){
                this.fire();
            }
        }
        step--;
    }
}
