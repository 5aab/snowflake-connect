package com.example.cqrs.app.rest;

import com.example.cqrs.app.domain.vehicle.RequestParameter;
import com.example.cqrs.app.domain.vehicle.Vehicle;
import com.example.cqrs.app.domain.vehicle.VehicleRepository;
import com.example.cqrs.app.domain.vehicle.dto.VehicleDto;
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

import java.util.Collection;
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

    @GetMapping("unpredicated")
    public Page<VehicleDto> unpredicated(@QuerydslPredicate(root = Vehicle.class) Predicate spec, @PageableDefault(sort = {"createdDate"}, direction = Sort.Direction.DESC, size = 50) Pageable pageable) {
        return vehicleRepository.findAll(vehicleProjection(), byBrandName("Ferrari"), pageable);
    }

    //http://localhost:9095/search/vehicle?brandName=Ferrari
    // @ResponseBody
    @GetMapping("search/vehicle")
    public Page<VehicleDto> searchVehicle(@QuerydslPredicate(root = Vehicle.class) Predicate predicate) {
        return vehicleRepository.findAll(vehicleProjection(), predicate, Pageable.ofSize(10));
    }

    @GetMapping("search/dynamic/projection")
    public Collection<VehicleDto> dynamicProjection(@QuerydslPredicate(root = Vehicle.class) Predicate predicate) {
        return vehicleRepository.findByBrandName("Ferrari", VehicleDto.class);
    }

    @GetMapping("search/dynamic/projection2")
    public Collection<VehicleDto> dynamicProjection2(@QuerydslPredicate(root = Vehicle.class) Predicate predicate) {
        return vehicleRepository.findBy(VehicleDto.class);
    }

    @GetMapping("simple")
    public List<Vehicle> searchSimple() {
        long l = System.currentTimeMillis();
        List<Vehicle> all = vehicleRepository.findAll();
        log.info("Time Consumed : {} Sec", (System.currentTimeMillis() - l) / 1000d);
        return all;
    }

    @GetMapping("request/param")
    public String requestParam(RequestParameter requestParameter) {
        log.info("Request Params : {} ", requestParameter);
        return requestParameter.toString();
    }


    @GetMapping("search/predicate")
    public Iterable<Vehicle> searchPredicate(@QuerydslPredicate(root = Vehicle.class) Predicate predicate) {
        log.info("Going to search in car database");
        return vehicleRepository.findAll(predicate);
    }
}
