package codegenerator2;

import codegenerator.Main;
import com.google.gson.Gson;
import junit.framework.Assert;
import org.junit.Test;

import java.math.BigInteger;

public class CodeGenerator2Test {

    boolean equalsArrays(int[][] a, int[][] b){
        int equalsCount = 0;
        for (int i = 0; i < a.length; i++)
            for (int j = 0; j < a[0].length; j++)
                if (a[i][j] == b[i][j])
                    equalsCount++;
        return equalsCount == a.length * a[0].length;
    }

    @Test
    public void getCodeTest(){
        CodeGenerator2 generator = new CodeGenerator2(1);
        BigInteger index = generator.maxIndexInFunction.subtract(BigInteger.ONE);
        int[][] code = generator.getCode(index);
        Assert.assertEquals(1, code[0][0]);
        Assert.assertEquals(0, code[0][1]);
        Assert.assertEquals(8, code[0][2]);
        Assert.assertEquals(4, code[0][3]);
        Assert.assertEquals(4, code[0][4]);
        Assert.assertEquals(0, code[0][5]);

        BigInteger index2 = generator.getIndex(code);
        Assert.assertEquals(index, index2);
    }

    @Test
    public void getCode2Test(){
        CodeGenerator2 generator = new CodeGenerator2(2);
        int[][] code = generator.getCode(generator.maxIndexInFunction.subtract(BigInteger.ONE));
        Assert.assertEquals(1, code[0][0]);
        Assert.assertEquals(1, code[0][1]);
        Assert.assertEquals(8, code[0][2]);
        Assert.assertEquals(5, code[0][3]);
        Assert.assertEquals(5, code[0][4]);
        Assert.assertEquals(1, code[0][5]);

        Assert.assertEquals(1, code[1][0]);
        Assert.assertEquals(1, code[1][1]);
        Assert.assertEquals(8, code[1][2]);
        Assert.assertEquals(5, code[1][3]);
        Assert.assertEquals(5, code[1][4]);
        Assert.assertEquals(1, code[1][5]);
    }


    public static double calculatePiInTableView() {
        // {0.0, 1.0, 6.0, 0.5};
        double res = 0.0 + 0.0;
        double i = 1.0 + 0.0;
        double dx = 1.0 + 0.0;
        do {
            i = i + 1.0;
            res = res + dx;
            double sqrI = i * i;
            dx = 1.0 / sqrI;
        } while (i < Math.pow(6.0, 6.0));
        double resMul6 = res * 6.0;
        return Math.pow(resMul6, 0.5);
    }

    int[][] calculatePIInTableView = {
            {0, 0, 0, 0, 0, 0},
            {0, 0, 0, 1, 0, 0},
            {0, 0, 0, 1, 0, 0},
            {0, 2, 0, 5, 1, 0},
            {0, 1, 0, 4, 6, 0},
            {0, 0, 2, 5, 5, 0},
            {0, 3, 3, 1, 9, 0},
            {0, 0, 4, 2, 2, 0},
            {0, 0, 8, 5, 11, 4},
            {0, 0, 2, 4, 2, 0},
            {1, 0, 4, 13, 3, 0}
    };

    @Test
    public void calculatePiInTableViewTest(){
        Assert.assertTrue(new CodeGenerator2(1).testPiValue(calculatePiInTableView()));
    }

    @Test
    public void getIndexAndGetCodeTest(){
        CodeGenerator2 generator = new CodeGenerator2(calculatePIInTableView.length);
        BigInteger index = generator.getIndex(calculatePIInTableView);
        int[][] code = generator.getCode(index);
        Assert.assertTrue(equalsArrays(calculatePIInTableView, code));
    }

    @Test
    public void runCodeTest(){
        CodeGenerator2 generator = new CodeGenerator2(calculatePIInTableView.length);
        Double functionResult = generator.runCode(calculatePIInTableView, BigInteger.valueOf(1000000));
        Assert.assertTrue(generator.testPiValue(functionResult));
    }
}
