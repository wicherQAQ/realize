package org.dream.exception;

/**
 * 文件异常
 *
 * @author wuwc
 * @date 2019/4/4
 */
public class AttachmentException extends Exception {

    public AttachmentException() {
    }

    public AttachmentException(String message) {
        super(message);
    }

    public AttachmentException(Throwable cause) {
        super(cause);
    }
}
