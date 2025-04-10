package com.replicacia.repo;

import com.replicacia.model.Application;
import com.replicacia.model.HostInfo;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ApplicationRepository extends JpaRepository<Application, Long> {
  Optional<Application> findByAppName(String appName);
}