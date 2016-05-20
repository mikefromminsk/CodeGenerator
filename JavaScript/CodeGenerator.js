/**
 * @author  Гайдук Михаил (Минск 2016г. x29a100@mail.ru vk.com/x29a100 +375255451247)
 *
 *          Генератор кода, для поиска реализаций вычисления числа Пи.
 *
 *          Задача данного класса показать что любое решение задачи можно найти перебором.
 *
 *          Введение:
 *
 *          Даннай метод поиска решения был скопирован с живых организмов, которые используют механизм ДНК.
 *          Он может создавать новые системы и алгоритмы которых ранее не было. Это возможно только с помощью
 *          перебора всех возможных вариантов организмов(программ) которые способны выжить(выполняться).
 *          А так же ДНК имеет возможность к репликации. Данный механизм скрещивает в случайном порядке уже
 *          сгенерированные системы(ООП), что позволяет быстрее и эффективнее создавать большие организмы.
 *
 *          Описание метода генерации кода:
 *
 *          Для тестирования я взял реализацию расчета числа Пи без расчета до точности знака. (см. calculatePiInJavaView)
 *          Чем проще функция тем быстрее её можно найти перебором.
 *          Такой вид функции является простым для человеческого восприятия, но он отличается от машинного представления.
 *          Например одна строка в Java "dx = 1.0 / (i * i);" раскладывается в несколько действий.
 *          А именно на: умножение "(i * i)" и деление "1.0 / x".
 *          Так что в машинном представлении расчет числа Пи будет иметь другой(менее крссивый) вид. (см. calculatePiInMachineView)
 *              Для того что бы сгенерировать такую функцию необходимо понять что мы в ней видим.
 *          1) Константы
 *          2) Локльные переменные
 *          3) Присваивания
 *          4) Циклы или условные переходы
 *          5) Выполнение операций с параметрами
 *          6) Возвращение результата
 *              Рассмотрим код(он не имеет смысла но показывает все необходимые конструкции):
 *          int result = 1 + 2;
 *          if (result == 3)
 *              result += 2;
 *          return result;
 *              В псевдокоде это буде выглядеть:
 *          1: Создать переменную result. Проинициализировать значением сложения числа 1 и числа 2
 *          2: Если равны result и число 3 то перейти на строку 3, иначе перейти на строку 4
 *          3: Присвоить переменной result значением сложения result и числа 2
 *          4: Вернуть результат result.
 *              И сдесь мы видим что в данном случае почти все проводимые операции имеют по два параметра.
 *          Но есть варианты где у операции всего один параметр. (Например: return result, Math.sqrt, int result = 1)
 *          В данном случае искуственно добавим пустую операцию что бы у всех строк было по два парамета(return result + 0),
 *          или заменим аналогичной ей операцией с двумя параметрами(Math.sqrt(9) => Math.pow(9, 0.5)).
 *              Таким образом в каждой строке кода будет операция с двумя параметрами.(см. calculatePiInTableView)
 *          Строки будут иметь формализованный вид:
 *          1) Возвращает ли строка значение
 *          2) К какой переменной присваивается значение
 *          3) Какая операция выполняется
 *          4) Первый параметр операции
 *          5) Второй параметр операции
 *          6) На какую строку перейти если значение текущей равно 1(true)
 *              Так программу можно предствить в виде матрицы.
 *          Строки которой будут одной строкой кода, а столбцы это параметр каждой конструкции(в последовательности формализации).
 *              Теперь осталось только перебрать все возможные варианты значений этой таблицы.
 *          Максимальными значениями каждого слобца будут(см. setMaxIndexes):
 *          1) Флаг возвращает ли значение. 2 варианта.
 *          2) Номер строки к которой присваивается значение. Вариантов столько же сколько и строк.
 *          3) Индекс выполняемой операции. Вариантов столько же сколько и фунцкий.
 *          4) Индекс значения первой переменной. Вариантов столько же сколько и строк + количество констант.
 *          5) Индекс значения второй переменной. -//-
 *          6) Номер строки на которую нужно перейти. Вариантов столько же сколько и строк.
 *          Итого реализацию calculatePiInTableView можно представить в виде calculatePIInTable;
 *              Используя алгоритмы комбинаторики можно объединить одну строку матрицы в большое число.
 *          Например:
 *          Значение переменных:
 *          {1, 0, 2, 3, 4, 0}
 *          Вариантов значений переменных:
 *          {2, 1, 9, 5, 5, 1}
 *          Объединение в одно число(см getIndex):
 *          (1 mod 1) + (0 mod 2) + (2 mod (2*1)) + (3 mod (2*1*9)) + (4 mod (2*1*5)) + (0 mod(2*1*5*5)) = 419
 *          Разбиение на массив(см. getCode):
 *          a[0] = (419 / 1) mod (2*1*9*5*5*1) = 1
 *          a[1] = (419 / (2)) mod (1*9*5*5*1) = 0
 *          a[2] = (419 / (2*1)) mod (9*5*5*1) = 2
 *          a[3] = (419 / (2*1*9)) mod (5*5*1) = 3
 *          a[4] = (419 / (2*1*9*5)) mod (5*1) = 4
 *          a[5] = (419 / (2*1*9*5*5)) mod (1) = 0
 *          Добавление единицы к данному числу будет являться следующей реализацией кода программы.
 *          Таким же способом возможно объединить и идентификаторы каждой строки.
 *          Итого в данном примере может быть (2*1*9*5*5*1) = 450 вариантов алгоритмов.
 *          Теперь просто переберая числа с 0 до 450-1 можно получить тела всех вариантов алгоритмов.
 *          Задача эта трудоемкая, поэтому её нужно разбить на равные части и рассчитывать на нескольких машинах.
 *          Нужно не забывать что существуют вечные циклы, и для предотвращения вечных циклов на выполнение каждому
 *          алгоритму необходимо дать ограниченное число действий. (см. invoke)
 *
 *          Пример работающей программы можно найти в фунции main.
 *
 *          Плюсы данного генератора:
 *              Простота реализации.
 *              Большую задачу можно разбить на блоки, и с каждым блоком сложность расчета увеличивается линейно.
 *
 *          Минусы данного генератора:
 *              Наличие довольно большого количества нерабочих программ.
 *
 *          Возможности:
 *              Можно распределить задачу генерации на несколько компьютеров.
 *              Возможно эта программа найдёт неизвестный алгоритм расчета числа Пи. Которй будет работать быстрее
 *              чем все существующие алгоритмы.
 *              Возможно изменить операции и константы под любую задачу. Но это гипотетически опасное действие. Так
 *              как оно может причинить вред выполняемому устройству, иным устройствам или человечеству.
 *              Можно основываясь на данном методе изменить данный алгоритм для расчета на квантовых компьютерах
 *              для того что бы скорость генерации была сравнима со скоростью генарции ДНК.
 *
 *          Вдохновило меня, на создание данного генератора, выступление Денни Хиллиса на конференции TED 1994г.
 *          https://www.youtube.com/watch?v=xID7QCCJiXU
 *          Ну и конечно возможный конец света для органических организмов и восстанию роботов :)
 */


    /**
     * Демонстрация фунция расчета чила Пи, которую нужно найти.
     */
