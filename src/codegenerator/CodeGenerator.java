package codegenerator;


import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

/**
 * Генератор кода и поиск реализаций вычисления числа Пи.
 *
 * Задача данного кода класса показать что любую задачу можно решить перебором.
 * Этот генератор поддерживает генерацию:
 * функций, параметров к ним, условные операторы, локальные переменные, присваивания, возврат значения.
 * Таких конструкций хватит для вычисления числа пи.
 * Я взял простейшую реализацию поиска числа Пи, для тестирования работоспособности генератора.
 *
 *
 * @author Mikhael Haiduk
 * @email x29a100@mail.ru x29a100@gmail.com
 * @phone +375255451247
 * @year 2016
 */
public class CodeGenerator implements Runnable {

    /**
     * Вступление
     * <p/>
     * "Космос велик!" сообщает путеводитель по галактике во вступлении. Страшно велик!
     * Вы даже не поверите на сколько он умопомрочительно велик!
     * из фильма "Автостопом по галактике".
     * <p/>
     * Количество возможных функций бесконечно. Я задался целью перебрать все их, в надежде на то что однажды одна из
     * них скажет мне - "Hello world! Может купим текилы и вызовем девочек?". Теоретически это возможно подумал я.
     * И тут понеслось...
     * <p/>
     * Генерация функций для компилируемого языка типа Java это не удобно: долгая компиляция,
     * отсутствие многопоточности генерации и невозможность генерировать функции пулом устройств.
     * Скриптовые языки типа JavaScript более подходят для такой задачи но они медленно выполняются и у них трудности с доступом к жесткому диску.
     * Решение было простое: создать мини язык(если можно так назвать) именно для этой зададчи, где будет высокая скорость
     * генерации фунции, будет многопоточность и будет быстрое выполнение этих функций.
     * Я постарался уменьшить количество фичей языка но оставить все возможности для реализации любой логической конструкции.
     * Представить его можно данном виде:
     * <p/>
     * //Данный код не выполняет какую либо функцию а служит только для представления возможностей кода.
     * GeneratedFunction(double inputVariable1, double inputVariable2, double inputVariable3){
     * line1: double localVariable1 = funcAdd(inputVariable1, inputVariable2);
     * line2: double localVariable2 = funcSub(inputVariable1, localVariable1);
     * line3: double localVariable3 = funcEquals(inputVariable2, inputVariable3);
     * if (localVariable3 == 1)
     * goto line5;
     * line4: double localVariable4 = funcNonEquals(localVariable1, localVariable2);
     * if (localVariable4 == 1)
     * goto line2; //Способ зацикливания. Когда переход на строку выше текущей.
     * line5: double localVariable5 = funcMul(inputVariable3, inputVariable3);
     * return localVariable5;
     * }
     * <p/>
     * В реальности данный код существует в виде массива структур CodeLine где указаны переменные для создания и воссоздания
     * любой функции. Данные структуры и заполняются в этом классе.
     */
    Long blockID;

    CodeGenerator(Integer blockSize, Long blockID) {
        this.blockSize = BigInteger.valueOf(blockSize);
        this.blockID = blockID;
    }

    // Устанавливаем исходные переменные которые будут вечно складываться умножаться и т.д.
    // Теоретически можно установить всего два числа в исходных данных, но тогда количество строк которые нужно
    // для реализации того или иного алгоритма будут увеличиваться в геометрической прогрессии.
    static final double[] inputVars = {0.5, 0.0, 1.0, 6.0};
    // Устанавливаем количество используемых функций. (+ - * / pow == != > <)
    // В конце кода есть switch с расшифровкой соответствия номера к действию.
    // BigInteger для того что бы меньше делать cast
    static final BigInteger funcCount = BigInteger.valueOf(9);
    // Нужна что бы поделить функции на сравнители и выполнители
    static final int firstCompareFuncPosition = 5;

    // Массив всех функций которые дали правильный ответ
    ArrayList<FunctionID> goodFunctionID = new ArrayList<FunctionID>();

    // Размер блока для майнинга. Значит что в блоке находится такое количество фунций которые будут сгенерированы.
    final BigInteger blockSize;

    // Вычесляем идентификатор первой фунции. По этому идентификатору будет определяться тело функции.
    BigInteger begFuncIndex(Long blockID) {
        return BigInteger.valueOf(blockID).multiply(blockSize);
    }

