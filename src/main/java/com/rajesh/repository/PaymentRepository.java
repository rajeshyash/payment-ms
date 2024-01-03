package com.rajesh.repository;

import com.rajesh.entity.Payment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PaymentRepository extends JpaRepository<Payment,Long> {
    public List<Payment> findByOrderId(long orderId);
}
