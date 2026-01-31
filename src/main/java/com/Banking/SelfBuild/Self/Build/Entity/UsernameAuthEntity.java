package com.Banking.SelfBuild.Self.Build.Entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "username_auth_entity")
@JsonPropertyOrder({"customerId", "isProfileLocked", "isProfileSuspended", "invalidLoginCounter", "lastLoginAttempt"})
public class UsernameAuthEntity
{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @JsonProperty(value = "customerId")
    @Column(name = "customerId", unique = true)
    private String customerId;

    @JsonProperty(value = "authLevel")
    @Column(name = "authLevel")
    private Integer authLevel;

    @JsonProperty(value = "isProfileLocked")
    @Column(name = "isProfileLocked")
    private Integer isProfileLocked;

    @JsonProperty(value = "isProfileSuspended")
    @Column(name = "isProfileSuspended")
    private Integer isProfileSuspended;

    @JsonProperty(value = "invalidLoginCounter")
    @Column(name = "invalidAttemptCounter")
    private Integer invalid_login_attempt_counter;

    @JsonProperty(value = "lastLoginAttempt")
    @Column(name = "lastLoginTimeStamp")
    private String last_login_timeStamp;

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

    public Integer getAuthLevel() {
        return authLevel;
    }

    public void setAuthLevel(Integer authLevel) {
        this.authLevel = authLevel;
    }

    public Integer getIsProfileLocked() {
        return isProfileLocked;
    }

    public void setIsProfileLocked(Integer profileLocked) {
        isProfileLocked = profileLocked;
    }

    public Integer getProfileSuspended() {
        return isProfileSuspended;
    }

    public void setProfileSuspended(Integer profileSuspended) {
        isProfileSuspended = profileSuspended;
    }

    public Integer getInvalid_login_attempt_counter() {
        return invalid_login_attempt_counter;
    }

    public void setInvalid_login_attempt_counter(Integer invalid_login_attempt_counter) {
        this.invalid_login_attempt_counter = invalid_login_attempt_counter;
    }

    public String getLast_login_timeStamp() {
        return last_login_timeStamp;
    }

    public void setLast_login_timeStamp(String last_login_timeStamp) {
        this.last_login_timeStamp = last_login_timeStamp;
    }

    @Override
    public String toString() {
        return "UsernameAuthService {" +
                " customerId='" + customerId + '\'' +
                ", isProfileLocked=" + isProfileLocked +
                ", isProfileSuspended=" + isProfileSuspended +
                ", invalid_login_attempt_counter=" + invalid_login_attempt_counter +
                ", last_login_timeStamp='" + last_login_timeStamp + '\'' +
                '}';
    }
}
