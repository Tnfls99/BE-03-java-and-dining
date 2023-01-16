package com.prgms.allen.dining.domain.reservation.entity;

import java.time.LocalDateTime;

import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

import com.prgms.allen.dining.domain.common.entity.BaseEntity;
import com.prgms.allen.dining.domain.customer.entity.Customer;
import com.prgms.allen.dining.domain.restaurant.entity.Restaurant;

@Entity
public class Reservation extends BaseEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = "reservation_id")
	private Long id;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "customer_id", nullable = false)
	private Customer customer;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "restaurant_id", nullable = false)
	private Restaurant restaurant;

	@Enumerated(EnumType.STRING)
	@Column(name = "status", nullable = false)
	private ReservationStatus status;

	@Embedded
	@Column(name = "detail", nullable = false)
	private ReservationDetail detail;

	protected Reservation() {
	}

	public Reservation(Long id, Customer customer, Restaurant restaurant, ReservationStatus status,
		ReservationDetail detail) {
		this.id = id;
		this.customer = customer;
		this.restaurant = restaurant;
		this.status = status;
		this.detail = detail;
	}

	public Reservation(Customer customer, Restaurant restaurant, ReservationStatus status, ReservationDetail detail) {
		this(null, customer, restaurant, status, detail);
	}

	public long getRestaurantId() {
		return restaurant.getId();
	}

	public LocalDateTime getVisitDateTime() {
		return LocalDateTime.of(detail.getVisitDate(), detail.getVisitTime());
	}

	public int getVisitorCount() {
		return detail.getVisitorCount();
	}

	public String getCustomerPhone() {
		return customer.getPhone();
	}

	public String getCustomerName() {
		return customer.getName();
	}

	public ReservationStatus getStatus() {
		return status;
	}

	public Long getId() {
		return id;
	}

	public Customer getCustomer() {
		return customer;
	}

	public Restaurant getRestaurant() {
		return restaurant;
	}

	public ReservationDetail getDetail() {
		return detail;
	}
}
