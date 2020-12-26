package com.uber.uberapi.models;

import lombok.*;

import javax.persistence.*;
import java.util.*;

@Entity
@Table(name="passenger")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Passenger extends Auditable{
    @OneToOne(cascade = CascadeType.ALL)
    private Account account;

    private String name;

    @Enumerated(value=EnumType.STRING)
    private Gender gender;

    @OneToMany(mappedBy = "passenger")
    private List<Booking> bookings=new ArrayList<>();

    @Temporal(value = TemporalType.DATE)
    private  Date dob;

    private String phoneNumber;

    @OneToOne
    private ExactLocation home;
    @OneToOne
    private ExactLocation work;
    @OneToOne
    private ExactLocation lastKnownExactLocation;

}
