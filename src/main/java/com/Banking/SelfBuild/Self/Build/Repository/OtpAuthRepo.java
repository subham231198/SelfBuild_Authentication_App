package com.Banking.SelfBuild.Self.Build.Repository;

import com.Banking.SelfBuild.Self.Build.Entity.OTPEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface OtpAuthRepo extends JpaRepository<OTPEntity, Long>
{
    Optional<OTPEntity> findByCustomerId(String customerId);
    Optional<OTPEntity> findByTokenSerial(String tokenSerial);
}
