package util;

import jakarta.mail.Authenticator;
import jakarta.mail.Message;
import jakarta.mail.MessagingException;
import jakarta.mail.PasswordAuthentication;
import jakarta.mail.Session;
import jakarta.mail.Transport;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;
import java.io.IOException;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import model.NguoiDung;

public class OtpDeliveryService {

    private static final Logger LOGGER = Logger.getLogger(OtpDeliveryService.class.getName());
    private static final HttpClient HTTP_CLIENT = HttpClient.newHttpClient();
    private static final String SMS_PROVIDER_TWILIO = "twilio";

    public static class DeliveryResult {
        private final String userMessage;
        private final String debugMessage;

        public DeliveryResult(String userMessage, String debugMessage) {
            this.userMessage = userMessage;
            this.debugMessage = debugMessage;
        }

        public String getUserMessage() {
            return userMessage;
        }

        public String getDebugMessage() {
            return debugMessage;
        }
    }

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

    private String debugOtpMessage(String message) {
        return getBooleanConfig("AUTOPRO_OTP_DEBUG_ENABLED", false) ? message : null;
    }

    private String maskEmail(String email) {
        if (email == null || !email.contains("@")) {
            return email == null ? "" : email;
        }
        String[] parts = email.split("@", 2);
        String local = parts[0];
        if (local.length() <= 2) {
            return local.charAt(0) + "***@" + parts[1];
        }
        return local.substring(0, 2) + "***@" + parts[1];
    }

    private String maskPhone(String phone) {
        if (phone == null || phone.length() < 4) {
            return phone == null ? "" : phone;
        }
        return "***" + phone.substring(Math.max(0, phone.length() - 4));
    }

    private boolean prefersEmail(String identifier, NguoiDung user) {
        if (identifier != null && identifier.contains("@")) {
            return true;
        }
        return user != null
                && user.getEmail() != null
                && !user.getEmail().isBlank()
                && (user.getSoDienThoai() == null || user.getSoDienThoai().isBlank());
    }

    private boolean isSmtpConfigured() {
        return !getConfig("AUTOPRO_SMTP_HOST").isEmpty()
                && !getConfig("AUTOPRO_SMTP_PORT").isEmpty()
                && !getConfig("AUTOPRO_SMTP_FROM").isEmpty();
    }

    private boolean isTwilioConfigured() {
        String provider = getConfig("AUTOPRO_SMS_PROVIDER");
        if (!provider.isEmpty() && !SMS_PROVIDER_TWILIO.equalsIgnoreCase(provider)) {
            return false;
        }

        String accountSid = getConfig("AUTOPRO_TWILIO_ACCOUNT_SID");
        String authPassword = getConfig("AUTOPRO_TWILIO_AUTH_PASSWORD");
        String authToken = getConfig("AUTOPRO_TWILIO_AUTH_TOKEN");
        String from = getConfig("AUTOPRO_TWILIO_FROM");
        String messagingServiceSid = getConfig("AUTOPRO_TWILIO_MESSAGING_SERVICE_SID");

        return !accountSid.isEmpty()
                && (!authPassword.isEmpty() || !authToken.isEmpty())
                && (!from.isEmpty() || !messagingServiceSid.isEmpty());
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

    private void sendMail(String toEmail, String otp) throws MessagingException {
        String from = getConfig("AUTOPRO_SMTP_FROM");
        String fromName = getConfig("AUTOPRO_SMTP_FROM_NAME");
        if (fromName.isEmpty()) {
            fromName = "AutoPro";
        }

        MimeMessage message = new MimeMessage(buildMailSession());
        try {
            message.setFrom(new InternetAddress(from, fromName, "UTF-8"));
        } catch (Exception ex) {
            message.setFrom(new InternetAddress(from));
        }
        message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(toEmail, false));
        message.setSubject("Mã xác minh AutoPro", "UTF-8");
        message.setContent(
                "<div style=\"font-family:Arial,sans-serif;font-size:14px;color:#111827\">"
                + "<h2 style=\"margin-bottom:12px\">AutoPro</h2>"
                + "<p>Bạn vừa yêu cầu xác minh tài khoản hoặc thông tin liên hệ trên AutoPro.</p>"
                + "<p>Mã OTP của bạn là:</p>"
                + "<div style=\"font-size:28px;font-weight:700;letter-spacing:4px;margin:16px 0;color:#f59e0b\">" + otp + "</div>"
                + "<p>Mã có hiệu lực trong 10 phút.</p>"
                + "<p>Nếu bạn không thực hiện yêu cầu này, vui lòng bỏ qua email.</p>"
                + "</div>",
                "text/html; charset=UTF-8"
        );
        Transport.send(message);
    }

    private String normalizePhoneForSms(String phone) {
        String normalized = phone == null ? "" : phone.trim().replaceAll("[^0-9+]", "");
        if (normalized.isEmpty()) {
            return normalized;
        }
        if (normalized.startsWith("+")) {
            return normalized;
        }

        String defaultCountryCode = getConfig("AUTOPRO_SMS_DEFAULT_COUNTRY_CODE");
        if (defaultCountryCode.isEmpty()) {
            return normalized;
        }

        if (normalized.startsWith("0")) {
            return defaultCountryCode + normalized.substring(1);
        }
        return defaultCountryCode + normalized;
    }

    public void sendVerificationCode(String target, String type, String otp) throws Exception {
        if ("email".equals(type)) {
            if (!isSmtpConfigured()) throw new IllegalStateException("SMTP is not configured");
            sendMail(target, otp);
        } else if ("phone".equals(type)) {
            if (!isTwilioConfigured()) throw new IllegalStateException("SMS gateway is not configured");
            sendSmsViaTwilio(target, otp);
        } else throw new IllegalArgumentException("Unknown contact type");
    }

