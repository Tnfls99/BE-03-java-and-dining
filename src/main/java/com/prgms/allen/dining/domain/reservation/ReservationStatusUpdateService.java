package com.prgms.allen.dining.domain.reservation;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.prgms.allen.dining.domain.reservation.entity.Reservation;
import com.prgms.allen.dining.domain.reservation.exception.IllegalReservationStateException;
import com.prgms.allen.dining.global.error.exception.IllegalModificationException;

@Service
@Transactional
public class ReservationStatusUpdateService {

	private static final Logger log = LoggerFactory.getLogger(ReservationStatusUpdateService.class);

	private final ReservationService reservationService;

	public ReservationStatusUpdateService(ReservationService reservationService) {
		this.reservationService = reservationService;
	}

	public void confirm(Long reservationId, Long ownerId) {
		Reservation findReservation = reservationService.findById(reservationId);

		try {
			findReservation.confirm(ownerId);
		} catch (IllegalReservationStateException e) {
			throw new IllegalModificationException(e.getMessage());
		}

		log.info("Reservation {}'s status updated to {}", reservationId, findReservation.getStatus());
	}
}