    BigInteger maxFuncIndex(Long blockID) {
        return BigInteger.valueOf(blockID + 1).multiply(blockSize);
    }

    static int getCodeLineCount(BigInteger funcIndex) {
        int codeLineCount = 0;
        while (funcIndex.compareTo(BigInteger.ZERO) > 0) {
            codeLineCount++;
            funcIndex = funcIndex.divide(funcCount);
        }
        return codeLineCount;
    }

    /**
     * Напомню что в блоке есть много функций со своим индексом(номером).
     * Тут происходит преобразование индекса функции в тело функции.
     * Происходит это по принципу комбинаторных алгоритмов.
     * Допустим у нас должно быть 5 строчек кода и в каждой из них может быть одна из 8 функций.
     * Нам нужно по номеру функции определить состояние всех 5 строк.
     * Мы представляем 5 строчек в виде пятизначного числа (00000), каждый знак может принимать 8 состояний (функций).
     * И это пятизначное число будет в восмеричной системе счисления.
     * Прибавляя номеру функции 1, мы будем получать следующее состояние всех строчек.
     * Например 0=(00000), 1=(00001), 2=(00002), 7=(00007), 8=(00010), 9=(00011) и тд до маесимального числа (77777).
     * Из этого можно расчитать что всего вариаций в этом числе будет 8^5=32768 вариантов расположения функций.
     */
    static void setFunctions(BigInteger funcIndex, List<CodeLine> codeLines) {
        codeLines.clear();
        int codeLineCount = getCodeLineCount(funcIndex);
        for (int i = 0; i < codeLineCount; i++) {
            CodeLine codeLine = new CodeLine();
            codeLine.func = funcIndex.mod(funcCount).intValue();
            codeLines.add(codeLine);
            funcIndex = funcIndex.divide(funcCount);
        }
    }

    /**
     * Считаем максимальное количество присваиваний.
     * Первая строчка не может использовать присваивание.
     * Вторая строчка может присвоить только первую.
     * Пример: Если код состоит из 5 строчек то вариации присваиваний будут
     *
     * @param codeLine
     * @return
     */
    public static BigInteger maxValueIndex(List<CodeLine> codeLine) {
        BigInteger maxValueIndex = BigInteger.ONE;
        for (int i = 0; i < codeLine.size(); i++)
            maxValueIndex = maxValueIndex.multiply(BigInteger.valueOf(i + 1));
        return maxValueIndex;
    }

    /**
     * Генератор присваиваний по индексу.
     * Присваивание не обязательное действие. Поэтому может принимать значение -1 что значит что приссваивание отсуствует.
     */
    public static void setValues(BigInteger valueIndex, List<CodeLine> codeLines) {
        for (int i = 0; i < codeLines.size(); i++) {
            BigInteger possibleStagesInLine = BigInteger.valueOf(i + 1);
            BigInteger valueIndexInLine = valueIndex.mod(possibleStagesInLine);
            codeLines.get(i).value = valueIndexInLine.intValue();
            valueIndex = valueIndex.divide(possibleStagesInLine);
        }
    }


    // всего всех вариантов параметров
    public static BigInteger maxParamIndex(List<CodeLine> codeLines) {
        BigInteger maxParamIndex = BigInteger.ONE;
        int possibleParamsInFirstLine = inputVars.length + 1;
        for (int i = 0; i < codeLines.size(); i++)
            maxParamIndex = maxParamIndex.multiply(BigInteger.valueOf(possibleParamsInFirstLine + i).pow(2));
        return maxParamIndex;
    }

