package codegenerator2;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

public class CodeGenerator2 {

    static final double[] inputs = {0.0, 1.0, 6.0, 0.5};

    final int returnPosInCodeLine = 0;
    final int valuePosInCodeLine = 1;
    final int funcPosInCodeLine = 2;
    final int param1PosInCodeLine = 3;
    final int param2PosInCodeLine = 4;
    final int jumpPosInCodeLine = 5;

    BigInteger inputVariableCount = BigInteger.valueOf(inputs.length);
    BigInteger returnFlagCount = BigInteger.valueOf(2); // Флаг что в этой строчке находится результат.
    BigInteger valuePlaceCount; // Мест куда можно присвоить значение.
    BigInteger functionCount = BigInteger.valueOf(9); // Количество возможных функций.
    BigInteger paramValuesCount = functionCount.add(inputVariableCount); // Количество возможных значений одного параметра
    BigInteger jumpPlaceCount;  // Количество возможных переходов.

    private void setGlobalVariables(Integer lineCount) {
        valuePlaceCount = BigInteger.valueOf(lineCount - 1);
        paramValuesCount = inputVariableCount.add(functionCount);
        jumpPlaceCount = BigInteger.valueOf(lineCount - 1);
        setMaxIndexInOneLine();
        setMaxIndexInFunction(lineCount);
    }

    private BigInteger maxIndexInOneLine; // Максимальный индекс одной строке.
    void setMaxIndexInOneLine() {
        maxIndexInOneLine = BigInteger.ONE;
        maxIndexInOneLine = maxIndexInOneLine.multiply(returnFlagCount);
        maxIndexInOneLine = maxIndexInOneLine.multiply(valuePlaceCount);
        maxIndexInOneLine = maxIndexInOneLine.multiply(functionCount);
        maxIndexInOneLine = maxIndexInOneLine.multiply(paramValuesCount); // Количество возможных значений параметра 1.
        maxIndexInOneLine = maxIndexInOneLine.multiply(paramValuesCount); // Количество возможных значений параметра 2.
        maxIndexInOneLine = maxIndexInOneLine.multiply(jumpPlaceCount);
    }

    private BigInteger maxIndexInFunction; // Максимальный индекс во всех строках функции

    void setMaxIndexInFunction(Integer lineCount) {
        setMaxIndexInOneLine();
        maxIndexInFunction = BigInteger.ONE;
        for (int i = 0; i < lineCount; i++)
            maxIndexInFunction = maxIndexInFunction.multiply(maxIndexInOneLine);
    }

    int[][] getCode(Integer lineCount, BigInteger index) {
        // 6 = результат, присваивание, фунция, параметр1, параметр2, переход
        int[][] code = new int[lineCount][6];
        for (int i = 0; i < lineCount; i++) {
            BigInteger indexInLine = index.mod(maxIndexInOneLine);
            code[i][returnPosInCodeLine] = indexInLine.mod(returnFlagCount).intValue();
            indexInLine = indexInLine.divide(returnFlagCount);
            code[i][valuePosInCodeLine] = indexInLine.mod(valuePlaceCount).intValue();
            indexInLine = indexInLine.divide(valuePlaceCount);
            code[i][funcPosInCodeLine] = indexInLine.mod(functionCount).intValue();
            indexInLine = indexInLine.divide(functionCount);
            code[i][param1PosInCodeLine] = indexInLine.mod(paramValuesCount).intValue(); // Параметр 1
            indexInLine = indexInLine.divide(paramValuesCount);
            code[i][param2PosInCodeLine] = indexInLine.mod(paramValuesCount).intValue(); // Параметр 2
            indexInLine = indexInLine.divide(paramValuesCount);
            code[i][jumpPosInCodeLine] = indexInLine.mod(jumpPlaceCount).intValue();
            index = index.divide(maxIndexInOneLine);
        }
        return code;
    }

    BigInteger runCode(int[][] code, BigInteger maxOfRunLine) {
        int lineCount = code.length;
        double[] results = new double[lineCount]; //Столько же сколько и строк в функции
        BigInteger countOfRunLine = BigInteger.ZERO;
        int activeLine = 0;
        while ((activeLine < lineCount) & (countOfRunLine.compareTo(maxOfRunLine) < 0)) {
            int param1Index = code[activeLine][param1PosInCodeLine];
            double param1Value = param1Index < inputs.length ? inputs[param1Index] : results[param1Index - inputs.length];
            int param2Index = code[activeLine][param2PosInCodeLine];
            double param2Value = param2Index < inputs.length ? inputs[param2Index] : results[param2Index - inputs.length];
            int functionIndex = code[activeLine][funcPosInCodeLine];
            switch (functionIndex) {
                case 0:
                    results[activeLine] = param1Value + param2Value;
                    break;
                case 1:
                    results[activeLine] = param1Value - param2Value;
                    break;
                case 2:
                    results[activeLine] = param1Value * param2Value;
                    break;
                case 3:
                    results[activeLine] = param1Value / param2Value;
                    break;
                case 4:
                    results[activeLine] = Math.pow(param1Value, param2Value);
                    break;
                case 5:
                    results[activeLine] = (param1Value == param2Value) ? 1 : 0;
                    break;
                case 6:
                    results[activeLine] = (param1Value != param2Value) ? 1 : 0;
                    break;
                case 7:
                    results[activeLine] = (param1Value > param2Value) ? 1 : 0;
                    break;
                case 8:
                    results[activeLine] = (param1Value < param2Value) ? 1 : 0;
                    break;
            }
            int valueIndex = code[valuePosInCodeLine][activeLine];
            if (valueIndex != 0)
                results[valueIndex - 1 >= activeLine ? valueIndex : valueIndex - 1] = results[activeLine];

            countOfRunLine = countOfRunLine.add(BigInteger.ONE);

            int jumpIndex = code[activeLine][jumpPosInCodeLine];
            if (jumpIndex != 0 && results[activeLine] == 1) {
                activeLine = jumpIndex - 1 >= activeLine ? jumpIndex : jumpIndex - 1;
                continue;
            }

            activeLine++;
        }

        int returnInPos = -1;
        for (int i = 0; i < code[0].length; i++)
            if (code[i][returnPosInCodeLine] == 1)
                returnInPos = i;

        double roundResult = Math.round(results[returnInPos] * 100.0) / 100.0;
        if (roundResult == 3.14)
            return countOfRunLine;

        return BigInteger.ZERO;
    }

