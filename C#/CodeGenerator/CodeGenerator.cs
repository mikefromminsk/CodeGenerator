using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Numerics;

namespace CodeGenerator
{


    /**
     * @author Гайдук Михаил (Минск 2016г. x29a100@mail.ru vk.com/x29a100 +375255451247)
     *         <p/>
     *         Генератор кода, для поиска реализаций вычисления числа Пи.
     *         <p/>
     *         Задача данного класса показать что любое решение задачи можно найти перебором.
     *         <p/>
     *         Введение:
     *         <p/>
     *         Даннай метод поиска решения был скопирован с живых организмов, которые используют механизм ДНК.
     *         Он может создавать новые системы и алгоритмы которых ранее не было. Это возможно только с помощью
     *         перебора всех возможных вариантов организмов(программ) которые способны выжить(выполняться).
     *         А так же ДНК имеет возможность к репликации. Данный механизм скрещивает в случайном порядке уже
     *         сгенерированные системы(ООП), что позволяет быстрее и эффективнее создавать большие организмы.
     *         <p/>
     *         Описание метода генерации кода:
     *         <p/>
     *         Для тестирования я взял реализацию расчета числа Пи без расчета до точности знака. (см. calculatePiInJavaView)
     *         Чем проще функция тем быстрее её можно найти перебором.
     *         Такой вид функции является простым для человеческого восприятия, но он отличается от машинного представления.
     *         Например одна строка в Java "dx = 1.0 / (i * i);" раскладывается в несколько действий.
     *         А именно на: умножение "(i * i)" и деление "1.0 / x".
     *         Так что в машинном представлении расчет числа Пи будет иметь другой(менее крссивый) вид. (см. calculatePiInMachineView)
     *         Для того что бы сгенерировать такую функцию необходимо понять что мы в ней видим.
     *         1) Константы
     *         2) Локльные переменные
     *         3) Присваивания
     *         4) Циклы или условные переходы
     *         5) Выполнение операций с параметрами
     *         6) Возвращение результата
     *         Рассмотрим код(он не имеет смысла но показывает все необходимые конструкции):
     *         int result = 1 + 2;
     *         if (result == 3)
     *         result += 2;
     *         return result;
     *         В псевдокоде это буде выглядеть:
     *         1: Создать переменную result. Проинициализировать значением сложения числа 1 и числа 2
     *         2: Если равны result и число 3 то перейти на строку 3, иначе перейти на строку 4
     *         3: Присвоить переменной result значением сложения result и числа 2
     *         4: Вернуть результат result.
     *         И сдесь мы видим что в данном случае почти все проводимые операции имеют по два параметра.
     *         Но есть варианты где у операции всего один параметр. (Например: return result, Math.sqrt, int result = 1)
     *         В данном случае искуственно добавим пустую операцию что бы у всех строк было по два парамета(return result + 0),
     *         или заменим аналогичной ей операцией с двумя параметрами(Math.sqrt(9) => Math.Pow(9, 0.5)).
     *         Таким образом в каждой строке кода будет операция с двумя параметрами.(см. calculatePiInTableView)
     *         Строки будут иметь формализованный вид:
     *         1) Возвращает ли строка значение
     *         2) К какой переменной присваивается значение
     *         3) Какая операция выполняется
     *         4) Первый параметр операции
     *         5) Второй параметр операции
     *         6) На какую строку перейти если значение текущей равно 1(true)
     *         Так программу можно предствить в виде матрицы.
     *         Строки которой будут одной строкой кода, а столбцы это параметр каждой конструкции(в последовательности формализации).
     *         Теперь осталось только перебрать все возможные варианты значений этой таблицы.
     *         Максимальными значениями каждого слобца будут(см. setMaxIndexes):
     *         1) Флаг возвращает ли значение. 2 варианта.
     *         2) Номер строки к которой присваивается значение. Вариантов столько же сколько и строк.
     *         3) Индекс выполняемой операции. Вариантов столько же сколько и фунцкий.
     *         4) Индекс значения первой переменной. Вариантов столько же сколько и строк + количество констант.
     *         5) Индекс значения второй переменной. -//-
     *         6) Номер строки на которую нужно перейти. Вариантов столько же сколько и строк.
     *         Итого реализацию calculatePiInTableView можно представить в виде calculatePIInTable;
     *         Используя алгоритмы комбинаторики можно объединить одну строку матрицы в большое число.
     *         Например:
     *         Значение переменных:
     *         {1, 0, 2, 3, 4, 0}
     *         Вариантов значений переменных:
     *         {2, 1, 9, 5, 5, 1}
     *         Объединение в одно число(см getIndex):
     *         (1 BigInteger.DivRem 1) + (0 BigInteger.DivRem 2) + (2 BigInteger.DivRem (2*1)) + (3 BigInteger.DivRem (2*1*9)) + (4 BigInteger.DivRem (2*1*5)) + (0 BigInteger.DivRem(2*1*5*5)) = 419
     *         Разбиение на массив(см. getCode):
     *         a[0] = (419 / 1) BigInteger.DivRem (2*1*9*5*5*1) = 1
     *         a[1] = (419 / (2)) BigInteger.DivRem (1*9*5*5*1) = 0
     *         a[2] = (419 / (2*1)) BigInteger.DivRem (9*5*5*1) = 2
     *         a[3] = (419 / (2*1*9)) BigInteger.DivRem (5*5*1) = 3
     *         a[4] = (419 / (2*1*9*5)) BigInteger.DivRem (5*1) = 4
     *         a[5] = (419 / (2*1*9*5*5)) BigInteger.DivRem (1) = 0
     *         Добавление единицы к данному числу будет являться следующей реализацией кода программы.
     *         Таким же способом возможно объединить и идентификаторы каждой строки.
     *         Итого в данном примере может быть (2*1*9*5*5*1) = 450 вариантов алгоритмов.
     *         Теперь просто переберая числа с 0 до 450-1 можно получить тела всех вариантов алгоритмов.
     *         Задача эта трудоемкая, поэтому её нужно разбить на равные части и рассчитывать на нескольких машинах.
     *         Нужно не забывать что существуют вечные циклы, и для предотвращения вечных циклов на выполнение каждому
     *         алгоритму необходимо дать ограниченное число действий. (см. invoke)
     *         <p/>
     *         Пример работающей программы можно найти в фунции main.
     *         <p/>
     *         Плюсы данного генератора:
     *         Простота реализации.
     *         Большую задачу можно разбить на блоки, и с каждым блоком сложность расчета увеличивается линейно.
     *         <p/>
     *         Минусы данного генератора:
     *         Наличие довольно большого количества нерабочих программ.
     *         <p/>
     *         Возможности:
     *         Можно распределить задачу генерации на несколько компьютеров.
     *         Возможно эта программа найдёт неизвестный алгоритм расчета числа Пи. Которй будет работать быстрее
     *         чем все существующие алгоритмы.
     *         Возможно изменить операции и константы под любую задачу. Но это гипотетически опасное действие. Так
     *         как оно может причинить вред выполняемому устройству, иным устройствам или человечеству.
     *         Можно основываясь на данном методе изменить данный алгоритм для расчета на квантовых компьютерах
     *         для того что бы скорость генерации была сравнима со скоростью генарции ДНК.
     *         <p/>
     *         Вдохновило меня, на создание данного генератора, выступление Денни Хиллиса на конференции TED 1994г.
     *         https://www.youtube.com/watch?v=xID7QCCJiXU
     *         Ну и конечно возможный конец света для органических организмов и восстанию роботов :)
     */
    class CodeGenerator
    {

