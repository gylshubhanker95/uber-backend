package com.uber.uberapi.models;

import lombok.*;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(name="color")
@Setter
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Color extends Auditable{
    @Column(unique = true,nullable = false)
    private String name;
}