function calculatePiInJavaView(){
        var result = 0;
        var dx = 1;
        for (var i = 1; i < 10000; i++) {
            result = result + dx;
            dx = 1.0 / (i * i);
        }
        return Math.sqrt(result * 6.0);
    }
    /**
     * Демонстрация фунции расчета чила Пи в разбитом на отдельные строки виде.
     */
    function calculatePiInMachineView() {
        var result = 0;
        var dx = 1;
        var i = 1;
        while (i < 10000) {
            result = result + dx;
            var sqrI = i * i;
            dx = 1.0 / sqrI;
            i++;
        }
        var resultMul6 = result * 6.0;
        return Math.sqrt(resultMul6);
    }

    /**
     * Демонстрация вида функции расчета числа Пи перед превращением в матрицу кода.
     */
    function calculatePiInTableView() {
        var res = 0.0 + 0.0;
        var i = 1.0 + 0.0;
        var dx = 1.0 + 0.0;
        do {
            i = i + 1.0;
            res = res + dx;
            var sqrI = i * i;
            dx = 1.0 / sqrI;
        } while (i < Math.pow(6.0, 6.0));
        var resMul6 = res * 6.0;
        return Math.pow(resMul6, 0.5);
    }

    /**
     * Демонстрация кода расчета числа Пи в матричном виде.
     */
    var calculatePIInTable = [
    [0, 0, 0, 0, 0, 0],
    [0, 0, 0, 1, 0, 0],
    [0, 0, 0, 1, 0, 0],
    [0, 2, 0, 5, 1, 0],
    [0, 1, 0, 4, 6, 0],
    [0, 0, 2, 5, 5, 0],
    [0, 3, 3, 1, 9, 0],
    [0, 0, 4, 2, 2, 0],
    [0, 0, 8, 5, 11, 4],
    [0, 0, 2, 4, 2, 0],
    [1, 0, 4, 13, 3, 0]
];

