package org.dream.utils;

import org.dream.domain.Attachment;
import org.dream.domain.MailConf;
import org.dream.exception.MailSendException;

import javax.activation.DataHandler;
import javax.activation.FileDataSource;
import javax.mail.*;
import javax.mail.internet.*;
import java.io.File;
import java.net.URL;
import java.util.*;

/**
 * 邮件发送类
 *
 * @author wuwc
 */
public class MailSendUtil {

    private static Session session;
    private static String  user;
    private MimeMessage msg;
    private String text;
    private String html;
    private List<MimeBodyPart> attachments = new ArrayList<MimeBodyPart>();

    /*MimeBodyPart对象代表一个MimeMessage对象内容的一部分。每个MimeBodyPart被认为有两部分：MIME类型和匹配这个类型的内容*/
    /**
     * SMTP_host
     */
    public static final String SMTP_HOST = "mail.smtp.host";

    /**
     * 163邮箱
     */
    public static final String SMTP_163 = "smtp.163.com";

    private static Properties defaultConfig(Boolean debug,Integer port) {
        Properties props = new Properties();
        props.put("mail.transport.protocol", "smtp");
        props.put("mail.port", port.toString());
        props.put("mail.smtp.auth", "true");
        props.put("mail.debug", debug.toString());
        //props.put("mail.debug", "true");
        //开启ssl加密
        props.put("mail.smtp.ssl.enable", "true");
        //props.put("mail.smtp.timeout", "10000");
        return props;
    }



    /**
     * smtp 163网易邮箱
     * @param debug 是否开启debug模式
     * @return
     */
    public static Properties SMTP_163(Boolean debug,Integer port) {
        Properties props = defaultConfig(debug,port);
        props.put(SMTP_HOST, SMTP_163);
        return props;
    }


