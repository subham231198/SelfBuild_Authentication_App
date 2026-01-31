package com.Banking.SelfBuild.Self.Build.Repository;

import com.Banking.SelfBuild.Self.Build.Entity.PasswordAuthEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;


@Repository
public interface PasswordAuthRepo extends JpaRepository<PasswordAuthEntity, Long>
{
    Optional<PasswordAuthEntity> findByCustomerIdAndPassword(String customerId, String password);
}