/**
 * Константы используемые в алгоритме расчета числа Пи.
 * Теоретически можно использовать любые константы, но это усложнит поиск.
 */
var inputs = [0.0, 1.0, 6.0, 0.5];

/*
 Расположение конструкций в столбцах таблицы кода.
 */
var returnPosInCodeLine = 0;
var valuePosInCodeLine = 1;
var funcPosInCodeLine = 2;
var param1PosInCodeLine = 3;
var param2PosInCodeLine = 4;
var jumpPosInCodeLine = 5;

var lineCount; //Длинна строк программы.
var inputVariableCount = new BigNumber(inputs.length); //Количество констант.
var returnFlagCount = new BigNumber(2); // Флаг что в этой строчке находится результат.
var valuePlaceCount; // Мест куда можно присвоить значение.
var functionCount = new BigNumber(9); // Количество возможных функций.
var paramValuesCount; // Количество возможных значений одного параметра
var jumpPlaceCount;  // Количество возможных переходов.
var maxIndexInOneLine; // Максимальный идентификатор в строки.
var maxIndexInProgram; // Максимальный идентификатор программы.
/**
 * Когструктор генератора.
 * @param lineCount количество строк в генерируемых программ.
 */
function initCodeGenerator(lineCount) {
    this.lineCount = lineCount;
    var lineCountBigInt = new BigNumber(lineCount);
    valuePlaceCount = lineCountBigInt;
    paramValuesCount = inputVariableCount.add(lineCountBigInt);
    jumpPlaceCount = lineCountBigInt;
    setMaxIndexInOneLine();
    setMaxIndexInProgram();
}

/**
 * Расчет максимального индекса в одной строке.
 */
function setMaxIndexInOneLine() {
    maxIndexInOneLine = new BigNumber(1);
    maxIndexInOneLine = maxIndexInOneLine.multiply(returnFlagCount);
    maxIndexInOneLine = maxIndexInOneLine.multiply(valuePlaceCount);
    maxIndexInOneLine = maxIndexInOneLine.multiply(functionCount);
    maxIndexInOneLine = maxIndexInOneLine.multiply(paramValuesCount); // Количество возможных значений параметра 1.
    maxIndexInOneLine = maxIndexInOneLine.multiply(paramValuesCount); // Количество возможных значений параметра 2.
    maxIndexInOneLine = maxIndexInOneLine.multiply(jumpPlaceCount);
}

/**
 * Расчет максимального индекса во всей программе.
 */
