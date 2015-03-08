package net.tevp.dragon_go_countdown.authentication;

import java.util.Date;

public class LoginResult {
    private LoginStatus status;
    private String sessionCode;
    private Date expiry;

    public LoginStatus getStatus() {
        return status;
    }

    public void setStatus(LoginStatus status) {
        this.status = status;
    }

    public void setSessionCode(String sessionCode) {
        this.sessionCode = sessionCode;
    }

    public void setExpiry(Date expiry) {
        this.expiry = expiry;
    }

    public Date getExpiry() {
        return expiry;
    }

    public String getSessionCode() {
        return sessionCode;
    }
}
