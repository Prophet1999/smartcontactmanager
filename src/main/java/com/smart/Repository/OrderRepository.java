package com.smart.Repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.smart.Entities.LocalOrder;

public interface OrderRepository extends JpaRepository<LocalOrder, Long>{

	public LocalOrder findByOrderId(String orderId);
}