function setMaxIndexInProgram() {
    setMaxIndexInOneLine();
    maxIndexInProgram = new BigNumber(1);
    for (var i = 0; i < lineCount; i++)
    maxIndexInProgram = maxIndexInProgram.multiply(maxIndexInOneLine);
}

function matrixArray(rows,columns){
    var arr = new Array();
    for(var i=0; i<rows; i++){
        arr[i] = new Array();
        for(var j=0; j<columns; j++){
            arr[i][j] = 0;
        }
    }
    return arr;
}

/**
 * Фунция преобразования индентификатора программы в код программы.
 * @param index идентификатор программы
 * @return код программы
 */
function getCode(index) {
    var code = matrixArray(lineCount, 6);

    for (var i = 0; i < lineCount; i++) {
        var indexInLine = index.mod(maxIndexInOneLine);
        code[i][returnPosInCodeLine] = parseInt(indexInLine.mod(returnFlagCount).intPart().toString());
        indexInLine = indexInLine.divide(returnFlagCount);
        code[i][valuePosInCodeLine] = parseInt(indexInLine.mod(valuePlaceCount).intPart().toString());
        indexInLine = indexInLine.divide(valuePlaceCount);
        code[i][funcPosInCodeLine] = parseInt(indexInLine.mod(functionCount).intPart().toString());
        indexInLine = indexInLine.divide(functionCount);
        code[i][param1PosInCodeLine] = parseInt(indexInLine.mod(paramValuesCount).intPart().toString()); // Параметр 1
        indexInLine = indexInLine.divide(paramValuesCount);
        code[i][param2PosInCodeLine] = parseInt(indexInLine.mod(paramValuesCount).intPart().toString()); // Параметр 2
        indexInLine = indexInLine.divide(paramValuesCount);
        code[i][jumpPosInCodeLine] = parseInt(indexInLine.mod(jumpPlaceCount).intPart().toString());
        index = index.divide(maxIndexInOneLine);
    }
    return code;
}


/**
 * Выполнение сгенерированного кода программы.
 * @param code код программы в табличном виде.
 * @param deadTime максимальное количество операций до прерывания работы программы.
 * @return результат работы программы. (null результата нет)
 */
function invoke(code, deadTime) {
    var results = new Array(); //Столько же сколько и строк в функции
    for (var i = 0; i < lineCount; i++)
        results.push(0);
    var lifeTime = new BigNumber(0);
    var activeLine = 0;
    while ((activeLine < lineCount) & (lifeTime.compare(deadTime) < 0)) {
        var param1Index = code[activeLine][param1PosInCodeLine];
        var param1Value = param1Index < inputs.length ? inputs[param1Index] : results[param1Index - inputs.length];
        var param2Index = code[activeLine][param2PosInCodeLine];
        var param2Value = param2Index < inputs.length ? inputs[param2Index] : results[param2Index - inputs.length];
        var functionIndex = code[activeLine][funcPosInCodeLine];
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
        var valueIndex = code[activeLine][valuePosInCodeLine];
        if (valueIndex != 0)
            results[valueIndex >= activeLine ? valueIndex : valueIndex - 1] = results[activeLine];

        if (code[activeLine][returnPosInCodeLine] == 1)
            return results[activeLine];

        lifeTime = lifeTime.add(new BigNumber(1));

        var jumpIndex = code[activeLine][jumpPosInCodeLine];
        if (jumpIndex != 0 && results[activeLine] == 1) {
            activeLine = jumpIndex >= activeLine ? jumpIndex : jumpIndex - 1;
            continue;
        }
        activeLine++;
    }
    return null;
}

/**
 * Получение идентификатора программы по её коду. Необходима для тестирования.
 * @param code код программы
 * @return идентификатор программы
 */
