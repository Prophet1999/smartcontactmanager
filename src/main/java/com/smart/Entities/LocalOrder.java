package com.smart.Entities;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name="orders")
public class LocalOrder {

	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	private Integer recordId;
	
	private String orderId;
	
	private Integer amount;
	
	private String reciept;
	
	private String status;
	
	@ManyToOne
	private User user;
	
	private String paymentId;

	
	public LocalOrder(Integer recordId, String orderId, Integer amount, String reciept, String status, User user,
			String paymentId) {
		super();
		this.recordId = recordId;
		this.orderId = orderId;
		this.amount = amount;
		this.reciept = reciept;
		this.status = status;
		this.user = user;
		this.paymentId = paymentId;
	}

	public LocalOrder() {
		super();
	}


	public Integer getRecordId() {
		return recordId;
	}

	public void setRecordId(Integer recordId) {
		this.recordId = recordId;
	}

	public String getOrderId() {
		return orderId;
	}

	public void setOrderId(String orderId) {
		this.orderId = orderId;
	}

	public Integer getAmount() {
		return amount;
	}

	public void setAmount(Integer amount) {
		this.amount = amount;
	}

	public String getReciept() {
		return reciept;
	}

	public void setReciept(String reciept) {
		this.reciept = reciept;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public String getPaymentId() {
		return paymentId;
	}

	public void setPaymentId(String paymentId) {
		this.paymentId = paymentId;
	}

	@Override
	public String toString() {
		return "Order [recordId=" + recordId + ", orderId=" + orderId + ", amount=" + amount + ", reciept=" + reciept
				+ ", status=" + status + ", user=" + user + ", paymentId=" + paymentId + "]";
	}
	
	
}
