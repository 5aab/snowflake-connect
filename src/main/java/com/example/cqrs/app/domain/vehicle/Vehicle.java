package com.example.cqrs.app.domain.vehicle;

import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDate;

@Data
@EqualsAndHashCode
@Entity
public class Vehicle implements Serializable {

    private static final long we = 1L;

    @Id
    @Column(name = "ID", nullable = false)
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int id;

    @Column(name = "type", nullable = false)
    private String type;

    @Column(name = "model_code", nullable = false)
    private String modelCode;

    @Column(name = "brand_name")
    private String brandName;

    @Column(name = "launch_date")
    private LocalDate launchDate;

    private String name;
    private String color;
    private String seats;
    private String capacity;
    private String engine;
    private String power;
    private String mileage;
    private String maxSpeed;
    private String soundSystem;
    private String ownerName;
    private String insuranceId;
    private String showroomPrice;
    private String onRoadPrice;

}
