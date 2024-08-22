package Proyect.service;

import com.azure.communication.email.EmailClient;
import com.azure.communication.email.EmailClientBuilder;
import com.azure.communication.email.models.EmailAddress;
import com.azure.communication.email.models.EmailMessage;
import com.azure.communication.email.models.EmailSendResult;
import com.azure.core.util.polling.PollResponse;
import com.azure.core.util.polling.SyncPoller;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class SendEmailService {

    private static final Logger logger = LoggerFactory.getLogger(SendEmailService.class);

    private final EmailClient emailClient;
    private final String senderAddress;

    public SendEmailService(
            @Value("${azure.communication.connection.string}") String connectionString,
            @Value("${azure.communication.sender.address}") String senderAddress) {
        this.emailClient = new EmailClientBuilder()
                .connectionString(connectionString)
                .buildClient();
        this.senderAddress = senderAddress;
    }

    public void sendEmail(String toEmail, String subject, String body) {
        try {
            EmailAddress toAddress = new EmailAddress(toEmail);

            EmailMessage emailMessage = new EmailMessage()
                    .setSenderAddress(senderAddress)
                    .setToRecipients(toAddress)
                    .setSubject(subject)
                    .setBodyPlainText(body);

            SyncPoller<EmailSendResult, EmailSendResult> poller = emailClient.beginSend(emailMessage, null);
            PollResponse<EmailSendResult> result = poller.waitForCompletion();

            if (result.getValue() != null) {
                logger.info("Email sent successfully to {}", toEmail);
            } else {
                logger.error("Error sending email to {}: {}", toEmail, result.getStatus());
            }
        } catch (Exception e) {
            logger.error("Exception occurred while sending email to {}: {}", toEmail, e.getMessage(), e);
        }
    }
}
