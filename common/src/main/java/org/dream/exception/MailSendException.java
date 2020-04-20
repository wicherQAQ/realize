package org.dream.exception;

/**
 * 邮件发送Exception
 *
 * @author wuwc
 * @date 2019/4/4
 */
public class MailSendException extends Exception {

    public MailSendException() {
    }

    public MailSendException(String message) {
        super(message);
    }

    public MailSendException(Throwable cause) {
        super(cause);
    }
}
