package com.Banking.SelfBuild.Self.Build.Repository;

import com.Banking.SelfBuild.Self.Build.Entity.UsernameAuthEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UsernameAuthRepo extends JpaRepository<UsernameAuthEntity, Long>
{
    Optional<UsernameAuthEntity> findByCustomerId(String customerId);
}
