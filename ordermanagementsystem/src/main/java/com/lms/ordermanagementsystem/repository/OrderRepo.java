package com.lms.ordermanagementsystem.repository;

import com.lms.ordermanagementsystem.entity.Orders;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OrderRepo extends JpaRepository<Orders, Long> {
}
