package site.mylittlestore.repository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.jdbc.Sql;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Sql(scripts = {"classpath:sql/test.sql"}, executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
public class StoreTableRepositoryTest {
    @BeforeEach
    void setUp() {

    }

    @Test
    @DisplayName("storeTableId로 storeTableStatus가 DELETED가 아닌 storeTable을 찾는다.")
    void findNotDeletedById() {
        //given


        //when


        //then

        assertThat(1).isEqualTo(2);
    }

    @Test
    @DisplayName("storeTableId와 storeId로 StoreTableStatus가 DELETED가 아닌 storeTable을 찾는다.")
    void findNotDeletedByIdAndStoreId() {
        //given


        //when


        //then

        assertThat(1).isEqualTo(2);
    }
}