    public List<BigInteger> generatePI(int lineCount, BigInteger startIndex, BigInteger blockSize) {
        setGlobalVariables(lineCount);
        List<BigInteger> goodFunctionIndexes = new ArrayList<BigInteger>();

        BigInteger maxIndexInBlock = startIndex.add(blockSize);
        for (BigInteger index = startIndex;
             index.compareTo(maxIndexInFunction) <= 0 &&
                     index.compareTo(maxIndexInBlock) < 0;
             index = index.add(BigInteger.ONE)) {
            int[][] code = getCode(lineCount, index);
            BigInteger countOfRunLine = runCode(code, BigInteger.valueOf(10000));
            if (countOfRunLine.compareTo(BigInteger.ZERO) > 0)
                goodFunctionIndexes.add(index);
        }
        return goodFunctionIndexes;
    }


    BigInteger getIndexByCode(int[][] code) {
        BigInteger index = BigInteger.ONE;
        BigInteger mulFactor = BigInteger.ONE;
        for (int i = 0; i < code.length; i++) {
            for (int j = 0; j < 6; j++) {
                switch (j){
                    case returnPosInCodeLine:
                        mulFactor = mulFactor.multiply(returnFlagCount);
                        index = index.add(mulFactor.multiply(BigInteger.valueOf(code[i][j])));
                        break;
                    case valuePosInCodeLine:
                        mulFactor = mulFactor.multiply(valuePlaceCount);
                        index = index.add(mulFactor.multiply(BigInteger.valueOf(code[i][j])));
                        break;
                    case funcPosInCodeLine:
                        mulFactor = mulFactor.multiply(functionCount);
                        index = index.add(mulFactor.multiply(BigInteger.valueOf(code[i][j])));
                        break;
                    case param1PosInCodeLine:
                        mulFactor = mulFactor.multiply(paramValuesCount);
                        index = index.add(mulFactor.multiply(BigInteger.valueOf(code[i][j])));
                        break;
                    case param2PosInCodeLine:
                        mulFactor = mulFactor.multiply(paramValuesCount);
                        index = index.add(mulFactor.multiply(BigInteger.valueOf(code[i][j])));
                        break;
                    case jumpPosInCodeLine:
                        mulFactor = mulFactor.multiply(jumpPlaceCount);
                        index = index.add(mulFactor.multiply(BigInteger.valueOf(code[i][j])));
                        break;
                }
            }
        }
        return index;
    }


    public double calculatePI() {
        double piResult = 0;
        double result = 0;
        double dx = 1;
        for (int i = 1; i < 10000; i++) {
            result = result + dx;
            dx = 1.0 / (i * i);
        }
        return Math.sqrt(result * 6.0);
    }

    //        static final double[] inputs = {0.0, 1.0, 6.0, 0.5};
    public static void calculatePiInTableView() {
        double res = 0.0 + 0.0;
        double i = 1.0 + 0.0;
        double dx = 1.0 + 0.0;
        do {
            i = i + 1.0;
            res = res + dx;
            double sqrI = i * i;
            dx = 1.0 / sqrI;
            double resMul6 = res * 6.0;
            double piResult = Math.pow(resMul6, 0.5);
        } while (i < 1000000.0 / 6.0); // while (0.0 == 0.0)
    }

    public static void main(String[] args) {
        CodeGenerator2 generator = new CodeGenerator2();
        int[][] calculatePIInTableView = {
                {0, 0, 0, 0, 0, 0},
                {0, 0, 0, 1, 0, 0},
                {0, 0, 0, 1, 0, 0},
                {0, 2, 0, 5, 1, 0},
                {0, 1, 0, 4, 6, 0},
                {0, 0, 2, 5, 5, 0},
                {0, 3, 3, 1, 9, 0},
                {0, 0, 2, 4, 2, 0},
                {1, 0, 4, 11, 3, 0},
                {0, 0, 5, 0, 0, 7}
        };
        generator.setGlobalVariables(calculatePIInTableView[0].length);
        BigInteger indexOfPiAlgorithm = generator.getIndexByCode(calculatePIInTableView);

        List<BigInteger> goodFunctionIndexes = generator.generatePI(
                calculatePIInTableView[0].length, indexOfPiAlgorithm, BigInteger.ONE);
        for (int i = 0; i < goodFunctionIndexes.size(); i++)
            if (goodFunctionIndexes.get(i).compareTo(indexOfPiAlgorithm) == 0)
                System.out.println("Wow");
    }

}
