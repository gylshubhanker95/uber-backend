package com.uber.uberapi.models;

import lombok.*;

import javax.persistence.*;

@Entity
@Table(name = "car")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Car extends Auditable{
    @OneToOne
    private Driver driver;

    @ManyToOne
    private Color color;

    private String plateNumber;

    private String brandAndModel;

    @Enumerated(EnumType.STRING)
    private CarType carType;

}
