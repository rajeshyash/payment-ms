package com.rajesh.service;

import com.rajesh.dto.CustomerOrder;
import com.rajesh.dto.OrderEvent;
import com.rajesh.dto.PaymentEvent;
import com.rajesh.entity.Payment;
import com.rajesh.repository.PaymentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class ReversePayment {
    @Autowired
    private PaymentRepository repository;

    @Autowired
    private KafkaTemplate<String, OrderEvent> kafkaTemplate;

    @KafkaListener(topics = "reversed-payments", groupId = "payments-group")
    public void reversePayment(PaymentEvent paymentEvent) {
        System.out.println("Inside reverse payment-ms for order "+paymentEvent);

        try {
            CustomerOrder order = paymentEvent.getOrder();
            Iterable<Payment> payments = this.repository.findByOrderId(order.getOrderId());
            payments.forEach(p -> {
                p.setStatus("FAILED");
                repository.save(p);
            });

            OrderEvent orderEvent = new OrderEvent();
            orderEvent.setOrder(paymentEvent.getOrder());
            orderEvent.setType("ORDER_REVERSED");
            kafkaTemplate.send("reversed-orders", orderEvent);
            System.out.println("ORDER_REVERSED");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
