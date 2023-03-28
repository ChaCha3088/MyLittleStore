package site.mylittlestore.repository;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.jdbc.Sql;
import site.mylittlestore.repository.orderitem.OrderItemRepository;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Sql(scripts = {"classpath:sql/test.sql"}, executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
public class OrderItemRepositoryTest {
    @Autowired
    private OrderItemRepository orderItemRepository;

    @Test
    @DisplayName("주문 Id와 상품 Id, 가격이 같은 주문 상품을 찾는다.")
    void findByOrderIdAndItemIdAndPrice() {
        //given
        //주문 Id1에 상품1, 가격1 주문 상품 만들기
        //주문 Id1에 상품2, 가격1 주문 상품 만들기
        //주문 Id1에 상품1, 가격2 주문 상품 만들기
        //주문 Id2에 상품1, 가격1 주문 상품 만들기

        //주문 Id1에 상품1, 가격1 주문 상품이 잘 찾아지는지 확인

        //when


        //then

        assertThat(1).isEqualTo(2);
    }

    @Test
    @DisplayName("주문 Id와 주문 상품 Id, 상품 Id, 가격이 같은 주문 상품을 찾는다.")
    void findByOrderIdOrderItemIdAndItemIdAndPrice() {
        //given
        //주문 Id1에 상품1, 가격1 주문 상품 만들기
        //주문 Id1에 상품2, 가격1 주문 상품 만들기
        //주문 Id1에 상품1, 가격2 주문 상품 만들기
        //주문 Id2에 상품1, 가격1 주문 상품 만들기

        //주문 Id1에 상품1, 가격1 주문 상품이 잘 찾아지는지 확인

        //when


        //then

        assertThat(1).isEqualTo(2);
    }
}
