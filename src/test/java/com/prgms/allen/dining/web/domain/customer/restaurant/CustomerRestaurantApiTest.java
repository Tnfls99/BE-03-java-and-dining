package com.prgms.allen.dining.web.domain.customer.restaurant;

import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.*;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.math.BigInteger;
import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import com.prgms.allen.dining.domain.member.MemberRepository;
import com.prgms.allen.dining.domain.member.dto.MemberSignupRequest;
import com.prgms.allen.dining.domain.member.entity.Member;
import com.prgms.allen.dining.domain.member.entity.MemberType;
import com.prgms.allen.dining.domain.restaurant.RestaurantRepository;
import com.prgms.allen.dining.domain.restaurant.entity.ClosingDay;
import com.prgms.allen.dining.domain.restaurant.entity.FoodType;
import com.prgms.allen.dining.domain.restaurant.entity.Menu;
import com.prgms.allen.dining.domain.restaurant.entity.Restaurant;

@AutoConfigureRestDocs
@AutoConfigureMockMvc
@Transactional
@SpringBootTest
class CustomerRestaurantApiTest {

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private MemberRepository memberRepository;

	@Autowired
	private RestaurantRepository restaurantRepository;

	@Test
	@DisplayName("고객은 하나의 식당의 상세 정보를 조회할 수 있다.")
	void testGetMyRestaurant() throws Exception {

		Member owner = createOwner();
		Restaurant restaurant = createRestaurant(owner);

		mockMvc.perform(
				get("/customer/api/restaurants/{restaurantId}", restaurant.getId()))
			.andExpect(status().isOk())
			.andDo(print())
			.andDo(document("customer-get-one-restaurant",
				responseFields(
					fieldWithPath("foodType").type(JsonFieldType.STRING).description("food type"),
					fieldWithPath("name").type(JsonFieldType.STRING).description("restaurant name"),
					fieldWithPath("openTime").type(JsonFieldType.STRING).description("restaurant open time"),
					fieldWithPath("lastOrderTime").type(JsonFieldType.STRING).description("restaurant last order time"),
					fieldWithPath("location").type(JsonFieldType.STRING).description("restaurant location"),
					fieldWithPath("description").type(JsonFieldType.STRING).description("restaurant description"),
					fieldWithPath("phone").type(JsonFieldType.STRING).description("restaurant phone"),
					fieldWithPath("menuList").type(JsonFieldType.ARRAY).optional().description("menu list"),
					fieldWithPath("menuList[].name").type(JsonFieldType.STRING).optional().description("menu name"),
					fieldWithPath("menuList[].price").type(JsonFieldType.NUMBER).optional().description("menu price"),
					fieldWithPath("closingDays").type(JsonFieldType.ARRAY).optional().description("closing days"),
					fieldWithPath("closingDays[].dayOfWeek").optional().type(JsonFieldType.STRING)
						.description("closing day of week")
				))
			);
	}

	private Member createOwner() {
		String nickName = "이세상에제일가는짱구";
		MemberSignupRequest memberSignupRequest =
			new MemberSignupRequest(
				nickName,
				"짱구",
				"01011112222",
				"1q2w3e4r!",
				MemberType.OWNER);

		return memberRepository.save(memberSignupRequest.toEntity());
	}

	private Restaurant createRestaurant(Member owner) {
		List<ClosingDay> closingDays = List.of(new ClosingDay(DayOfWeek.MONDAY));
		List<Menu> menu = List.of(new Menu("맛있는 밥", BigInteger.valueOf(10000), "맛있어용"));

		Restaurant restaurant = new Restaurant(
			owner,
			FoodType.WESTERN,
			"유명한 레스토랑",
			30,
			LocalTime.of(12, 0),
			LocalTime.of(20, 0),
			"서울특별시 어딘구 어딘가로 222",
			"BTS가 다녀간 유명한 레스토랑",
			"023334444",
			menu,
			closingDays
		);
		return restaurantRepository.save(restaurant);
	}
}