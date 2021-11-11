package com.example.cqrs.app.domain.vehicle;

import com.example.cqrs.app.domain.vehicle.dto.VehicleDto;
import com.querydsl.core.types.ConstructorExpression;
import com.querydsl.core.types.Expression;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.EntityPathBase;
import com.querydsl.core.types.dsl.PathBuilder;

import java.util.Arrays;

public class VehicleQueryFilter {

    private static QVehicle $ = QVehicle.vehicle;

    public static BooleanExpression byBrandName(String name) {
        return $.brandName.equalsIgnoreCase(name);
    }

    public static ConstructorExpression<VehicleDto> vehicleProjection() {
        Class<? extends Vehicle> type = $.getType();
        System.out.println(type);
        return getProjection(VehicleDto.class, $);
    }

    private static <T, E> ConstructorExpression<T> getProjection(Class<T> clazz, EntityPathBase<E> dsl) {
        PathBuilder path = new PathBuilder(dsl.getType(), dsl.getRoot().toString());
        Expression[] expressions = Arrays.stream(clazz.getDeclaredFields()).map(field -> path.get(field.getName(), field.getType())).toArray(Expression[]::new);
        return Projections.constructor(clazz , expressions);
    }

    public static ConstructorExpression<VehicleDto> vehicleProjection2() {
        return Projections.constructor(VehicleDto.class,
                $.id,
                $.brandName,
                $.name,
                $.capacity,
                $.color,
                $.engine);
    }

}
