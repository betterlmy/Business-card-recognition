package com.example.demo.bean;

import java.io.Serializable;

/**
 * @author Lmy
 * @date 2021/4/10 5:53 上午
 */
public class Contact implements Serializable {
    private String name;
    private String phone;
    private String email;
    private String addr;
    private String company;
    private String titles;
    private String flag;

    public Contact() {
        //设置默认值
        this.flag="lmy";
        this.name = "未识别到或识别错误，请手动修改";
        this.phone = "未识别到或识别错误，请手动修改";
        this.email = "未识别到或识别错误，请手动修改";
        this.addr = "未识别到或识别错误，请手动修改";
        this.company = "未识别到或识别错误，请手动修改";
        this.titles = "未识别到或识别错误，请手动修改";
    }


    public Contact(String name, String phone, String email, String addr, String company,String titles) {
        if(name==null) this.name="未识别到或识别错误，请手动修改";
        else this.name = name;
        if(phone==null) this.phone="未识别到或识别错误，请手动修改";
        else this.phone = phone;
        if(email==null) this.email="未识别到或识别错误，请手动修改";
        else this.email = email;
        if(addr==null) this.addr="未识别到或识别错误，请手动修改";
        else this.addr = addr;
        if(company==null) this.company="未识别到或识别错误，请手动修改";
        else this.company = company;
        if(titles==null) this.titles="未识别到或识别错误，请手动修改";
        else this.titles = titles;
        this.flag="lmy";
    }

    @Override
    public String toString() {
        return "{" +
                "name='" + name + '\'' +
                ", phone='" + phone + '\'' +
                ", email='" + email + '\'' +
                ", addr='" + addr + '\'' +
                ", company='" + company + '\'' +
                ", titles='" + titles + '\'' +
                ", flag='" + flag + '\'' +
                '}';
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        if(this.name=="未识别到或识别错误，请手动修改")
        this.name = name;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        if(this.phone=="未识别到或识别错误，请手动修改")

            this.phone = phone;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        if(this.email=="未识别到或识别错误，请手动修改")
            this.email = email;
    }

    public String getAddr() {
        return addr;
    }

    public void setAddr(String addr) {
        if(this.addr=="未识别到或识别错误，请手动修改")
            this.addr = addr;
    }

    public String getCompany() {
        return company;
    }

    public void setCompany(String company) {
        if(this.company=="未识别到或识别错误，请手动修改")
            this.company = company;
    }

    public String getTitles() {
        return titles;
    }

    public void setTitles(String titles) {
        if(this.titles=="未识别到或识别错误，请手动修改")
            this.titles = titles;
    }

    public String getFlag() {
        return flag;
    }

    public void setFlag(String flag) {
        this.flag = flag;
    }
}
