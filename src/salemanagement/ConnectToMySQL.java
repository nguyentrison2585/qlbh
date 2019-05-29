/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package salemanagement;

import java.awt.HeadlessException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;
import model.ChiTietHD;
import model.HoaDonBan;
import model.HoaDonNhap;
import model.KhachHang;
import model.NhaCC;
import model.NhanVien;
import model.SanPham;

/**
 *
 * @author Admin
 */
public class ConnectToMySQL {
    public Connection con;
    public final String CLASS_NAME = "com.mysql.cj.jdbc.Driver";
    public final String DB_URL = "jdbc:mysql://localhost:3306/qlbh";
    public final String USER_NAME = "root";
    public final String PASSWORD = "sonditnhon";
    public String command;
    public Statement state;
    public PreparedStatement pState;
    public ResultSet result;
    
    public ConnectToMySQL() {
        connect();
    }
    
    //Kết nối đến database
    public void connect() {
        try {
            Class.forName(CLASS_NAME);
            con = DriverManager.getConnection(DB_URL,USER_NAME,PASSWORD);    
            state = con.createStatement();
        } catch (ClassNotFoundException | SQLException e) {
            JOptionPane.showMessageDialog(null, "Lỗi kết nối tới cơ sở dữ liệu !","Error",JOptionPane.ERROR_MESSAGE);
            System.out.println(e);
        }
    }
    
