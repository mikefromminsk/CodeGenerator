package codegenerator;
import org.junit.Test;

import static junit.framework.Assert.assertEquals;


public class MainTest {
    @Test
    public void testMultiply() {
    }
    @Test
    public void testMultiplsy() {
        Main main = new Main();
        assertEquals("пи должно быть 3.14", 3.14, 3.14);
    }

    @Test(timeout = 1000)
    public void runForever() throws InterruptedException {
        Thread.sleep(1000);
    }
}
