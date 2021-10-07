package com.example.cqrs.app.domain.vehicle.event;

import com.example.cqrs.app.domain.vehicle.Vehicle;
import com.example.cqrs.event.DomainEvent;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public abstract class VehicleEvent extends DomainEvent<VehicleEventType, Vehicle> {

    public VehicleEvent(VehicleEventType type, Vehicle entity){
        super(type, entity);
    }
}
