package codegenerator2;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Гайдук Михаил
 * @version 2.0
 *          <p/>
 *          Генератор кода, для поиска реализаций вычисления числа Пи.
 *          <p/>
 *          Задача данного класса показать что любое решение задачи можно найти перебором.
 *          <p/>
 *          Для тестирования я взял реализацию расчета числа Пи без расчета до точности знака. (см. calculatePiInJavaView)
 *          Чем проще функция тем быстрее её можно найти перебором.
 *          Такой вид функции является простым для человеческого восприятия, но он отличается от машинного представления.
 *          Например одна строка в Java "dx = 1.0 / (i * i);" раскладывается в несколько действий.
 *          А именно на: умножение "(i * i)" и деление "1.0 / x".
 *          Так что в машинном представлении расчет числа Пи будет иметь другой(менее крссивый) вид. (см. calculatePiInMachineView)
 *          <p/>
 *          Для того что бы сгенерировать такую функцию необходимо понять что мы в ней видим.
 *          1) Константы
 *          2) Локльные переменные
 *          3) Присваивания
 *          4) Циклы или условные переходы
 *          5) Выполнение операций с параметрами
 *          6) Возвращение результата
 *          <p/>
 *          Рассмотрим код(он не имеет смысла но показывает все необходимые конструкции):
 *          int result = 1 + 2;
 *          if (result == 3)
 *          result += 2;
 *          return result;
 *          <p/>
 *          В псевдокоде это буде выглядеть:
 *          1: Создать переменную result. Проинициализировать значением сложения числа 1 и числа 2
 *          2: Если равны result и число 3 то перейти на строку 3, иначе перейти на строку 4
 *          3: Присвоить переменной result значением сложения result и числа 2
 *          4: Вернуть результат result.
 *          <p/>
 *          И сдесь мы видим что в данном случае почти все проводимые операции имеют по два параметра.
 *          Но есть варианты где у операции всего один параметр. (Например: return result, Math.sqrt, int result = 1)
 *          В данном случае искуственно добавим пустую операцию что бы у всех строк было по два парамета(return result + 0),
 *          или заменим аналогичной ей операцией с двумя параметрами(Math.sqrt(9) => Math.pow(9, 0.5)).
 *          <p/>
 *          Таким образом в каждой строке кода будет операция с двумя параметрами.(см. calculatePiInTableView)
 *          Строки будут иметь формализованный вид:
 *          1) Возвращает ли строка значение
 *          2) К какой переменной присваивается значение
 *          3) Какая операция выполняется
 *          4) Первый параметр операции
 *          5) Второй параметр операции
 *          6) На какую строку перейти если значение текущей равно 1(true)
 *          <p/>
 *          Так программу можно предствить в виде матрицы.
 *          Строки которой будут одной строкой кода, а столбцы это параметр каждой конструкции(в последовательности формализации).
 *
 *          Теперь осталось только перебрать все возможные варианты значений этой таблицы.
 *          Максимальными значениями каждого слобца будут(см. setGlobalVariables):
 *          1) Флаг возвращает ли значение. 2 варианта.
 *          2) Номер строки к которой присваивается значение. Вариантов столько же сколько и строк.
 *          3) Индекс выполняемой операции. Вариантов столько же сколько и фунцкий.
 *          4) Индекс значения первой переменной. Вариантов столько же сколько и строк + количество констант.
 *          5) Индекс значения второй переменной. -//-
 *          6) Номер строки на которую нужно перейти. Вариантов столько же сколько и строк.
 *          Итого реализацию calculatePiInTableView можно представить в виде calculatePIInTable;
 *
 *          Используя алгоритмы комбинаторики можно объединить одну строку матрицы в большое число.
 *          Например:
 *          Значение переменных:
 *          {1, 0, 2, 3, 4, 0}
 *          Максимальные значения переменныз:
 *          {2, 1, 9, 5, 5, 1}
 *          // TODO проверить вычисления
 *          Объединение в одно число(см getIndex):
 *          (1) + (0 * (2)) + (2 * (2*1)) + (3 * (2*1*9)) + (4 * (2*1*9*5)) + (0 * (2*1*9*5)) = 1636200
 *          Разбиение на массив(см. getCode):
 *          x = 1635200
 *          a[0] = x mod (2*1*9*5*1) = 1;
 *          x = x / 2;
 *          a[1] = x mod (1*9*5*1) = 0;
 *          x = x / 1;
 *          a[2] = x mod (9*5*1) = 2;
 *          x = x / 9;
 *          a[3] = x mod (5*1) = 3;
 *          x = x / 5
 *          a[4] = x mod (1) = 4;
 *
 */
public class CodeGenerator2 {

    public double calculatePiInJavaView() {
        double result = 0;
        double dx = 1;
        for (int i = 1; i < 10000; i++) {
            result = result + dx;
            dx = 1.0 / (i * i);
        }
        return Math.sqrt(result * 6.0);
    }

    public double calculatePiInMachineView() {
        double result = 0;
        double dx = 1;
        int i = 1;
        while (i < 10000) {
            result = result + dx;
            double sqrI = i * i;
            dx = 1.0 / sqrI;
            i++;
        }
        double resultMul6 = result * 6.0;
        return Math.sqrt(resultMul6);
    }

