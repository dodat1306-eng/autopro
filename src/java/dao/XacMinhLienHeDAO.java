package dao;

import context.DBContext;
import java.sql.*;
import java.util.*;
import util.PasswordUtil;
import static util.DbWork.*;
import util.DbWork;

public class XacMinhLienHeDAO extends DBContext {
    public static String normalize(String type,String raw) {
        String value=raw==null?"":raw.trim();
        if("email".equals(type)) {
            value=value.toLowerCase(Locale.ROOT);
            require(value.length()<=100 && value.matches("^[^\\s@]+@[^\\s@]+\\.[^\\s@]+$"),"Email không hợp lệ.");
        } else {
            require("phone".equals(type),"Loại liên hệ không hợp lệ.");
            value=value.replaceAll("[\\s().-]","");
            if(value.startsWith("+84")) value="0"+value.substring(3);
            require(value.matches("0[0-9]{9}"),"Số điện thoại cần 10 chữ số, bắt đầu bằng 0 hoặc +84.");
        }
        return value;
    }

    private void targetLock(Connection c,String type,String target) throws SQLException {
        update(c,"INSERT IGNORE INTO khoalienhe(lienHe) VALUES(?)",type+":"+target);
        one(c,"SELECT lienHe FROM khoalienhe WHERE lienHe=? FOR UPDATE",type+":"+target);
    }
    private void available(Connection c,String type,String target,Integer user) throws SQLException {
        String column="email".equals(type)?"email":"soDienThoai";
        require(list(c,"SELECT maNguoiDung FROM nguoidung WHERE "+column+"=? AND maNguoiDung<>?",target,user==null?0:user).isEmpty(),"Thông tin liên hệ này đã được sử dụng.");
    }

    public String create(String sessionKey,Integer user,String purpose,String type,String target,String name,String hash,String otp) throws Exception {
        String normalized=normalize(type,target);
        require("DANG_KY".equals(purpose) && user==null || "LIEN_HE".equals(purpose) && user!=null,"Mục đích xác minh không hợp lệ.");
        require(otp!=null && otp.matches("[0-9]{6}"),"Mã OTP không hợp lệ.");
        return transaction(c -> {
            targetLock(c,type,normalized);
            if(user!=null) activeUser(c,user);
            available(c,type,normalized,user);
            Map<String,Object> limits=one(c,"SELECT COUNT(*) AS n,MAX(ngayTao)>DATE_SUB(NOW(),INTERVAL 60 SECOND) AS quaSom FROM xacminhlienhe WHERE lienHe=? AND ngayTao>DATE_SUB(NOW(),INTERVAL 1 HOUR)",normalized);
            require(id(limits,"n")<5 && !flag(limits,"quaSom"),"Vui lòng chờ 60 giây trước khi gửi lại; tối đa 5 mã mỗi giờ.");
            update(c,"UPDATE xacminhlienhe SET daDung=1 WHERE khoaPhien=? AND mucDich=? AND loai=? AND daDung=0",sessionKey,purpose,type);
            String id=UUID.randomUUID().toString();
            update(c,"INSERT INTO xacminhlienhe(maXacMinh,khoaPhien,maNguoiDung,mucDich,loai,lienHe,hoTen,matKhauHash,otpHash,hanDung) VALUES(?,?,?,?,?,?,?,?,?,DATE_ADD(NOW(),INTERVAL 10 MINUTE))",
                    id,sessionKey,user,purpose,type,normalized,name,hash,PasswordUtil.hashPassword(otp));
            return id;
        });
    }

    public void delivered(String id,boolean sent) throws Exception {
        try(Connection c=getConnection()) { update(c,"UPDATE xacminhlienhe SET daGui=?,daDung=? WHERE maXacMinh=?",sent,!sent,id); }
    }
    public Map<String,Object> get(String id,String sessionKey,Integer user) throws Exception {
        try(Connection c=getConnection()) {
            Map<String,Object> r=one(c,"SELECT maXacMinh,mucDich,loai,lienHe,hanDung,daDung,daGui FROM xacminhlienhe WHERE maXacMinh=? AND khoaPhien=? AND maNguoiDung <=> ?",id,sessionKey,user);
            return r;
        }
    }