        /**
         * Демонстрация фунция расчета чила Пи, которую нужно найти.
         */
        public double calculatePiInJavaView()
        {
            double result = 0;
            double dx = 1;
            for (int i = 1; i < 10000; i++)
            {
                result = result + dx;
                dx = 1.0 / (i * i);
            }
            return Math.Sqrt(result * 6.0);
        }

        /**
         * Демонстрация фунции расчета чила Пи в разбитом на отдельные строки виде.
         */
        public double calculatePiInMachineView()
        {
            double result = 0;
            double dx = 1;
            int i = 1;
            while (i < 10000)
            {
                result = result + dx;
                double sqrI = i * i;
                dx = 1.0 / sqrI;
                i++;
            }
            double resultMul6 = result * 6.0;
            return Math.Sqrt(resultMul6);
        }

        /**
         * Демонстрация вида функции расчета числа Пи перед превращением в матрицу кода.
         */
        public static double calculatePiInTableView()
        {
            double res = 0.0 + 0.0;
            double i = 1.0 + 0.0;
            double dx = 1.0 + 0.0;
            do
            {
                i = i + 1.0;
                res = res + dx;
                double sqrI = i * i;
                dx = 1.0 / sqrI;
            } while (i < Math.Pow(6.0, 6.0));
            double resMul6 = res * 6.0;
            return Math.Pow(resMul6, 0.5);
        }

