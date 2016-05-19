package codegenerator;

import junit.framework.Assert;
import org.junit.Test;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

public class CodeGeneratorTest {
    @Test
    public void getCodeLineCountTest() {
        Assert.assertEquals(9, CodeGenerator.funcCount.intValue());
        Assert.assertEquals(0, CodeGenerator.getCodeLineCount(BigInteger.valueOf(0)));
        Assert.assertEquals(1, CodeGenerator.getCodeLineCount(BigInteger.valueOf(9 - 1)));
        Assert.assertEquals(2, CodeGenerator.getCodeLineCount(BigInteger.valueOf(9)));
        Assert.assertEquals(2, CodeGenerator.getCodeLineCount(BigInteger.valueOf(9 * 9 - 1)));
        Assert.assertEquals(3, CodeGenerator.getCodeLineCount(BigInteger.valueOf(9 * 9)));
        Assert.assertEquals(3, CodeGenerator.getCodeLineCount(BigInteger.valueOf(9 * 9 * 9 - 1)));
        Assert.assertEquals(4, CodeGenerator.getCodeLineCount(BigInteger.valueOf(9 * 9 * 9)));
    }

    @Test
    public void setFunctionsTest() {
        List<CodeLine> codeLines = new ArrayList<CodeLine>();
        CodeGenerator.setFunctions(BigInteger.valueOf(9), codeLines);
        Assert.assertEquals(0, codeLines.get(0).func);
        Assert.assertEquals(1, codeLines.get(1).func);
        CodeGenerator.setFunctions(BigInteger.valueOf(80), codeLines);
        Assert.assertEquals(8, codeLines.get(0).func);
        Assert.assertEquals(8, codeLines.get(1).func);
    }

