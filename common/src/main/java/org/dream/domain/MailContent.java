package org.dream.domain;

import lombok.Getter;
import lombok.Setter;

/**
 * 邮件发送内容
 */
@Getter
@Setter
public class MailContent {

    private String subject;
    private String nickname;

    /**
     * 是否群发，默认为false
     */
    private Boolean isMulti = false;
    /*
     * 收件人
     */
    private String[] to;
    /**
     * 抄送人
     */
    private String[] cc;

    /**
     * 密送人
     */
    private String[] bcc;

    /**
     * 文本内容
     */
    private String text;

    /**
     * 附件
     */
    private Attachment attachment;
}
