package com.qinfen.exception;

/**重复秒杀异常（运行时异常）
 * @author QinFen
 * @date 2019/11/3 0003 11:05
 */
public class RepeatKillException extends SeckillException{
    public RepeatKillException(String message) {
        super(message);
    }

    public RepeatKillException(String message, Throwable cause) {
        super(message, cause);
    }
}
