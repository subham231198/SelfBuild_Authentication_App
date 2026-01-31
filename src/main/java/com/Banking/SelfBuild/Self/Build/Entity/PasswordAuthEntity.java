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
@Table(name = "password_auth_entity")
public class PasswordAuthEntity
{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @JsonProperty(value = "customerId")
    @Column(name = "customerId", unique = true)
    private String customerId;

    @JsonProperty(value = "password")
    @Column(name = "password")
    private String password;

    @Override
    public String toString() {
        return "PasswordAuthEntity{" +
                " customerId='" + customerId + '\'' +
                ", password='" + password + '\'' +
                '}';
    }
}