function getIndex(code) {
    var index = new BigNumber(0);
    var mulFactor = new BigNumber(1);
    for (var i = 0; i < code.length; i++) {
        for (var j = 0; j < 6; j++) {
            switch (j) {
                case returnPosInCodeLine:
                    index = index.add(mulFactor.multiply(new BigNumber(code[i][j])));
                    mulFactor = mulFactor.multiply(returnFlagCount);
                    break;
                case valuePosInCodeLine:
                    index = index.add(mulFactor.multiply(new BigNumber(code[i][j])));
                    mulFactor = mulFactor.multiply(valuePlaceCount);
                    break;
                case funcPosInCodeLine:
                    index = index.add(mulFactor.multiply(new BigNumber(code[i][j])));
                    mulFactor = mulFactor.multiply(functionCount);
                    break;
                case param1PosInCodeLine:
                    index = index.add(mulFactor.multiply(new BigNumber(code[i][j])));
                    mulFactor = mulFactor.multiply(paramValuesCount);
                    break;
                case param2PosInCodeLine:
                    index = index.add(mulFactor.multiply(new BigNumber(code[i][j])));
                    mulFactor = mulFactor.multiply(paramValuesCount);
                    break;
                case jumpPosInCodeLine:
                    index = index.add(mulFactor.multiply(new BigNumber(code[i][j])));
                    mulFactor = mulFactor.multiply(jumpPlaceCount);
                    break;
            }
        }
    }
    return index;
}

/**
 * Проверка результата выполнения программы на равенство числу Пи.
 * Проверка не четкая что бы получить больше правильных результатов для анализа.
 * @param piValue результат работы программы
 * @return флаг, является ли результат работы программы числом Пи.
 */
function testPiValue(piValue) {
    return piValue != null && Math.round(piValue * 100.0) / 100.0 == 3.14;
}

/**
 * Функция для генерации блока задачи по поиску реализаций алгоритма числа Пи.
 * @param startIndex начало блока генерации.
 * @param blockSize размер блока.
 * @param deadTime максимальное количество операций до прерывания работы программы.
 * @return список идентификаторов программ в блоке, которые нашли число Пи.
 */
function generateBlockOfTask(startIndex, blockSize, deadTime) {
    var goodFunctionIndexes = new Array();

    var maxIndexInBlock = startIndex.add(blockSize);
    for (var index = startIndex;
    index.compare(maxIndexInProgram) <= 0 &&
    index.compare(maxIndexInBlock) < 0;
    index = index.add(new BigNumber(1))) {
        var code = getCode(index);
        var functionResult = invoke(code, deadTime);
        if (testPiValue(functionResult))
            goodFunctionIndexes.push(index);
    }
    return goodFunctionIndexes;
}

function println(message){
    console.log(message);
    document.body.innerHTML += message + "<br>";
}

/**
 * Тест работоспособности генератора.
 * @param args
 */
window.onload =
    function testCodeGenerator() {
        setTimeout(function(){
            initCodeGenerator(calculatePIInTable.length);
            // Получаем идентификатор одного из алгоритмов расчета числа Пи.
            var index = getIndex(calculatePIInTable);
            // Ищем алгоритмы в блоке начинающимся с искомого алгоритма и размером блока в котором ищем равным 3
            // и устанавливаем время выполнения каждой программы в 1 000 000 операций.
            var deadTime = new BigNumber(1000000);
            var goodFunctions = generateBlockOfTask(index, new BigNumber(3), deadTime);
            // Проверяем нашли ли мы искомый алгоритм
            var findCalculatePIInTableView = false;
            for (var i = 0; i < goodFunctions.length; i++)
                if (goodFunctions[i].compare(index) == 0)
                    findCalculatePIInTableView = true;
            if (findCalculatePIInTableView == true){
                println("Hello world! I'm alive!");
                println("Идентификатор найденной программы расчета числа Пи: " + index.toString());
                println("Результат работы найденной программы: " + invoke(getCode(index), deadTime));
                println("Код найденной функции в табличном виде:" +  JSON.stringify(getCode(index)));
                println("Код найденной функцииможно посмотреть в функции calculatePiInTableView");
            }else{
                println("Фунция не найдена.");
            }
        }, 500);

}

