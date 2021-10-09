package com.example.cqrs.app.domain.vehicle.event;

import com.example.cqrs.app.domain.vehicle.Vehicle;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Getter
@ToString
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class CreateVehicleEvent extends VehicleEvent {

    private Vehicle vehicle;

    public CreateVehicleEvent(Vehicle entity) {
        super(VehicleEventType.CREATED, entity);
        this.vehicle = entity;
    }
}
