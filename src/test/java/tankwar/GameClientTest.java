package tankwar;

import com.alibaba.fastjson.JSON;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import static org.junit.jupiter.api.Assertions.*;

class GameClientTest {

    @Test
    void save() throws IOException {
        String dest = "tmp/game.sav";
        GameClient.getInstance().save(dest);

        byte[] bytes = Files.readAllBytes(Paths.get(dest));
        Save save = JSON.parseObject(bytes, Save.class);
        assertTrue(save.isGameContinued());
        assertEquals(12, save.getEnemyPosition().size());
    }
}