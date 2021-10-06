package com.example.cqrs.app.domain.vehicle;

import com.querydsl.core.types.dsl.BooleanExpression;

public class VehicleQueryFilter {

    private static QVehicle $ = QVehicle.vehicle;

    public static BooleanExpression byBrandName(String name) {
        return $.brandName.equalsIgnoreCase(name);
    }

}
