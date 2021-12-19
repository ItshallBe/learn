package com.zzx.model;

public class Customer {
    private long id;
    private String name;
    private String contact;
    private String telephone;
    private String email;
    private String remark;

    public void setContact(String contact) {
        this.contact = contact;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setId(long id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public void setTelephone(String telephone) {
        this.telephone = telephone;
    }

    public long getId() {
        return id;
    }

    public String getContact() {
        return contact;
    }

    public String getEmail() {
        return email;
    }

    public String getName() {
        return name;
    }

    public String getRemark() {
        return remark;
    }

    public String getTelephone() {
        return telephone;
    }
}
