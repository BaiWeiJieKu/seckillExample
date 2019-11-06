package com.qinfen.exception;

/**秒杀相关业务异常
 * @author QinFen
 * @date 2019/11/3 0003 11:08
 */
public class SeckillException extends RuntimeException{
    public SeckillException(String message) {
        super(message);
    }

    public SeckillException(String message, Throwable cause) {
        super(message, cause);
    }
}