    //Thực thi truy vấn và gán dữ liệu cho table model
    public DefaultTableModel query(DefaultTableModel model) {
        try {
            model.setRowCount(0);
            result = state.executeQuery(command);
            ResultSetMetaData rsMD = result.getMetaData();
            int colCount = rsMD.getColumnCount();
            Object arr[] = new Object[colCount+1];
            int count = 1;
            while (result.next()) {
                arr[0] = Integer.toString(count++);
                for(int i=0;i<colCount;i++) {
                    arr[i+1] = result.getString(i+1);
                }
                model.addRow(arr);
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Lỗi lấy dữ liệu từ cơ sở dữ liệu !", "Error", JOptionPane.ERROR_MESSAGE);
            System.out.println(e);
        }
        return model;
    }
    
    //Lấy dữ liệu của 1 bảng đơn
    public DefaultTableModel getData(DefaultTableModel model,String table) {
        model.setRowCount(0);
        command = "select * from " + table;
        query(model);
        return model;
    }
    
    //Lấy sản phẩm theo nhà cung cấp
    public ArrayList<String> getListSP(String nhaCC) {
        ArrayList<String> listData = new ArrayList<>();
        command = "select ten_san_pham from san_pham where ncc = '" + nhaCC + "' order by id";
        try {
            result = state.executeQuery(command);
            while (result.next()) {
                listData.add(result.getString(1));
            }
        } catch (SQLException e) {
            System.out.println(e);
        }
        return listData;
    }
    
    //Lấy dữ liệu cho bảng chi tiết sản phẩm
    public DefaultTableModel getCTSP(DefaultTableModel model) {
        model.setRowCount(0);
        command = "select sp.id, sp.ten_san_pham, sp.loai_san_pham, ncc.ten_ncc,"
                + "sp.don_vi, sp.gia_nhap, sp.gia_tien, sp.so_luong, sp.hinh_anh from san_pham sp, "
                + "nha_cung_cap ncc where sp.ncc = ncc.id order by sp.id";
        query(model);
        return model;
    }
    
    //Lấy dữ liệu cho bảng hóa đơn bán
    public DefaultTableModel getHDB(DefaultTableModel model) {
        model.setRowCount(0);
        command = "select hdb.id, nv.ho_ten, kh.ho_ten, hdb.ngay_ban, sum(ctb.so_luong * sp.gia_tien) "
                + "from hoa_don_ban hdb, chi_tiet_ban ctb, nhan_vien nv, san_pham sp, khach_hang kh "
                + "where hdb.id = ctb.ma_hoa_don and ctb.ma_san_pham = sp.id and "
                + "hdb.ma_nhan_vien = nv.id and hdb.ma_khach_hang = kh.id group by hdb.id order by hdb.id";
        query(model);
        return model;
    }
    
    //Lấy dữ liệu cho bảng hóa đơn nhập
    public DefaultTableModel getHDN(DefaultTableModel model) {
        model.setRowCount(0);
        command = "select hdn.id, nv.ho_ten, ncc.ten_ncc, hdn.ngay_nhap, sum(ctn.so_luong * sp.gia_nhap) "
                + "from hoa_don_nhap hdn, chi_tiet_nhap ctn, nhan_vien nv, nha_cung_cap ncc, "
                + "san_pham sp where hdn.id = ctn.ma_hoa_don and hdn.ma_nhan_vien = nv.id "
                + "and ctn.ma_san_pham = sp.id and hdn.ncc = ncc.id group by hdn.id order by hdn.id";
        query(model);
        return model;
    }
    
    //Lấy chi tiết hóa đơn bán
    public DefaultTableModel getCTHDB(DefaultTableModel model, String maHDB) {
        model.setRowCount(0);
        command = "select sp.ten_san_pham, sp.don_vi, sp.gia_tien, ctb.so_luong, (sp.gia_tien * ctb.so_luong) as thanh_tien "
                + "from chi_tiet_ban ctb, san_pham sp where ctb.ma_san_pham = sp.id and ctb.ma_hoa_don = '" + maHDB + "'";
        query(model);
        return model;
    }
    
    //Lấy chi tiết hóa đơn nhập
    public DefaultTableModel getCTHDN(DefaultTableModel model, String maHDN) {
        model.setRowCount(0);
        command = "select sp.ten_san_pham, sp.don_vi, sp.gia_nhap, ctn.so_luong, (sp.gia_nhap * ctn.so_luong) as thanh_tien "
                + "from chi_tiet_nhap ctn, san_pham sp where ctn.ma_san_pham = sp.id and ctn.ma_hoa_don = '" + maHDN + "'";
        query(model);
        return model;
    }
    
    //Lấy dữ liệu khi tìm kiếm
    public DefaultTableModel getData(DefaultTableModel model, String table, String column, String value) {
        model.setRowCount(0);
        command = "select * from " + table + " where " + column + " like '%" + value + "%'";
        query(model);
        return model;
    }
    
    //Lấy dữ liệu tìm kiếm sản phẩm theo nhà cung cấp
    public DefaultTableModel getTKSP_NCC(DefaultTableModel model, String tenNCC) {
        model.setRowCount(0);
        command = "select sp.id, sp.ten_san_pham, sp.loai_san_pham, ncc.ten_ncc,"
                + "sp.don_vi, sp.gia_tien, sp.so_luong, sp.hinh_anh from san_pham sp, "
                + "nha_cung_cap ncc where sp.ncc = ncc.id and ncc.ten_ncc = '" + tenNCC + "'";
        query(model);
        return model;
    }
    
    //Lấy dữ liệu tìm kiếm hóa đơn
    //Theo tên nhân viên
    public DefaultTableModel getTKHDB_NV(DefaultTableModel model, String tenNV) {
        model.setRowCount(0);
        command = "select hdb.id, nv.ho_ten, kh.ho_ten, hdb.ngay_ban, "
                + "sum(ctb.so_luong * sp.gia_tien) from hoa_don_ban hdb, "
                + "chi_tiet_ban ctb, nhan_vien nv, khach_hang kh, san_pham sp where "
                + "hdb.id = ctb.ma_hoa_don and ctb.ma_san_pham = sp.id and hdb.ma_khach_hang = kh.id and "
                + "hdb.ma_nhan_vien = nv.id and nv.ho_ten like '%" + tenNV + "%' group by hdb.id";
        query(model);
        return model;
    }
    
    //Theo tên khách hàng
    public DefaultTableModel getTKHDB_KH(DefaultTableModel model, String tenKH) {
        model.setRowCount(0);
        command = "select hdb.id, nv.ho_ten, kh.ho_ten, hdb.ngay_ban, "
                + "sum(ctb.so_luong * sp.gia_tien) from hoa_don_ban hdb, "
                + "chi_tiet_ban ctb, nhan_vien nv,  san_pham sp, khach_hang kh where "
                + "hdb.id = ctb.ma_hoa_don and ctb.ma_san_pham = sp.id and hdb.ma_khach_hang = kh.id and " 
                + "hdb.ma_nhan_vien = nv.id and kh.ho_ten like '%" + tenKH + "%' group by hdb.id";
        query(model);
        return model;
    }
    
    //Theo ngày
    public DefaultTableModel getTKHDB_Ngay(DefaultTableModel model, String ngayBan) {
        model.setRowCount(0);
        command = "select hdb.id, nv.ho_ten, kh.ho_ten, hdb.ngay_ban, "
                + "sum(ctb.so_luong * sp.gia_tien) from hoa_don_ban hdb, "
                + "chi_tiet_ban ctb, nhan_vien nv,  san_pham sp, khach_hang kh where "
                + "hdb.id = ctb.ma_hoa_don and ctb.ma_san_pham = sp.id and hdb.ma_khach_hang = kh.id and " 
                + "hdb.ma_nhan_vien = nv.id and hdb.ngay_ban = '" + ngayBan + "' group by hdb.ngay_ban";
        query(model);
        return model;
    }
    
    //Tìm kiếm hóa đơn nhập
    //Theo tên nhân viên
    public DefaultTableModel getTKHDN_NV(DefaultTableModel model, String tenNV) {
        model.setRowCount(0);
        command = "select hdn.id, nv.ho_ten, ncc.ten_ncc, hdn.ngay_nhap, "
                + "sum(ctn.so_luong * sp.gia_nhap) from hoa_don_nhap hdn, "
                + "chi_tiet_nhap ctn, nhan_vien nv, nha_cung_cap ncc, san_pham sp where "
                + "hdn.id = ctn.ma_hoa_don and ctn.ma_san_pham = sp.id and hdn.ma_nhan_vien = nv.id and "
                + "hdn.ncc = ncc.id and nv.ho_ten like '%" + tenNV + "%' group by hdn.id";
        query(model);
        return model;
    }
    //Theo nhà cung cấp
    public DefaultTableModel getTKHDN_NCC(DefaultTableModel model, String tenNCC) {
        model.setRowCount(0);
        command = "select hdn.id, nv.ho_ten, ncc.ten_ncc, hdn.ngay_nhap, "
                + "sum(ctn.so_luong * sp.gia_nhap) from hoa_don_nhap hdn, "
                + "chi_tiet_nhap ctn, nhan_vien nv, nha_cung_cap ncc, san_pham sp where "
                + "hdn.id = ctn.ma_hoa_don and hdn.ma_nhan_vien = nv.id and ctn.ma_san_pham = sp.id and "
                + "hdn.ncc = ncc.id and ncc.ten_ncc like '%" + tenNCC + "%' group by hdn.id";
        query(model);
        return model;
    }
    
    //Theo ngày
    public DefaultTableModel getTKHDN_Ngay(DefaultTableModel model, String ngayNhap) {
        model.setRowCount(0);
        command = "select hdn.id, nv.ho_ten, ncc.ten_ncc, hdn.ngay_nhap, "
                + "sum(ctn.so_luong * sp.gia_nhap) from hoa_don_nhap hdn, "
                + "chi_tiet_nhap ctn, nhan_vien nv, nha_cung_cap ncc, san_pham sp where "
                + "hdn.id = ctn.ma_hoa_don and hdn.ma_nhan_vien = nv.id and ctn.ma_san_pham = sp.id and "
                + "hdn.ncc = ncc.id and hdn.ngay_nhap = '" + ngayNhap + " ' group by hdn.ngay_nhap";
        query(model);
        return model;
    }
    
    //Lấy dữ liệu cần thiết của các bảng
    //Lấy một cột trong 1 bảng
    public ArrayList<String> getItemList(String table,String columnName) {
        ArrayList<String> listItem = new ArrayList<>();
        command = "select distinct " + columnName + " from " + table + " order by id";
        try {
            result = state.executeQuery(command);
            while (result.next()) {
                listItem.add(result.getString(1));
            }
        }
        catch (SQLException e) {
            System.out.println(e);
        }
        return listItem;
    }
    
    //Lấy mã khách hàng khi biết tên
    public String getMaKH(String tenKH) {
        String maKH = "";
        command = "select id from khach_hang where ho_ten = '" + tenKH + "'";
        try {
            result = state.executeQuery(command);
            if (result.next()) {
                maKH = result.getString(1);
            }
        } catch (SQLException e) {
            System.out.println(e);
        }
        return maKH;
    }
    
    //Lấy mã nhà cung cấp khi biết tên
    public String getMaNCC(String tenNCC) {
        String maNCC = "";
        command = "select id from nha_cung_cap where ten_ncc = '" + tenNCC + "'";
        try {
            result = state.executeQuery(command);
            if (result.next()) {
                maNCC = result.getString(1);
            }
        } catch (SQLException e) {
            System.out.println(e);
        }
        return maNCC;
    }
    
    //Lấy mã nhân viên khi biết tên 
    public String getMaNV(String tenNV) {
        String maNV = "";
        command = "select id from nhan_vien where ho_ten = '" + tenNV + "'";
        try {
            result = state.executeQuery(command);
            if (result.next()) {
                maNV = result.getString(1);
            }
        } catch (Exception e) {
            System.out.println(e);
        }
        return maNV;
    }
    
    //Lấy số lượng sản phẩm còn trong kho
    public int getSoLuong(String maSP) {
        int so_luong = 0;
        command = "select so_luong from san_pham where id = '" + maSP + "'";
        try {
            result = state.executeQuery(command);
            if (result.next()) {
                so_luong = result.getInt(1);
            }
        } catch (SQLException e) {
            System.out.println(e);
        }
        return so_luong;
    }
    
    //Thay đổi số lượng sản phẩm sau khi bán và nhập
    public void thayDoiSoLuong(String maSP, int chenhLech) {
        int so_luong_cu = getSoLuong(maSP);
        int so_luong_moi = so_luong_cu + chenhLech;
        command = "update san_pham set so_luong = '" +  so_luong_moi + "' where id = '" + maSP + "'";
        try {
            if (state.executeUpdate(command)>0) {
                System.out.println("Cập nhật thành công");
            }
            else {
                System.out.println("Lỗi cập nhật");
            }
        } catch (SQLException e) {
            System.out.println(e);
        }
    }
    
    //Lấy mật khẩu để kiểm chứng khi đăng nhập
    public String getPassWord(String id) {
        String password="";
        command = "select mat_khau from nhan_vien where id='" + id + "'";
        try {
            result = state.executeQuery(command);
            if (result.next()) {
                password = result.getString(1);
            }
        } catch (SQLException e) {
            System.out.println(e);
        }
        return password;
    } 
    
    //Lấy dữ liệu thống kê
    //Thống kê cơ bản
    public DefaultTableModel thongKeCoBan(DefaultTableModel model, String table, String column) {
        command = "select " + column + ", count(id) from " + table + " group by " + column;
        query(model);
        return model;
    }

    //Thống kê sản phẩm theo nhà cung cấp
    public DefaultTableModel TKSPTheoNCC(DefaultTableModel model) {
        command = "select ncc.ten_ncc, count(sp.id) from san_pham sp, "
                + "nha_cung_cap ncc where sp.ncc = ncc.id group by sp.ncc";
        query(model);
        return model;
    }
    
    //Thống kê doanh thu theo nhân viên
    public DefaultTableModel TKDTTheoNV(DefaultTableModel model) {
        command = "select nv.ho_ten, count(hdb.id) from hoa_don_ban hdb, nhan_vien nv "
                + "where hdb.ma_nhan_vien = nv.id group by nv.id order by nv.id";
        query(model);
        return model;
    }
    //Lấy tông tiền cho từng nhân viên
    public ArrayList<Integer> getTongTienTheoNV() {
        ArrayList<Integer> listTongTien = new ArrayList<>();
        command = "select sum(ctb.so_luong * sp.gia_tien) from hoa_don_ban hdb, "
                + "chi_tiet_ban ctb, san_pham sp where hdb.id = ctb.ma_hoa_don " 
                + "and ctb.ma_san_pham = sp.id group by hdb.ma_nhan_vien order by hdb.ma_nhan_vien";
        try {
            result = state.executeQuery(command);
            while (result.next()) {
                listTongTien.add(result.getInt(1));
            }
        } catch (SQLException e) {
            System.out.println(e);
        }
   
        return listTongTien;
    }
    
    //Thống kê doanh thu theo khách hàng
    public DefaultTableModel TKDTTheoKH(DefaultTableModel model) {
        command = "select kh.ho_ten, count(hdb.id) from hoa_don_ban hdb, khach_hang kh "
                + "where hdb.ma_khach_hang = kh.id group by kh.id order by kh.id";
        query(model);
        return model;
    }
    //Lấy tông tiền cho từng khách hàng
    public ArrayList<Integer> getTongTienTheoKH() {
        ArrayList<Integer> listTongTien = new ArrayList<>();
        command = "select sum(ctb.so_luong * sp.gia_tien) from hoa_don_ban hdb, "
                + "chi_tiet_ban ctb, san_pham sp where hdb.id = ctb.ma_hoa_don " 
                + "and ctb.ma_san_pham = sp.id group by hdb.ma_khach_hang order by hdb.ma_khach_hang";
        try {
            result = state.executeQuery(command);
            while (result.next()) {
                listTongTien.add(result.getInt(1));
            }
        } catch (SQLException e) {
            System.out.println(e);
        }
   
        return listTongTien;
    }
    
    //Thống kê doanh thu theo khách hàng
    public DefaultTableModel TKDTTheoNgay(DefaultTableModel model) {
        command = "select hdb.ngay_ban, count(hdb.id) from hoa_don_ban hdb group by hdb.ngay_ban";
        query(model);
        return model;
    }
    //Lấy tông tiền cho từng khách hàng
    public ArrayList<Integer> getTongTienTheoNgay() {
        ArrayList<Integer> listTongTien = new ArrayList<>();
        command = "select sum(ctb.so_luong * sp.gia_tien) from hoa_don_ban hdb, "
                + "chi_tiet_ban ctb, san_pham sp where hdb.id = ctb.ma_hoa_don " 
                + "and ctb.ma_san_pham = sp.id group by hdb.ngay_ban";
        try {
            result = state.executeQuery(command);
            while (result.next()) {
                listTongTien.add(result.getInt(1));
            }
        } catch (SQLException e) {
            System.out.println(e);
        }
   
        return listTongTien;
    }
    
    //Các hàm insert
    public void insertSanPham(SanPham sanPham) {
        command = "insert into san_pham value(?, ?, ?, ?, ?, ?, ?, ?)";
        try {
            pState = con.prepareStatement(command);
            pState.setString(1, sanPham.getId());
            pState.setString(2, sanPham.getTenSP());
            pState.setString(3, sanPham.getLoaiSP());
            pState.setString(4, sanPham.getNhaCC());
            pState.setString(5, sanPham.getDonVi());
            pState.setInt(6, sanPham.getGiaTien());
            pState.setInt(7, sanPham.getSoLuong());
            pState.setString(8, sanPham.getHinhAnh());
            if (pState.executeUpdate()>0) {
                System.out.println("Thêm sản phẩm thành công");
            }
            else {
                JOptionPane.showMessageDialog(null, "Lỗi cập nhật",
                        "Error",JOptionPane.ERROR_MESSAGE);
            }
        } catch (HeadlessException | SQLException e) {
            JOptionPane.showMessageDialog(null, "Error!",
                    "Error",JOptionPane.ERROR_MESSAGE);
            System.out.println(e);
        }
    }
    
    public void insertNhanVien(NhanVien nhanVien) {
        command = "insert into nhan_vien value(?, ?, ?, ?, ?, ?, ?, ?)";
        try {
            pState = con.prepareStatement(command);
            pState.setString(1, nhanVien.getId());
            pState.setString(2, nhanVien.getMatKhau());
            pState.setString(3, nhanVien.getHoTen());
            pState.setString(4, nhanVien.getGioiTinh());
            pState.setString(5, nhanVien.getNgaySinh());
            pState.setString(6, nhanVien.getDiaChi());
            pState.setString(7, nhanVien.getSoDT());
            pState.setInt(8, nhanVien.getTienLuong());
            if (pState.executeUpdate()>0) {
                System.out.println("Thêm nhân viên thành công");
            }
            else {
                JOptionPane.showMessageDialog(null, "Lỗi cập nhật",
                        "Error",JOptionPane.ERROR_MESSAGE);
            }
        } catch (HeadlessException | SQLException e) {
            JOptionPane.showMessageDialog(null, "Error!",
                    "Error",JOptionPane.ERROR_MESSAGE);
            System.out.println(e);
        }
    }
    
    public void insertHoaDonBan(HoaDonBan hoaDonBan) {
        command = "insert into hoa_don_ban value(?, ?, ?, ?)";
        try {
            pState = con.prepareStatement(command);
            pState.setString(1, hoaDonBan.getMaHDB());
            pState.setString(2, hoaDonBan.getMaNV());
            pState.setString(3, hoaDonBan.getTenKH());
            pState.setString(4, hoaDonBan.getNgayBan());
            if (pState.executeUpdate()>0) {
                System.out.println("Thêm hóa dơn thành công");
            }
            else {
                JOptionPane.showMessageDialog(null, "Lỗi cập nhật",
                        "Error",JOptionPane.ERROR_MESSAGE);
            }
        } catch (HeadlessException | SQLException e) {
            System.out.println(e);
        }
    }
    
    public void insertKhachHang(KhachHang khachHang) {
        command = "insert into khach_hang value(?, ?, ?, ?, ?)";
        try {
            pState = con.prepareStatement(command);
            pState.setString(1, khachHang.getId());
            pState.setString(2, khachHang.getHoTen());
            pState.setString(3, khachHang.getGioiTinh());
            pState.setString(4, khachHang.getDiaChi());
            pState.setString(5, khachHang.getSoDT());
            if (pState.executeUpdate()>0) {
                System.out.println("Thêm khách hàng thành công");
            }
            else {
                JOptionPane.showMessageDialog(null, "Lỗi cập nhật",
                        "Error",JOptionPane.ERROR_MESSAGE);
            }
        } catch (HeadlessException | SQLException e) {
            System.out.println(e);
        }
    }
    
    public void insertHoaDonNhap(HoaDonNhap hoaDonNhap) {
        command = "insert into hoa_don_nhap value(?, ?, ?, ?)";
        try {
            pState = con.prepareStatement(command);
            pState.setString(1, hoaDonNhap.getMaHDN());
            pState.setString(2, hoaDonNhap.getMaNV());
            pState.setString(3, hoaDonNhap.getNhaCC());
            pState.setString(4, hoaDonNhap.getNgayNhap());
            if (pState.executeUpdate()>0) {
                System.out.println("Thêm hóa dơn thành công");
            }
            else {
                JOptionPane.showMessageDialog(null, "Lỗi cập nhật",
                        "Error",JOptionPane.ERROR_MESSAGE);
            }
        } catch (HeadlessException | SQLException e) {
            System.out.println(e);
        }
    }
    
    public void insertCTHDB(ChiTietHD chiTietHD) {
        command = "insert into chi_tiet_ban value(?, ?, ?)";
        try {
            pState = con.prepareStatement(command);
            pState.setString(1, chiTietHD.getMaHD());
            pState.setString(2, chiTietHD.getMaSP());
            pState.setInt(3, chiTietHD.getSoLuong());
            if (pState.executeUpdate()>0) {
                System.out.println("Thêm chi tiết hóa dơn thành công");
            }
            else {
                JOptionPane.showMessageDialog(null, "Lỗi cập nhật",
                        "Error",JOptionPane.ERROR_MESSAGE);
            }
        } catch (HeadlessException | SQLException e) {
            System.out.println(e);
        }
    }
    
    public void insertCTHDN(ChiTietHD chiTietHD) {
        command = "insert into chi_tiet_nhap value(?, ?, ?)";
        try {
            pState = con.prepareStatement(command);
            pState.setString(1, chiTietHD.getMaHD());
            pState.setString(2, chiTietHD.getMaSP());
            pState.setInt(3, chiTietHD.getSoLuong());
            if (pState.executeUpdate()>0) {
                System.out.println("Thêm chi tiết hóa dơn thành công");
            }
            else {
                JOptionPane.showMessageDialog(null, "Lỗi cập nhật",
                        "Error",JOptionPane.ERROR_MESSAGE);
            }
        } catch (HeadlessException | SQLException e) {
            System.out.println(e);
        }
    }
    
    public void insertNCC(NhaCC ncc) {
        command = "insert into nha_cung_cap value(?, ?, ?, ?)";
        try {
            pState = con.prepareStatement(command);
            pState.setString(1, ncc.getId());
            pState.setString(2, ncc.getTenNCC());
            pState.setString(3, ncc.getDiaChi());
            pState.setString(4, ncc.getSoDT());
            if (pState.executeUpdate()>0) {
                System.out.println("Thêm nhà cung cấp thành công");
            }
            else {
                JOptionPane.showMessageDialog(null, "Lỗi cập nhật",
                        "Error",JOptionPane.ERROR_MESSAGE);
            }
        } catch (HeadlessException | SQLException e) {
            System.out.println(e);
        }
    }
    //Các hàm update
    public void updateSP(SanPham sanPham) {
        command = "update san_pham set ten_san_pham = ?,loai_san_pham = ?,ncc = ?,"
                + " don_vi = ?, gia_nhap = ?, gia_tien = ?, so_luong = ?, hinh_anh = ? where id = ?";
        try {
            pState = con.prepareStatement(command);
            pState.setString(1, sanPham.getTenSP());
            pState.setString(2, sanPham.getLoaiSP());
            pState.setString(3, sanPham.getNhaCC());
            pState.setString(4, sanPham.getDonVi());
            pState.setInt(5, sanPham.getGiaNhap());
            pState.setInt(6, sanPham.getGiaTien());
            pState.setInt(7, sanPham.getSoLuong());
            pState.setString(8, sanPham.getHinhAnh());
            pState.setString(9, sanPham.getId());
            if (pState.executeUpdate()>0) {
                System.out.println("Cập nhật sản phẩm thành công");
            }
            else {
                JOptionPane.showMessageDialog(null, "Lỗi cập nhật",
                        "Error",JOptionPane.ERROR_MESSAGE);
            }
        } catch(HeadlessException | SQLException e) {
            System.out.println(e);
        }
    }
    
    public void updateNhanVien(NhanVien nhanVien) {
        command = "update nhan_vien set mat_khau = ?, ho_ten = ?, gioi_tinh = ?, ngay_sinh = ?,"
                + "dia_chi = ?, so_dien_thoai = ?, tien_luong = ? where id = ?";
        try {
            pState = con.prepareStatement(command);
            pState.setString(1, nhanVien.getMatKhau());
            pState.setString(2, nhanVien.getHoTen());
            pState.setString(3, nhanVien.getGioiTinh());
            pState.setString(4, nhanVien.getNgaySinh());
            pState.setString(5, nhanVien.getDiaChi());
            pState.setString(6, nhanVien.getSoDT());
            pState.setInt(7, nhanVien.getTienLuong());
            pState.setString(8, nhanVien.getId());
            if (pState.executeUpdate()>0) {
                System.out.println("Cập nhật nhân viên thành công");
            }
            else {
                JOptionPane.showMessageDialog(null, "Lỗi cập nhật",
                        "Error",JOptionPane.ERROR_MESSAGE);
            }
        } catch(HeadlessException | SQLException e) {
            JOptionPane.showMessageDialog(null, "Error!",
                    "Error",JOptionPane.ERROR_MESSAGE);
            System.out.println(e);
        }
    }
    
    public void updateKH(KhachHang khachHang) {
        command = "update khach_hang set ho_ten = ?, gioi_tinh = ?, dia_chi = ?,"
                + " so_dt = ? where id = ?";
        try {
            pState = con.prepareStatement(command);
            pState.setString(1, khachHang.getHoTen());
            pState.setString(2, khachHang.getGioiTinh());
            pState.setString(3, khachHang.getDiaChi());
            pState.setString(4, khachHang.getSoDT());
            pState.setString(5, khachHang.getId());
            if (pState.executeUpdate()>0) {
                System.out.println("Cập nhật khách hàng thành công");
            } 
            else {
                JOptionPane.showMessageDialog(null, "Lỗi cập nhật",
                        "Error",JOptionPane.ERROR_MESSAGE);
            }
        } catch(HeadlessException | SQLException e) {
            System.out.println(e);
        }
    }
    
    public void updateHDB(HoaDonBan hdBan) {
        command = "update hoa_don_ban set ma_nhan_vien = ?, ten_khach_hang = ?,"
                + " ngay_ban = ? where id = ?";
        try {
            pState = con.prepareStatement(command);
            pState.setString(1, hdBan.getMaNV());
            pState.setString(2, hdBan.getTenKH());
            pState.setString(3, hdBan.getNgayBan());
            pState.setString(4, hdBan.getMaHDB());
            if (pState.executeUpdate()>0) {
                System.out.println("Cập nhật hóa đơn bán thành công");
            } 
            else {
                JOptionPane.showMessageDialog(null, "Lỗi cập nhật",
                        "Error",JOptionPane.ERROR_MESSAGE);
            }
        } catch(HeadlessException | SQLException e) {
            System.out.println(e);
        }
    }
    
    public void updateHDN(HoaDonNhap hdNhap) {
        command = "update hoa_don_nhap set ma_nhan_vien = ?, nha_cung_cap = ?,"
                + " ngay_nhap = ? where id = ?";
        try {
            pState = con.prepareStatement(command);
            pState.setString(1, hdNhap.getMaNV());
            pState.setString(2, hdNhap.getNhaCC());
            pState.setString(3, hdNhap.getNgayNhap());
            pState.setString(4, hdNhap.getMaHDN());
            if (pState.executeUpdate()>0) {
                System.out.println("Cập nhật hóa đơn nhập thành công");
            } 
            else {
                JOptionPane.showMessageDialog(null, "Lỗi cập nhật",
                        "Error",JOptionPane.ERROR_MESSAGE);
            }
        } catch(HeadlessException | SQLException e) {
            System.out.println(e);
        }
    }
    
    public void updateNCC(NhaCC ncc) {
        command = "update nha_cung_cap set ten_ncc = ?, dia_chi = ?,"
                + " so_dien_thoai = ? where id = ?";
        try {
            pState = con.prepareStatement(command);
            pState.setString(1, ncc.getTenNCC());
            pState.setString(2, ncc.getDiaChi());
            pState.setString(3, ncc.getSoDT());
            pState.setString(4, ncc.getId());
            if (pState.executeUpdate()>0) {
                System.out.println("Cập nhà cung cấp thành công");
            } 
            else {
                JOptionPane.showMessageDialog(null, "Lỗi cập nhật",
                        "Error",JOptionPane.ERROR_MESSAGE);
            }
        } catch(HeadlessException | SQLException e) {
            System.out.println(e);
        }
    }
    
    //Hàm xóa một dòng trong bảng dữ liệu
    public void delete(String table, String id) {
        command = "delete from " + table + " where id = '" + id + "'";
        try {
            if  (state.executeUpdate(command)>0) {
                JOptionPane.showMessageDialog(null, "Xóa thành công",
                    "Message", JOptionPane.INFORMATION_MESSAGE);
            }
            else {
                JOptionPane.showMessageDialog(null, "Dữ liệu không tồn tại!",
                    "Error",JOptionPane.WARNING_MESSAGE);
            }
        } catch (HeadlessException | SQLException e) {
            JOptionPane.showMessageDialog(null, "Lỗi xóa dữ liệu!",
                    "Error", JOptionPane.ERROR_MESSAGE);
            System.out.println(e);
        }
    }
    
    //Xóa chi tiết của một hóa đơn 
    public void deleteCTHD(String table, String maHD) {
        command = "delete from " + table + " where ma_hoa_don = '" + maHD + "'";
        try {
            if  (state.executeUpdate(command)>0) {
                System.out.println("Xóa chi tiết hóa đơn thành công");
            }
            else {
                System.out.println("Dữ liệu không tồn tại!");
            }
        } catch (HeadlessException | SQLException e) {
            JOptionPane.showMessageDialog(null, "Lỗi xóa dữ liệu!",
                    "Error", JOptionPane.ERROR_MESSAGE);
            System.out.println(e);
        }
    }
}
