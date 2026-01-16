package org.example.kafkaspringcloudstream.services;

import org.example.kafkaspringcloudstream.entities.BillEvent;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Service;

import java.util.function.Consumer;

@Service
public class BillAnalyticsService {

    @Bean
    public Consumer<BillEvent> billConsumer() {
        return (input) -> {
            System.out.println("*********************");
            System.out.println("Bill Created Event Received");
            System.out.println("Bill ID: " + input.getId());
            System.out.println("Customer ID: " + input.getCustomerID());
            System.out.println("Date: " + input.getBillingDate());
            System.out.println("*********************");
        };
    }
}
