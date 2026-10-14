package util;

import jakarta.mail.Authenticator;
import jakarta.mail.Message;
import jakarta.mail.PasswordAuthentication;
import jakarta.mail.Session;
import jakarta.mail.Transport;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

public class EmailDeliveryService {

    private static final Logger LOGGER = Logger.getLogger(EmailDeliveryService.class.getName());

    private String getConfig(String key) {
        String systemValue = System.getProperty(key);
        if (systemValue != null && !systemValue.isBlank()) {
            return systemValue.trim();
        }
        String envValue = System.getenv(key);
        return envValue == null ? "" : envValue.trim();
    }

    private boolean getBooleanConfig(String key, boolean defaultValue) {
        String value = getConfig(key);
        if (value.isEmpty()) {
            return defaultValue;
        }
        return "true".equalsIgnoreCase(value)
                || "1".equals(value)
                || "yes".equalsIgnoreCase(value);
    }

    public boolean isConfigured() {
        return !getConfig("AUTOPRO_SMTP_HOST").isEmpty()
                && !getConfig("AUTOPRO_SMTP_PORT").isEmpty()
                && !getConfig("AUTOPRO_SMTP_FROM").isEmpty();
    }

    private Session buildMailSession() {
        String host = getConfig("AUTOPRO_SMTP_HOST");
        String port = getConfig("AUTOPRO_SMTP_PORT");
        String username = getConfig("AUTOPRO_SMTP_USERNAME");
        String password = getConfig("AUTOPRO_SMTP_PASSWORD");
        boolean startTls = getBooleanConfig("AUTOPRO_SMTP_STARTTLS", true);
        boolean ssl = getBooleanConfig("AUTOPRO_SMTP_SSL", false);
        boolean auth = !username.isEmpty();

        Properties props = new Properties();
        props.put("mail.smtp.host", host);
        props.put("mail.smtp.port", port);
        props.put("mail.smtp.auth", String.valueOf(auth));
        props.put("mail.smtp.starttls.enable", String.valueOf(startTls));
        props.put("mail.smtp.ssl.enable", String.valueOf(ssl));
        props.put("mail.smtp.connectiontimeout", "10000");
        props.put("mail.smtp.timeout", "10000");
        props.put("mail.smtp.writetimeout", "10000");

        if (auth) {
            return Session.getInstance(props, new Authenticator() {
                @Override
                protected PasswordAuthentication getPasswordAuthentication() {
                    return new PasswordAuthentication(username, password);
                }
            });
        }

        return Session.getInstance(props);
    }

    public boolean sendHtmlEmail(String toEmail, String subject, String htmlBody) {
        if (toEmail == null || toEmail.isBlank() || !isConfigured()) {
            return false;
        }

        String from = getConfig("AUTOPRO_SMTP_FROM");
        String fromName = getConfig("AUTOPRO_SMTP_FROM_NAME");
        if (fromName.isEmpty()) {
            fromName = "AutoPro";
        }

        try {
            MimeMessage message = new MimeMessage(buildMailSession());
            try {
                message.setFrom(new InternetAddress(from, fromName, "UTF-8"));
            } catch (Exception ex) {
                message.setFrom(new InternetAddress(from));
            }
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(toEmail, false));
            message.setSubject(subject, "UTF-8");
            message.setContent(htmlBody, "text/html; charset=UTF-8");
            Transport.send(message);
            return true;
        } catch (Exception ex) {
            LOGGER.log(Level.WARNING, "Cannot send email to {0}. Cause: {1}", new Object[]{toEmail, ex.getMessage()});
            return false;
        }
    }
}
