/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package model;

/**
 *
 * @author Admin
 */
public class HoaDonBan {
    private String maHDB;
    private String maNV;
    private String tenKH;
    private String ngayBan;
    
    public HoaDonBan(String maHDB, String maNV, String tenKH, String ngayBan) {
        this.maHDB = maHDB;
        this.maNV = maNV;
        this.tenKH = tenKH;
        this.ngayBan = ngayBan;
    }

    public String getMaHDB() {
        return maHDB;
    }

    public void setMaHDB(String maHDB) {
        this.maHDB = maHDB;
    }

    public String getMaNV() {
        return maNV;
    }

    public void setMaNV(String maNV) {
        this.maNV = maNV;
    }

    public String getTenKH() {
        return tenKH;
    }

    public void setTenKH(String tenKH) {
        this.tenKH = tenKH;
    }

    public String getNgayBan() {
        return ngayBan;
    }

    public void setNgayBan(String ngayBan) {
        this.ngayBan = ngayBan;
    }
    
}