    @Test
    public void maxValueIndexTest() {
        List<CodeLine> codeLines = new ArrayList<CodeLine>();
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
    public void setValuesTest() {
        List<CodeLine> codeLines = new ArrayList<CodeLine>();
        CodeGenerator.setFunctions(BigInteger.valueOf(1000), codeLines);
        CodeGenerator.setValues(BigInteger.valueOf(0), codeLines);
        Assert.assertEquals(0, codeLines.get(0).value);
        Assert.assertEquals(0, codeLines.get(1).value);
        Assert.assertEquals(0, codeLines.get(2).value);
        Assert.assertEquals(0, codeLines.get(3).value);
        CodeGenerator.setValues(BigInteger.valueOf(1), codeLines);
        Assert.assertEquals(0, codeLines.get(0).value);
        Assert.assertEquals(1, codeLines.get(1).value);
        Assert.assertEquals(0, codeLines.get(2).value);
        Assert.assertEquals(0, codeLines.get(3).value);
        CodeGenerator.setValues(BigInteger.valueOf(5), codeLines);
        Assert.assertEquals(0, codeLines.get(0).value);
        Assert.assertEquals(1, codeLines.get(1).value);
        Assert.assertEquals(2, codeLines.get(2).value);
        Assert.assertEquals(0, codeLines.get(3).value);
        CodeGenerator.setValues(BigInteger.valueOf(6), codeLines);
        Assert.assertEquals(0, codeLines.get(0).value);
        Assert.assertEquals(0, codeLines.get(1).value);
        Assert.assertEquals(0, codeLines.get(2).value);
        Assert.assertEquals(1, codeLines.get(3).value);
        CodeGenerator.setValues(BigInteger.valueOf(7), codeLines);
        Assert.assertEquals(0, codeLines.get(0).value);
        Assert.assertEquals(1, codeLines.get(1).value);
        Assert.assertEquals(0, codeLines.get(2).value);
        Assert.assertEquals(1, codeLines.get(3).value);
    }

    @Test
    public void maxParamIndexTest() {
        Assert.assertEquals(4, CodeGenerator.inputVars.length);
        List<CodeLine> codeLines = new ArrayList<CodeLine>();
        CodeGenerator.setFunctions(BigInteger.valueOf(1), codeLines);
        Assert.assertEquals(5 * 5, CodeGenerator.maxParamIndex(codeLines).intValue());
        CodeGenerator.setFunctions(BigInteger.valueOf(9), codeLines);
        Assert.assertEquals(5 * 5 * 6 * 6, CodeGenerator.maxParamIndex(codeLines).intValue());
    }

    @Test
    public void setParamsTest() {
        Assert.assertEquals(4, CodeGenerator.inputVars.length);
        List<CodeLine> codeLines = new ArrayList<CodeLine>();
        CodeGenerator.setFunctions(BigInteger.valueOf(1000), codeLines);
        CodeGenerator.setParams(BigInteger.valueOf(0), codeLines);
        Assert.assertEquals(0, codeLines.get(0).par1);
        Assert.assertEquals(0, codeLines.get(0).par2);
        Assert.assertEquals(0, codeLines.get(1).par1);
        Assert.assertEquals(0, codeLines.get(1).par2);
        CodeGenerator.setParams(BigInteger.valueOf(1), codeLines);
        Assert.assertEquals(1, codeLines.get(0).par1);
        Assert.assertEquals(0, codeLines.get(0).par2);
        Assert.assertEquals(0, codeLines.get(1).par1);
        Assert.assertEquals(0, codeLines.get(1).par2);

        /*
        CodeGenerator.setParams(BigInteger.valueOf(15), codeLines);
        Assert.assertEquals(3, codeLines.get(0).par1);
        Assert.assertEquals(3, codeLines.get(0).par2);
        Assert.assertEquals(0, codeLines.get(1).par1);
        Assert.assertEquals(0, codeLines.get(1).par2);
        CodeGenerator.setParams(BigInteger.valueOf(16), codeLines);
        Assert.assertEquals(0, codeLines.get(0).par1);
        Assert.assertEquals(0, codeLines.get(0).par2);
        Assert.assertEquals(1, codeLines.get(1).par1);
        Assert.assertEquals(0, codeLines.get(1).par2);*/

    }


    @Test
    public void maxJumpIndexTest() {
        Assert.assertEquals(9, CodeGenerator.funcCount.intValue());
        List<CodeLine> codeLines = new ArrayList<CodeLine>();
        CodeGenerator.setFunctions(BigInteger.valueOf(2000), codeLines);
        Assert.assertEquals(2, codeLines.get(0).func);
        Assert.assertEquals(6, codeLines.get(1).func);
        Assert.assertEquals(6, codeLines.get(2).func);
        Assert.assertEquals(2, codeLines.get(3).func);
        Assert.assertEquals(9, CodeGenerator.maxJumpIndex(codeLines).intValue());
    }


    @Test
    public void setJumpsTest() {
        Assert.assertEquals(9, CodeGenerator.funcCount.intValue());
        List<CodeLine> codeLines = new ArrayList<CodeLine>();
        CodeGenerator.setFunctions(BigInteger.valueOf(2000), codeLines);
        Assert.assertEquals(2, codeLines.get(0).func);
        Assert.assertEquals(6, codeLines.get(1).func);
        Assert.assertEquals(6, codeLines.get(2).func);
        Assert.assertEquals(2, codeLines.get(3).func);
        CodeGenerator.setJumps(BigInteger.valueOf(0), codeLines);
        Assert.assertEquals(0, codeLines.get(1).jump);
        Assert.assertEquals(0, codeLines.get(2).jump);
        CodeGenerator.setJumps(BigInteger.valueOf(1), codeLines);
        Assert.assertEquals(2, codeLines.get(1).jump);
        Assert.assertEquals(0, codeLines.get(2).jump);
        CodeGenerator.setJumps(BigInteger.valueOf(2), codeLines);
        Assert.assertEquals(3, codeLines.get(1).jump);
        Assert.assertEquals(0, codeLines.get(2).jump);
        CodeGenerator.setJumps(BigInteger.valueOf(3), codeLines);
        Assert.assertEquals(0, codeLines.get(1).jump);
        Assert.assertEquals(1, codeLines.get(2).jump);
    }

    @Test
    public void generatePiFunctionTest() {

        //Восстановим данную функцию
        /*
    static double piResult = 0; // Можно убрать
    public static void calculatePI() {
        double res = 0.0 + 0.0;
        double i = 1.0 + 0.0;
        double dx = 1.0 + 0.0;
        do {
            i = i + 1.0;
            res = res + dx;
            double sqrI = i * i;
            dx = 1.0 / sqrI;
            double resMul6 = res * 6.0;
            piResult = Math.pow(resMul6, 0.5);
    } while (i < 1000000.0 / 6.0); // while (1.0 == 1.0)
    }
    + - * / pow == != > <

         */
        Assert.assertEquals(9, CodeGenerator.funcCount.intValue());
        Assert.assertEquals(0.5, CodeGenerator.inputVars[0]);
        Assert.assertEquals(0.0, CodeGenerator.inputVars[1]);
        Assert.assertEquals(1.0, CodeGenerator.inputVars[2]);
        Assert.assertEquals(6.0, CodeGenerator.inputVars[3]);

        FunctionID functionID = new FunctionID();
        functionID.funcIndex = BigInteger.ZERO;
        functionID.funcIndex = functionID.funcIndex.add(BigInteger.valueOf(0).multiply(CodeGenerator.funcCount.pow(0)));
        functionID.funcIndex = functionID.funcIndex.add(BigInteger.valueOf(0).multiply(CodeGenerator.funcCount.pow(1)));
        functionID.funcIndex = functionID.funcIndex.add(BigInteger.valueOf(0).multiply(CodeGenerator.funcCount.pow(2)));
        functionID.funcIndex = functionID.funcIndex.add(BigInteger.valueOf(0).multiply(CodeGenerator.funcCount.pow(3)));
        functionID.funcIndex = functionID.funcIndex.add(BigInteger.valueOf(0).multiply(CodeGenerator.funcCount.pow(4)));
        functionID.funcIndex = functionID.funcIndex.add(BigInteger.valueOf(2).multiply(CodeGenerator.funcCount.pow(5)));
        functionID.funcIndex = functionID.funcIndex.add(BigInteger.valueOf(3).multiply(CodeGenerator.funcCount.pow(6)));
        functionID.funcIndex = functionID.funcIndex.add(BigInteger.valueOf(2).multiply(CodeGenerator.funcCount.pow(7)));
        functionID.funcIndex = functionID.funcIndex.add(BigInteger.valueOf(4).multiply(CodeGenerator.funcCount.pow(8)));
        functionID.funcIndex = functionID.funcIndex.add(BigInteger.valueOf(5).multiply(CodeGenerator.funcCount.pow(9)));

        List<CodeLine> codeLines = new ArrayList<CodeLine>();
        CodeGenerator.setFunctions(functionID.funcIndex, codeLines);
        Assert.assertEquals(0, codeLines.get(0).func);
        Assert.assertEquals(0, codeLines.get(1).func);
        Assert.assertEquals(0, codeLines.get(2).func);
        Assert.assertEquals(0, codeLines.get(3).func);
        Assert.assertEquals(0, codeLines.get(4).func);
        Assert.assertEquals(2, codeLines.get(5).func);
        Assert.assertEquals(3, codeLines.get(6).func);
        Assert.assertEquals(2, codeLines.get(7).func);
        Assert.assertEquals(4, codeLines.get(8).func);
        Assert.assertEquals(5, codeLines.get(9).func);

        functionID.valueIndex = BigInteger.ZERO;
        functionID.valueIndex = functionID.valueIndex.add(BigInteger.valueOf(0).multiply(BigInteger.valueOf(1)));
        functionID.valueIndex = functionID.valueIndex.add(BigInteger.valueOf(0).multiply(BigInteger.valueOf(1)));
        functionID.valueIndex = functionID.valueIndex.add(BigInteger.valueOf(0).multiply(BigInteger.valueOf(1 * 2)));
        functionID.valueIndex = functionID.valueIndex.add(BigInteger.valueOf(2).multiply(BigInteger.valueOf(1 * 2 * 3)));
        functionID.valueIndex = functionID.valueIndex.add(BigInteger.valueOf(1).multiply(BigInteger.valueOf(1 * 2 * 3 * 4)));
        functionID.valueIndex = functionID.valueIndex.add(BigInteger.valueOf(0).multiply(BigInteger.valueOf(1 * 2 * 3 * 4 * 5)));
        functionID.valueIndex = functionID.valueIndex.add(BigInteger.valueOf(3).multiply(BigInteger.valueOf(1 * 2 * 3 * 4 * 5 * 6)));
        functionID.valueIndex = functionID.valueIndex.add(BigInteger.valueOf(0).multiply(BigInteger.valueOf(1 * 2 * 3 * 4 * 5 * 6 * 7)));
        functionID.valueIndex = functionID.valueIndex.add(BigInteger.valueOf(0).multiply(BigInteger.valueOf(1 * 2 * 3 * 4 * 5 * 6 * 7 * 8)));
        functionID.valueIndex = functionID.valueIndex.add(BigInteger.valueOf(0).multiply(BigInteger.valueOf(1 * 2 * 3 * 4 * 5 * 6 * 7 * 8 * 9)));

        CodeGenerator.setValues(functionID.valueIndex, codeLines);
        Assert.assertEquals(0, codeLines.get(0).value);
        Assert.assertEquals(0, codeLines.get(1).value);
        Assert.assertEquals(0, codeLines.get(2).value);
        Assert.assertEquals(2, codeLines.get(3).value);
        Assert.assertEquals(1, codeLines.get(4).value);
        Assert.assertEquals(0, codeLines.get(5).value);
        Assert.assertEquals(3, codeLines.get(6).value);
        Assert.assertEquals(0, codeLines.get(7).value);
        Assert.assertEquals(0, codeLines.get(8).value);
        Assert.assertEquals(0, codeLines.get(9).value);

        functionID.paramsIndex = BigInteger.ZERO;
        functionID.paramsIndex = functionID.paramsIndex.add(BigInteger.valueOf(((1 + 1 * 5) * 1)));
        functionID.paramsIndex = functionID.paramsIndex.add(BigInteger.valueOf(((2 + 1 * 6) * 5 * 5)));
        functionID.paramsIndex = functionID.paramsIndex.add(BigInteger.valueOf(((2 + 1 * 7) * 5 * 5 * 6 * 6)));
        functionID.paramsIndex = functionID.paramsIndex.add(BigInteger.valueOf(((5 + 2 * 8) * 5 * 5 * 6 * 6 * 7 * 7)));
        functionID.paramsIndex = functionID.paramsIndex.add(BigInteger.valueOf(((4 + 6 * 9) * 5 * 5 * 6 * 6 * 7 * 7 * 8 * 8)));
        functionID.paramsIndex = functionID.paramsIndex.add(BigInteger.valueOf(5 + 5 * 10).multiply(BigInteger.valueOf(5 * 5 * 6 * 6 * 7 * 7)).multiply(BigInteger.valueOf(8 * 8 * 9 * 9)));
        functionID.paramsIndex = functionID.paramsIndex.add(BigInteger.valueOf(2 + 9 * 11).multiply(BigInteger.valueOf(5 * 5 * 6 * 6 * 7 * 7)).multiply(BigInteger.valueOf(8 * 8 * 9 * 9 * 10 * 10)));
        functionID.paramsIndex = functionID.paramsIndex.add(BigInteger.valueOf(4 + 3 * 12).multiply(BigInteger.valueOf(5 * 5 * 6 * 6 * 7 * 7)).multiply(BigInteger.valueOf(8 * 8 * 9 * 9 * 10 * 10)).multiply(BigInteger.valueOf(11 * 11)));
        functionID.paramsIndex = functionID.paramsIndex.add(BigInteger.valueOf(11 + 0 * 13).multiply(BigInteger.valueOf(5 * 5 * 6 * 6 * 7 * 7)).multiply(BigInteger.valueOf(8 * 8 * 9 * 9 * 10 * 10)).multiply(BigInteger.valueOf(11 * 11 * 12 * 12)));
        functionID.paramsIndex = functionID.paramsIndex.add(BigInteger.valueOf(2 + 2 * 14).multiply(BigInteger.valueOf(5 * 5 * 6 * 6 * 7 * 7)).multiply(BigInteger.valueOf(8 * 8 * 9 * 9 * 10 * 10)).multiply(BigInteger.valueOf(11 * 11 * 12 * 12 * 13 * 13)));

        CodeGenerator.setParams(functionID.paramsIndex, codeLines);
        Assert.assertEquals(1, codeLines.get(0).par1);
        Assert.assertEquals(1, codeLines.get(0).par2);
        Assert.assertEquals(2, codeLines.get(1).par1);
        Assert.assertEquals(1, codeLines.get(1).par2);
        Assert.assertEquals(2, codeLines.get(2).par1);
        Assert.assertEquals(1, codeLines.get(2).par2);
        Assert.assertEquals(5, codeLines.get(3).par1);
        Assert.assertEquals(2, codeLines.get(3).par2);
        Assert.assertEquals(4, codeLines.get(4).par1);
        Assert.assertEquals(6, codeLines.get(4).par2);
        Assert.assertEquals(5, codeLines.get(5).par1);
        Assert.assertEquals(5, codeLines.get(5).par2);
        Assert.assertEquals(2, codeLines.get(6).par1);
        Assert.assertEquals(9, codeLines.get(6).par2);
        Assert.assertEquals(4, codeLines.get(7).par1);
        Assert.assertEquals(3, codeLines.get(7).par2);
        Assert.assertEquals(11, codeLines.get(8).par1);
        Assert.assertEquals(0, codeLines.get(8).par2);
        Assert.assertEquals(2, codeLines.get(9).par1);
        Assert.assertEquals(2, codeLines.get(9).par2);

        functionID.jumpIndex = BigInteger.ZERO;
        functionID.jumpIndex = BigInteger.valueOf(4);

        CodeGenerator.setJumps(functionID.jumpIndex, codeLines);
        Assert.assertEquals(4, codeLines.get(9).jump);

        CodeGenerator.runCode(codeLines);
        Assert.assertEquals(Main.calculatePI(), codeLines.get(8).result);

        System.out.println("funcIndex of pi function " + functionID.funcIndex);
    }


}
