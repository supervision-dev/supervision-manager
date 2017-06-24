package com.rmbank.supervision.common.utils;

//import javax.mail.Authenticator;
//import javax.mail.Message;
//import javax.mail.Message.RecipientType;
//import javax.mail.PasswordAuthentication;
//import javax.mail.Session;
//import javax.mail.Transport;
//import javax.mail.internet.InternetAddress;
//import javax.mail.internet.MimeMessage;

public class EmailUtil
{
  private static String validate = "true";
  private String mailServerHost;
  private String userName;
  private String password;
  private String fromAddress;

  public EmailUtil(String mailServerHost, String fromAddress, String userName, String password)
  {
    this.mailServerHost = mailServerHost;
    this.fromAddress = fromAddress;
    this.userName = userName;
    this.password = password;
  }

  public boolean sendMail(String subject, String fromName, String to, String text)
  {
//    try
//    {
//      Properties props = new Properties();
//      props.put("mail.smtp.host", this.mailServerHost);
//      props.put("mail.smtp.auth", validate);
//      Session session = Session.getDefaultInstance(props, 
//        new Authenticator() {
//        protected PasswordAuthentication getPasswordAuthentication() {
//          return new PasswordAuthentication(EmailUtil.this.userName, 
//            EmailUtil.this.password);
//        }
//      });
//      MimeMessage message = new MimeMessage(session);
//      message.setFrom(new InternetAddress(this.fromAddress, fromName));
//      message.addRecipient(Message.RecipientType.TO, new InternetAddress(to));
//      message.setSubject(subject);
//      message.setSentDate(new Date());
//      message.setText(text);
//      Transport.send(message);
//      return true;
//    } catch (Exception e) {
//      e.printStackTrace();
//    }
	  return false;
  }

  public boolean sendHtmlMail(String subject, String fromName, String to, String text)
  {
//    try
//    {
//      Properties props = new Properties();
//      props.put("mail.smtp.host", this.mailServerHost);
//      props.put("mail.smtp.auth", validate);
//      Session session = Session.getDefaultInstance(props, 
//        new Authenticator() {
//        protected PasswordAuthentication getPasswordAuthentication() {
//          return new PasswordAuthentication(EmailUtil.this.userName, 
//            EmailUtil.this.password);
//        }
//      });
//      MimeMessage message = new MimeMessage(session);
//      message.setFrom(new InternetAddress(this.fromAddress, fromName));
//      message.addRecipient(Message.RecipientType.TO, new InternetAddress(to));
//      message.setSubject(subject);
//      message.setSentDate(new Date());
//      message.setContent(text, "text/html;charset=gb2312");
//      Transport.send(message);
//      return true;
//    } catch (Exception e) {
//      e.printStackTrace();
//    }
    return false;
  }
}