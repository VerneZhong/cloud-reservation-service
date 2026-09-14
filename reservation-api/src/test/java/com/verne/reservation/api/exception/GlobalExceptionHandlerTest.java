package com.verne.reservation.api.exception;

import com.verne.reservation.domain.exception.InsufficientInventoryException;
import org.junit.jupiter.api.Test;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * 例外が共通エラーレスポンスへ変換されることを検証します。
 */
class GlobalExceptionHandlerTest {

    /**
     * 在庫不足が409レスポンスへ変換されることを確認します。
     *
     * @throws Exception HTTPリクエスト実行時の例外
     */
    @Test
    void convertsInsufficientInventoryToConflictResponse() throws Exception {
        MockMvc mockMvc = MockMvcBuilders
                .standaloneSetup(new TestController())
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();

        mockMvc.perform(get("/test/insufficient-inventory"))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.status").value(409))
                .andExpect(jsonPath("$.code").value("INSUFFICIENT_INVENTORY"))
                .andExpect(jsonPath("$.message").value(
                        "Insufficient inventory: requested=2, available=1"
                ))
                .andExpect(jsonPath("$.path").value("/test/insufficient-inventory"))
                .andExpect(jsonPath("$.violations").isArray());
    }

    /**
     * テスト用に例外を発生させるコントローラーです。
     */
    @RestController
    private static class TestController {

        /**
         * 在庫不足例外を発生させます。
         */
        @GetMapping("/test/insufficient-inventory")
        void throwInsufficientInventory() {
            throw new InsufficientInventoryException(2, 1);
        }
    }
}