    public static double calculatePiInTableView() {
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

    int[][] calculatePIInTable = {
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

    static final double[] inputs = {0.0, 1.0, 6.0, 0.5};

    Integer lineCount;

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
    BigInteger paramValuesCount; // Количество возможных значений одного параметра
    BigInteger jumpPlaceCount;  // Количество возможных переходов.

    public CodeGenerator2(int lineCount) {
        this.lineCount = lineCount;
        setGlobalVariables();
    }

    public void setGlobalVariables() {
        BigInteger lineCountBigInt = BigInteger.valueOf(lineCount);
        valuePlaceCount = lineCountBigInt;
        paramValuesCount = inputVariableCount.add(lineCountBigInt);
        jumpPlaceCount = lineCountBigInt;
        setMaxIndexInOneLine();
        setMaxIndexInFunction(lineCount);
    }

    public BigInteger maxIndexInOneLine; // Максимальный индекс одной строке.

    void setMaxIndexInOneLine() {
        maxIndexInOneLine = BigInteger.ONE;
        maxIndexInOneLine = maxIndexInOneLine.multiply(returnFlagCount);
        maxIndexInOneLine = maxIndexInOneLine.multiply(valuePlaceCount);
        maxIndexInOneLine = maxIndexInOneLine.multiply(functionCount);
        maxIndexInOneLine = maxIndexInOneLine.multiply(paramValuesCount); // Количество возможных значений параметра 1.
        maxIndexInOneLine = maxIndexInOneLine.multiply(paramValuesCount); // Количество возможных значений параметра 2.
        maxIndexInOneLine = maxIndexInOneLine.multiply(jumpPlaceCount);
    }

    public BigInteger maxIndexInFunction; // Максимальный индекс во всех строках функции

    void setMaxIndexInFunction(Integer lineCount) {
        setMaxIndexInOneLine();
        maxIndexInFunction = BigInteger.ONE;
        for (int i = 0; i < lineCount; i++)
            maxIndexInFunction = maxIndexInFunction.multiply(maxIndexInOneLine);
    }

    int[][] getCode(BigInteger index) {
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

    Double invoke(int[][] code, BigInteger maxOfRunLine) {
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
            int valueIndex = code[activeLine][valuePosInCodeLine];
            if (valueIndex != 0)
                results[valueIndex >= activeLine ? valueIndex : valueIndex - 1] = results[activeLine];

            if (code[activeLine][returnPosInCodeLine] == 1)
                return results[activeLine];

            countOfRunLine = countOfRunLine.add(BigInteger.ONE);

            int jumpIndex = code[activeLine][jumpPosInCodeLine];
            if (jumpIndex != 0 && results[activeLine] == 1) {
                activeLine = jumpIndex >= activeLine ? jumpIndex : jumpIndex - 1;
                continue;
            }

            activeLine++;
        }
        return null;
    }


    BigInteger getIndex(int[][] code) {
        BigInteger index = BigInteger.ZERO;
        BigInteger mulFactor = BigInteger.ONE;
        for (int i = 0; i < code.length; i++) {
            for (int j = 0; j < 6; j++) {
                switch (j) {
                    case returnPosInCodeLine:
                        index = index.add(mulFactor.multiply(BigInteger.valueOf(code[i][j])));
                        mulFactor = mulFactor.multiply(returnFlagCount);
                        break;
                    case valuePosInCodeLine:
                        index = index.add(mulFactor.multiply(BigInteger.valueOf(code[i][j])));
                        mulFactor = mulFactor.multiply(valuePlaceCount);
                        break;
                    case funcPosInCodeLine:
                        index = index.add(mulFactor.multiply(BigInteger.valueOf(code[i][j])));
                        mulFactor = mulFactor.multiply(functionCount);
                        break;
                    case param1PosInCodeLine:
                        index = index.add(mulFactor.multiply(BigInteger.valueOf(code[i][j])));
                        mulFactor = mulFactor.multiply(paramValuesCount);
                        break;
                    case param2PosInCodeLine:
                        index = index.add(mulFactor.multiply(BigInteger.valueOf(code[i][j])));
                        mulFactor = mulFactor.multiply(paramValuesCount);
                        break;
                    case jumpPosInCodeLine:
                        index = index.add(mulFactor.multiply(BigInteger.valueOf(code[i][j])));
                        mulFactor = mulFactor.multiply(jumpPlaceCount);
                        break;
                }
            }
        }
        return index;
    }


    boolean testPiValue(Double piValue) {
        return piValue != null && Math.round(piValue * 100.0) / 100.0 == 3.14;
    }

    public List<BigInteger> generatePI(BigInteger startIndex, BigInteger blockSize, BigInteger maxOfRunLine) {
        List<BigInteger> goodFunctionIndexes = new ArrayList<BigInteger>();

        BigInteger maxIndexInBlock = startIndex.add(blockSize);
        for (BigInteger index = startIndex;
             index.compareTo(maxIndexInFunction) <= 0 &&
                     index.compareTo(maxIndexInBlock) < 0;
             index = index.add(BigInteger.ONE)) {
            int[][] code = getCode(index);
            Double functionResult = invoke(code, maxOfRunLine);
            if (testPiValue(functionResult))
                goodFunctionIndexes.add(index);
            System.out.println("end " + index.toString());
        }
        return goodFunctionIndexes;
    }
}
