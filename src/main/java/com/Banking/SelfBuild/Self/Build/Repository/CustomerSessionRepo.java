package com.Banking.SelfBuild.Self.Build.Repository;

import com.Banking.SelfBuild.Self.Build.Entity.CustomerSession;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CustomerSessionRepo extends JpaRepository<CustomerSession, Long>
{

}
