package com.smart.Service;

import java.io.File;
import java.io.IOException;

import jakarta.mail.Message;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.AddressException;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeBodyPart;
import jakarta.mail.internet.MimeMessage;
import jakarta.mail.internet.MimeMultipart;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Lazy;
import org.springframework.core.io.ClassPathResource;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.stereotype.Service;

@Service
@Lazy
public class EmailService {
	
	@Lazy
	@Autowired
	private JavaMailSenderImpl mailSender;
	
	@Value("${mail.subject}:#{null}")
	private String mail_subject;
	
	public boolean sendEmail(String message,String to) throws IOException, AddressException, MessagingException{

			MimeMessage msg = mailSender.createMimeMessage();
			File imagePath=new ClassPathResource("static/images").getFile();
			String path=imagePath.getAbsolutePath()+File.separator+"SCM logo.jpg";
			
			msg.setFrom(new InternetAddress("no-reply-OTP@scm.org"));
			setMailRecipients(msg, to);
			msg.setSubject(mail_subject);
						
			MimeMultipart multipart = new MimeMultipart();
			MimeBodyPart textMime = new MimeBodyPart();
			textMime.setContent(message,"text/html");
			MimeBodyPart fileMime=new MimeBodyPart();
			fileMime.attachFile(new File(path));
			multipart.addBodyPart(textMime);
			multipart.addBodyPart(fileMime);
			
			msg.setContent(multipart);
			mailSender.send(msg);
			System.out.println("Email Sent!!");
	     
	     return true;
}

	private void setMailRecipients(MimeMessage msg, String TO_EMAIL) throws MessagingException, AddressException {
		if (TO_EMAIL!=null && !TO_EMAIL.isEmpty()){
			int length = TO_EMAIL.split(",").length;
			for(int i=0;i<=length-1;i++){

				String toAddresses=TO_EMAIL.split(",")[i];
				if ((!(toAddresses==null))||(!(toAddresses==""))){	
					msg.addRecipient(Message.RecipientType.TO, new InternetAddress(toAddresses));
				}
			}
		}
		else{
			System.out.println("Mail - To address not set . mail cannot be sent");
		}
	}
}
