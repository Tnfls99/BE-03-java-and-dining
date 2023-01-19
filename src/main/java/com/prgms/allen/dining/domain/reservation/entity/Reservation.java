package com.prgms.allen.dining.domain.reservation.entity;

import static com.prgms.allen.dining.domain.reservation.entity.ReservationStatus.*;

import java.text.MessageFormat;
import java.time.LocalDateTime;
import java.util.Arrays;

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

import org.springframework.util.Assert;

import com.prgms.allen.dining.domain.common.entity.BaseEntity;
import com.prgms.allen.dining.domain.member.entity.Member;
import com.prgms.allen.dining.domain.reservation.exception.IllegalReservationStateException;
import com.prgms.allen.dining.domain.restaurant.entity.Restaurant;
import com.prgms.allen.dining.global.util.TimeUtils;

@Entity
public class Reservation extends BaseEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = "reservation_id")
	private Long id;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "customer_id", nullable = false)
	private Member customer;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "restaurant_id", nullable = false)
	private Restaurant restaurant;

	@Enumerated(EnumType.STRING)
	@Column(name = "status", nullable = false)
	private ReservationStatus status;

	@Embedded
	@Column(name = "detail", nullable = false)
	private ReservationCustomerInput customerInput;

	protected Reservation() {
	}

	public Reservation(Long id, Member customer, Restaurant restaurant, ReservationStatus status,
		ReservationCustomerInput customerInput) {
		validate(customer, restaurant, customerInput);

		this.id = id;
		this.customer = customer;
		this.restaurant = restaurant;
		this.status = status;
		this.customerInput = customerInput;
	}

	public Reservation(Member customer, Restaurant restaurant, ReservationStatus status,
		ReservationCustomerInput customerInput) {
		this(null, customer, restaurant, status, customerInput);
	}

	public Reservation(Member customer, Restaurant restaurant, ReservationCustomerInput customerInput) {
		validate(customer, restaurant, customerInput);

		this.customer = customer;
		this.restaurant = restaurant;
		if (customerInput.checkVisitingToday()) {
			this.status = CONFIRMED;
		} else {
			this.status = PENDING;
		}
		this.customerInput = customerInput;
	}

	private void validate(Member customer, Restaurant restaurant, ReservationCustomerInput customerInput) {
		validateCustomer(customer);
		validateRestaurant(restaurant);
		validateReservationDetail(customerInput);
	}

	private void validateCustomer(Member customer) {
		Assert.notNull(customer, "Customer must not be null.");
	}

	private void validateRestaurant(Restaurant restaurant) {
		Assert.notNull(restaurant, "Restaurant must not be null.");
	}

	private void validateReservationDetail(ReservationCustomerInput customerInput) {
		Assert.notNull(customerInput, "ReservationDetail must not be null.");
	}

	public Long getId() {
		return id;
	}

	public Member getCustomer() {
		return customer;
	}

	public Restaurant getRestaurant() {
		return restaurant;
	}

	public ReservationStatus getStatus() {
		return status;
	}

	public ReservationCustomerInput getCustomerInput() {
		return customerInput;
	}

	public long getRestaurantId() {
		return restaurant.getId();
	}

	public int getVisitorCount() {
		return customerInput.getVisitorCount();
	}

	public String getCustomerPhone() {
		return customer.getPhone();
	}

	public String getCustomerName() {
		return customer.getName();
	}

	public LocalDateTime getVisitDateTime() {
		return customerInput.getVisitDateTime();
	}

	public Member getRestaurantOwner() {
		return restaurant.getOwner();
	}

	public void confirm(Long ownerId) {
		validUpdatableReservationState(ownerId, PENDING);
		assertVisitDateTimeAfter(TimeUtils.getCurrentSeoulDateTime());
		this.status = CONFIRMED;
	}

	public void cancel(Long ownerId) {
		validUpdatableReservationState(ownerId, PENDING, CONFIRMED);
		assertVisitDateTimeAfter(TimeUtils.getCurrentSeoulDateTime());
		this.status = CANCELLED;
	}

	private void validUpdatableReservationState(Long ownerId, ReservationStatus... validStatuses) {
		assertMatchesOwner(ownerId);
		assertReservationStatus(validStatuses);
	}

	private void assertMatchesOwner(Long ownerId) {
		if (!getRestaurantOwner().matchesId(ownerId)) {
			throw new IllegalReservationStateException(MessageFormat.format(
				"Owner does not match. Parameter ownerId={0} but actual ownerId={1}",
				ownerId,
				getRestaurantOwner().getId()
			));
		}
	}

	private void assertReservationStatus(ReservationStatus... validStatuses) {
		if (!Arrays.asList(validStatuses).contains(this.status)) {
			throw new IllegalReservationStateException(MessageFormat.format(
				"ReservationStatus should be {0} but was {1}", Arrays.toString(validStatuses), this.status
			));
		}
	}

	private void assertVisitDateTimeAfter(LocalDateTime dateTime) {
		if (!this.customerInput.isVisitDateTimeAfter(dateTime)) {
			throw new IllegalReservationStateException(MessageFormat.format(
				"dateTime={0} should precede visitDateTime={1}",
				dateTime,
				this.customerInput.getVisitDateTime()
			));
		}
	}

	private void assertVisitDateTimeBefore(LocalDateTime dateTime) {
		if (!this.customerInput.isVisitDateTimeBefore(dateTime)) {
			throw new IllegalReservationStateException(MessageFormat.format(
				"visitDateTime={0} should precede dateTime={1}",
				dateTime,
				this.customerInput.getVisitDateTime()
			));
		}
	}
}
