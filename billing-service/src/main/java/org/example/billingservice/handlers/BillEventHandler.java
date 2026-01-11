package org.example.billingservice.handlers;

import org.example.billingservice.entities.Bill;
import org.example.billingservice.services.BillEventProducer;
import org.springframework.data.rest.core.annotation.HandleAfterCreate;
import org.springframework.data.rest.core.annotation.RepositoryEventHandler;
import org.springframework.stereotype.Component;

@Component
@RepositoryEventHandler(Bill.class)
public class BillEventHandler {

    private final BillEventProducer billEventProducer;

    public BillEventHandler(BillEventProducer billEventProducer) {
        this.billEventProducer = billEventProducer;
    }

    @HandleAfterCreate
    public void handleBillSave(Bill bill) {
        billEventProducer.publishBillCreated(bill);
    }
}
