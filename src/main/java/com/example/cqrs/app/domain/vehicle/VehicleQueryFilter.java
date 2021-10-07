package com.example.cqrs.app.domain.vehicle;

import com.example.cqrs.app.domain.vehicle.dto.VehicleDto;
import com.querydsl.core.types.ConstructorExpression;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;

public class VehicleQueryFilter {

    private static QVehicle $ = QVehicle.vehicle;

    public static BooleanExpression byBrandName(String name) {
        return $.brandName.equalsIgnoreCase(name);
    }


    public static ConstructorExpression<VehicleDto> vehicleProjection() {
        return Projections.constructor(VehicleDto.class,
                $.id,
                $.brandName,
                $.name,
                $.capacity,
                $.color,
                $.engine);
    }

}
