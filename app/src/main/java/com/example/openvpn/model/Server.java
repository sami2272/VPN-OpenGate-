package com.example.openvpn.model;

public class Server {

    private String country;
    private String flagUrl;
    private String oVPN;
    private String oVPNUserName;
    private String oVPNUserPassword;


    public Server() {
        // empty constructor
    }

    public Server(String country, String flagUrl, String oVPN) {
        this.country = country;
        this.flagUrl = flagUrl;
        this.oVPN = oVPN;
    }

    public Server(String country, String flagUrl, String oVPN, String oVPNUserName, String oVPNUserPassword) {
        this.country = country;
        this.flagUrl = flagUrl;
        this.oVPN = oVPN;
        this.oVPNUserName = oVPNUserName;
        this.oVPNUserPassword = oVPNUserPassword;
    }

    // getters and setters
    public String getCountryName() {
        return country;
    }

    public void setCountryName(String country) {
        this.country = country;
    }

    public String getFlagUrl() {
        return flagUrl;
    }

    public void setFlagUrl(String flagUrl) {
        this.flagUrl = flagUrl;
    }

    public String getToVPN() {
        return oVPN;
    }

    public void setToVPN(String oVPN) {
        this.oVPN = oVPN;
    }

    public String getToVPNUserName() {
        return oVPNUserName;
    }

    public void setToVPNUserName(String oVPNUserName) {
        this.oVPNUserName = oVPNUserName;
    }

    public String getToVPNUserPassword() {
        return oVPNUserPassword;
    }

    public void setToVPNUserPassword(String oVPNUserPassword) {
        this.oVPNUserPassword = oVPNUserPassword;
    }
}
