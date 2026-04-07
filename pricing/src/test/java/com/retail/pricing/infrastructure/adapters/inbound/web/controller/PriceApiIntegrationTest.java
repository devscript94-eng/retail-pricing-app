package com.retail.pricing.infrastructure.adapters.inbound.web.controller;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.web.servlet.MockMvc;

import java.util.stream.Stream;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(
        webEnvironment = SpringBootTest.WebEnvironment.MOCK,
        properties = {
                "spring.jpa.hibernate.ddl-auto=none",
                "spring.jpa.generate-ddl=false",
                "spring.sql.init.mode=always"
        }
)
@AutoConfigureMockMvc
class PriceApiIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    private static final String APPLICABLE_PRICE_URI = "/api/v1/products/{productId}/brands/{brandId}/prices/applicable";

    @ParameterizedTest(name = "[{index}] applicationDate={0}, expectedPriceList={1}")
    @MethodSource("applicablePriceCases")
    void shouldReturnApplicablePrice(
            String applicationDate,
            int expectedPriceList,
            String expectedStartDate,
            String expectedEndDate,
            double expectedFinalPrice
    ) throws Exception {
        mockMvc.perform(get(APPLICABLE_PRICE_URI, 35455, 1)
                        .param("applicationDate", applicationDate))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.productId").value(35455))
                .andExpect(jsonPath("$.brandId").value(1))
                .andExpect(jsonPath("$.priceList").value(expectedPriceList))
                .andExpect(jsonPath("$.startDate").value(expectedStartDate))
                .andExpect(jsonPath("$.endDate").value(expectedEndDate))
                .andExpect(jsonPath("$.finalPrice").value(expectedFinalPrice))
                .andExpect(jsonPath("$.currency").value("EUR"));
    }

    @Test
    void shouldReturn404WhenNoApplicablePriceExists() throws Exception {
        mockMvc.perform(get(APPLICABLE_PRICE_URI, 99999, 1)
                        .param("applicationDate", "2020-06-14T10:00:00"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.title").value("Applicable price not found"))
                .andExpect(jsonPath("$.status").value(404));
    }

    private static Stream<Arguments> applicablePriceCases() {
        return Stream.of(
                Arguments.of("2020-06-14T10:00:00", 1, "2020-06-14T00:00:00", "2020-12-31T23:59:59", 35.50),
                Arguments.of("2020-06-14T16:00:00", 2, "2020-06-14T15:00:00", "2020-06-14T18:30:00", 25.45),
                Arguments.of("2020-06-14T21:00:00", 1, "2020-06-14T00:00:00", "2020-12-31T23:59:59", 35.50),
                Arguments.of("2020-06-15T10:00:00", 3, "2020-06-15T00:00:00", "2020-06-15T11:00:00", 30.50),
                Arguments.of("2020-06-16T21:00:00", 4, "2020-06-15T16:00:00", "2020-12-31T23:59:59", 38.95)
        );
    }
}
