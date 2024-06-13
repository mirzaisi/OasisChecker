package com.oasischecker;

import java.util.ArrayList;
import java.util.Properties;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;


public class Mail {
    private String mailSubject;
    private String mailContent;
    private String senderAdress;
    private String senderPassword;
    private String receiverAdress;

    private static final String emailHost = "smtp.gmail.com";

    
    public Mail(String mailSubject, String mailContent, String senderAdress, String senderPassword, String receiverAdress) {
        this.mailSubject = mailSubject;
        this.mailContent = mailContent;
        this.senderAdress = senderAdress;
        this.senderPassword = senderPassword;
        this.receiverAdress = receiverAdress;
    }

    public String getMailSubject() {
        return mailSubject;
    }


    public void setMailSubject(String mailSubject) {
        this.mailSubject = mailSubject;
    }


    public String getMailContent() {
        return mailContent;
    }


    public void setMailContent(String mailContent) {
        this.mailContent = mailContent;
    }

    
    public String getSenderAdress() {
        return senderAdress;
    }

    public void setSenderAdress(String senderAdress) {
        this.senderAdress = senderAdress;
    }

    public String getSenderPassword() {
        return senderPassword;
    }

    public void setSenderPassword(String senderPassword) {
        this.senderPassword = senderPassword;
    }

    public String getReceiverAdress() {
        return receiverAdress;
    }

    public void setReceiverAdress(String receiverAdress) {
        this.receiverAdress = receiverAdress;
    }

    private MimeMessage composeMail(Session session) throws MessagingException{

        MimeMessage mimeMessage = new MimeMessage(session);
        mimeMessage.addRecipient(Message.RecipientType.TO, new InternetAddress(receiverAdress));
        mimeMessage.setSubject(this.getMailSubject());
        
        MimeBodyPart bodyPart = new MimeBodyPart();
        bodyPart.setContent(this.getMailContent(), "text/html");

        MimeMultipart multipart = new MimeMultipart();
        multipart.addBodyPart(bodyPart);
        mimeMessage.setContent(multipart);

        return mimeMessage;

    }
 
    public Session setupServerProperties() {
        Properties serverProperties = new Properties();
        serverProperties.put("mail.smtp.port", "587");
        serverProperties.put("mail.smtp.auth", "true");
        serverProperties.put("mail.smtp.starttls.enable", "true");
        Session session  = Session.getDefaultInstance(serverProperties, null);
        
        return session;
    }

    private void sendMail(Session session, MimeMessage mimeMessage) throws MessagingException{
        Transport transport = null;
        try {
            transport = session.getTransport("smtp");
            transport.connect(emailHost, senderAdress, senderPassword);
            transport.sendMessage(mimeMessage, mimeMessage.getAllRecipients());
            transport.close();
            System.out.println("Mail is sent with changed course info.");
        } catch (MessagingException me) {
            throw new MessagingException();
        } finally {
            if (transport != null) {
                transport.close();
            }
        }

    }

    public static void generateMail(ArrayList<String> courseCodesToAddToMail, String senderAdress, String senderPassword, String receiverAdress) throws MessagingException{

        String mailSubject = "";
        if (courseCodesToAddToMail.size() == 1) {
            mailSubject = "[NEW OASIS Grade] New grade published in course " + courseCodesToAddToMail.get(0) + "!";
        } else {
            mailSubject = "[NEW OASIS Grades] New grades are published in multiple courses!";
        }

        StringBuilder mailContentBuilder = new StringBuilder();
        mailContentBuilder.append("New grades were detected in course(s): ");
        for (String courseCodeToAddToMail : courseCodesToAddToMail) {
            mailContentBuilder.append(courseCodeToAddToMail + ", ");
        }

        String mailContent = mailContentBuilder.toString();

        Mail mailToSend = new Mail(mailSubject, mailContent, senderAdress, senderPassword, receiverAdress);
        Session session = mailToSend.setupServerProperties();
        MimeMessage mimeMessage = mailToSend.composeMail(session);

        mailToSend.sendMail(session, mimeMessage);
        
    }
}