    /**
     * 设置邮件发送基本的配置信息
     * @param props     email property conf
     * @param mailConf  email auth
     */
    public static MailSendUtil config(Properties props, MailConf mailConf) {
        props.setProperty("username", mailConf.getUsername());
        props.setProperty("password", mailConf.getPassword());
        user = mailConf.getUsername();
        session = Session.getInstance(props, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(mailConf.getUsername(), mailConf.getPassword());
            }
        });
        MailSendUtil mailSendUtil = new MailSendUtil();
        mailSendUtil.msg = new MimeMessage(session);
        return mailSendUtil;
    }


    /**
     * 添加邮箱的标题
     *
     * @param subject 邮箱的标题
     */
    public MailSendUtil subject(String subject) throws MailSendException {
        try {
            msg.setSubject(subject, "UTF-8");
        } catch (Exception e) {
            throw new MailSendException(e);
        }
        return this;
    }

    /**
     * 设置发送人的昵称
     *
     * @param nickName 昵称
     */
    public MailSendUtil from(String nickName) throws MailSendException {
        try {
            String encodeNickName = MimeUtility.encodeText(nickName);
            msg.setFrom(new InternetAddress(encodeNickName + " <" + user + ">"));
        } catch (Exception e) {
            throw new MailSendException(e);
        }
        return this;
    }

    /**
     * 设置收件人
     * @param to
     * @return
     * @throws MailSendException
     */
    public MailSendUtil to(String... to) throws MailSendException {
        try {
            return addRecipients(to, Message.RecipientType.TO);
        } catch (MessagingException e) {
            throw new MailSendException(e);
        }
    }


    /**
     * 设置抄送人
     * @param cc
     * @return
     * @throws MailSendException
     */
    public MailSendUtil cc(String... cc) throws MailSendException {
        try {
            return addRecipients(cc, Message.RecipientType.CC);
        } catch (MessagingException e) {
            throw new MailSendException(e);
        }
    }


    /**
     * 设置密送人
     * @param bcc
     * @return
     * @throws MailSendException
     */
    public MailSendUtil bcc(String... bcc) throws MailSendException {
        try {
            return addRecipients(bcc, Message.RecipientType.BCC);
        } catch (MessagingException e) {
            throw new MailSendException(e);
        }
    }


    private MailSendUtil addRecipients(String[] recipients, Message.RecipientType type) throws MessagingException {
        if(recipients.length>1){
            String result = Arrays.asList(recipients)
                    .toString().replace("(^/[|/]$)", "")
                    .replace(", ", ",");
            msg.setRecipients(type, InternetAddress.parse(result));
        }else if(recipients.length==1){
            msg.setRecipients(type, InternetAddress.parse(recipients[0].replace(";", ",")));
        }
        return this;
    }

    /**
     * 设置文本内容
     * @param text
     * @return
     */
    public MailSendUtil text(String text) {
        this.text = text;
        return this;
    }

    /**
     * 设置html内容
     * @param html
     * @return
     */
    public MailSendUtil html(String html) {
        this.html = html;
        return this;
    }

    /**
     * 设置附件
     * @param file 附件
     * @return
     * @throws MailSendException
     */
    public MailSendUtil attach(Attachment file) throws MailSendException {
        List<File> attachFileList = file.getAttachFileList();
        List<String> attachFileNameList = file.getAttachFileNameList();
        for (int i = 0; i < attachFileList.size(); i++) {
            if(i<attachFileNameList.size())
                attachments.add(createAttachment(attachFileList.get(i), attachFileNameList.get(i)));
            else
                attachments.add(createAttachment(attachFileList.get(i), null));
        }
        return this;
    }



    /**
     * 通过url设置附件
     * @param url
     * @param fileName
     * @return
     * @throws MailSendException
     */
    public MailSendUtil attachURL(URL url, String fileName) throws MailSendException {
        attachments.add(createURLAttachment(url, fileName));
        return this;
    }

    /**
     * 通过file设置附件
     * @param file
     * @param fileName
     * @return
     * @throws MailSendException
     */
    private MimeBodyPart createAttachment(File file, String fileName) throws MailSendException {
        MimeBodyPart attachmentPart = new MimeBodyPart();
        FileDataSource fds = new FileDataSource(file);
        //判断是否包含后缀
        if(null!=fileName&&!fileName.contains(".")){
            String oldName = file.getName();
            String suffix = oldName.substring(file.getName().lastIndexOf("."));
            fileName+=suffix;
        }else
            fileName = fds.getFile().getName();
        try {
            //中文名过长MimeUtility.encodeText方法会自动给添加下划线;超出长度会自动通过"/r","/n"——然而并不是这个问题
            String encodeName = MimeUtility.encodeText(fileName);
            //encodeName = encodeName.replaceAll("/+", "");
            attachmentPart.setDataHandler(new DataHandler(fds));
            attachmentPart.setFileName(encodeName);
        } catch (Exception e) {
            throw new MailSendException(e);
        }
        return attachmentPart;
    }

    /**
     * 通过url设置附件
     * @param url
     * @param fileName
     * @return
     * @throws MailSendException
     */
    private MimeBodyPart createURLAttachment(URL url, String fileName) throws MailSendException {
        MimeBodyPart attachmentPart = new MimeBodyPart();

        DataHandler dataHandler = new DataHandler(url);
        try {
            attachmentPart.setDataHandler(dataHandler);
            attachmentPart.setFileName(MimeUtility.encodeText(fileName));
        } catch (Exception e) {
            throw new MailSendException(e);
        }
        return attachmentPart;
    }


    /**
     * 发送邮件
     * multipart/mixed：附件。
     *
     * multipart/related：内嵌资源。
     *
     * multipart/alternative：纯文本与超文本共存
     * @throws MailSendException
     */
    public void send() throws MailSendException {
        if (text == null && html == null) {
            throw new MailSendException("请至少设置一种邮件发送的内容: Text or Html");
        }
        MimeMultipart cover;
        boolean usingAlternative = false;
        boolean hasAttachments = attachments.size() > 0;
        try {
            if (text != null && html == null) {
                // TEXT ONLY
                cover = new MimeMultipart("mixed");
                cover.addBodyPart(textPart());
            } else if (text == null && html != null) {
                // HTML ONLY
                cover = new MimeMultipart("mixed");
                cover.addBodyPart(htmlPart());
            } else {
                // HTML + TEXT
                cover = new MimeMultipart("alternative");
                cover.addBodyPart(textPart());
                cover.addBodyPart(htmlPart());
                usingAlternative = true;
            }

            MimeMultipart content = cover;
            if (usingAlternative && hasAttachments) {
                content = new MimeMultipart("mixed");
                content.addBodyPart(toBodyPart(cover));
            }

            for (MimeBodyPart attachment : attachments) {
                content.addBodyPart(attachment);
            }

            msg.setContent(content);
            msg.setSentDate(new Date());
            Transport.send(msg);
        } catch (Exception e) {
            throw new MailSendException(e);
        }
    }

    private MimeBodyPart toBodyPart(MimeMultipart cover) throws MessagingException {
        MimeBodyPart wrap = new MimeBodyPart();
        wrap.setContent(cover);
        return wrap;
    }

    private MimeBodyPart textPart() throws MessagingException {
        MimeBodyPart bodyPart = new MimeBodyPart();
        bodyPart.setText(text);
        return bodyPart;
    }

    private MimeBodyPart htmlPart() throws MessagingException {
        MimeBodyPart bodyPart = new MimeBodyPart();
        bodyPart.setContent(html, "text/html; charset=utf-8");
        return bodyPart;
    }

    public static void main(String[] args) {
        try {
            Attachment attachment = new Attachment();
            attachment.getAttachFileList().add(new File("C:/Users/X1 Carbon/Desktop/考完研后研究技术的第一天/复试.txt"));
            attachment.getAttachFileList().add(new File("C:/Users/X1 Carbon/Desktop/考完研后研究技术的第一天/简历/书-华东师范大学.docx"));
            attachment.getAttachFileNameList().add("小可爱的附件");
            //attachment.getAttachFileNameList().add("doc文档");
            MailSendUtil.config(SMTP_163(true,25),new MailConf())
                    .from("wicher")
                    .to("weichao_wu@163.com")
                    .cc("10285342@qq.com")
                    .bcc("10285342@qq.com")
                    .subject("##congratulations for your admission to escu##")
                    .text("this is the main text")
                    .attach(attachment)
                    .send();
        } catch (MailSendException e) {
            new MailSendException(e).printStackTrace();
        }
    }

}
