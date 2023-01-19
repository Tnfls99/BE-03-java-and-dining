package com.prgms.allen.dining.domain.reservation;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.prgms.allen.dining.domain.reservation.entity.Reservation;
import com.prgms.allen.dining.domain.reservation.entity.ReservationStatus;
import com.prgms.allen.dining.domain.restaurant.entity.Restaurant;

public interface ReservationRepository extends JpaRepository<Reservation, Long> {

	Page<Reservation> findAllByRestaurantIdAndStatus(long restaurantId, ReservationStatus status, Pageable pageable);

	@Query("select sum(r.detail.visitorCount) "
		+ " from Reservation r "
		+ " where r.restaurant = :restaurant "
		+ " AND r.status In (:statuses) "
		+ " AND r.detail.visitDate = :visitDate"
		+ " AND r.detail.visitTime = :visitTime")
	Optional<Integer> countTotalVisitorCount(
		@Param("restaurant") Restaurant restaurant,
		@Param("visitDate") LocalDate visitDate,
		@Param("visitTime") LocalTime visitTime,
		@Param("statuses") List<ReservationStatus> statuses);
}
