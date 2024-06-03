package com.smart.Service;

import java.io.File;
import java.io.IOException;
import java.util.Properties;

import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

import org.springframework.stereotype.Service;

@Service
public class EmailService {
	public boolean sendEmail(String subject,String message,String to)
	{
		// get system properties
		Properties properties=System.getProperties();
		String host="smtp.gmail.com";
		properties.put("mail.smtp.host",host);
		properties.put("mail.smtp.port","465");
		properties.put("mail.smtp.ssl.enable","true");
		properties.put("mail.smtp.auth","true");
		String from = "hppatel1235@gmail.com";
		String password="vfivjkybwheztnbk";
		
		try {
			// get current session
			Session session=Session.getInstance(properties,new Authenticator() {

				@Override
				protected PasswordAuthentication getPasswordAuthentication() {
				return new PasswordAuthentication(from,password);
				}
			});
			session.setDebug(true);
			
			// compose the message
			MimeMessage mail=new MimeMessage(session);
			mail.setFrom(from);
			mail.addRecipient(Message.RecipientType.TO,new InternetAddress(to));
			
			// sending text messages
			//mail.setText(message);
			
			// sending text messages with attachments
			String path="C:\\Users\\himanshu.patel\\Pictures\\Saved Pictures\\download.jpg";
			File file=new File(path);
			MimeMultipart mimeMultipart=new MimeMultipart();
			MimeBodyPart textMime=new MimeBodyPart();
			MimeBodyPart fileMime=new MimeBodyPart();
			textMime.setText(message);
			fileMime.attachFile(file);
			mimeMultipart.addBodyPart(textMime);
			mimeMultipart.addBodyPart(fileMime);
			mail.setContent(mimeMultipart);
			
			mail.setSubject(subject);
			
			// sending the mail
			Transport.send(mail);
		}
		catch(MessagingException e) {
			System.out.println("ERROR: "+e.getMessage());
			e.printStackTrace();
			return false;
		}
		catch(IOException e) {
			System.out.println("ERROR: "+e.getMessage());
			e.printStackTrace();
			return false;
		}
		
		return true;
	}
}
