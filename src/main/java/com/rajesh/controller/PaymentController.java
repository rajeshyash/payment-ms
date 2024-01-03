package com.rajesh.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.rajesh.dto.CustomerOrder;
import com.rajesh.dto.OrderEvent;
import com.rajesh.dto.PaymentEvent;
import com.rajesh.entity.Payment;
import com.rajesh.repository.PaymentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class PaymentController {
    @Autowired
    private PaymentRepository repository;

    @Autowired
    private KafkaTemplate<String, PaymentEvent> kafkaTemplate;

    @Autowired
    private KafkaTemplate<String, OrderEvent> kafkaOrderTemplate;

    @KafkaListener(topics = "new-orders", groupId = "orders-group")
    public void processPayment(OrderEvent orderEvent) {
        System.out.println("Inside processPayment event for payment-ms for  " + orderEvent);

        CustomerOrder order = orderEvent.getOrder();
        Payment payment = new Payment();

        try {
            payment.setAmount(order.getAmount());
            payment.setMode(order.getPaymentMode());
            payment.setOrderId(order.getOrderId());
            payment.setStatus("SUCCESS");
            repository.save(payment);

            PaymentEvent paymentEvent = new PaymentEvent();
            paymentEvent.setOrder(orderEvent.getOrder());

            paymentEvent.setType("PAYMENT_CREATED");
            kafkaTemplate.send("new-payments", paymentEvent);

        } catch (Exception e) {
            payment.setOrderId(order.getOrderId());
            payment.setStatus("FAILED");
            repository.save(payment);

            OrderEvent oe = new OrderEvent();
            oe.setOrder(order);
            oe.setType("ORDER_REVERSED");
            kafkaOrderTemplate.send("reversed-orders", orderEvent);
            System.out.println("ORDER_REVERSED");
        }
    }
}
