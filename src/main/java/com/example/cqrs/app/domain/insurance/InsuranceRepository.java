package com.example.cqrs.app.domain.insurance;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface InsuranceRepository extends JpaRepository<Insurance, Integer>, JpaSpecificationExecutor<Insurance> {
}
