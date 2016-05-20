package codegenerator;

import junit.framework.Assert;
import org.junit.Test;

import java.math.BigInteger;
import java.util.List;

public class CodeGeneratorTest {

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
        CodeGenerator generator = new CodeGenerator(1);
        BigInteger index = generator.maxIndexInProgram.subtract(BigInteger.ONE);
        int[][] code = generator.getCode(index);
        Assert.assertTrue(equalsArrays(new int[][]{{1, 0, 8, 4, 4, 0}}, code));
    }

    @Test
    public void getCode2Test(){
        CodeGenerator generator = new CodeGenerator(2);
        int[][] code = generator.getCode(generator.maxIndexInProgram.subtract(BigInteger.ONE));
        Assert.assertTrue(equalsArrays(new int[][]{{1,1,8,5,5,1}, {1,1,8,5,5,1}}, code));
    }

    @Test
    public void getIndexTest(){
        CodeGenerator generator = new CodeGenerator(1);
        int[][] code = new int[][]{{1,0,2,3,4,0}};
        BigInteger index = generator.getIndex(code);
        int[][] encode = generator.getCode(index);
        Assert.assertTrue(equalsArrays(code, encode));
    }

    @Test
    public void calculatePiInTableViewTest(){
        Assert.assertTrue(new CodeGenerator(1).testPiValue(CodeGenerator.calculatePiInTableView()));
    }

    @Test
    public void getIndexAndGetCodeTest(){
        CodeGenerator generator = new CodeGenerator(CodeGenerator.calculatePIInTable.length);
        BigInteger index = generator.getIndex(CodeGenerator.calculatePIInTable);
        int[][] code = generator.getCode(index);
        Assert.assertTrue(equalsArrays(CodeGenerator.calculatePIInTable, code));
    }

    @Test
    public void runCodeTest(){
        CodeGenerator generator = new CodeGenerator(CodeGenerator.calculatePIInTable.length);
        Double functionResult = generator.invoke(CodeGenerator.calculatePIInTable, BigInteger.valueOf(1000000));
        Assert.assertTrue(generator.testPiValue(functionResult));
    }

    @Test
    public void generatePiTest(){
        CodeGenerator generator = new CodeGenerator(CodeGenerator.calculatePIInTable.length);
        BigInteger index = generator.getIndex(CodeGenerator.calculatePIInTable);
        List<BigInteger> goodFunctions = generator.generateBlockOfTask(index, BigInteger.valueOf(3), BigInteger.valueOf(1000000));
        boolean findCalculatePIInTableView = false;
        for (int i = 0; i < goodFunctions.size(); i++)
            if (goodFunctions.get(i).compareTo(index) == 0)
                findCalculatePIInTableView = true;
        Assert.assertTrue(findCalculatePIInTableView);
    }
}
