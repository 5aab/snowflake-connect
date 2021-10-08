package com.example.cqrs.app.rest.functional;

import com.example.cqrs.app.domain.vehicle.Vehicle;
import com.example.cqrs.app.domain.vehicle.VehicleRepository;
import lombok.AllArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.function.Supplier;

@Configuration
@AllArgsConstructor
public class VehicleController {

    private VehicleRepository vehicleRepository;

    @Bean
    public Supplier<String> persistVehicle(){
        final Vehicle vehicle = new Vehicle();
        vehicle.setType("type");
        vehicle.setModelCode("modelCode");
        vehicle.setBrandName("brandName");
        vehicle.setLaunchDate(LocalDate.now());
        Vehicle save = this.vehicleRepository.save(vehicle);
        vehicle.setCreatedBy("Ish");
        vehicle.setCreatedDate(LocalDateTime.now());
        vehicle.setLastModifiedBy("Ish");
        vehicle.setLastModifiedDate(LocalDateTime.now());
        return ()-> save.toString();
    }
}
