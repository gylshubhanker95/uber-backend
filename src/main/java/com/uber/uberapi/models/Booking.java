package com.uber.uberapi.models;

import com.uber.uberapi.exceptions.InvalidActionForBookingStateException;
import com.uber.uberapi.exceptions.InvalidOTPException;
import lombok.*;

import javax.persistence.*;
import java.util.*;


@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Table(name = "booking",indexes = {
        @Index(columnList = "passenger_id"),
        @Index(columnList = "driver_id"),
})
public class Booking extends Auditable{
    @ManyToOne
    private Passenger passenger;

    @ManyToOne
    private Driver driver;

    @ManyToMany(cascade = CascadeType.PERSIST)
    private Set<Driver> notifiedDrivers = new HashSet<>();

    @Enumerated(EnumType.STRING)
    private BookingType bookingType;

    @Enumerated(EnumType.STRING)
    private BookingStatus bookingStatus;

    @OneToOne
    private Review reviewByPassenger;

    @OneToOne
    private Review reviewByDriver;

    @OneToOne
    private PaymentReceipt paymentReceipt;

    @OneToMany(cascade = CascadeType.ALL)
    @JoinTable(
            name = "booking_route",
            joinColumns = @JoinColumn(name = "booking_id"),
            inverseJoinColumns = @JoinColumn(name = "exact_location_id"),
            indexes = {@Index(columnList = "booking_id")}
    )


    @OrderColumn(name = "location_index")
    private List<ExactLocation> route=new ArrayList<>();

    @OneToMany(cascade = CascadeType.ALL)
    @JoinTable(
            name = "booking_completed_route",
            joinColumns = @JoinColumn(name = "booking_id"),
            inverseJoinColumns = @JoinColumn(name = "exact_location_id"),
            indexes = {@Index(columnList = "booking_id")}
    )

    @OrderColumn(name = "location_index")
    private List<ExactLocation> completedRoute=new ArrayList<>();

    @Temporal(TemporalType.TIMESTAMP)
    private Date scheduledTime;

    @Temporal(TemporalType.TIMESTAMP)
    private Date startTime;

    @Temporal(TemporalType.TIMESTAMP)
    private Date endTime;

    @Temporal(TemporalType.TIMESTAMP)
    private Date expectedCompletionTime;
    @OneToOne
    private OTP rideStartOTP;

    private Long totalDistanceMeters;

    public void startRide(OTP otp,int rideStartOTPExpiryMinutes) {
        if(!bookingStatus.equals(BookingStatus.CAB_ARRIVED)){
            throw new InvalidActionForBookingStateException("Cannot start the ride before the driver has reached the pickup point");
        }
        if (!rideStartOTP.validateEnteredOTP(otp, rideStartOTPExpiryMinutes)){
            throw new InvalidOTPException();
        }
        bookingStatus=BookingStatus.IN_RIDE;
    }

    public void endRide() {
        if(!bookingStatus.equals(BookingStatus.IN_RIDE)){
            throw new InvalidActionForBookingStateException("The Ride hasn't started yet");
        }
        driver.setActiveBooking(null);
        bookingStatus=BookingStatus.COMPLETED;
    }


    public boolean canChangeRoute() {
        return bookingStatus.equals(BookingStatus.CAB_ARRIVED)
                || bookingStatus.equals(BookingStatus.ASSIGNING_DRIVER)
                || bookingStatus.equals(BookingStatus.IN_RIDE)
                || bookingStatus.equals(BookingStatus.REACHING_PICKUP_LOCATION)
                || bookingStatus.equals(BookingStatus.SCHEDULED);

    }

    public boolean needsDriver() {
        return bookingStatus.equals(BookingStatus.ASSIGNING_DRIVER);
    }

    public ExactLocation getPickupLocation() {
        return route.get(0);
    }

    public void cancel() {
        if(!(bookingStatus.equals(BookingStatus.ASSIGNING_DRIVER)
                | bookingStatus.equals(BookingStatus.CAB_ARRIVED)
                || bookingStatus.equals(BookingStatus.REACHING_PICKUP_LOCATION)
                || bookingStatus.equals(BookingStatus.SCHEDULED))){
            throw new InvalidActionForBookingStateException("Cannot cancel the booking now.If the ride is in progress,ank the driver to end the ride");
        }
        bookingStatus = BookingStatus.CANCELLED;
        driver = null;
        notifiedDrivers.clear();
    }
}
