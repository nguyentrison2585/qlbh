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
public class HoaDonNhap {
    private String maHDN;
    private String maNV;
    private String nhaCC;
    private String ngayNhap;
    
    public HoaDonNhap(String maHDN, String maNV, String nhaCC, String ngayNhap) {
        this.maHDN = maHDN;
        this.maNV = maNV;
        this.nhaCC = nhaCC;
        this.ngayNhap = ngayNhap;
    }

    public String getMaHDN() {
        return maHDN;
    }

    public void setMaHDN(String maHDN) {
        this.maHDN = maHDN;
    }

    public String getMaNV() {
        return maNV;
    }

    public void setMaNV(String maNV) {
        this.maNV = maNV;
    }

    public String getNhaCC() {
        return nhaCC;
    }

    public void setNhaCC(String nhaCC) {
        this.nhaCC = nhaCC;
    }

    public String getNgayNhap() {
        return ngayNhap;
    }

    public void setNgayNhap(String ngayNhap) {
        this.ngayNhap = ngayNhap;
    }
    
}
