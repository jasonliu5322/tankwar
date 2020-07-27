package tankwar;

import org.junit.jupiter.api.Test;
import org.opentest4j.AssertionFailedError;

import javax.swing.plaf.SeparatorUI;

import java.awt.*;

import static org.junit.jupiter.api.Assertions.*;

class TankTest {

    @Test
    void getImage() {

        for(Direction direction : Direction.values()){
            Tank tank = new Tank(0, 0, false, direction);
            assertTrue(tank.getImage().getWidth(null) > 0, direction + "cannot get valid image!");

            Tank enemyTank = new Tank(0, 0, true, direction);
            Image image = enemyTank.getImage();
            assertTrue(image.getWidth(null) > 0, direction + "cannot get valid image!");
        }
    }
}