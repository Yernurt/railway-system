package org.example.zernovoz1.models;

import jakarta.persistence.*;

@Entity
public class Users {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // 👈 МІН
    @Column(name="id")
    private int id;
    @Column(name="username", unique = true)
    private String username;
    @Column(name="password")
    private String password;
    @Column(name="role")
    private String role;

    @Column(name = "station")
    private String station;

    @Column(name="name")
    private String name;
    @Column(name="sname")
    private String sname;
    @Column(name="phone")
    private String phone;

    @Column(name = "role_request")
    private String roleRequest;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
    public String getRoleRequest(){return roleRequest;}
    public void setRoleRequest(String roleRequest){
        this.roleRequest = roleRequest;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getStation(){
        return station;
    }
    public void setStation(String station){
        this.station = station;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSname() {
        return sname;
    }

    public void setSname(String sname) {
        this.sname = sname;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    @Override
    public String toString() {
        return "Users{" +
                "id=" + id +
                ", username='" + username + '\'' +
                ", password='" + password + '\'' +
                ", station='" + station + '\'' +
                ", name='" + name + '\'' +
                ", sname='" + sname + '\'' +
                ", phone='" + phone + '\'' +
                '}';
    }
}