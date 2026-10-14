package dao;

import context.DBContext;
import java.sql.*;
import java.util.*;
import static util.DbWork.*;
import util.DbWork;

public class LichHenDAO extends DBContext {
    private static final String QUERY = "SELECT l.*,v.trangThai AS tinhTrangHen,v.phienBan,v.nguoiDeXuat,y.maNguoiMua,y.maNguoiBan,y.maXe,y.trangThai AS tinhTrangYeuCau,x.tenXe "
            + "FROM lichhenxemxe l JOIN lichhenxacnhan v ON v.maLichHen=l.maLichHen JOIN yeucaumuaxe y ON y.maYeuCau=l.maYeuCau JOIN xe x ON x.maXe=y.maXe ";

    public Map<String,Object> get(int requestId, int user) throws Exception {
        try(Connection c=getConnection()) {
            Map<String,Object> row=one(c,QUERY+"WHERE l.maYeuCau=? ORDER BY l.maLichHen DESC LIMIT 1",requestId);
            participant(row,user);
            if (Set.of("DA_HUY","HOAN_TAT","DA_CHOT").contains(row.get("tinhTrangYeuCau"))) row.put("locked",true);
            return row;
        }
    }

    private void participant(Map<String,Object> row, int user) {
        require(id(row,"maNguoiMua")==user || id(row,"maNguoiBan")==user,"Bạn không có quyền xem lịch hẹn này.");
    }

    public void act(int requestId, int user, int version, String action, Timestamp time, String place, String note) throws Exception {
        transaction(c -> {
            Map<String,Object> key=one(c,"SELECT maXe FROM yeucaumuaxe WHERE maYeuCau=?",requestId);
            one(c,"SELECT maXe FROM xe WHERE maXe=? FOR UPDATE",key.get("maXe"));
            one(c,"SELECT maYeuCau FROM yeucaumuaxe WHERE maYeuCau=? FOR UPDATE",requestId);
            Map<String,Object> row=one(c,QUERY+"WHERE l.maYeuCau=? ORDER BY l.maLichHen DESC LIMIT 1 FOR UPDATE",requestId);
            participant(row,user);
            activeUser(c,user);
            require(id(row,"phienBan")==version,"Lịch hẹn vừa thay đổi. Hãy tải lại trước khi xác nhận.");
            String before=(String)row.get("tinhTrangHen");
            require(Set.of("CHO_XAC_NHAN","DA_XAC_NHAN").contains(before),"Lịch hẹn đã kết thúc.");
            require(!Set.of("DA_HUY","HOAN_TAT","DA_CHOT").contains(row.get("tinhTrangYeuCau")),"Yêu cầu này không còn cho phép đổi lịch.");
            require(list(c,"SELECT maGiaoDich FROM giaodichxe WHERE maYeuCau=? AND trangThai<>'HUY'",requestId).isEmpty(),"Đã có hồ sơ giao dịch cho yêu cầu này.");
            require(list(c,"SELECT maGiaoDich FROM giaodichxe WHERE maXe=? AND trangThai NOT IN ('MOI_TAO','HUY')",row.get("maXe")).isEmpty(),"Xe đang giao dịch hoặc đã bán.");
            String after, title;
            int appointment=id(row,"maLichHen");
            if("propose".equals(action)) {
                require(time!=null && time.after(new Timestamp(System.currentTimeMillis())),"Thời gian hẹn phải ở tương lai.");
                require(place!=null && !place.isBlank() && place.length()<=255,"Nhập địa điểm tối đa 255 ký tự.");
                require(note==null || note.length()<=1000,"Ghi chú tối đa 1.000 ký tự.");
                require(!time.after(new Timestamp(System.currentTimeMillis()+180L*86400000)),"Chỉ đặt lịch trong 180 ngày tới.");
                checkOverlap(c,row,appointment,time);
                update(c,"UPDATE lichhenxemxe SET thoiGianHen=?,diaDiemHen=?,ghiChuHen=?,ngayCapNhat=NOW() WHERE maLichHen=?",time,place.trim(),note,appointment);
                update(c,"UPDATE lichhenxacnhan SET nguoiDeXuat=?,daNhac=0 WHERE maLichHen=?",user,appointment);
                after="CHO_XAC_NHAN"; title="Đề xuất lịch xem xe mới";
            } else if("confirm".equals(action)) {
                require("CHO_XAC_NHAN".equals(before) && id(row,"nguoiDeXuat")!=user,"Chỉ bên nhận đề xuất được xác nhận lịch.");
                Timestamp existing=(Timestamp)row.get("thoiGianHen");
                require(existing!=null && existing.after(new Timestamp(System.currentTimeMillis())),"Lịch đã quá giờ. Vui lòng đề xuất lịch mới.");
                checkOverlap(c,row,appointment,existing);
                after="DA_XAC_NHAN"; title="Hai bên đã thống nhất lịch xem xe";
            } else if("visited".equals(action) || "no-show".equals(action)) {
                require("DA_XAC_NHAN".equals(before),"Lịch hẹn chưa được hai bên xác nhận.");
                Timestamp existing=(Timestamp)row.get("thoiGianHen");
                require(existing!=null && !existing.after(new Timestamp(System.currentTimeMillis())),"Chưa tới giờ hẹn.");
                require("visited".equals(action) ? id(row,"maNguoiMua")==user : id(row,"maNguoiBan")==user,
                        "Người mua xác nhận đã xem xe; người bán ghi nhận khách không đến.");
                after="visited".equals(action)?"DA_XEM_XE":"KHONG_DEN";
                title="visited".equals(action)?"Người mua đã xem xe":"Người bán ghi nhận khách không đến";
            } else {
                require("cancel".equals(action),"Thao tác không hợp lệ.");
                require(note!=null && !note.isBlank() && note.length()<=1000,"Vui lòng ghi lý do hủy (tối đa 1.000 ký tự).");
                after="DA_HUY"; title="Lịch hẹn đã hủy";
            }
            update(c,"UPDATE lichhenxacnhan SET trangThai=?,phienBan=phienBan+1 WHERE maLichHen=?",after,appointment);
            String requestStatus="DA_XEM_XE".equals(after)?"DA_XEM_XE":Set.of("DA_HUY","KHONG_DEN").contains(after)?"DA_HUY":"DA_LIEN_HE";
            update(c,"UPDATE yeucaumuaxe SET trangThai=?,ngayCapNhat=NOW() WHERE maYeuCau=?",requestStatus,requestId);
            update(c,"UPDATE lichhenxemxe SET trangThai=?,ngayCapNhat=NOW() WHERE maLichHen=?",requestStatus,appointment);
            audit(c,"LICH_HEN",appointment,user,title,before,after,note,null);
            int other=id(row,"maNguoiMua")==user?id(row,"maNguoiBan"):id(row,"maNguoiMua");
            DbWork.notify(c,other,title,"Lịch xem xe "+row.get("tenXe")+" vừa được cập nhật.","lich-hen/chi-tiet?id="+requestId);
            return null;
        });
    }

