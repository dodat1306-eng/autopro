import context.DBContext;
import dao.*;
import model.*;
import util.*;
import java.sql.*;
import java.util.*;
import java.util.concurrent.*;
import static util.DbWork.*;

/** Integration tests. Run only against a disposable database ending in _qa. */
public class PriorityWorkflowTest {
    private static int checks;
    private static final String RUN=UUID.randomUUID().toString().substring(0,8);
    private static final String PASSWORD="AutoproQa@2026";
    private static int buyer,seller,other,admin,province;
    private static final GiaoDichXeDAO TX=new GiaoDichXeDAO();
    private static final GiaoDichWorkflowDAO FLOW=new GiaoDichWorkflowDAO();
    private static final LichHenDAO APPOINTMENTS=new LichHenDAO();
    private static void check(boolean value,String label) { if(!value) throw new AssertionError(label); checks++; System.out.println("PASS "+label); }
    @FunctionalInterface private interface Action { void run() throws Exception; }
    private static void denied(Action action,String label) throws Exception {
        try { action.run(); } catch(IllegalArgumentException e) { check(true,label); return; }
        throw new AssertionError("Was not rejected: "+label);
    }
    private static int user(String role,String tag) throws Exception {
        return transaction(c -> insert(c,"INSERT INTO nguoidung(hoTen,email,matKhau,vaiTro,trangThai) VALUES(?,?,?,?,'active')",
                "QA "+tag,tag+"."+RUN+"@example.invalid",PasswordUtil.hashPassword(PASSWORD),role));
    }
    private static int car() throws Exception {
        return transaction(c -> {
            int id=insert(c,"INSERT INTO xe(tenXe,gia,namSanXuat) VALUES('QA workflow car',500000000,2022)");
            insert(c,"INSERT INTO baidang(maNguoiDung,maXe,tieuDe,trangThai,maTinh) VALUES(?,?,'QA workflow post','da_duyet',?)",seller,id,province);
            return id;
        });
    }
    private static Object scalar(String sql,Object...args) throws Exception {
        try(Connection c=new DBContext().getConnection()) { return one(c,sql,args).values().iterator().next(); }
    }
    private static String status(int id) { return TX.getById(id).getTrangThai(); }
    private static int pending(int car,int person) {
        GiaoDichXe g=TX.createInProgressTransactionForCar(person,car,500000000,"QA request","QA address","0900000000");
        check(g!=null,"pending transaction created"); return g.getMaGiaoDich();
    }
    private static void transactionTests() throws Exception {
        int car=car(), a=pending(car,buyer), b=pending(car,other);
        check("MOI_TAO".equals(status(a)),"new transaction awaits seller");
        check("da_duyet".equals(scalar("SELECT trangThai FROM baidang WHERE maXe=?",car)),"pending request does not lock listing");
        check(!TX.hasActiveTransactionForCar(car),"pending request not counted as accepted");
        check(!TX.buyerConfirmReceived(a,buyer,"premature"),"buyer cannot confirm before acceptance");
        check(!TX.sellerConfirmDelivered(a,seller,"premature"),"seller cannot deliver before acceptance");
        denied(()->FLOW.accept(a,buyer),"buyer cannot accept as seller");
        FLOW.accept(a,seller);
        check("CHO_XAC_NHAN_HAI_BEN".equals(status(a)),"seller acceptance transitions state");
        check("HUY".equals(status(b)),"other pending request closes");
        check("dang_giao_dich".equals(scalar("SELECT trangThai FROM baidang WHERE maXe=?",car)),"accepted listing is locked");
        denied(()->FLOW.accept(a,seller),"repeat acceptance rejected");
        check(!TX.buyerConfirmReceived(a,other,"wrong buyer"),"outsider cannot confirm receipt");
        check(TX.sellerConfirmDelivered(a,seller,"Vehicle delivered"),"seller confirms delivery");
        check(!TX.sellerConfirmDelivered(a,seller,"again"),"duplicate delivery rejected");
        check(!TX.cancelTransaction(a,buyer,"cancel after delivery"),"cannot cancel after counterparty confirms");
        FLOW.dispute(a,buyer,"Documents need clarification before confirming receipt.",null);
        check("TRANH_CHAP".equals(status(a)),"buyer can submit dispute");
        check(!TX.buyerConfirmReceived(a,buyer,"cannot"),"confirmation frozen during dispute");
        check(!TX.adminUpdateStatus(a,"HOAN_TAT",admin,"Should not fabricate buyer confirmation"),"admin cannot fabricate participant confirmations");
        check(!TX.adminUpdateStatus(a,"CHO_XAC_NHAN_HAI_BEN",other,"Not administrator"),"DAO enforces admin role");
        check(TX.adminUpdateStatus(a,"CHO_XAC_NHAN_HAI_BEN",admin,"Resolved missing documents; resume confirmations"),"admin resumes dispute without erasing confirmation");
        check("CHO_XAC_NHAN_CON_LAI".equals(status(a)),"resume derives remaining confirmation state");
        check(TX.buyerConfirmReceived(a,buyer,"Paid offline and received vehicle"),"buyer completes transaction");
        check("HOAN_TAT".equals(status(a)),"both confirmations complete transaction");
        check("da_ban".equals(scalar("SELECT trangThai FROM baidang WHERE maXe=?",car)),"completed car marked sold");
        check(new XeDAO().getPublicXeById(car)==null,"sold car hidden from public detail");
        check(FLOW.history(a).size()>=6,"audit retains creation acceptance dispute and confirmations");
        int expCar=car(), exp=pending(expCar,buyer);
        transaction(c->{update(c,"UPDATE giaodichtiepnhan SET hanPhanHoi=DATE_SUB(NOW(),INTERVAL 1 MINUTE) WHERE maGiaoDich=?",exp); return null;});
        denied(()->FLOW.accept(exp,seller),"expired request cannot be accepted before cleanup");
        FLOW.expirePending(); FLOW.expirePending();
        check("HUY".equals(status(exp)),"expired pending transaction closes idempotently");
        check("da_duyet".equals(scalar("SELECT trangThai FROM baidang WHERE maXe=?",expCar)),"expiry leaves listing available");
        int raceCar=car(), r1=pending(raceCar,buyer), r2=pending(raceCar,other);
        ExecutorService pool=Executors.newFixedThreadPool(2);
        try {
            CountDownLatch latch=new CountDownLatch(1);
            Callable<Boolean> first=()->{latch.await();try { FLOW.accept(r1,seller); return true; }catch(IllegalArgumentException e){return false;}};
            Callable<Boolean> second=()->{latch.await();try { FLOW.accept(r2,seller); return true; }catch(IllegalArgumentException e){return false;}};
            Future<Boolean> one=pool.submit(first),two=pool.submit(second); latch.countDown();
            check(one.get(20,TimeUnit.SECONDS)^two.get(20,TimeUnit.SECONDS),"concurrent acceptance allows only one buyer");
        } finally {pool.shutdownNow();}
    }
    private static int appointment(int car,int person,Timestamp time) {
        int id=new YeuCauKetNoiDAO().createYeuCauKetNoi(person,car,500000000,"QA",time,"QA showroom","QA appointment");
        check(id>0,"appointment created"); return id;
    }
    private static int version(int id,int person) throws Exception {return id(APPOINTMENTS.get(id,person),"phienBan");}
    private static void appointmentTests() throws Exception {
        Timestamp future=new Timestamp(System.currentTimeMillis()+3*3600000);
        int id=appointment(car(),buyer,future);
        denied(()->APPOINTMENTS.get(id,other),"appointment privacy");
        denied(()->APPOINTMENTS.act(id,buyer,1,"confirm",null,null,null),"proposer cannot self-confirm appointment");
        APPOINTMENTS.act(id,seller,1,"confirm",null,null,null);
        check("DA_XAC_NHAN".equals(APPOINTMENTS.get(id,buyer).get("tinhTrangHen")),"seller confirms actual proposed time");
        check(!"DA_XEM_XE".equals(new YeuCauKetNoiDAO().getYeuCauKetNoiById(id).getTrangThai()),"confirmed appointment is not a completed visit");
        check(TX.createFromYeuCau(id)==null,"cannot transact on appointment confirmation alone");
        denied(()->APPOINTMENTS.act(id,buyer,2,"visited",null,null,null),"cannot report visit before appointment time");
        APPOINTMENTS.act(id,seller,2,"propose",new Timestamp(future.getTime()+3600000),"QA new place","Changed time");
        denied(()->APPOINTMENTS.act(id,buyer,2,"confirm",null,null,null),"stale version cannot confirm a changed time");
        APPOINTMENTS.act(id,buyer,3,"confirm",null,null,null);
        int conflict=appointment(car(),other,new Timestamp(future.getTime()+3600000));
        denied(()->APPOINTMENTS.act(conflict,seller,1,"confirm",null,null,null),"seller cannot double-book confirmed slots");
        long before=((Number)scalar("SELECT COUNT(*) FROM thongbao WHERE maNguoiDung=?",buyer)).longValue();
        APPOINTMENTS.remindUpcoming(); APPOINTMENTS.remindUpcoming();
        long after=((Number)scalar("SELECT COUNT(*) FROM thongbao WHERE maNguoiDung=?",buyer)).longValue();
        check(after-before==1,"24-hour reminder sent once");
        transaction(c->{update(c,"UPDATE lichhenxemxe SET thoiGianHen=DATE_SUB(NOW(),INTERVAL 1 HOUR) WHERE maYeuCau=?",id);return null;});
        APPOINTMENTS.act(id,buyer,4,"visited",null,null,null);
        check(TX.createFromYeuCau(id)!=null,"completed visit can create pending transaction");
        int noShow=appointment(car(),other,new Timestamp(future.getTime()+10*3600000));
        APPOINTMENTS.act(noShow,seller,1,"confirm",null,null,null);
        transaction(c->{update(c,"UPDATE lichhenxemxe SET thoiGianHen=DATE_SUB(NOW(),INTERVAL 1 HOUR) WHERE maYeuCau=?",noShow);return null;});
        APPOINTMENTS.act(noShow,seller,2,"no-show",null,null,null);
        check("KHONG_DEN".equals(APPOINTMENTS.get(noShow,other).get("tinhTrangHen")),"seller records no-show");
    }
    private static void contactTests() throws Exception {
        XacMinhLienHeDAO dao=new XacMinhLienHeDAO();
        String key="a".repeat(64), target="signup."+RUN+"@example.invalid", hash=PasswordUtil.hashPassword(PASSWORD);
        String id=dao.create(key,null,"DANG_KY","email",target,"QA Signup",hash,"123456");
        denied(()->dao.verify(id,key,null,"123456"),"undelivered OTP cannot verify");
        dao.delivered(id,true);
        denied(()->dao.verify(id,"b".repeat(64),null,"123456"),"OTP is bound to requesting session");
        denied(()->dao.verify(id,key,null,"000000"),"incorrect OTP rejected");
        int account=dao.verify(id,key,null,"123456");
        check(new NguoiDungDAO().login(target,PASSWORD)!=null,"account created only after verified registration");
        denied(()->dao.verify(id,key,null,"123456"),"consumed OTP cannot be reused");
        String newContact="changed."+RUN+"@example.invalid";
        String change=dao.create(key,account,"LIEN_HE","email",newContact,null,null,"234567");
        check(target.equals(new NguoiDungDAO().getUserById(account).getEmail()),"old contact retained while OTP pending");
        dao.delivered(change,true); dao.verify(change,key,account,"234567");
        check(newContact.equals(new NguoiDungDAO().getUserById(account).getEmail()),"verified new contact becomes active");
        String wrong=dao.create(key,account,"LIEN_HE","email","wrong."+RUN+"@example.invalid",null,null,"345678"); dao.delivered(wrong,true);
        for(int i=0;i<5;i++) denied(()->dao.verify(wrong,key,account,"000000"),"OTP incorrect attempt "+i);
        denied(()->dao.verify(wrong,key,account,"345678"),"correct code locked out after five failed attempts");
        denied(()->dao.create(key,account,"LIEN_HE","email","wrong."+RUN+"@example.invalid",null,null,"111111"),"resend cooldown enforced server-side");
        String expired=dao.create(key,account,"LIEN_HE","email","expired."+RUN+"@example.invalid",null,null,"456789");dao.delivered(expired,true);
        transaction(c->{update(c,"UPDATE xacminhlienhe SET hanDung=DATE_SUB(NOW(),INTERVAL 1 MINUTE) WHERE maXacMinh=?",expired);return null;});
        denied(()->dao.verify(expired,key,account,"456789"),"expired OTP rejected");
        denied(()->dao.changePassword(account,"wrong", "NewPassword@2026"),"changing password requires existing password");
        dao.changePassword(account,PASSWORD,"NewPassword@2026");
        check(new NguoiDungDAO().login(newContact,PASSWORD)==null && new NguoiDungDAO().login(newContact,"NewPassword@2026")!=null,"password change invalidates old password");
    }
    private static void identityTests() throws Exception {
        DangKySellerDAO dao=new DangKySellerDAO();
        int old=transaction(c->insert(c,"INSERT INTO dangkyseller(maNguoiDung,hoTen,soDienThoai,email,soCCCD,anhCCCDMatTruoc,anhCCCDMatSau,maTinh,trangThai) VALUES(?,'QA seller','0900000000',?,'123456789012','private/original-front.png','private/original-back.png',?,'DA_DUYET')",seller,new NguoiDungDAO().getUserById(seller).getEmail(),province));
        check(dao.updateLatestSellerIdentityInfo(seller,"123456789013","private/new-front.png","private/new-back.png"),"identity edit creates review request");
        int revision=dao.getByUserId(seller).getMaDangKySeller();
        check(revision!=old && dao.getById(revision).isChoDuyet(),"identity revision awaits admin");
        check("123456789012".equals(dao.getById(old).getSoCCCD()) && dao.getById(old).isDaDuyet(),"approved identity snapshot preserved");
        check(!dao.updateLatestSellerIdentityInfo(seller,"123456789014",null,null),"duplicate pending identity blocked");
        check(!dao.approveSeller(revision,buyer),"buyer cannot approve identity through DAO");
        check(dao.rejectSeller(revision,admin,"Need a clearer photo of the identity document"),"admin can reject revision");
        check(!dao.approveSeller(revision,admin),"rejected revision cannot be approved later");
        check(new NguoiDungDAO().getUserById(seller).isSeller(),"rejected revision preserves previously approved seller role");
        check(dao.updateLatestSellerIdentityInfo(seller,"123456789015","private/second-front.png","private/second-back.png"),"new submission allowed after rejection");
        int second=dao.getByUserId(seller).getMaDangKySeller();
        check(dao.approveSeller(second,admin),"admin approves new identity revision");
        check(!dao.approveSeller(second,admin),"repeat identity approval rejected");
    }
    public static void main(String[] args) throws Exception {
        try(Connection c=new DBContext().getConnection()) {
            if(!c.getCatalog().endsWith("_qa")) throw new IllegalStateException("Refusing to write test fixtures outside a _qa database");
            province=id(one(c,"SELECT maTinh FROM tinhthanh LIMIT 1"),"maTinh");
        }
        buyer=user("buyer","buyer");seller=user("seller","seller");other=user("buyer","other");admin=user("admin","admin");
        transactionTests();appointmentTests();contactTests();identityTests();
        System.out.println("ALL PASSED: "+checks);
        System.out.println("HTTP_FIXTURES: "+RUN+" buyer="+buyer+" seller="+seller+" other="+other+" admin="+admin);
    }
}
