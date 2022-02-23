package br.com.javamoon.config.email;

import br.com.javamoon.config.email.model.EmailInfo;
import br.com.javamoon.exception.EmailSendingException;
import java.util.Map;
import java.util.Objects;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import org.apache.logging.log4j.util.Strings;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.thymeleaf.ITemplateEngine;
import org.thymeleaf.context.Context;

@Service
public class EmailSender {
	private final String TEXT_HTML_CHARSET_UTF_8 = "text/html;charset=UTF-8";
	
	private JavaMailSender javaMailSender;
	private ITemplateEngine templateEngine;

	public EmailSender(JavaMailSender javaMailSender, ITemplateEngine templateEngine) {
		this.javaMailSender = javaMailSender;
		this.templateEngine = templateEngine;
	}
	
	@Async
	public void send(EmailInfo emailInfo) {
		MimeMessage mimeMessage = javaMailSender.createMimeMessage();
		MimeMultipart mimeMultipart = new MimeMultipart();
		
		addBasicDetails(emailInfo, mimeMessage);
		addHtmlBody(emailInfo.getTemplate(), emailInfo.getTemplateData(), mimeMultipart);
		setContent(mimeMessage, mimeMultipart);
		
		javaMailSender.send(mimeMessage);
	}
	
	private void addBasicDetails(EmailInfo emailInfo, MimeMessage mimeMessage) {
		try {
			mimeMessage.setFrom(emailInfo.getFrom());
			mimeMessage.setSubject(emailInfo.getSubject());
			mimeMessage.addRecipients(Message.RecipientType.TO, Strings.join(emailInfo.getTo(), ','));
			
			if (Objects.nonNull(emailInfo.getCc()))
				mimeMessage.addRecipients(Message.RecipientType.CC, Strings.join(emailInfo.getCc(), ','));
			
			if (Objects.nonNull(emailInfo.getBcc()))
				mimeMessage.addRecipients(Message.RecipientType.BCC, Strings.join(emailInfo.getBcc(), ','));
		} catch (MessagingException e) {
			throw new EmailSendingException("Error adding data to mime message");
		}
	}
	
	private void addHtmlBody(String template, Map<String, Object> templateData, MimeMultipart mimeMultipart) {
		MimeBodyPart mimeBodyPart = new MimeBodyPart();
		Context context = new Context();
		
		if (Objects.nonNull(templateData))
			context.setVariables(templateData);
		
		try {
			mimeBodyPart.setContent(templateEngine.process(template, context), TEXT_HTML_CHARSET_UTF_8);
			mimeMultipart.addBodyPart(mimeBodyPart);
		} catch (MessagingException e) {
			throw new EmailSendingException("Error adding data to mime body");
		}
	}
	
	private void setContent(MimeMessage mimeMessage, MimeMultipart mimeMultipart) {
		try {
			mimeMessage.setContent(mimeMultipart);
		} catch (MessagingException e) {
			throw new EmailSendingException("Error when try to set content to MIME message");
		}
	}
}
