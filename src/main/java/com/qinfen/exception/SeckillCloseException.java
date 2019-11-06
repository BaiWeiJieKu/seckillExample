package com.qinfen.exception;

/**秒杀关闭异常
 * @author QinFen
 * @date 2019/11/3 0003 11:07
 */
public class SeckillCloseException extends SeckillException{
    public SeckillCloseException(String message) {
        super(message);
    }

    public SeckillCloseException(String message, Throwable cause) {
        super(message, cause);
    }
}