    /**
     * Для упрощения задачи были использованы функции только с двумя параметрами.
     * И ещё каждая строка имеет локальную переменную в которой хранится значение выполненной функции.
     * С параметрами немного хитрее. Каждый параметр может принимать значение не только из статического списка переменных,
     * Но так же функция может принять значение результата предыдущей выполненной функции.
     * Вот пример абсолютно нашего тестого кода.
     * Входящие переменные: var0, var1, var2
     * line0: value0 = func2( var1, var2 );
     * line1: value1 = func1( var0, var1 );
     * line2: value2 = func4( value1, var1 );
     * <p/>
     * И тут нужно расчитать колчичество всех вариантов для каждой строки отдельно.
     * на строчке line0: могут быть использованы параметры var0, var1, var2
     * на строчке line1: могут быть использованы параметры var0, var1, var2, value0
     * на строчке line2: могут быть использованы параметры var0, var1, var2, value0, value1
     * Расчитаем количество вариантов параметров для каждой строчки:
     * на строчке line0: 3 возможных параметра. И нам их нужно поставить в 2 места. т.е. 3^2=9 вариантов.
     * на строчке line1: 4 возможных параметра. И нам их нужно поставить в 2 места. т.е. 4^2=16 вариантов.
     * на строчке line3: 5 возможных параметра. И нам их нужно поставить в 2 места. т.е. 5^2=25 вариантов.
     * Итого всего всех вариантов параметров может быть 9^1 + 16^2 + 25^3 = 9 + 144 + 15625 = 15778 вариантов параметров.
     * <p/>
     * И для того что по номеру параметров получить состовяние каждого параметра нужно создать массив
     * в котором будут отдельные итераторы для каждой из строки.
     * И получится:
     * 0=(0,0,0), 1=(1,0,0), 9=(8,0,0), 10=(0,1,0), 19=(8,1,0), 20=(0,2,0), 144=(0,16,0), 145=(0,0,1) и т.д.
     */
    public static void setParams(BigInteger paramIndex, List<CodeLine> codeLines) {

        for (int i = 0; i < codeLines.size(); i++) {
            BigInteger possibleParamsInLine = BigInteger.valueOf(inputVars.length + i + 1);
            BigInteger maxParamIndexInLine = possibleParamsInLine.pow(2);
            // Вычисляем индекс в строчке
            BigInteger indexInLine = paramIndex.mod(maxParamIndexInLine);
            // Делим этот итератор на две части
            CodeLine codeLine = codeLines.get(i);
            codeLine.par1 = indexInLine.mod(possibleParamsInLine).intValue();
            codeLine.par2 = indexInLine.divide(possibleParamsInLine).intValue();

            paramIndex = paramIndex.divide(maxParamIndexInLine);
        }
    }

    /**
     * Условные переходы и циклы.
     * Условный переход может быть из функции сравнения(== != < >) в любую строку кроме себя.
     * Что бы посчитать колчичество возможных условых переходов возводим колчество строк - 1 (т.к. нельзя переходить на себя)
     * в степень количество строк с функциями сравнения.
     * Например в функции 5 строк и 2 из них с функциями сравнения.
     * Итого 4^2=16 вариантов перехода из 2ух строк сравнения во все другие строки.
     */
    static BigInteger maxJumpIndex(List<CodeLine> codeLines) {
        // Считаем количество строк в которых есть функции сравнения
        int compareFunctionsCount = 0;
        for (int i = 0; i < codeLines.size(); i++)
            if (codeLines.get(i).func >= firstCompareFuncPosition) // == != > <
                compareFunctionsCount++;
        return BigInteger.valueOf(codeLines.size() - 1).pow(compareFunctionsCount);
    }

    static void setJumps(BigInteger jumpIndex, List<CodeLine> codeLines) {
        BigInteger codeLineCountWithoutOne = BigInteger.valueOf(codeLines.size() - 1);
        for (int i = 0; i < codeLines.size(); i++) {
            CodeLine codeLine = codeLines.get(i);
            if (codeLine.func >= firstCompareFuncPosition) // == != > <
            {
                codeLine.jump = jumpIndex.mod(codeLineCountWithoutOne).intValue();
                // Если номер перехода указывает на себя то прибавляем 1.
                if (codeLine.jump >= i)
                    codeLine.jump++;
                jumpIndex = jumpIndex.divide(codeLineCountWithoutOne);
            }
        }
    }

    static List<CodeLine> getFunctionBody(FunctionID functionID) {
        List<CodeLine> codeLines = new ArrayList<CodeLine>();
        setFunctions(functionID.funcIndex, codeLines);
        setValues(functionID.valueIndex, codeLines);
        setParams(functionID.paramsIndex, codeLines);
        setJumps(functionID.jumpIndex, codeLines);
        return codeLines;
    }


