package com.uber.uberapi.models;

import com.uber.uberapi.exceptions.UnapprovedDriverException;
import com.uber.uberapi.utils.DateUtils;
import lombok.*;

import javax.persistence.*;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@Table(name="driver")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Driver extends Auditable{
    private Gender gender;
    private String name;
    private String phoneNumber;
    @OneToOne(cascade = CascadeType.ALL)
    private Account account;

    @OneToOne(mappedBy = "driver")
    private Car car;

    private String licenseDetails;

    @Temporal(value = TemporalType.DATE)
    private Date dob;

    @Enumerated(value = EnumType.STRING)
    private DriverApprovalStatus approvalStatus;

    @OneToMany(mappedBy = "driver")
    private List<Booking> bookings;

    @ManyToMany(mappedBy = "notifiedDrivers",cascade = CascadeType.PERSIST)
    private Set<Booking> acceptableBookings = new HashSet<>();

    @OneToOne
    private Booking activeBooking = null;

    private Boolean isAvailable;

    private String activeCity;

    @OneToOne
    private ExactLocation lastKnownExactLocation;

    @OneToOne
    private ExactLocation home;

    public void setAvailable(Boolean available){
        if(available && !approvalStatus.equals(DriverApprovalStatus.APPROVED)){
            throw new UnapprovedDriverException("Driver approval pending or denied "+getId());
        }
        isAvailable=available;
    }

    public boolean canAcceptBooking(int maxWaitTimeForPreviousRide) {
        if(isAvailable && activeBooking==null){
            return true;
        }
        return activeBooking.getExpectedCompletionTime().before(
                DateUtils.addMinutes(new Date(),maxWaitTimeForPreviousRide));
    }
}
