package com.verne.reservation.api.system;

import org.junit.jupiter.api.Test;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * システム状態APIのHTTPレスポンスを検証します。
 */
class SystemControllerTest {

    /**
     * 状態APIが200と稼働情報を返すことを確認します。
     *
     * @throws Exception HTTPリクエスト実行時の例外
     */
    @Test
    void returnsApiStatus() throws Exception {
        MockMvc mockMvc = MockMvcBuilders
                .standaloneSetup(new SystemController())
                .build();

        mockMvc.perform(get("/api/v1/status"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.service").value("reservation-api"))
                .andExpect(jsonPath("$.status").value("UP"))
                .andExpect(jsonPath("$.timestamp").exists());
    }
}
