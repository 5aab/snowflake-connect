package com.example.cqrs.app.rest;

import an.awesome.pipelinr.Pipeline;
import com.example.cqrs.app.domain.ping.Ping;
import com.example.cqrs.app.domain.token.TokenGenerationService;
import com.example.cqrs.app.domain.vehicle.Vehicle;
import com.example.cqrs.app.domain.vehicle.VehicleRepository;
import com.example.cqrs.app.domain.vehicle.dto.VehicleDto;
import com.example.cqrs.app.domain.vehicle.record.VehicleRecord;
import com.example.cqrs.security.jwt.JwtUser;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.querydsl.binding.QuerydslPredicate;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.Set;
import java.util.function.Predicate;

import static com.example.cqrs.app.domain.vehicle.VehicleQueryFilter.byBrandName;
import static com.example.cqrs.app.domain.vehicle.VehicleQueryFilter.vehicleProjection;

@Slf4j
@RestController
@AllArgsConstructor
public class PipelinerController {

    private Pipeline pipeline;
    private VehicleRepository vehicleRepository;
    private TokenGenerationService tokenGenerationService;

     //content-type: application/json
    //accept: application/json
    //Authorization: Bearer eyJhbGciOiJIUzUxMiJ9.eyJqdGkiOiJkMjc1ZTFlZS0zMzNlLTRjY2MtODI0My05OTA2NTU0M2QyYWQiLCJzdWIiOiJJc2hNYWhhamFuIiwicm9sZXMiOlsiQURNSU4iXSwicHJvZHVjdHMiOlsiSU5TVElUVVRJT05BTCJdLCJjcmVhdGVkIjoxNjM0Mzk4MDUzMzQ0LCJ1c2VyVHlwZSI6IkhVTUFOIiwiZXhwIjoxNjM0NDM0MDUzfQ.sVTnuxitmrevcQZKWoanuspu6Sh8oL3bZ3S11_UYayajVukj-BAiGG_2IleN_aqWlhLmFsMmNkfHppNE3sGNgA
    //janus_user: IshMahajan
    @GetMapping("testPipe")
    public String testPipe(@AuthenticationPrincipal JwtUser jwtUser){
        log.info("Hell0 : {}", jwtUser);
        return new Ping("PONGA PONGA").execute(pipeline);
    }

    @ResponseBody
    @GetMapping("token")
    public String token(){
        String token = tokenGenerationService.generateToken();
        log.info("Token : {}", token);
        return token;
    }


    @ResponseBody
    @GetMapping("predicated")
    public Page<VehicleDto> search(String brandName, @QuerydslPredicate (root = Vehicle.class)Predicate spec, @PageableDefault(sort = {"createdDate"}, direction = Sort.Direction.DESC, size = 50)Pageable pageable){
        return vehicleRepository.findAll(vehicleProjection(), byBrandName(brandName), pageable);
    }


    @GetMapping("vehicle/record")
    public Set<VehicleRecord> searchVehicles(){
        Set<VehicleRecord> allVehicles = vehicleRepository.findByBrandName("brandName");
        return allVehicles;
    }
}
