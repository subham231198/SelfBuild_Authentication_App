package com.Banking.SelfBuild.Self.Build.Entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "customerSession")
public class CustomerSession
{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @JsonProperty(value = "dspSession")
    @Column(unique = true)
    private String dspSession;

    @JsonProperty(value = "session_correlationId")
    @Column(name = "session_correlationId")
    private String session_correlationId;

    @JsonProperty(value = "issuedAt")
    @Column(name = "issuedAt")
    private String issuedAt;

    @JsonProperty(value = "expired")
    @Column(name = "expired")
    private String expired;

    @JsonProperty(value = "customerId")
    @Column(name = "customerId")
    private String customerId;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getDspSession() {
        return dspSession;
    }

    public void setDspSession(String dspSession) {
        this.dspSession = dspSession;
    }

    public String getSession_correlationId() {
        return session_correlationId;
    }

    public void setSession_correlationId(String session_correlationId) {
        this.session_correlationId = session_correlationId;
    }

    public String getIssuedAt() {
        return issuedAt;
    }

    public void setIssuedAt(String issuedAt) {
        this.issuedAt = issuedAt;
    }

    public String getExpired() {
        return expired;
    }

    public void setExpired(String expired) {
        this.expired = expired;
    }

    public String getCustomerId() {
        return customerId;
    }

    public void setCustomerId(String customerId) {
        this.customerId = customerId;
    }

    @Override
    public String toString() {
        return "CustomerSession{" +
                "id=" + id +
                ", dspSession='" + dspSession + '\'' +
                ", session_correlationId='" + session_correlationId + '\'' +
                ", issuedAt='" + issuedAt + '\'' +
                ", expired='" + expired + '\'' +
                '}';
    }
}
