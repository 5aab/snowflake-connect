package com.example.cqrs.app.rest;

import com.example.cqrs.app.domain.vehicle.Vehicle;
import com.example.cqrs.app.domain.vehicle.VehicleRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.function.Supplier;

@Slf4j
@RestController
@AllArgsConstructor
public class VehicleController {

    private VehicleRepository vehicleRepository;

    @GetMapping("vehicle/persist")
    public Supplier<String> saveVehicle(){
        log.info("Going to Create Anonymous Vehicle");
        final Vehicle vehicle = new Vehicle();
        vehicle.setType("type");
        vehicle.setModelCode("modelCode");
        vehicle.setBrandName("brandName");
        vehicle.setLaunchDate(LocalDate.now());
        Vehicle save = this.vehicleRepository.save(vehicle.create());
        return ()-> save.toString();
    }
}