    private void checkOverlap(Connection c, Map<String,Object> row, int appointment, Timestamp time) throws SQLException {
        // Serialize all appointment changes for the same seller, including different cars.
        one(c,"SELECT maNguoiDung FROM nguoidung WHERE maNguoiDung=? FOR UPDATE",row.get("maNguoiBan"));
        require(list(c,"SELECT l.maLichHen FROM lichhenxemxe l JOIN lichhenxacnhan v ON v.maLichHen=l.maLichHen JOIN yeucaumuaxe y ON y.maYeuCau=l.maYeuCau "
                +"WHERE y.maNguoiBan=? AND l.maLichHen<>? AND v.trangThai='DA_XAC_NHAN' AND ABS(TIMESTAMPDIFF(MINUTE,l.thoiGianHen,?))<60",row.get("maNguoiBan"),appointment,time).isEmpty(),
                "Người bán có lịch đã xác nhận trong khung giờ này. Hãy chọn giờ cách ít nhất 60 phút.");
    }

    public void remindUpcoming() throws Exception {
        transaction(c -> {
            for(Map<String,Object> row:list(c,QUERY+"WHERE v.trangThai='DA_XAC_NHAN' AND v.daNhac=0 AND l.thoiGianHen>NOW() AND l.thoiGianHen<=DATE_ADD(NOW(),INTERVAL 24 HOUR) AND y.trangThai NOT IN ('DA_HUY','DA_CHOT','HOAN_TAT') FOR UPDATE")) {
                update(c,"UPDATE lichhenxacnhan SET daNhac=1 WHERE maLichHen=?",row.get("maLichHen"));
                String body="Xe "+row.get("tenXe")+", lúc "+row.get("thoiGianHen")+", tại "+row.get("diaDiemHen");
                DbWork.notify(c,id(row,"maNguoiMua"),"Nhắc lịch xem xe trong 24 giờ tới",body,"lich-hen/chi-tiet?id="+row.get("maYeuCau"));
                DbWork.notify(c,id(row,"maNguoiBan"),"Nhắc lịch xem xe trong 24 giờ tới",body,"lich-hen/chi-tiet?id="+row.get("maYeuCau"));
            }
            return null;
        });
    }
}
