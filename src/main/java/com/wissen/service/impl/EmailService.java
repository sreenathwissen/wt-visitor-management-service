package com.wissen.service.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import com.wissen.dto.EmailDTO;

/**
 * Service class to send an email. It fetches the details from DTO class and
 * email subject & email body message from properties file
 * 
 * @author Ankit Garg
 *
 */
@Service
public class EmailService {

	@Autowired
	private JavaMailSender emailSender;

	@Value("${email.subject}")
	String emailSubject;

	@Value("${email.body}")
	String emailBody;

	/**
	 * Method for sending simple e-mail message.
	 * 
	 * @param emailDTO - data to be send.
	 */
	private void sendSimpleMessage(EmailDTO emailDTO) {

		SimpleMailMessage mailMessage = new SimpleMailMessage();
		mailMessage.setTo(emailDTO.getRecipients().stream().collect(Collectors.joining(",")));
		mailMessage.setSubject(emailDTO.getSubject());
		mailMessage.setText(emailDTO.getBody());

		emailSender.send(mailMessage);
	}

	/**
	 * Method creates a template to send an email
	 * 
	 * @param name         : Name of the Visitor
	 * @param emailId      : Email Id of the visitor
	 * @param otp          : OTP for login
	 * @param emailSubject : Email Subject
	 * @param emailBody    : Email Body
	 */
	public void sendEmail(String name, String emailId, String otp) {

		List<String> recipients = new ArrayList<>();
		recipients.add(emailId);

		// generate emailDTO object
		EmailDTO emailDTO = new EmailDTO();
		emailDTO.setSubject(emailSubject);
		emailDTO.setBody(String.format((String) emailBody, name, otp));
		emailDTO.setRecipients(recipients);

		// send generated e-mail
		sendSimpleMessage(emailDTO);
	}

}