package util;

import dao.XacMinhLienHeDAO;
import jakarta.servlet.http.HttpSession;
import java.nio.charset.StandardCharsets;
import java.security.*;
import java.util.HexFormat;
import model.NguoiDung;

public final class VerificationFlow {
    private static final SecureRandom RANDOM=new SecureRandom();
    private VerificationFlow() {}
    public static String sessionKey(HttpSession session) {
        try { return HexFormat.of().formatHex(MessageDigest.getInstance("SHA-256").digest(session.getId().getBytes(StandardCharsets.UTF_8))); }
        catch(NoSuchAlgorithmException e) { throw new IllegalStateException(e); }
    }
    public static String start(HttpSession session,Integer user,String purpose,String type,String target,String name,String passwordHash) throws Exception {
        String normalized=XacMinhLienHeDAO.normalize(type,target);
        String otp=String.format("%06d",RANDOM.nextInt(1000000));
        XacMinhLienHeDAO dao=new XacMinhLienHeDAO();
        String id=dao.create(sessionKey(session),user,purpose,type,normalized,name,passwordHash,otp);
        try {
            new OtpDeliveryService().sendVerificationCode(normalized,type,otp);
            dao.delivered(id,true);
            return id;
        } catch(Exception e) {
            dao.delivered(id,false);
            throw new IllegalArgumentException("Chưa gửi được OTP. Vui lòng kiểm tra kênh email/SMS hoặc thử lại sau.",e);
        }
    }
}
