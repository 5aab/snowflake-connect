package com.example.cqrs.app.rest;

import com.example.cqrs.app.domain.vehicle.Vehicle;
import com.example.cqrs.app.domain.vehicle.VehicleRepository;
import com.example.cqrs.app.domain.vehicle.dto.VehicleDto;
import com.example.cqrs.event.DomainAggregateRoot;
import com.querydsl.core.types.Predicate;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.querydsl.binding.QuerydslPredicate;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import static com.example.cqrs.app.domain.vehicle.VehicleQueryFilter.byBrandName;
import static com.example.cqrs.app.domain.vehicle.VehicleQueryFilter.vehicleProjection;

@Slf4j
@RestController
@AllArgsConstructor
public class PipelinerController {

    private VehicleRepository vehicleRepository;

    //@ResponseBody
    @GetMapping("predicated")
    public Page<VehicleDto> search(@QuerydslPredicate(root = Vehicle.class) Predicate spec, @PageableDefault(sort = {"createdDate"}, direction = Sort.Direction.DESC, size = 50) Pageable pageable) {
        return vehicleRepository.findAll(vehicleProjection(), byBrandName("Ferrari"), pageable);
    }

    //http://localhost:9090/search/vehicle?brandName=brandName
   // @ResponseBody
    @GetMapping("search/vehicle")
    public Page<VehicleDto> searchVehicle(@QuerydslPredicate(root = DomainAggregateRoot.class) Predicate predicate) {
        return vehicleRepository.findAll(vehicleProjection(), predicate, Pageable.ofSize(10));
    }

    @GetMapping("simple")
    public List<Vehicle> searchSimple() {
        long l = System.currentTimeMillis();
        List<Vehicle> all = vehicleRepository.findAll();
        log.info("Time Consumed : {} Sec", (System.currentTimeMillis() - l)/1000d);
        return all;
    }
}
