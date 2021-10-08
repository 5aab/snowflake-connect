package com.example.cqrs.app.domain.vehicle.event;

import com.example.cqrs.app.domain.vehicle.Vehicle;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class CreateVehicleEvent extends VehicleEvent {

    public CreateVehicleEvent(Vehicle entity){
        super(VehicleEventType.CREATED, entity);
    }
}
