package com.uber.uberapi.models;

import lombok.*;

import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(name="exactlocation")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ExactLocation extends Auditable{
    private String latitude;
    private String longitude;
}
