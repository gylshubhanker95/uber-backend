package com.uber.uberapi.controllers;

import com.uber.uberapi.exceptions.InvalidBookingException;
import com.uber.uberapi.exceptions.InvalidDriverException;
import com.uber.uberapi.exceptions.UnapprovedDriverException;
import com.uber.uberapi.models.*;
import com.uber.uberapi.repositories.BookingRepository;
import com.uber.uberapi.repositories.DriverRepository;
import com.uber.uberapi.repositories.ReviewRepository;
import com.uber.uberapi.services.BookingService;
import com.uber.uberapi.services.Constants;
import com.uber.uberapi.services.DriverMatchingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RequestMapping("/driver")
@RestController
public class DriverController {
    @Autowired
    DriverRepository driverRepository;
    @Autowired
    BookingRepository bookingRepository;
    @Autowired
    DriverMatchingService driverMatchingService;
    @Autowired
    ReviewRepository reviewRepository;
    @Autowired
    Constants constants;
    @Autowired
    BookingService bookingService;

    public Driver getDriverFromId(Long DriverId){
        Optional<Driver> driver=driverRepository.findById(DriverId);
        if(driver.isEmpty()){
            throw new InvalidDriverException("No Driver with id "+DriverId);
        }
        return driver.get();
    }
    public Booking getBookingFromId(Long BookingId){
        Optional<Booking> booking=bookingRepository.findById(BookingId);
        if(booking.isEmpty()){
            throw new InvalidBookingException("No Such Booking with id "+BookingId);
        }
        return booking.get();
    }
    public Booking getDriverBookingFromId(Long BookingId,Driver driver){
        Booking booking=getBookingFromId(BookingId);
        if(!booking.getDriver().equals(driver)){
            throw new InvalidBookingException("Driver "+driver+" has no such booking "+BookingId);
        }
        return booking;
    }
    @GetMapping("/{driverId}")
    public Driver getDriverDetails(@PathVariable(name="driverId") Long driverId){
        return getDriverFromId(driverId);
    }

    @PatchMapping("/{driverId}")
    public void changeAvailability(@PathVariable(name="driverId") Long driverId,
                                   @RequestBody Boolean available){
        Driver driver=getDriverFromId(driverId);
        driver.setIsAvailable(available);
        driverRepository.save(driver);
    }

    @GetMapping("/{driverId}/bookings")
    public List<Booking> getAllBookings(@PathVariable(name="driverId") Long driverId){
        Driver driver=getDriverFromId(driverId);
        return driver.getBookings();
    }
    @GetMapping("/{driverId}/bookings/{bookingId}")
    public Booking getBooking(@PathVariable(name="driverId") Long driverId,
                                    @PathVariable(name="bookingId") Long bookingId){
        Driver driver=getDriverFromId(driverId);
        return getDriverBookingFromId(bookingId,driver);
    }

    @PostMapping("/{driverId}/bookings/{bookingId}")
    public void acceptBooking(@PathVariable(name = "driverId")Long driverId,
                              @PathVariable(name = "bookingId")Long bookingId){
        Driver driver=getDriverFromId(driverId);
        Booking booking=getDriverBookingFromId(bookingId,driver);
        bookingService.acceptBooking(driver,booking);
    }
    @DeleteMapping("/{driverId}/bookings/{bookingId}")
    public void cancelBooking(@PathVariable(name = "driverId")Long driverId,
                              @PathVariable(name = "bookingId")Long bookingId){
        Driver driver=getDriverFromId(driverId);
        Booking booking=getDriverBookingFromId(bookingId,driver);
        bookingService.cancelByDriver(driver,booking);
    }

    @PatchMapping("/{driverId}/bookings/{bookingId}/start")
    public void startRide(@PathVariable(name = "driverId")Long driverId,
                          @PathVariable(name = "bookingId")Long bookingId,
                          @RequestBody OTP otp){
        Driver driver=getDriverFromId(driverId);
        Booking booking=getDriverBookingFromId(bookingId,driver);
        booking.startRide(otp,constants.getRideStartOTPExpiryMinutes());
        bookingRepository.save(booking);
    }
    @PatchMapping("/{driverId}/bookings/{bookingId}/end")
    public void endRide(@PathVariable(name = "driverId")Long driverId,
                          @PathVariable(name = "bookingId")Long bookingId){
        Driver driver=getDriverFromId(driverId);
        Booking booking=getDriverBookingFromId(bookingId,driver);
        booking.endRide();
        driverRepository.save(driver);
        bookingRepository.save(booking);
    }

    @PatchMapping("/{driverId}/bookings/{bookingId}/rate")
    public void rateRide(@PathVariable(name = "driverId")Long driverId,
                         @PathVariable(name = "bookingId")Long bookingId,
                         @RequestBody Review data){
        Driver driver=getDriverFromId(driverId);
        Booking booking=getDriverBookingFromId(bookingId,driver);
        Review review=Review.builder().
                note(data.getNote()).
                ratingOutOfFive(data.getRatingOutOfFive()).
                build();
        booking.setReviewByDriver(review);
        reviewRepository.save(review);
        bookingRepository.save(booking);
    }

}
