package codegenerator;

import junit.framework.Assert;
import org.junit.Test;

import java.io.File;

public class SyncronizatorTest {

    @Test
    public void generateBlockTest(){
        // 2120567688 function index of pi function
        // 2120567688 / blockSize - блок в котором находится одна из реализаций алгоритма числа PI
        //Syncronizator syncronizator = new Syncronizator();
        Long piFunctionIndex = 2120567688L;
        Integer blockSize = 1;
        Syncronizator syncronizator = new Syncronizator(1, blockSize, piFunctionIndex / blockSize - 1L, piFunctionIndex / blockSize, null);
        syncronizator.run();
        System.out.println(syncronizator.json.toJson(syncronizator.data));
        Assert.assertNotNull(syncronizator.data.blocks.get(piFunctionIndex));
    }
}
