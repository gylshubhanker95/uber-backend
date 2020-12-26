package com.uber.uberapi.services;

import com.uber.uberapi.models.Booking;
import com.uber.uberapi.models.Driver;
import com.uber.uberapi.models.ExactLocation;
import com.uber.uberapi.models.Passenger;
import com.uber.uberapi.repositories.BookingRepository;
import com.uber.uberapi.repositories.DriverRepository;
import com.uber.uberapi.services.messagequeue.MQMessage;
import com.uber.uberapi.services.messagequeue.MessageQueue;
import com.uber.uberapi.services.notification.NotificationService;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DriverMatchingService {
    @Autowired
    Constants constants;
    @Autowired
    MessageQueue messageQueue;
    @Autowired
    LocationTrackingService locationTrackingService;
    @Autowired
    NotificationService notificationService;
    @Autowired
    BookingRepository bookingRepository;

    public void consumer(){
        MQMessage m = messageQueue.consumeMessage(constants.getDriverMatchingTopicName());
        if(m == null){
            return;
        }
        Message message = (Message) m;
        findNearByDrivers(message.getBooking());
    }

    private void findNearByDrivers(Booking booking) {
        ExactLocation pickup = booking.getPickupLocation();
        List<Driver> drivers = locationTrackingService.getDriversNearLocation(pickup);
        if(drivers.size() == 0){
            notificationService.notify(booking.getPassenger().getPhoneNumber(),"No cabs nearby");
            return;
        }
        notificationService.notify(booking.getPassenger().getPhoneNumber(),
                String.format("Contacting %s cabs around you ",drivers.size()));

        if(drivers.size() == 0){
            notificationService.notify(booking.getPassenger().getPhoneNumber(),"No cabs nearby");
            return;
        }
        drivers.forEach(driver -> {
            notificationService.notify(driver.getPhoneNumber(), "Booking near you: "+booking.toString());
            driver.getAcceptableBookings().add(booking);
        });
        bookingRepository.save(booking);
    }


    @AllArgsConstructor
    @Getter
    @Setter
    public static class Message implements MQMessage {
        private Booking booking;

        @Override
        public String toString() {
            return String.format("Need to find drivers for %s", booking.toString());
        }
    }
}
