package com.example.cqrs.app.rest;

import an.awesome.pipelinr.Pipeline;
import com.example.cqrs.app.domain.vehicle.Vehicle;
import com.example.cqrs.app.domain.vehicle.VehicleRepository;
import com.example.cqrs.app.domain.vehicle.dto.VehicleDto;
import com.example.cqrs.app.domain.ping.Ping;
import com.example.cqrs.security.jwt.JwtUser;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.querydsl.binding.QuerydslPredicate;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.function.Predicate;

import static com.example.cqrs.app.domain.vehicle.VehicleQueryFilter.byBrandName;
import static com.example.cqrs.app.domain.vehicle.VehicleQueryFilter.vehicleProjection;

@Slf4j
@Controller
@AllArgsConstructor
public class PipelinerController {

    private Pipeline pipeline;
    private VehicleRepository vehicleRepository;

    @ResponseBody
    @GetMapping("testPipe")
    public String testPipe(@AuthenticationPrincipal JwtUser jwtUser){
        log.info("Hell0 : {}", jwtUser);
        return new Ping("PONGA PONGA").execute(pipeline);
    }


    @ResponseBody
    @GetMapping("predicated")
    public Page<VehicleDto> search(String brandName, @QuerydslPredicate (root = Vehicle.class)Predicate spec, @PageableDefault(sort = {"createdDate"}, direction = Sort.Direction.DESC, size = 50)Pageable pageable){
        return vehicleRepository.findAll(vehicleProjection(), byBrandName(brandName), pageable);
    }
}
