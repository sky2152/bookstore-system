package com.book.entity;
import java.math.BigDecimal;
public class User {
    private Integer id;
    private String username;
    private String password;
    private Integer role;
    private BigDecimal balance;

    public User() {}
    public Integer getId() {return id;}
    public void setId(Integer id) {this.id = id;}
    public String getUsername() {return username;}
    public void setUsername(String username) {this.username = username;}
    public String getPassword() {return password;}
    public void setPassword(String password) {this.password = password;}
    public Integer getRole() {return role;}
    public void setRole(Integer role) {this.role = role;}
    public BigDecimal getBalance() {return balance;}
    public void setBalance(BigDecimal balance) {this.balance = balance;}
}