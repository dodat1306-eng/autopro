package dao;

import context.DBContext;
import java.sql.*;
import java.util.*;
import util.DbWork;
import static util.DbWork.*;

public class GiaoDichWorkflowDAO extends DBContext {
    private static final String DETAIL = "giao-dich/chi-tiet?id=";

    private Map<String,Object> lock(Connection c, int id) throws SQLException {
        Map<String,Object> key = one(c,"SELECT maXe FROM giaodichxe WHERE maGiaoDich=?",id);
        one(c,"SELECT maXe FROM xe WHERE maXe=? FOR UPDATE",key.get("maXe"));
        return one(c,"SELECT * FROM giaodichxe WHERE maGiaoDich=? FOR UPDATE",id);
    }

    public void accept(int id, int seller) throws Exception {
        transaction(c -> {
            Map<String,Object> g = lock(c,id);
            require(id(g,"maNguoiBan")==seller,"Chỉ người bán được tiếp nhận giao dịch.");
            activeUser(c,seller);
            require("MOI_TAO".equals(g.get("trangThai")),"Giao dịch đã được xử lý.");
            Map<String,Object> deadline=one(c,"SELECT hanPhanHoi > NOW() AS conHan FROM giaodichtiepnhan WHERE maGiaoDich=? FOR UPDATE",id);
            require(flag(deadline,"conHan"),"Yêu cầu đã hết thời hạn phản hồi.");
            require(list(c,"SELECT maGiaoDich FROM giaodichxe WHERE maXe=? AND maGiaoDich<>? AND trangThai NOT IN ('MOI_TAO','HUY')",g.get("maXe"),id).isEmpty(),
                    "Xe đã có giao dịch khác được tiếp nhận hoặc đã bán.");
            require(!list(c,"SELECT maBaiDang FROM baidang WHERE maXe=? AND trangThai='da_duyet'",g.get("maXe")).isEmpty(),"Tin xe không còn được phép giao dịch.");
            update(c,"UPDATE giaodichxe SET trangThai='CHO_XAC_NHAN_HAI_BEN' WHERE maGiaoDich=?",id);
            update(c,"UPDATE giaodichtiepnhan SET ngayChapNhan=NOW() WHERE maGiaoDich=?",id);
            update(c,"UPDATE baidang SET trangThai='dang_giao_dich' WHERE maXe=? AND trangThai='da_duyet'",g.get("maXe"));
            update(c,"UPDATE yeucaumuaxe SET trangThai='DA_CHOT' WHERE maYeuCau=?",g.get("maYeuCau"));
            audit(c,"GIAO_DICH",id,seller,"Người bán tiếp nhận","MOI_TAO","CHO_XAC_NHAN_HAI_BEN",null,null);
            notifyBoth(c,g,"Người bán đã tiếp nhận giao dịch","Hai bên có thể tiến hành giao xe và xác nhận sau khi hoàn tất ngoài thực tế.");
            // Other pending buyers get a definite result instead of waiting on an unavailable car.
            for (Map<String,Object> other : list(c,"SELECT * FROM giaodichxe WHERE maXe=? AND trangThai='MOI_TAO' AND maGiaoDich<>? FOR UPDATE",g.get("maXe"),id)) {
                closePending(c,other,seller,"Xe đã được người bán tiếp nhận giao dịch với khách khác.");
            }
            return null;
        });
    }

    private void closePending(Connection c, Map<String,Object> g, Integer actor, String reason) throws SQLException {
        int id=id(g,"maGiaoDich");
        update(c,"UPDATE giaodichxe SET trangThai='HUY' WHERE maGiaoDich=?",id);
        update(c,"UPDATE yeucaumuaxe SET trangThai='DA_HUY' WHERE maYeuCau=?",g.get("maYeuCau"));
        update(c,"UPDATE lichhenxemxe SET trangThai='DA_HUY' WHERE maYeuCau=?",g.get("maYeuCau"));
        audit(c,"GIAO_DICH",id,actor,"Đóng yêu cầu chờ tiếp nhận","MOI_TAO","HUY",reason,null);
        notifyBoth(c,g,"Yêu cầu giao dịch đã đóng",reason);
    }