    /**
     * Выполнение сгенерированной функции.
     * В данный момент готова вся сгенерированная фукнция. Осталось её только выполнить.
     * Так как в программах могут быть вечные циклы, устанавливается максимальное колчество
     * выполненных строк кода.
     */
    static double runCode(List<CodeLine> codeLines) {

        // Максимальное количество выполненных строк кода. (Ограничитель вечных циклов)
        double maxRunTime = 10000;
        double runTime = 0;
        int activeLine = 0;
        // Функция считается завершенной если мы закончили последнюю строку или превысили допустимое
        // количество действий
        while ((activeLine < codeLines.size()) & (runTime < maxRunTime)) {
            runTime++;
            CodeLine codeLine = codeLines.get(activeLine);

            // Получаем значение первого параметра
            double par1Value = 0;
            if (codeLine.par1 < inputVars.length)
                par1Value = inputVars[codeLine.par1]; // Если параметр является числом
            else
                par1Value = codeLines.get(codeLine.par1 - inputVars.length).result; // Если параметр является результат выполнения строки

            double par2Value = 0;
            if (codeLine.par2 < inputVars.length)
                par2Value = inputVars[codeLine.par2];
            else
                par2Value = codeLines.get(codeLine.par2 - inputVars.length).result;


            // В этом блоке происходит расшифровка номера функции в выполняемое действие.
            // Если поменять места функций сравнения тогда будет неверно высчитываться позиции функций
            // сравнения в коде.
            switch (codeLine.func) {
                case 0:
                    codeLine.result = par1Value + par2Value;
                    break;
                case 1:
                    codeLine.result = par1Value - par2Value;
                    break;
                case 2:
                    codeLine.result = par1Value * par2Value;
                    break;
                case 3:
                    codeLine.result = par1Value / par2Value;
                    break;
                case 4:
                    codeLine.result = Math.pow(par1Value, par2Value);
                    break;
                case 5:
                    codeLine.result = (par1Value == par2Value) ? 1 : 0;
                    break;
                case 6:
                    codeLine.result = (par1Value != par2Value) ? 1 : 0;
                    break;
                case 7:
                    codeLine.result = (par1Value > par2Value) ? 1 : 0;
                    break;
                case 8:
                    codeLine.result = (par1Value < par2Value) ? 1 : 0;
                    break;
            }

            if (codeLine.value != 0) {
                codeLines.get(codeLine.value - 1).result = codeLine.result;
            }

            // Переходим по условному оператору если результат выполнения функции сравнения равна еденице
            if (codeLine.jump != 0 && codeLine.result == 1) {
                activeLine = codeLine.jump - 1;
                continue;
            }
            // Переходим на следующую строку
            activeLine++;
        }
        return runTime;
    }


    /**
     * Проверка результата.
     * Так как мы не знаем в какой строке будет находится результат, то проверим сразу все строки.
     * Берем округленное число пи что бы посмотреть похожие функции.
     */
    void setResult(FunctionID functionID, List<CodeLine> codeLines) {

        double rightResult = 3.14;
        for (int resultPosition = 0; resultPosition < codeLines.size(); resultPosition++) {
            CodeLine codeLine = codeLines.get(resultPosition);
            // Округлим результат для того что бы найти все похожие на то что мы ищем.
            // Это можно убрать и будет искаться точное значение.
            double roundResult = Math.round(codeLine.result * 100.0) / 100.0;

            if (roundResult == rightResult) {
                // Устанавливаем
                functionID.resultPosition = resultPosition;
                goodFunctionID.add(functionID);
                break;
            }

        }
    }

