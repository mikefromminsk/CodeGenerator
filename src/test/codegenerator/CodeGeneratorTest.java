package codegenerator;

import junit.framework.Assert;
import org.junit.Test;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

public class CodeGeneratorTest {
    @Test
    public void getCodeLineCountTest() {
        Assert.assertEquals(0, CodeGenerator.getCodeLineCount(BigInteger.valueOf(0)));
        Assert.assertEquals(1, CodeGenerator.getCodeLineCount(BigInteger.valueOf(8)));
        Assert.assertEquals(2, CodeGenerator.getCodeLineCount(BigInteger.valueOf(9)));
        Assert.assertEquals(2, CodeGenerator.getCodeLineCount(BigInteger.valueOf(64+8)));
        Assert.assertEquals(3, CodeGenerator.getCodeLineCount(BigInteger.valueOf(64+8+1)));
        Assert.assertEquals(3, CodeGenerator.getCodeLineCount(BigInteger.valueOf(512+64+8)));
        Assert.assertEquals(4, CodeGenerator.getCodeLineCount(BigInteger.valueOf(512+64+8+1)));
    }

    @Test
    public void setFuncBodyTest(){
        List<CodeLine> codeLines = new ArrayList<CodeLine>();
        CodeGenerator.setFunctions(BigInteger.valueOf(9), codeLines);
        Assert.assertEquals(2, codeLines.size());

        CodeGenerator.setFunctions(BigInteger.valueOf(8), codeLines);
        Assert.assertEquals(1, codeLines.size());
    }

    /*@Test
    public void setFuncBodyTest2(){
        List<CodeLine> codeLines = new ArrayList<CodeLine>();
        CodeGenerator.setFunctions(BigInteger.valueOf(0), codeLines);
        Assert.assertEquals(0, codeLines.get(0).func);
         //Наверно из за getCodeLineCount
    }*/

    @Test
    public void maxValueIndexTest(){
        List<CodeLine> codeLines = new ArrayList<CodeLine>();
//        Assert.assertEquals(BigInteger.valueOf(0), CodeGenerator.maxValueIndex(codeLines));
        codeLines.add(new CodeLine());
        Assert.assertEquals(BigInteger.valueOf(1), CodeGenerator.maxValueIndex(codeLines));
        codeLines.add(new CodeLine());
        Assert.assertEquals(BigInteger.valueOf(2), CodeGenerator.maxValueIndex(codeLines));
        codeLines.add(new CodeLine());
        Assert.assertEquals(BigInteger.valueOf(6), CodeGenerator.maxValueIndex(codeLines));
        codeLines.add(new CodeLine());
        Assert.assertEquals(BigInteger.valueOf(24), CodeGenerator.maxValueIndex(codeLines));
    }

    @Test
    public void setValuesTest(){
        List<CodeLine> codeLines = new ArrayList<CodeLine>();
        CodeGenerator.setFunctions(BigInteger.valueOf(1000), codeLines);
        CodeGenerator.setValues(BigInteger.valueOf(0), codeLines);
        Assert.assertEquals(-1, codeLines.get(0).value);
        Assert.assertEquals(-1, codeLines.get(1).value);
        Assert.assertEquals(-1, codeLines.get(2).value);
        Assert.assertEquals(-1, codeLines.get(3).value);
        CodeGenerator.setValues(BigInteger.valueOf(1), codeLines);
        Assert.assertEquals(-1, codeLines.get(0).value);
        Assert.assertEquals(0, codeLines.get(1).value);
        Assert.assertEquals(-1, codeLines.get(2).value);
        Assert.assertEquals(-1, codeLines.get(3).value);
        CodeGenerator.setValues(BigInteger.valueOf(5), codeLines);
        Assert.assertEquals(-1, codeLines.get(0).value);
        Assert.assertEquals(0, codeLines.get(1).value);
        Assert.assertEquals(1, codeLines.get(2).value);
        Assert.assertEquals(-1, codeLines.get(3).value);
        CodeGenerator.setValues(BigInteger.valueOf(6), codeLines);
        Assert.assertEquals(-1, codeLines.get(0).value);
        Assert.assertEquals(-1, codeLines.get(1).value);
        Assert.assertEquals(-1, codeLines.get(2).value);
        Assert.assertEquals(0, codeLines.get(3).value);
        CodeGenerator.setValues(BigInteger.valueOf(7), codeLines);
        Assert.assertEquals(-1, codeLines.get(0).value);
        Assert.assertEquals(0, codeLines.get(1).value);
        Assert.assertEquals(-1, codeLines.get(2).value);
        Assert.assertEquals(0, codeLines.get(3).value);
    }

    @Test
    public void maxParamIndexTest(){
        List<CodeLine> codeLines = new ArrayList<CodeLine>();
        if (CodeGenerator.inputVars.length == 3){
            CodeGenerator.setFunctions(BigInteger.valueOf(1), codeLines);
            Assert.assertEquals(BigInteger.valueOf(4*4), CodeGenerator.maxParamIndex(codeLines));
            CodeGenerator.setFunctions(BigInteger.valueOf(9), codeLines);
            Assert.assertEquals(BigInteger.valueOf(4*4*5*5), CodeGenerator.maxParamIndex(codeLines));
            //??
        }
    }
}
