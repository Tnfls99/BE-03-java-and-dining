package com.prgms.allen.dining.domain.reservation;

import static org.assertj.core.api.Assertions.*;

import java.math.BigInteger;
import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.transaction.annotation.Transactional;

import com.prgms.allen.dining.domain.member.MemberRepository;
import com.prgms.allen.dining.domain.member.entity.Member;
import com.prgms.allen.dining.domain.member.entity.MemberType;
import com.prgms.allen.dining.domain.reservation.dto.VisitorCountPerVisitTimeProj;
import com.prgms.allen.dining.domain.reservation.entity.Reservation;
import com.prgms.allen.dining.domain.reservation.entity.ReservationCustomerInput;
import com.prgms.allen.dining.domain.reservation.entity.ReservationStatus;
import com.prgms.allen.dining.domain.restaurant.RestaurantRepository;
import com.prgms.allen.dining.domain.restaurant.entity.ClosingDay;
import com.prgms.allen.dining.domain.restaurant.entity.FoodType;
import com.prgms.allen.dining.domain.restaurant.entity.Menu;
import com.prgms.allen.dining.domain.restaurant.entity.Restaurant;

@DataJpaTest
@Transactional
class ReservationRepositoryTest {

	Logger log = LoggerFactory.getLogger(ReservationRepositoryTest.class);

	private final Member customer = new Member(
		"customer",
		"김환이",
		"01012342345",
		"asdfg123!",
		MemberType.CUSTOMER
	);

	private final Member owner = new Member(
		"owner",
		"김환이",
		"01012342345",
		"asdfg123!",
		MemberType.OWNER
	);

	private final Restaurant restaurant = new Restaurant(
		owner,
		FoodType.KOREAN,
		"유명 레스토랑",
		6,
		LocalTime.of(11, 0),
		LocalTime.of(21, 0),
		"서울특별시 강남구 어딘가로 123 무슨빌딩 1층",
		"우리는 유명한 한식당입니다.",
		"0211112222",
		List.of(new Menu("맛있는 밥", BigInteger.valueOf(10000), "맛있어용")),
		List.of(new ClosingDay(DayOfWeek.MONDAY))
	);

	@Autowired
	private MemberRepository memberRepository;

	@Autowired
	private RestaurantRepository restaurantRepository;

	@Autowired
	private ReservationRepository reservationRepository;

	@BeforeEach
	void initMembersAndRestaurant() {
		memberRepository.saveAll(List.of(customer, owner));
		restaurantRepository.save(restaurant);
	}

	@Test
	@DisplayName("예약 날짜와 예약 상태들을 통해 예약 시간 별 총 예약 인원수를 조회할 수 있습니다.")
	void find_visitor_counts_per_visit_time() {
		// given
		LocalDateTime visitTomorrow = LocalDateTime.now()
			.plusDays(1L)
			.truncatedTo(ChronoUnit.HOURS);
		ReservationCustomerInput tomorrowCustomerInput1 = new ReservationCustomerInput(
			visitTomorrow,
			6,
			"메모메모"
		);
		ReservationCustomerInput tomorrowCustomerInput2 = new ReservationCustomerInput(
			visitTomorrow,
			4,
			"메모메모"
		);
		saveReservation(
			customer,
			restaurant,
			ReservationStatus.CONFIRMED,
			tomorrowCustomerInput1
		);
		saveReservation(
			customer,
			restaurant,
			ReservationStatus.CONFIRMED,
			tomorrowCustomerInput2
		);

		LocalDateTime visitToday = LocalDateTime.now()
			.plusHours(1L)
			.truncatedTo(ChronoUnit.HOURS);
		ReservationCustomerInput todayCustomerInput = new ReservationCustomerInput(
			visitToday,
			5,
			"메모메모"
		);
		saveReservation(
			customer,
			restaurant,
			ReservationStatus.CONFIRMED,
			todayCustomerInput
		);

		// when
		List<VisitorCountPerVisitTimeProj> visitorCountPerVisitTime = reservationRepository
			.findVisitorCountPerVisitTime(
				restaurant,
				visitToday.toLocalDate(),
				List.of(ReservationStatus.PENDING, ReservationStatus.CONFIRMED)
			);
		visitorCountPerVisitTime.forEach(
			v -> log.info("visitTime at {} - totalVisitCount is {}", v.visitTime(), v.totalVisitorCount()));

		// then
		Long actualTotalVisitorCount = visitorCountPerVisitTime.get(0)
			.totalVisitorCount();
		assertThat(actualTotalVisitorCount).isEqualTo(5);
	}

	@Test
	@DisplayName("예약의 방문 날짜와 시간을 통해 그 시간대의 총 방문 예정 인원수를 조회할 수 있다.")
	void countTotalVisitorCount() {
		// given
		LocalDateTime visitDateTime = LocalDateTime.now()
			.plusHours(1L)
			.truncatedTo(ChronoUnit.HOURS);
		ReservationCustomerInput customerInput = new ReservationCustomerInput(
			visitDateTime, 2, "메모메모"
		);
		saveReservation(
			customer,
			restaurant,
			ReservationStatus.CONFIRMED,
			customerInput
		);

		// when
		Optional<Integer> currentReservedCount = reservationRepository.countTotalVisitorCount(restaurant,
			visitDateTime.toLocalDate(),
			visitDateTime.toLocalTime(),
			List.of(ReservationStatus.CONFIRMED, ReservationStatus.PENDING));

		// then
		assertThat(currentReservedCount).contains(2);
	}

	private void saveReservation(
		Member consumer,
		Restaurant savedRestaurant,
		ReservationStatus status,
		ReservationCustomerInput customerInput
	) {

		reservationRepository.save(new Reservation(
			consumer,
			savedRestaurant,
			status,
			customerInput
		));
	}

	private Member createOwner() {
		return memberRepository.save(new Member(
			"dlxortmd123",
			"이택승",
			"01012341234",
			"qwer1234!",
			MemberType.OWNER
		));
	}
}
