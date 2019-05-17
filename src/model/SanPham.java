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
public class SanPham {
    private String id;
    private String tenSP;
    private String loaiSP;
    private String nhaCC;
    private String donVi;
    private int giaTien;
    private int soLuong;
    private String hinhAnh;

    public SanPham(String id, String tenSP, String loaiSP, String nhaCC, String donVi, int giaTien, int soLuong, String hinhAnh) {
        this.id = id;
        this.tenSP = tenSP;
        this.loaiSP = loaiSP;
        this.nhaCC = nhaCC;
        this.donVi = donVi;
        this.giaTien = giaTien;
        this.soLuong = soLuong;
        this.hinhAnh = hinhAnh;
    }
    
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTenSP() {
        return tenSP;
    }

    public void setTenSP(String tenSP) {
        this.tenSP = tenSP;
    }

    public String getLoaiSP() {
        return loaiSP;
    }

    public void setLoaiSP(String loaiSP) {
        this.loaiSP = loaiSP;
    }

    public String getNhaCC() {
        return nhaCC;
    }

    public void setNhaCC(String nhaCC) {
        this.nhaCC = nhaCC;
    }

    public String getDonVi() {
        return donVi;
    }

    public void setDonVi(String donVi) {
        this.donVi = donVi;
    }

    public int getGiaTien() {
        return giaTien;
    }

    public void setGiaTien(int giaTien) {
        this.giaTien = giaTien;
    }

    public int getSoLuong() {
        return soLuong;
    }

    public void setSoLuong(int soLuong) {
        this.soLuong = soLuong;
    }

    public String getHinhAnh() {
        return hinhAnh;
    }

    public void setHinhAnh(String hinhAnh) {
        this.hinhAnh = hinhAnh;
    }
    
}