    public void expirePending() throws Exception {
        List<Map<String,Object>> due;
        try(Connection c=getConnection()) {
            due=list(c,"SELECT g.maGiaoDich FROM giaodichxe g JOIN giaodichtiepnhan t ON t.maGiaoDich=g.maGiaoDich WHERE g.trangThai='MOI_TAO' AND t.hanPhanHoi<=NOW()");
        }
        for(Map<String,Object> row:due) transaction(c -> {
            Map<String,Object> g=lock(c,id(row,"maGiaoDich"));
            if("MOI_TAO".equals(g.get("trangThai")) && !list(c,"SELECT maGiaoDich FROM giaodichtiepnhan WHERE maGiaoDich=? AND hanPhanHoi<=NOW()",g.get("maGiaoDich")).isEmpty())
                closePending(c,g,null,"Người bán chưa tiếp nhận trong 48 giờ. Bạn có thể gửi yêu cầu mới.");
            return null;
        });
    }

    public void dispute(int id, int actor, String reason, String file) throws Exception {
        require(reason!=null && reason.trim().length()>=10 && reason.length()<=2000,"Nội dung khiếu nại cần từ 10 đến 2.000 ký tự.");
        transaction(c -> {
            Map<String,Object> g=lock(c,id);
            activeUser(c,actor);
            require(actor==id(g,"maNguoiMua") || actor==id(g,"maNguoiBan"),"Bạn không thuộc giao dịch này.");
            require(Set.of("CHO_XAC_NHAN_HAI_BEN","CHO_XAC_NHAN_CON_LAI","TRANH_CHAP").contains(g.get("trangThai")),"Không thể khiếu nại ở trạng thái hiện tại.");
            update(c,"UPDATE giaodichxe SET trangThai='TRANH_CHAP' WHERE maGiaoDich=?",id);
            audit(c,"GIAO_DICH",id,actor,"Gửi khiếu nại / bổ sung bằng chứng",(String)g.get("trangThai"),"TRANH_CHAP",reason.trim(),file);
            notifyBoth(c,g,"Giao dịch đang được xem xét","Có khiếu nại mới. Các xác nhận được tạm dừng để admin xem xét.");
            for(Map<String,Object> admin:list(c,"SELECT maNguoiDung FROM nguoidung WHERE vaiTro='admin' AND trangThai='active'"))
                DbWork.notify(c,id(admin,"maNguoiDung"),"Khiếu nại giao dịch #"+id,"Vui lòng xem nội dung và bằng chứng.","admin/giao-dich?id="+id);
            return null;
        });
    }

    public List<Map<String,Object>> history(int id) throws Exception {
        try(Connection c=getConnection()) {
            return list(c,"SELECT n.*,u.hoTen FROM nhatkyhoatdong n LEFT JOIN nguoidung u ON u.maNguoiDung=n.nguoiThucHien WHERE doiTuong='GIAO_DICH' AND maDoiTuong=? ORDER BY maNhatKy DESC",id);
        }
    }

    public static void pendingCreated(Connection c, int id, int actor) throws SQLException {
        update(c,"INSERT INTO giaodichtiepnhan(maGiaoDich,hanPhanHoi) VALUES(?,DATE_ADD(NOW(),INTERVAL 48 HOUR))",id);
        audit(c,"GIAO_DICH",id,actor,"Tạo yêu cầu chờ người bán",null,"MOI_TAO","Thời hạn phản hồi: 48 giờ.",null);
    }

    private void notifyBoth(Connection c, Map<String,Object> g, String title, String body) throws SQLException {
        DbWork.notify(c,id(g,"maNguoiMua"),title,body,DETAIL+g.get("maGiaoDich"));
        DbWork.notify(c,id(g,"maNguoiBan"),title,body,DETAIL+g.get("maGiaoDich"));
    }
}
