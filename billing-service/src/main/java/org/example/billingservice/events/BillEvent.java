package org.example.billingservice.events;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BillEvent {
    private Long id;
    private Long customerID;
    private Date billingDate;
    private double grandTotal;
}