    /**
     * Данная фунция генерирует код всех фунций в определенном блоке.
     */
    public void run() {

        // Перебераем все возможные функции в блоке
        for (BigInteger funcIndex = begFuncIndex(blockID);
             funcIndex.compareTo(maxFuncIndex(blockID)) < 0;
             funcIndex = funcIndex.add(BigInteger.ONE)) {

            List<CodeLine> codeLines = new ArrayList<CodeLine>();
            setFunctions(funcIndex, codeLines);

            // Перебераем все возможные присваивания
            for (BigInteger valueIndex = BigInteger.ZERO;
                 valueIndex.compareTo(maxValueIndex(codeLines)) < 0;
                 valueIndex = valueIndex.add(BigInteger.ONE)) {

                setValues(valueIndex, codeLines);

                // Перебераем все возможные параметры
                for (BigInteger paramIndex = BigInteger.ZERO;
                     paramIndex.compareTo(maxParamIndex(codeLines)) < 0;
                     paramIndex = paramIndex.add(BigInteger.ONE)) {

                    setParams(paramIndex, codeLines);

                    // Перебераем все возможные переходы
                    for (BigInteger jumpIndex = BigInteger.ZERO;
                         jumpIndex.compareTo(maxJumpIndex(codeLines)) < 0;
                         jumpIndex = jumpIndex.add(BigInteger.ONE)) {

                        setJumps(jumpIndex, codeLines);
                    }
                }
            }
        }
        if (1 == 1)
            return;
        BigInteger funcIndex = null;
        // Вычисляем количество строк в первой функции в блоке
        int lineSize = 0;
        BigInteger powFuncCountToLineSize = funcCount.pow(lineSize);
        while (funcIndex.compareTo(powFuncCountToLineSize) == 1) {
            funcIndex = funcIndex.subtract(powFuncCountToLineSize);
            lineSize++;
        }
        // Вычисляем количество строк в последней функции в блоке
        // Что бы остановить вечный цикл генерации
        BigInteger maxFuncIndex = BigInteger.valueOf(blockID + 1).multiply(blockSize);
        int maxLineSize = 0;
        BigInteger powFuncCountToMaxLineSize = funcCount.pow(maxLineSize);
        while (maxFuncIndex.compareTo(powFuncCountToMaxLineSize) == 1) {
            maxFuncIndex = maxFuncIndex.subtract(powFuncCountToMaxLineSize);
            maxLineSize++;
        }

        // Для единаждой инициализации funcIndex
        boolean firstFuncGenerate = true;

        //Засекаем время начала майнинга блока. Для статистики.
        long beginTime = System.currentTimeMillis();

        // Начинаем цикл генерации фукнций.
        // Каждый цикл while обрабатывает функции с разными количеством строк в генерируемых функциях.
        while (true) {

            // TODO Лучше было использовать статический массив.
            /**
             * Весь код функции состоит из объектов типа CodeLine.
             * Это упрощает способ выполнения функций.
             */
            //Создание строк для генерируемой функции
            ArrayList<CodeLine> codeLines = new ArrayList<CodeLine>();
            for (double i = 0; i <= lineSize; i++)
                codeLines.add(new CodeLine());

            // Блок может попасть на стык между функциями с разным количеством строк
            if (firstFuncGenerate == false)
                funcIndex = BigInteger.valueOf(0);
            firstFuncGenerate = false;

            // Начинаем перебор функций
            for (; funcIndex.compareTo(powFuncCountToLineSize) == -1; funcIndex = funcIndex.add(BigInteger.ONE)) {


                /**
                 * Напомню что в блоке есть много функций со своим индексом(номером).
                 * Тут происходит преобразование индекса функции в тело функции.
                 * Происходит это по принципу комбинаторных алгоритмов.
                 * Допустим у нас должно быть 5 строчек кода и в каждой из них может быть одна из 8 функций.
                 * Нам нужно по номеру функции определить состояние всех 5 строк.
                 * Мы представляем 5 строчек в виде пятизначного числа (00000), каждый знак может принимать 8 состояний (функций).
                 * И это пятизначное число будет в восмеричной системе счисления.
                 * Прибавляя номеру функции 1, мы будем получать следующее состояние всех строчек.
                 * Например 0=(00000), 1=(00001), 2=(00002), 7=(00007), 8=(00010), 9=(00011) и тд до маесимального числа (77777).
                 * Из этого можно расчитать что всего вариаций в этом числе будет 8^5=32768 вариантов расположения функций.
                 */
                // И тут происходит определение тела фунцкии по её номеру
                BigInteger funcIndex2 = funcIndex;
                for (int i = 0; i < codeLines.size(); i++) {
                    BigInteger razm = funcCount.pow(i + 1);
                    // Устанавливаем функцию в каждой строке (0=+ 1=- 2=* 3=/ ..)
                    codeLines.get(i).func = funcIndex2.mod(razm).divide(funcCount.pow(i)).intValue();
                    funcIndex2 = funcIndex2.subtract(funcIndex2.mod(razm));
                }


                /**
                 * Установка значений.
                 *
                 * Присваивание значений.
                 * Значение от выполнной строки переносится в значение другой строки.
                 * Присваивать значения можно для строк которые находятся выше присваеваемой.
                 */


                // TODO поменять и в мараметрах названия на maxIndexInLine maxValueIndex
                ArrayList<BigInteger> maxIndexInLine = new ArrayList<BigInteger>();
                for (int i = 0; i < codeLines.size(); i++)
                    maxIndexInLine.add(BigInteger.valueOf(i + 1));
                //Итого всего всех вариантов значений
                BigInteger maxValueIndex = BigInteger.ZERO;
                for (int i = 0; i < codeLines.size(); i++)
                    maxValueIndex = maxValueIndex.multiply(maxIndexInLine.get(i));

                // Перебераем все возможные значения
                for (BigInteger valueIndex = BigInteger.ZERO;
                     valueIndex.compareTo(maxValueIndex) == -1;
                     valueIndex = valueIndex.add(BigInteger.ONE)) {


                    // Тут происходит преобразование номера в состоянии параметров
                    BigInteger valueIndex2 = valueIndex;
                    for (int i = 0; i < codeLines.size(); i++) {
                        // Вычисляем на что нужно поделить valueIndex для того что бы получить номер значения
                        BigInteger razm = BigInteger.ONE;
                        for (int j = 1; j < maxIndexInLine.size() - i; j++)
                            razm = razm.multiply(maxIndexInLine.get(j));

                        BigInteger val = valueIndex2.divide(razm);
                        codeLines.get(i).value = val.intValue();
                        valueIndex2 = valueIndex2.subtract(val.multiply(razm));
                    }


                    /**
                     * Для упрощения задачи были использованы функции только с двумя параметрами.
                     * И ещё каждая строка имеет локальную переменную в которой хранится значение выполненной функции.
                     * С параметрами немного хитрее. Каждый параметр может принимать значение не только из статического списка переменных,
                     * Но так же функция может принять значение результата предыдущей выполненной функции.
                     * Вот пример абсолютно нашего тестого кода.
                     * Входящие переменные: var0, var1, var2
                     * line0: value0 = func2( var1, var2 );
                     * line1: value1 = func1( var0, var1 );
                     * line2: value2 = func4( value1, var1 );
                     *
                     * И тут нужно расчитать колчичество всех вариантов для каждой строки отдельно.
                     * на строчке line0: могут быть использованы параметры var0, var1, var2
                     * на строчке line1: могут быть использованы параметры var0, var1, var2, value0
                     * на строчке line2: могут быть использованы параметры var0, var1, var2, value0, value1
                     * Расчитаем количество вариантов параметров для каждой строчки:
                     * на строчке line0: 3 возможных параметра. И нам их нужно поставить в 2 места. т.е. 3^2=9 вариантов.
                     * на строчке line1: 4 возможных параметра. И нам их нужно поставить в 2 места. т.е. 4^2=16 вариантов.
                     * на строчке line3: 5 возможных параметра. И нам их нужно поставить в 2 места. т.е. 5^2=25 вариантов.
                     * Итого всего всех вариантов параметров может быть 9^1 + 16^2 + 25^3 = 9 + 144 + 15625 = 15778 вариантов параметров.
                     *
                     * И для того что по номеру параметров получить состовяние каждого параметра нужно создать массив
                     * в котором будут отдельные итераторы для каждой из строки.
                     * И получится:
                     * 0=(0,0,0), 1=(1,0,0), 9=(8,0,0), 10=(0,1,0), 19=(8,1,0), 20=(0,2,0), 144=(0,16,0), 145=(0,0,1) и т.д.
                     */

                    //prepare to consolidator generate
                    //Расчитаем количество вариантов параметров для каждой строчки
                    BigInteger inputVarsCount = BigInteger.valueOf(inputVars.length);
                    ArrayList<BigInteger> maxIndex = new ArrayList<BigInteger>();
                    for (int i = 0; i < codeLines.size(); i++)
                        maxIndex.add(inputVarsCount.add(BigInteger.valueOf(i)).pow(2));
                    //Итого всего всех вариантов параметров
                    BigInteger maxParamIndex = BigInteger.ZERO;
                    for (int i = 0; i < codeLines.size(); i++)
                        maxParamIndex = maxParamIndex.multiply(maxIndex.get(i));

                    // Создаем итераторы для каждой из строки
                    ArrayList<BigInteger> valIndex = new ArrayList<BigInteger>();
                    for (int i = 0; i < codeLines.size(); i++)
                        valIndex.add(BigInteger.ZERO);


                    // Перебераем все возможные параметры
                    for (BigInteger paramIndex = BigInteger.ZERO;
                         paramIndex.compareTo(maxParamIndex) == -1;
                         paramIndex = paramIndex.add(BigInteger.ONE)) {
                        // Тут происходит преобразование номера в состоянии параметров
                        BigInteger parIndex2 = paramIndex;
                        for (int i = 0; i < valIndex.size(); i++) {
                            BigInteger razm = BigInteger.ONE;
                            for (int j = 1; j < maxIndex.size() - i; j++)
                                razm = razm.multiply(maxIndex.get(j));
                            BigInteger val = parIndex2.divide(razm);
                            valIndex.set(maxIndex.size() - i - 1, val);
                            parIndex2 = parIndex2.subtract(val.multiply(razm));
                        }
                        for (int i = 0; i < valIndex.size(); i++) {
                            BigInteger val = valIndex.get(i);
                            CodeLine codeLine = codeLines.get(i);
                            // Устанавливаем параметры функций
                            codeLine.par1 = val.mod(maxIndex.get(i).pow(-2)).intValue();
                            codeLine.par2 = val.divide(maxIndex.get(i).pow(-2)).intValue();
                        }


                        /**
                         * Условные переходы и циклы.
                         * При переборе всех возможных вариантов нужно помнить что условный переход не может указывать на себя.
                         * Условием перехода решено использовать строки кода где используются функции сравнения(== != < >).
                         * Если в генерируемой функции нет функций сравнения то условных переходов не будет.
                         * Что бы посчитать колчичество возможных условых переходов возводим колчество строк - 1 (т.к. нельзя переходить на себя)
                         * в степень количество строк с функциями сравнения.
                         * Например в функции 5 строк и 2 из них с функциями сравнения.
                         * Итого 4^2=16 вариантов перехода из 2ух строк сравнения во все другие строки.
                         */
                        //set jump
                        // Создаем список номеров строк в которых есть функции сравнения
                        ArrayList<Integer> compareFunctionsInCode = new ArrayList<Integer>();
                        for (int i = 0; i < codeLines.size(); i++)
                            if (codeLines.get(i).func > 3) // == != > <
                                compareFunctionsInCode.add(i);

                        BigInteger codeLinesCountWithoutSelf = BigInteger.valueOf(codeLines.size() - 1);
                        for (BigInteger jumptoIndex = BigInteger.ZERO;
                             jumptoIndex.compareTo(codeLinesCountWithoutSelf.pow(compareFunctionsInCode.size())) == -1;
                             jumptoIndex = jumptoIndex.add(BigInteger.ONE)) {

                            // TODO проверить изменяется ли jumpIndex
                            BigInteger jumptoIndex2 = jumptoIndex;
                            for (int i = 0; i < compareFunctionsInCode.size(); i++) {
                                CodeLine codeLine = codeLines.get(compareFunctionsInCode.get(i));
                                BigInteger razm = codeLinesCountWithoutSelf.pow(i + 1);
                                // Присваиваем номер строки условного перехода.
                                codeLine.jump = jumptoIndex2.mod(razm).divide(codeLinesCountWithoutSelf.pow(i)).intValue();
                                jumptoIndex2.subtract(jumptoIndex2.mod(razm));
                                // Если номер перехода указывает на себя то прибавляем 1.
                                if (codeLine.jump >= compareFunctionsInCode.get(i))
                                    codeLine.jump++;
                            }

                            //run
                            /**
                             * Выполнение сгенерированной функции.
                             * В данный момент готова вся сгенерированная фукнция. Осталось её только выполнить.
                             * Так как в программах могут быть вечные циклы, устанавливается максимальное колчество
                             * выполненных строк кода.
                             */
                            double maxRunTime = 10000;
                            double runTime = 0;
                            int activeLine = 0;
                            // Функция считается завершенной если мы закончили последнюю строку или превысили допустимое
                            // количество действий
                            while ((activeLine < codeLines.size()) & (runTime < maxRunTime)) {
                                runTime++;
                                CodeLine codeLine = codeLines.get(activeLine);

                                // Получаем значение первого параметра
                                double par1Value = 0;
                                if (codeLine.par1 < inputVars.length)
                                    par1Value = inputVars[codeLine.par1]; // Если параметр является числом
                                else
                                    par1Value = codeLines.get(codeLine.par1 - inputVars.length).result; // Если параметр является результат выполнения строки

                                double par2Value = 0;
                                if (codeLine.par2 < inputVars.length)
                                    par2Value = inputVars[codeLine.par2];
                                else
                                    par2Value = codeLines.get(codeLine.par2 - inputVars.length).result;


                                // В этом блоке происходит расшифровка номера функции в выполняемое действие.
                                // Если поменять места функций сравнения тогда будет неверно высчитываться позиции функций
                                // сравнения в коде.
                                switch (codeLine.func) {
                                    case 0:
                                        codeLine.result = par1Value + par2Value;
                                        break;
                                    case 1:
                                        codeLine.result = par1Value - par2Value;
                                        break;
                                    case 2:
                                        codeLine.result = par1Value * par2Value;
                                        break;
                                    case 3:
                                        codeLine.result = par1Value / par2Value;
                                        break;
                                    case 4:
                                        codeLine.result = Math.pow(par1Value, par2Value);
                                        break;
                                    case 5:
                                        codeLine.result = (par1Value == par2Value) ? 1 : 0;
                                        break;
                                    case 6:
                                        codeLine.result = (par1Value != par2Value) ? 1 : 0;
                                        break;
                                    case 7:
                                        codeLine.result = (par1Value > par2Value) ? 1 : 0;
                                        break;
                                    case 8:
                                        codeLine.result = (par1Value < par2Value) ? 1 : 0;
                                        break;
                                }
                                // Переходим по условному оператору если результат выполнения функции сравнения равна еденице
                                if (codeLine.jump != 0 && codeLine.result == 1) {
                                    activeLine = codeLine.jump;
                                    continue;
                                }
                                // Переходим на следующую строку
                                activeLine++;
                            }


                            /**
                             * Проверка результата.
                             * Так как мы не знаем в какой строке будет находится результат, то проверим сразу все строки.
                             * Берем округленное число пи что бы посмотреть похожие функции.
                             */
                            double rightResult = 3.14;
                            if (runTime < maxRunTime) {
                                for (int resultPosition = 0; resultPosition < codeLines.size(); resultPosition++) {
                                    CodeLine codeLine = codeLines.get(resultPosition);
                                    // Округлим результат для того что бы найти все похожие на то что мы ищем.
                                    // Это можно убрать и будет искаться точное значение.
                                    double roundResult = Math.round(codeLine.result * 100.0) / 100.0;


                                    if (roundResult == rightResult) {
                                        // Записываем идентификаторы генерации в список хороших функций блока.
                                        FunctionID functionID = new FunctionID();
                                        functionID.lineSize = lineSize;
                                        functionID.funcIndex = funcIndex;
                                        functionID.paramsIndex = paramIndex;
                                        functionID.jumpIndex = jumptoIndex;
                                        functionID.resultPosition = resultPosition;
                                        goodFunctionID.add(functionID);
                                    }

                                }
                            }

                        }
                    }


                    // Когда все фукнции были сгенерированы и выполнены сохраняем результаты и выходим.
                    if (lineSize >= maxLineSize && funcIndex.compareTo(maxFuncIndex) == 1) {
                        Syncronizator.log("block " + blockID + " time " + lineSize + " func " + maxFuncIndex.toString() + " runtime " + (int) ((System.currentTimeMillis() - beginTime) / 1000) + " s");
                        // Передаем результат выполнения функций
                        Block generateBlock = Syncronizator.data.blocks.get(blockID);
                        generateBlock.goodFunctionID = goodFunctionID;
                        // И говорим что поток завершился успешно
                        generateBlock.threadEnd = true;
                        return;
                    }

                    // Выходим из функции если поток был остановлен главным потоком синхронизации. см. Sync
                    // Например потому что кто то уже смайнил блок или майнит.
                    if (Thread.interrupted())
                        return;
                }
            }
            // Если блок находится на границе фунций с разным колчеством строк
            // Плюсуем количество строк в генерируемых функций.
            lineSize++;
        }
    }


}
