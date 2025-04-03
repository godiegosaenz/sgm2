package util;

import java.io.Serializable;
import java.util.Date;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

/**
 * @author carlosloorvargas
 */
public class EmailUtil implements Serializable {

    private String to;
    private String subject;
    private String content;
    private String bcc;
    private String cc;

    private EmailConfigs getConfigs() {
        return new EmailConfigs();
    }

    public Boolean sendEmail(String to, String subject, String content, String bcc, String cc) {
        try {
            if (this.getConfigs().isMailServerUseSSL() == true) {
                return this.sendAuthEmail(to, subject, content, bcc, cc);
            } else {
                return this.sendBasicEmail(to, subject, content, bcc, cc);
            }
        } catch (Exception e) {
            Logger.getLogger(EmailUtil.class.getName()).log(Level.SEVERE, null, e);
        }
        return false;
    }

    public Boolean sendBasicEmail(String to, String subject, String content, String bcc, String cc) {
        boolean basic = true;
        try {
            final EmailConfigs ec = this.getConfigs();
            Integer port = ec.getMailServerPort();
            Properties props = new Properties();
            props.setProperty("mail.smtp.host", ec.getMailServerHost());
            props.setProperty("mail.smtp.port", port.toString());
            Session sess = Session.getInstance(props, null);
            Message message = new MimeMessage(sess);
            message.setFrom(new InternetAddress(ec.getMailServerUsername()));
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(to));
            message.setSentDate(new Date());
            if (bcc != null) {
                message.addRecipients(Message.RecipientType.BCC,
                        InternetAddress.parse(bcc));
            }
            if (cc != null) {
                message.addRecipients(Message.RecipientType.CC,
                        InternetAddress.parse(cc));
            }
            message.setText(content);
            message.setSubject(subject);
            message.setContent(content, "text/html; charset=utf-8");
            Transport.send(message);
        } catch (MessagingException e) {
            basic = false;
            Logger.getLogger(EmailUtil.class.getName()).log(Level.SEVERE, null, e);
        }
        return basic;
    }

    public Boolean sendAuthEmail(String to, String subject, String content, String bcc, String cc) {
        boolean auth = true;
        try {
            final EmailConfigs ec = this.getConfigs();
            Integer port = ec.getMailServerPort();
            Properties props = new Properties();
            props.setProperty("mail.transport.protocol", "smtp");
            props.setProperty("mail.smtp.host", ec.getMailServerHost());
            props.setProperty("mail.smtp.starttls.enable", "true");
            props.setProperty("mail.smtp.auth", "true");
            props.setProperty("mail.smtp.port", port.toString());
            Session sess = Session.getDefaultInstance(props, new javax.mail.Authenticator() {
                @Override
                protected PasswordAuthentication getPasswordAuthentication() {
                    return new PasswordAuthentication(ec.getMailServerUsername(), ec.getMailServerPassword());
                }
            });
            Message message = new MimeMessage(sess);
            message.setFrom(new InternetAddress(ec.getMailServerUsername()));
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(to));
            if (bcc != null) {
                message.addRecipients(Message.RecipientType.BCC,
                        InternetAddress.parse(bcc));
            }
            if (cc != null) {
                message.addRecipients(Message.RecipientType.CC,
                        InternetAddress.parse(cc));
            }
            message.setText(content);
            message.setSentDate(new Date());
            message.setSubject(subject);
            message.setContent(content, "text/html; charset=utf-8");
            try {
                Transport.send(message);
            } catch (MessagingException messagingException) {
            }
        } catch (MessagingException e) {
            auth = false;
            Logger.getLogger(EmailUtil.class.getName()).log(Level.SEVERE, null, e);
        }
        return auth;
    }

    public String getTo() {
        return to;
    }

    public void setTo(String to) {
        this.to = to;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getBcc() {
        return bcc;
    }

    public void setBcc(String bcc) {
        this.bcc = bcc;
    }

    public String getCc() {
        return cc;
    }

    public void setCc(String cc) {
        this.cc = cc;
    }

}