    public int verify(String challenge,String sessionKey,Integer user,String code) throws Exception {
        // Return the failed result from the transaction so the attempt counter is committed.
        int result=transaction(c -> {
            Map<String,Object> r=one(c,"SELECT * FROM xacminhlienhe WHERE maXacMinh=? AND khoaPhien=? AND maNguoiDung <=> ? FOR UPDATE",challenge,sessionKey,user);
            if(flag(r,"daDung") || !flag(r,"daGui") || id(r,"soLanSai")>=5 || ((Timestamp)r.get("hanDung")).before(new Timestamp(System.currentTimeMillis()))) return -1;
            if(code==null || !code.matches("[0-9]{6}") || !PasswordUtil.verifyPassword(code,(String)r.get("otpHash"))) {
                update(c,"UPDATE xacminhlienhe SET soLanSai=soLanSai+1 WHERE maXacMinh=?",challenge);
                return -1;
            }
            String type=(String)r.get("loai"), target=(String)r.get("lienHe");
            targetLock(c,type,target);
            available(c,type,target,user);
            int account;
            if("DANG_KY".equals(r.get("mucDich"))) {
                account=insert(c,"INSERT INTO nguoidung(hoTen,email,soDienThoai,matKhau,vaiTro,trangThai) VALUES(?,?,?,?,'buyer','active')",
                        r.get("hoTen"),"email".equals(type)?target:null,"phone".equals(type)?target:null,r.get("matKhauHash"));
            } else {
                require(user!=null,"Phiên xác minh không hợp lệ.");
                activeUser(c,user);
                account=user;
                String column="email".equals(type)?"email":"soDienThoai";
                update(c,"UPDATE nguoidung SET "+column+"=? WHERE maNguoiDung=?",target,account);
                // Pending applications may follow contact changes; approved records remain an audit snapshot.
                update(c,"UPDATE dangkyseller SET "+column+"=? WHERE maNguoiDung=? AND trangThai='CHO_DUYET'",target,account);
            }
            update(c,"DELETE FROM lienhedaxacminh WHERE maNguoiDung=? AND loai=?",account,type);
            update(c,"INSERT INTO lienhedaxacminh(maNguoiDung,loai,lienHe) VALUES(?,?,?)",account,type,target);
            update(c,"UPDATE xacminhlienhe SET daDung=1,matKhauHash=NULL,otpHash='' WHERE maXacMinh=?",challenge);
            audit(c,"TAI_KHOAN",account,account,"Xác minh "+type,null,null,null,null);
            DbWork.notify(c,account,"Đã xác minh thông tin liên hệ","Thông tin liên hệ của bạn đã được xác minh bằng OTP.","account-security");
            return account;
        });
        require(result>0,"OTP sai, hết hạn, đã dùng hoặc vượt quá 5 lần thử. Vui lòng yêu cầu mã mới.");
        return result;
    }

    public void changePassword(int user,String oldPassword,String newPassword) throws Exception {
        require(newPassword!=null && newPassword.length()>=8 && newPassword.length()<=128,"Mật khẩu mới cần từ 8 đến 128 ký tự.");
        transaction(c -> {
            activeUser(c,user);
            Map<String,Object> row=one(c,"SELECT matKhau FROM nguoidung WHERE maNguoiDung=? FOR UPDATE",user);
            require(PasswordUtil.verifyPassword(oldPassword,(String)row.get("matKhau")),"Mật khẩu hiện tại không đúng.");
            require(!PasswordUtil.verifyPassword(newPassword,(String)row.get("matKhau")),"Mật khẩu mới phải khác mật khẩu hiện tại.");
            update(c,"UPDATE nguoidung SET matKhau=?,otpReset=NULL,otpHetHan=NULL WHERE maNguoiDung=?",PasswordUtil.hashPassword(newPassword),user);
            audit(c,"TAI_KHOAN",user,user,"Đổi mật khẩu",null,null,null,null);
            DbWork.notify(c,user,"Mật khẩu đã thay đổi","Bạn vừa thay đổi mật khẩu. Hãy đăng nhập lại.","login");
            return null;
        });
    }
}
