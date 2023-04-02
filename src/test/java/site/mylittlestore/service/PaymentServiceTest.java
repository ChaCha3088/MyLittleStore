package site.mylittlestore.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.jdbc.Sql;

import static org.assertj.core.api.Assertions.*;

@SpringBootTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@Sql(scripts = {"classpath:sql/test.sql"}, executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
public class PaymentServiceTest {
    @BeforeEach
    void setUp() {
    }

    @Test
    @DisplayName("결제 시작")
    void startPayment() {
        //given


        //when


        //then

        assertThat(1).isEqualTo(2);
    }

    @Test
    @DisplayName("결제 확정")
    void confirmPayment() {
        //given


        //when


        //then

        assertThat(1).isEqualTo(2);
    }
}
