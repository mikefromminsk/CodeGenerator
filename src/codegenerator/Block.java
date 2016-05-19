package codegenerator;

import java.io.Serializable;
import java.math.BigInteger;
import java.util.ArrayList;

/**
 * Блок. В нем находится вся информации о части задачи генерации.
 */
class Block implements Serializable {
    /**
     * Уникальный идентификатор блока.
     */
    Long ID;
    /**
     * Идентификатор устройства которое майнит блок.
     */
    String mac;
    /**
     * Время окончания блока задачи.
     */
    Long endTime;
    /**
     * Функции давшие правильный результат
     */
    ArrayList<FunctionID> goodFunctionID = new ArrayList<FunctionID>();
    /**
     * Идентификатор выполняющего потока.
     */
    Long threadID = 0L;
    /**
     * Закончил ли поток генерацию.
     */
    Boolean threadEnd = false;
}


