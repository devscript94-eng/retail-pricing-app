package com.retail.pricing.infrastructure.adapters.inbound.web.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

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

    @Test
    void test1_shouldReturnPriceList1At2020_06_14_10_00() throws Exception {
        mockMvc.perform(get(APPLICABLE_PRICE_URI, 35455, 1)
                        .param("applicationDate", "2020-06-14T10:00:00"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.productId").value(35455))
                .andExpect(jsonPath("$.brandId").value(1))
                .andExpect(jsonPath("$.priceList").value(1))
                .andExpect(jsonPath("$.startDate").value("2020-06-14T00:00:00"))
                .andExpect(jsonPath("$.endDate").value("2020-12-31T23:59:59"))
                .andExpect(jsonPath("$.finalPrice").value(35.50))
                .andExpect(jsonPath("$.currency").value("EUR"));
    }

    @Test
    void test2_shouldReturnPriceList2At2020_06_14_16_00() throws Exception {
        mockMvc.perform(get(APPLICABLE_PRICE_URI, 35455, 1)
                        .param("applicationDate", "2020-06-14T16:00:00"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.productId").value(35455))
                .andExpect(jsonPath("$.brandId").value(1))
                .andExpect(jsonPath("$.priceList").value(2))
                .andExpect(jsonPath("$.startDate").value("2020-06-14T15:00:00"))
                .andExpect(jsonPath("$.endDate").value("2020-06-14T18:30:00"))
                .andExpect(jsonPath("$.finalPrice").value(25.45))
                .andExpect(jsonPath("$.currency").value("EUR"));
    }

    @Test
    void test3_shouldReturnPriceList1At2020_06_14_21_00() throws Exception {
        mockMvc.perform(get(APPLICABLE_PRICE_URI, 35455, 1)
                        .param("applicationDate", "2020-06-14T21:00:00"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.productId").value(35455))
                .andExpect(jsonPath("$.brandId").value(1))
                .andExpect(jsonPath("$.priceList").value(1))
                .andExpect(jsonPath("$.startDate").value("2020-06-14T00:00:00"))
                .andExpect(jsonPath("$.endDate").value("2020-12-31T23:59:59"))
                .andExpect(jsonPath("$.finalPrice").value(35.50))
                .andExpect(jsonPath("$.currency").value("EUR"));
    }

    @Test
    void test4_shouldReturnPriceList3At2020_06_15_10_00() throws Exception {
        mockMvc.perform(get(APPLICABLE_PRICE_URI, 35455, 1)
                        .param("applicationDate", "2020-06-15T10:00:00"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.productId").value(35455))
                .andExpect(jsonPath("$.brandId").value(1))
                .andExpect(jsonPath("$.priceList").value(3))
                .andExpect(jsonPath("$.startDate").value("2020-06-15T00:00:00"))
                .andExpect(jsonPath("$.endDate").value("2020-06-15T11:00:00"))
                .andExpect(jsonPath("$.finalPrice").value(30.50))
                .andExpect(jsonPath("$.currency").value("EUR"));
    }

    @Test
    void test5_shouldReturnPriceList4At2020_06_16_21_00() throws Exception {
        mockMvc.perform(get(APPLICABLE_PRICE_URI, 35455, 1)
                        .param("applicationDate", "2020-06-16T21:00:00"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.productId").value(35455))
                .andExpect(jsonPath("$.brandId").value(1))
                .andExpect(jsonPath("$.priceList").value(4))
                .andExpect(jsonPath("$.startDate").value("2020-06-15T16:00:00"))
                .andExpect(jsonPath("$.endDate").value("2020-12-31T23:59:59"))
                .andExpect(jsonPath("$.finalPrice").value(38.95))
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
}