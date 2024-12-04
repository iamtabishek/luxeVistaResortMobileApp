package com.example.luxevistaapp;
import java.util.Properties;
import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class JavaMailAPI {

    private final String toEmail;
    private final String subject;
    private final String message;

    private final String fromEmail = "luxevistaresort@gmail.com"; // Sender's email
    private final String fromPassword = "12345";    // Sender's email password

    // Constructor to initialize email details
    public JavaMailAPI(String toEmail, String subject, String message) {
        this.toEmail = toEmail;
        this.subject = subject;
        this.message = message;
    }

    public void sendEmail() {
        ExecutorService executorService = Executors.newSingleThreadExecutor();
        executorService.execute(() -> {
            try {
                // Set up email properties
                Properties properties = new Properties();
                properties.put("mail.smtp.host", "smtp.gmail.com");
                properties.put("mail.smtp.port", "587");
                properties.put("mail.smtp.auth", "true");
                properties.put("mail.smtp.starttls.enable", "true");

                // Create a session with authentication
                Session session = Session.getInstance(properties, new Authenticator() {
                    @Override
                    protected PasswordAuthentication getPasswordAuthentication() {
                        return new PasswordAuthentication(fromEmail, fromPassword);
                    }
                });

                // Create the email message
                Message emailMessage = new MimeMessage(session);
                emailMessage.setFrom(new InternetAddress(fromEmail));
                emailMessage.setRecipients(Message.RecipientType.TO, InternetAddress.parse(toEmail));
                emailMessage.setSubject(subject);
                emailMessage.setText(message);

                // Send the email
                Transport.send(emailMessage);

                // Log or notify success (optional)
                System.out.println("Email sent successfully!");

            } catch (MessagingException e) {
                e.printStackTrace();
                // Log or notify failure
                System.out.println("Failed to send email: " + e.getMessage());
            }
        });

        // Shut down the executor service (optional, ensures clean-up)
        executorService.shutdown();
    }
}
