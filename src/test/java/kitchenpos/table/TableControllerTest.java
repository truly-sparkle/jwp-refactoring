package kitchenpos.table;

import com.fasterxml.jackson.core.type.TypeReference;
import kitchenpos.common.BaseContollerTest;
import kitchenpos.dao.OrderTableDao;
import kitchenpos.domain.OrderTable;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MvcResult;

import java.util.ArrayList;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class TableControllerTest extends BaseContollerTest {

    @Autowired
    private OrderTableDao orderTableDao;

    @Test
    @DisplayName("새로운 테이블을 등록합니다.")
    void createTable() throws Exception {
        테이블_신규_등록_요청();
    }

    @Test
    @DisplayName("모든 테이블을 조회합니다.")
    void findAllTables() throws Exception {
        MvcResult mvcResult = this.mockMvc.perform(get("/api/tables")
                .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath(".id").exists())
                .andReturn();

        String responseOrderTables = mvcResult.getResponse().getContentAsString();
        ArrayList<OrderTable> orderTables
                = this.objectMapper.readValue(responseOrderTables, new TypeReference<ArrayList<OrderTable>>() {});

        assertThat(orderTables).hasSize(9);
    }


    @Test
    @DisplayName("테이블을 비웁니다.")
    void changeEmpty() throws Exception {
        OrderTable orderTable = this.orderTableDao.findAll().stream().findFirst().get();
        String uri = "/api/tables/" + orderTable.getId() + "/empty";
        orderTable.setEmpty(true);

        this.mockMvc.perform(put(uri)
                .contentType(MediaType.APPLICATION_JSON)
                .content(this.objectMapper.writeValueAsString(orderTable))
                )
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("empty").value(true))
                .andReturn();
    }


    @Test
    @DisplayName("테이블의 손님 수를 변경합니다.")
    void changeNumberOfGuests() throws Exception {
        테이블_신규_등록_요청();

        OrderTable orderTable = this.orderTableDao.findAll().stream()
                .filter(orderTable1 -> !orderTable1.isEmpty()).findFirst().get();
        String uri = "/api/tables/" + orderTable.getId() + "/number-of-guests";
        orderTable.setNumberOfGuests(4);

        MvcResult mvcResult = this.mockMvc.perform(put(uri)
                .contentType(MediaType.APPLICATION_JSON)
                .content(this.objectMapper.writeValueAsString(orderTable))
                )
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn();
    }

    private Long 테이블_신규_등록_요청() throws Exception {
        OrderTable orderTable = new OrderTable();
        orderTable.setNumberOfGuests(0);
        orderTable.setEmpty(false);

        MvcResult mvcResult = this.mockMvc.perform(post("/api/tables")
                .contentType(MediaType.APPLICATION_JSON)
                .content(this.objectMapper.writeValueAsString(orderTable))
        )
                .andDo(print())
                .andExpect(status().isCreated())
                .andReturn()
                ;

        String responseOrderTables = mvcResult.getResponse().getContentAsString();
        return this.objectMapper.readValue(responseOrderTables, OrderTable.class).getId();
    }
}