        /**
         * Демонстрация кода расчета числа Пи в матричном виде.
         */
        public static int[,] calculatePIInTable = new int[,]{
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

        /**
         * Константы используемые в алгоритме расчета числа Пи.
         * Теоретически можно использовать любые константы, но это усложнит поиск.
         */
        static readonly double[] inputs = new double[] { 0.0, 1.0, 6.0, 0.5 };

        /*
            Расположение конструкций в столбцах таблицы кода.
         */
        const int returnPosInCodeLine = 0;
        const int valuePosInCodeLine = 1;
        const int funcPosInCodeLine = 2;
        const int param1PosInCodeLine = 3;
        const int param2PosInCodeLine = 4;
        const int jumpPosInCodeLine = 5;

        int lineCount; //Длинна строк программы.
        BigInteger inputVariableCount = new BigInteger(inputs.Length); //Количество констант.
        BigInteger returnFlagCount = new BigInteger(2); // Флаг что в этой строчке находится результат.
        BigInteger valuePlaceCount; // Мест куда можно присвоить значение.
        BigInteger functionCount = new BigInteger(9); // Количество возможных функций.
        BigInteger paramValuesCount; // Количество возможных значений одного параметра
        BigInteger jumpPlaceCount;  // Количество возможных переходов.
        public BigInteger maxIndexInOneLine; // Максимальный идентификатор в строки.
        public BigInteger maxIndexInProgram; // Максимальный идентификатор программы.

        /**
         * Когструктор генератора.
         *
         * @param lineCount количество строк в генерируемых программ.
         */
        public CodeGenerator(int lineCount)
        {
            this.lineCount = lineCount;
            BigInteger lineCountBigInt = new BigInteger(lineCount);
            valuePlaceCount = lineCountBigInt;
            paramValuesCount = BigInteger.Add(inputVariableCount, lineCountBigInt);
            jumpPlaceCount = lineCountBigInt;
            setMaxIndexInOneLine();
            setMaxIndexInProgram();
        }

        /**
         * Расчет максимального индекса в одной строке.
         */
        void setMaxIndexInOneLine()
        {
            maxIndexInOneLine = BigInteger.One;
            maxIndexInOneLine = BigInteger.Multiply(maxIndexInOneLine, returnFlagCount);
            maxIndexInOneLine = BigInteger.Multiply(maxIndexInOneLine, valuePlaceCount);
            maxIndexInOneLine = BigInteger.Multiply(maxIndexInOneLine, functionCount);
            maxIndexInOneLine = BigInteger.Multiply(maxIndexInOneLine, paramValuesCount); // Количество возможных значений параметра 1.
            maxIndexInOneLine = BigInteger.Multiply(maxIndexInOneLine, paramValuesCount); // Количество возможных значений параметра 2.
            maxIndexInOneLine = BigInteger.Multiply(maxIndexInOneLine, jumpPlaceCount);
        }

        /**
         * Расчет максимального индекса во всей программе.
         */
        void setMaxIndexInProgram()
        {
            setMaxIndexInOneLine();
            maxIndexInProgram = BigInteger.One;
            for (int i = 0; i < lineCount; i++)
                maxIndexInProgram = BigInteger.Multiply(maxIndexInProgram, maxIndexInOneLine);
        }

        /**
         * Фунция преобразования индентификатора программы в код программы.
         *
         * @param index идентификатор программы
         * @return код программы
         */
        int[,] getCode(BigInteger index)
        {
            int[,] code = new int[lineCount, 6];
            for (int i = 0; i < lineCount; i++)
            {
                BigInteger indexInLine = index % maxIndexInOneLine;
                code[i, returnPosInCodeLine] = (int)(indexInLine % returnFlagCount);
                indexInLine = indexInLine / returnFlagCount;
                code[i, valuePosInCodeLine] = (int)(indexInLine % valuePlaceCount);
                indexInLine = indexInLine / valuePlaceCount;
                code[i, funcPosInCodeLine] = (int)(indexInLine % functionCount);
                indexInLine = indexInLine / functionCount;
                code[i, param1PosInCodeLine] = (int)(indexInLine % paramValuesCount); // Параметр 1
                indexInLine = indexInLine / paramValuesCount;
                code[i, param2PosInCodeLine] = (int)(indexInLine % paramValuesCount); // Параметр 2
                indexInLine = indexInLine / paramValuesCount;
                code[i, jumpPosInCodeLine] = (int)(indexInLine % jumpPlaceCount);
                index = index / maxIndexInOneLine;
            }
            return code;
        }

        /**
         * Выполнение сгенерированного кода программы.
         *
         * @param code     код программы в табличном виде.
         * @param deadTime максимальное количество операций до прерывания работы программы.
         * @return результат работы программы. (null результата нет)
         */
        Double invoke(int[,] code, BigInteger deadTime)
        {
            double[] results = new double[lineCount]; //Столько же сколько и строк в функции
            BigInteger lifeTime = BigInteger.Zero;
            int activeLine = 0;
            while ((activeLine < lineCount) & (lifeTime < deadTime))
            {
                int param1Index = code[activeLine, param1PosInCodeLine];
                double param1Value = param1Index < inputs.Length ? inputs[param1Index] : results[param1Index - inputs.Length];
                int param2Index = code[activeLine, param2PosInCodeLine];
                double param2Value = param2Index < inputs.Length ? inputs[param2Index] : results[param2Index - inputs.Length];
                int functionIndex = code[activeLine, funcPosInCodeLine];
                switch (functionIndex)
                {
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
                        results[activeLine] = Math.Pow(param1Value, param2Value);
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
                int valueIndex = code[activeLine, valuePosInCodeLine];
                if (valueIndex != 0)
                    results[valueIndex >= activeLine ? valueIndex : valueIndex - 1] = results[activeLine];

                if (code[activeLine, returnPosInCodeLine] == 1)
                    return results[activeLine];

                lifeTime++;

                int jumpIndex = code[activeLine, jumpPosInCodeLine];
                if (jumpIndex != 0 && results[activeLine] == 1)
                {
                    activeLine = jumpIndex >= activeLine ? jumpIndex : jumpIndex - 1;
                    continue;
                }
                activeLine++;
            }
            return 0;
        }

        /**
         * Получение идентификатора программы по её коду. Необходима для тестирования.
         *
         * @param code код программы
         * @return идентификатор программы
         */
        BigInteger getIndex(int[,] code)
        {
            BigInteger index = BigInteger.Zero;
            BigInteger mulFactor = BigInteger.One;
            for (int i = 0; i < code.GetLength(0); i++)
            {
                for (int j = 0; j < 6; j++)
                {
                    switch (j)
                    {
                        case returnPosInCodeLine:
                            index += mulFactor * code[i, j];
                            mulFactor = mulFactor * returnFlagCount;
                            break;
                        case valuePosInCodeLine:
                            index += mulFactor * code[i, j];
                            mulFactor = mulFactor * valuePlaceCount;
                            break;
                        case funcPosInCodeLine:
                            index += mulFactor * code[i, j];
                            mulFactor = mulFactor * functionCount;
                            break;
                        case param1PosInCodeLine:
                            index += mulFactor * code[i, j];
                            mulFactor = mulFactor * paramValuesCount;
                            break;
                        case param2PosInCodeLine:
                            index += mulFactor * code[i, j];
                            mulFactor = mulFactor * paramValuesCount;
                            break;
                        case jumpPosInCodeLine:
                            index += mulFactor * code[i, j];
                            mulFactor = mulFactor * jumpPlaceCount;
                            break;
                    }
                }
            }
            return index;
        }

        /**
         * Проверка результата выполнения программы на равенство числу Пи.
         * Проверка не четкая что бы получить больше правильных результатов для анализа.
         *
         * @param piValue результат работы программы
         * @return флаг, является ли результат работы программы числом Пи.
         */
        bool testPiValue(Double piValue)
        {
            return piValue != 0 && Math.Round(piValue * 100.0) / 100.0 == 3.14;
        }

        /**
         * Функция для генерации блока задачи по поиску реализаций алгоритма числа Пи.
         *
         * @param startIndex начало блока генерации.
         * @param blockSize  размер блока.
         * @param deadTime   максимальное количество операций до прерывания работы программы.
         * @return список идентификаторов программ в блоке, которые нашли число Пи.
         */
        public List<BigInteger> generateBlockOfTask(BigInteger startIndex, BigInteger blockSize, BigInteger deadTime)
        {
            List<BigInteger> goodFunctionIndexes = new List<BigInteger>();

            BigInteger maxIndexInBlock = startIndex + blockSize;
            for (BigInteger index = startIndex;
                 index <= maxIndexInProgram && index < maxIndexInBlock;
                 index += 1)
            {
                int[,] code = getCode(index);
                Double functionResult = invoke(code, deadTime);
                if (testPiValue(functionResult))
                    goodFunctionIndexes.Add(index);
            }
            return goodFunctionIndexes;
        }


        private static void println(String message)
        {
            Console.WriteLine(message);
        }

        private static String codeToString(int[,] code)
        {
            String codeInString = "\n";
            for (int i = 0; i < code.GetLength(0); i++)
            {
                for (int j = 0; j < code.GetLength(1); j++)
                    codeInString += code[i, j] + " ";
                codeInString += "\n";
            }
            return codeInString;
        }

        /**
         * Тест работоспособности генератора.
         *
         * @param args
         */
        static void Main(string[] args)
        {
            CodeGenerator generator = new CodeGenerator(CodeGenerator.calculatePIInTable.GetLength(0));
            // Получаем идентификатор одного из алгоритмов расчета числа Пи.
            BigInteger index = generator.getIndex(CodeGenerator.calculatePIInTable);
            // Ищем алгоритмы в блоке начинающимся с искомого алгоритма и размером блока в котором ищем равным 3
            // и устанавливаем время выполнения каждой программы в 1 000 000 операций.
            BigInteger deadTime = new BigInteger(1000000);
            println("Начат поиск алгоритма числа Пи. Пожалуйста подождите...");
            List<BigInteger> goodFunctions = generator.generateBlockOfTask(index, new BigInteger(3), deadTime);
            // Проверяем нашли ли мы искомый алгоритм
            bool findCalculatePIInTableView = false;
            for (int i = 0; i < goodFunctions.Count; i++)
                if (goodFunctions[i] == index)
                    findCalculatePIInTableView = true;
            if (findCalculatePIInTableView == true)
            {
                println("Hello world! I'm alive!");
                println("Идентификатор найденной программы расчета числа Пи: " + index.ToString());
                println("Результат работы найденной программы: " + generator.invoke(generator.getCode(index), deadTime));
                println("Код найденной функции в табличном виде:" + codeToString(generator.getCode(index)));
                println("Код найденной функции можно посмотреть в функции calculatePiInTableView");
            }
            else
            {
                println("Функция не найдена.");
            }
            Console.ReadKey();
        }

    }
}