    private String buildTwilioFormBody(String toPhone, String otp) {
        List<String> parts = new ArrayList<>();
        parts.add(formField("To", toPhone));
        parts.add(formField("Body", "Ma OTP AutoPro cua ban la " + otp + ". Ma co hieu luc trong 10 phut."));

        String messagingServiceSid = getConfig("AUTOPRO_TWILIO_MESSAGING_SERVICE_SID");
        if (!messagingServiceSid.isEmpty()) {
            parts.add(formField("MessagingServiceSid", messagingServiceSid));
        } else {
            parts.add(formField("From", getConfig("AUTOPRO_TWILIO_FROM")));
        }

        return String.join("&", parts);
    }

    private String formField(String key, String value) {
        return URLEncoder.encode(key, StandardCharsets.UTF_8)
                + "="
                + URLEncoder.encode(value == null ? "" : value, StandardCharsets.UTF_8);
    }

    private void sendSmsViaTwilio(String toPhone, String otp) throws IOException, InterruptedException {
        String accountSid = getConfig("AUTOPRO_TWILIO_ACCOUNT_SID");
        String authUsername = getConfig("AUTOPRO_TWILIO_AUTH_USERNAME");
        String authPassword = getConfig("AUTOPRO_TWILIO_AUTH_PASSWORD");
        if (authUsername.isEmpty()) {
            authUsername = accountSid;
        }
        if (authPassword.isEmpty()) {
            authPassword = getConfig("AUTOPRO_TWILIO_AUTH_TOKEN");
        }

        String authHeader = "Basic " + Base64.getEncoder()
                .encodeToString((authUsername + ":" + authPassword).getBytes(StandardCharsets.UTF_8));

        String normalizedPhone = normalizePhoneForSms(toPhone);
        String formBody = buildTwilioFormBody(normalizedPhone, otp);
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("https://api.twilio.com/2010-04-01/Accounts/" + accountSid + "/Messages.json"))
                .header("Authorization", authHeader)
                .header("Content-Type", "application/x-www-form-urlencoded")
                .POST(HttpRequest.BodyPublishers.ofString(formBody, StandardCharsets.UTF_8))
                .build();

        HttpResponse<String> response = HTTP_CLIENT.send(request, HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8));

        if (response.statusCode() < 200 || response.statusCode() >= 300) {
            String responseBody = response.body() == null ? "" : response.body();
            String shortenedBody = responseBody.length() > 400 ? responseBody.substring(0, 400) : responseBody;
            throw new IOException("Twilio SMS failed with status " + response.statusCode() + ": " + shortenedBody);
        }
    }

    public DeliveryResult sendOtp(NguoiDung user, String otp, String identifier) {
        String email = user != null ? user.getEmail() : null;
        String phone = user != null ? user.getSoDienThoai() : null;
        boolean useEmail = prefersEmail(identifier, user);

        if (useEmail) {
            if (email == null || email.isBlank()) {
                return new DeliveryResult(
                        "Tài khoản này chưa có email để nhận OTP.",
                        null
                );
            }

            if (isSmtpConfigured()) {
                try {
                    sendMail(email, otp);
                    return new DeliveryResult(
                            "Mã OTP đã được gửi tới email " + maskEmail(email) + ".",
                            null
                    );
                } catch (MessagingException ex) {
                    LOGGER.log(Level.WARNING, "Cannot send OTP email to {0}. Cause: {1}",
                            new Object[]{maskEmail(email), ex.getMessage()});
                    return new DeliveryResult(
                            "Chưa thể gửi email OTP tự động. Vui lòng thử lại sau.",
                            debugOtpMessage("Kiểm tra lại SMTP. Mã OTP kiểm thử là: " + otp)
                    );
                }
            }

            LOGGER.log(Level.INFO, "SMTP is not configured; OTP cannot be delivered to email {0}.", maskEmail(email));
            return new DeliveryResult(
                    "Kênh gửi OTP qua email chưa sẵn sàng. Vui lòng thử lại sau.",
                    debugOtpMessage("SMTP chưa được cấu hình. Mã OTP kiểm thử là: " + otp)
            );
        }

        if (phone == null || phone.isBlank()) {
            return new DeliveryResult(
                    "Tài khoản này chưa có số điện thoại để nhận OTP.",
                    null
            );
        }

        if (isTwilioConfigured()) {
            try {
                sendSmsViaTwilio(phone, otp);
                return new DeliveryResult(
                        "Mã OTP đã được gửi tới số điện thoại " + maskPhone(phone) + ".",
                        null
                );
            } catch (IOException | InterruptedException ex) {
                if (ex instanceof InterruptedException) {
                    Thread.currentThread().interrupt();
                }
                LOGGER.log(Level.WARNING, "Cannot send OTP SMS to {0}. Cause: {1}",
                        new Object[]{maskPhone(phone), ex.getMessage()});
                return new DeliveryResult(
                        "Chưa thể gửi SMS OTP tự động. Vui lòng thử lại sau.",
                        debugOtpMessage("Kiểm tra lại cấu hình SMS gateway. Mã OTP kiểm thử là: " + otp)
                );
            }
        }

        LOGGER.log(Level.INFO, "SMS gateway is not configured; OTP cannot be delivered to phone {0}.", maskPhone(phone));
        return new DeliveryResult(
                "Kênh gửi OTP qua SMS chưa sẵn sàng. Vui lòng thử lại sau.",
                debugOtpMessage("SMS gateway chưa được cấu hình. Mã OTP kiểm thử là: " + otp)
        );
    }
}
