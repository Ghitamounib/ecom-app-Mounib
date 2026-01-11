package org.example.billingservice.services;

import org.example.billingservice.entities.Bill;
import org.example.billingservice.events.BillEvent;
import org.springframework.cloud.stream.function.StreamBridge;
import org.springframework.stereotype.Service;

@Service
public class BillEventProducer {

    private final StreamBridge streamBridge;

    public BillEventProducer(StreamBridge streamBridge) {
        this.streamBridge = streamBridge;
    }

    public void publishBillCreated(Bill bill) {
        BillEvent event = BillEvent.builder()
                .id(bill.getId())
                .customerID(bill.getCustomerId())
                .billingDate(bill.getBillingDate())
                .grandTotal(0.0) // You might want to calculate this from items
                .build();

        streamBridge.send("billCreated-out-0", event);
    }
}
