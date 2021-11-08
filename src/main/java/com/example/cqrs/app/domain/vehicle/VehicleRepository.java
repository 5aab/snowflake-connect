package com.example.cqrs.app.domain.vehicle;

import com.example.cqrs.persistence.CustomRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface VehicleRepository extends CustomRepository<Vehicle, Integer> {



}
