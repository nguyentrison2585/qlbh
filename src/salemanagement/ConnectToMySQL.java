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
    
    //Lấy dữ liệu cho bảng chi tiết sản phẩm
    public DefaultTableModel getCTSP(DefaultTableModel model) {
        model.setRowCount(0);
        command = "select sp.id, sp.ten_san_pham, sp.loai_san_pham, ncc.ten_ncc,"
                + "sp.don_vi, sp.gia_tien, sp.so_luong, sp.hinh_anh from san_pham sp, "
                + "nha_cung_cap ncc where sp.ncc = ncc.id order by sp.id";
        query(model);
        return model;
    }
    
    //Lấy dữ liệu cho bảng hóa đơn bán
    public DefaultTableModel getHDB(DefaultTableModel model) {
        model.setRowCount(0);
        command = "select hdb.id, nv.ho_ten, hdb.ten_khach_hang, hdb.ngay_ban, sum(ctb.so_luong * sp.gia_tien) "
                + "from hoa_don_ban hdb, chi_tiet_ban ctb, nhan_vien nv,  san_pham sp where hdb.id = ctb.ma_hoa_don "
                + "and ctb.ma_san_pham = sp.id and hdb.ma_nhan_vien = nv.id group by hdb.id";
        query(model);
        return model;
    }
    
    //Lấy dữ liệu cho bảng hóa đơn nhập
    public DefaultTableModel getHDN(DefaultTableModel model) {
        model.setRowCount(0);
        command = "select hdn.id, nv.ho_ten, ncc.ten_ncc, hdn.ngay_nhap, sum(ctn.so_luong * ctn.gia_nhap) "
                + "from hoa_don_nhap hdn, chi_tiet_nhap ctn, nhan_vien nv, nha_cung_cap ncc where "
                + "hdn.id = ctn.ma_hoa_don and hdn.ma_nhan_vien = nv.id and hdn.ncc = ncc.id group by hdn.id";
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
        command = "select sp.ten_san_pham, sp.don_vi, ctn.gia_nhap, ctn.so_luong, (ctn.gia_nhap * ctn.so_luong) as thanh_tien "
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
        command = "select hdb.id, nv.ho_ten, hdb.ten_khach_hang, hdb.ngay_ban, "
                + "sum(ctb.so_luong * sp.gia_tien) from hoa_don_ban hdb, "
                + "chi_tiet_ban ctb, nhan_vien nv,  san_pham sp where "
                + "hdb.id = ctb.ma_hoa_don and ctb.ma_san_pham = sp.id and "
                + "hdb.ma_nhan_vien = nv.id and nv.ho_ten like '%" + tenNV + "%' group by hdb.id";
        query(model);
        return model;
    }
    
    //Theo tên khách hàng
    public DefaultTableModel getTKHDB_KH(DefaultTableModel model, String tenKH) {
        model.setRowCount(0);
        command = "select hdb.id, nv.ho_ten, hdb.ten_khach_hang, hdb.ngay_ban, "
                + "sum(ctb.so_luong * sp.gia_tien) from hoa_don_ban hdb, "
                + "chi_tiet_ban ctb, nhan_vien nv,  san_pham sp where "
                + "hdb.id = ctb.ma_hoa_don and ctb.ma_san_pham = sp.id and "
                + "hdb.ma_nhan_vien = nv.id and hdb.ten_khach_hang like '%" + tenKH + "%' group by hdb.id";
        query(model);
        return model;
    }
    
    //Tìm kiếm hóa đơn nhập
    //Theo tên nhân viên
    public DefaultTableModel getTKHDN_NV(DefaultTableModel model, String tenNV) {
        model.setRowCount(0);
        command = "select hdn.id, nv.ho_ten, ncc.ten_ncc, hdn.ngay_nhap, "
                + "sum(ctn.so_luong * ctn.gia_nhap) from hoa_don_nhap hdn, "
                + "chi_tiet_nhap ctn, nhan_vien nv, nha_cung_cap ncc where "
                + "hdn.id = ctn.ma_hoa_don and hdn.ma_nhan_vien = nv.id and "
                + "hdn.ncc = ncc.id and nv.ho_ten like '%" + tenNV + "%' group by hdn.id";
        query(model);
        return model;
    }
    //Theo nhà cung cấp
    public DefaultTableModel getTKHDN_NCC(DefaultTableModel model, String tenNCC) {
        model.setRowCount(0);
        command = "select hdn.id, nv.ho_ten, ncc.ten_ncc, hdn.ngay_nhap, "
                + "sum(ctn.so_luong * ctn.gia_nhap) from hoa_don_nhap hdn, "
                + "chi_tiet_nhap ctn, nhan_vien nv, nha_cung_cap ncc where "
                + "hdn.id = ctn.ma_hoa_don and hdn.ma_nhan_vien = nv.id and "
                + "hdn.ncc = ncc.id and ncc.ten_ncc like '%" + tenNCC + "%' group by hdn.id";
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
    
    //Lấy mã nhà cung cấp khi biết tên (dùng khi thực hiện việc thêm, cập nhật sản phẩm)
    public String getMaNCC(String tenNCC) {
        String maNCC = "";
        command = "select id from nha_cung_cap where ten_ncc = '" + tenNCC + "'";
        try {
            result = state.executeQuery(command);
            if (result.next()) {
                maNCC = result.getString(1);
            }
        } catch (Exception e) {
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
            JOptionPane.showMessageDialog(null, "Thêm thành công", 
                    "Message", JOptionPane.INFORMATION_MESSAGE);
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
            JOptionPane.showMessageDialog(null, "Thêm thành công", 
                    "Message", JOptionPane.INFORMATION_MESSAGE);
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
            JOptionPane.showMessageDialog(null, "Thêm thành công", 
                    "Message", JOptionPane.INFORMATION_MESSAGE);
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
            JOptionPane.showMessageDialog(null, "Thêm thành công", 
                    "Message", JOptionPane.INFORMATION_MESSAGE);
            System.out.println(e);
        }
    }
    //Các hàm update
    public void updateSP(SanPham sanPham) {
        command = "update san_pham set ten_san_pham = ?,loai_san_pham = ?,ncc = ?,"
                + " don_vi = ?, gia_tien = ?, so_luong = ?, hinh_anh = ? where id = ?";
        try {
            pState = con.prepareStatement(command);
            pState.setString(1, sanPham.getTenSP());
            pState.setString(2, sanPham.getLoaiSP());
            pState.setString(3, sanPham.getNhaCC());
            pState.setString(4, sanPham.getDonVi());
            pState.setInt(5, sanPham.getGiaTien());
            pState.setInt(6, sanPham.getSoLuong());
            pState.setString(7, sanPham.getHinhAnh());
            pState.setString(8, sanPham.getId());
            if (pState.executeUpdate()>0) {
                System.out.println("Cập nhật sản phẩm thành công");
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
                System.out.println("Cập nhật phiếu mượn thành công");
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
                System.out.println("Cập nhật phiếu mượn thành công");
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
            JOptionPane.showMessageDialog(null, "Error!",
                    "Error",JOptionPane.ERROR_MESSAGE);
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
                JOptionPane.showMessageDialog(null, "Dữ liệu không tồn tại !",
                    "Error",JOptionPane.WARNING_MESSAGE);
            }
        } catch (HeadlessException | SQLException e) {
            JOptionPane.showMessageDialog(null, "Lỗi xóa dữ liệu !",
                    "Error", JOptionPane.ERROR_MESSAGE);
            System.out.println(e);
        }
    }
}
