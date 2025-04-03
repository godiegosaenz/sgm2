/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package util;

import com.origami.config.SisVars;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.activation.DataHandler;
import javax.activation.FileDataSource;
import javax.mail.BodyPart;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

/**
 *
 * @author Origami
 */
public class Email {
    
    protected String usuarioCorreo = SisVars.correo;
    protected String password = SisVars.pass;
    protected String rutaArchivo;
    protected String nombreArchivo;
    protected String destinatario;
    protected String copiaOcultaBCC;
    protected String copiaCC;
    protected String asunto;
    protected String mensaje;
    
    public Email(String rutaArchivo, String nombreArchivo, String destinatario, String copiaOcultaBCC, String copiaCC, String asunto, String mensaje) {
        this.rutaArchivo = rutaArchivo;
        this.nombreArchivo = nombreArchivo;
        this.destinatario = destinatario;
        this.copiaOcultaBCC = copiaOcultaBCC;
        this.copiaCC = copiaCC;
        this.asunto = asunto;
        this.mensaje = mensaje;
    }
    
    public Email(String destinatario, String mensaje) {
        this("", "", destinatario, null, null, "", mensaje);
    }

    public Email(String destinatario, String asunto, String mensaje) {
        this("", "", destinatario, null, null, asunto, mensaje);
    }
    public boolean sendMail() {
        try {
            Properties props = new Properties();
            props.setProperty("mail.transport.protocol", "smtp");
            props.setProperty("mail.smtp.host", SisVars.smtp_Host);
            props.setProperty("mail.smtp.starttls.enable", "true");
            props.setProperty("mail.smtp.port", SisVars.smtp_Port);
            props.setProperty("mail.smtp.user", usuarioCorreo);
            props.setProperty("mail.smtp.auth", "true");
            props.setProperty("mail.transport.protocol.rfc822", "smtp");
            
            Session session = Session.getDefaultInstance(props, null);
            BodyPart texto = new MimeBodyPart();
            texto.setText(mensaje);
            texto.setContent(mensaje, "text/html; charset=utf-8");
            
            BodyPart adjunto = new MimeBodyPart();
            if (!rutaArchivo.equals("")) {
                adjunto.setDataHandler(
                        new DataHandler(new FileDataSource(rutaArchivo)));
                adjunto.setFileName(nombreArchivo);
            }
            
            MimeMultipart multiParte = new MimeMultipart();
            multiParte.addBodyPart(texto);
            if (!rutaArchivo.equals("")) {
                multiParte.addBodyPart(adjunto);
            }
            
            MimeMessage message = new MimeMessage(session);
            message.setFrom(new InternetAddress(usuarioCorreo));
            message.addRecipients(
                    Message.RecipientType.TO, InternetAddress.parse(destinatario));
            message.setSubject(asunto);
            message.setContent(multiParte);
            if (copiaOcultaBCC != null) {
                message.addRecipients(Message.RecipientType.BCC,
                        InternetAddress.parse(copiaOcultaBCC));
            }
            if (copiaCC != null) {
                message.addRecipients(Message.RecipientType.CC,
                        InternetAddress.parse(copiaCC));
            }
            Transport t = session.getTransport();
            t.connect(usuarioCorreo, password);
            
            try{
                t.sendMessage(message, message.getAllRecipients());
            }catch(MessagingException e){
                Logger.getLogger(Email.class.getName()).log(Level.SEVERE, null, e);
            }
            
        } catch (MessagingException ex) {
            Logger.getLogger(Email.class.getName()).log(Level.SEVERE, null, ex); 
        } catch (Exception e) {
            Logger.getLogger(Email.class.getName()).log(Level.SEVERE, null, e);
        }
        return true;
    }
    
}
