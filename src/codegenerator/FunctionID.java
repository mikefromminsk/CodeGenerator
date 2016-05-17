package codegenerator;

import java.io.Serializable;
import java.math.BigInteger;

/**
 * Класс содержащий данные для восстановления кода функции.
 */
class FunctionID implements Serializable {
    /**
     * Количество строк в функции.
     */
    Integer lineSize;
    /**
     * Идентфикатор по которому восстанавливается номера функций.
     */
    BigInteger funcIndex;
    /**
     * Идентификатор по которому восстанавливаются присваивания.
     */
    BigInteger valueIndex;
    /**
     * Идентификатор по которому восстанавливаются параметры к функциям.
     */
    BigInteger paramsIndex;
    /**
     * Идентификатор по которому восстанавливаются условные операторы к функиям сравнения.
     */
    BigInteger jumpIndex;
    /**
     * Номер строчки в которой находится результат после выполнения функции.
     */
    Integer resultPosition;
}