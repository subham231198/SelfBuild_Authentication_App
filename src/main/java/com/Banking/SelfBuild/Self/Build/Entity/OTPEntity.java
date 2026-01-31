package com.Banking.SelfBuild.Self.Build.Entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class OTPEntity
{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "customerId", unique = true, nullable = false)
    private String customerId;

    @Column(name = "tokenSerial")
    private String tokenSerial;

    @Column(name = "secureCode")
    private String secureCode;

    @Column(name = "issuedTimeStamp")
    private String issuedTimeStamp;

    @Column(name = "expiryTimeStamp")
    private String expiryTimeStamp;

    @Column(name = "otpUsed")
    private Boolean otpUsed;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCustomerId() {
        return customerId;
    }

    public void setCustomerId(String customerId) {
        this.customerId = customerId;
    }

    public String getTokenSerial() {
        return tokenSerial;
    }

    public void setTokenSerial(String tokenSerial) {
        this.tokenSerial = tokenSerial;
    }

    public String getSecureCode() {
        return secureCode;
    }

    public void setSecureCode(String secureCode) {
        this.secureCode = secureCode;
    }

    public String getIssuedTimeStamp() {
        return issuedTimeStamp;
    }

    public void setIssuedTimeStamp(String issuedTimeStamp) {
        this.issuedTimeStamp = issuedTimeStamp;
    }

    public String getExpiryTimeStamp() {
        return expiryTimeStamp;
    }

    public void setExpiryTimeStamp(String expiryTimeStamp) {
        this.expiryTimeStamp = expiryTimeStamp;
    }

    public Boolean getOtpUsed() {
        return otpUsed;
    }

    public void setOtpUsed(Boolean otpUsed) {
        this.otpUsed = otpUsed;
    }

    @Override
    public String toString() {
        return "Incoming Request {" +
                " customerId='" + customerId + '\'' +
                ", tokenSerial='" + tokenSerial + '\'' +
                ", secureCode='" + secureCode + '\'' +
                ", issuedTimeStamp='" + issuedTimeStamp + '\'' +
                ", expiryTimeStamp='" + expiryTimeStamp + '\'' +
                '}';
    }
}
