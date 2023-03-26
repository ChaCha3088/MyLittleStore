package site.mylittlestore.service;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.jdbc.Sql;
import site.mylittlestore.domain.Address;
import site.mylittlestore.dto.item.ItemCreationDto;
import site.mylittlestore.dto.member.MemberCreationDto;
import site.mylittlestore.dto.store.StoreDto;
import site.mylittlestore.dto.store.StoreUpdateDto;
import site.mylittlestore.dto.storetable.StoreTableFindDto;
import site.mylittlestore.dto.storetable.StoreTableFindDtoWithOrderFindDto;
import site.mylittlestore.enumstorage.status.StoreTableStatus;

import javax.persistence.EntityManager;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Sql(scripts = {"classpath:sql/test.sql"}, executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
public class StoreTableServiceTest {

    @Autowired
    private MemberService memberService;

    @Autowired
    private StoreTableService storeTableService;

    @Autowired
    private StoreService storeService;

    @Autowired
    private OrderService orderService;

    @Autowired
    private EntityManager em;

    private Long memberTestId;
    private Long storeTestId;
    private Long itemTestId;
    private Long storeTableTestId;

    @BeforeAll
    void setUp() {
        Long newMemberId = memberService.joinMember(MemberCreationDto.builder()
                .name("memberTest")
                .email("memberTest@gmail.com")
                .password("password")
                .address(Address.builder()
                        .city("city")
                        .street("street")
                        .zipcode("zipcode")
                        .build())
                .build());

        Long newStoreId = memberService.createStore(StoreDto.builder()
                .memberId(newMemberId)
                .name("storeTest")
                .address(Address.builder()
                        .city("city")
                        .street("street")
                        .zipcode("zipcode")
                        .build())
                .build());

        Long newItemId = storeService.createItem(ItemCreationDto.builder()
                .storeId(newStoreId)
                .name("itemTest")
                .price(10000)
                .stock(100)
                .build());

        //가게 열기
        memberService.changeStoreStatus(StoreUpdateDto.builder()
                .id(newStoreId)
                .memberId(newMemberId)
                .build());

        //테이블 생성
        Long newStoreTableId = storeTableService.createStoreTable(newStoreId);

        //주문 생성
        Long newOrderId = orderService.createOrder(newStoreId, newStoreTableId);

        memberTestId = newMemberId;
        storeTestId = newStoreId;
        itemTestId = newItemId;
        storeTableTestId = newStoreTableId;
    }

    @Test
    @DisplayName("테이블 조회")
    void findStoreTableFindDtoById() {
        //when
        StoreTableFindDto storeTableFindDtoById = storeTableService.findStoreTableFindDtoById(storeTableTestId);

        //then
        assertThat(storeTableFindDtoById.getStoreTableStatus()).isEqualTo(StoreTableStatus.USING.toString());
    }

    @Test
    @DisplayName("주문과 함께 테이블 조회")
    void findStoreTableFindDtoWithOrderFindDtoByStoreId() {
        //given
        //테이블 생성
        Long createdStoreTableId = storeTableService.createStoreTable(storeTestId);

        //주문 생성
        Long createdOrderId = orderService.createOrder(storeTestId, createdStoreTableId);

        //when
        StoreTableFindDtoWithOrderFindDto storeTableFindDtoWithOrderFindDtoByStoreId = storeTableService.findStoreTableFindDtoWithOrderFindDtoByStoreId(storeTableTestId, storeTestId);

        //then
        assertThat(storeTableFindDtoWithOrderFindDtoByStoreId.getStoreTableStatus()).isEqualTo(StoreTableStatus.USING.toString());
    }

    @Test
    @DisplayName("주문과 함께 테이블 전체 조회")
    void findAllStoreTableFindDtoWithOrderFindDtoByStoreId() {
        //given
        //테이블 여러개 생성
        Long createdStoreTable1 = storeTableService.createStoreTable(storeTestId);

        Long createdStoreTable2 = storeTableService.createStoreTable(storeTestId);

        //주문 생성
        Long createdOrderId1 = orderService.createOrder(storeTestId, createdStoreTable1);
        Long createdOrderId2 = orderService.createOrder(storeTestId, createdStoreTable2);

        //영속성 컨텍스트 초기화
        em.flush();
        em.clear();

        //when
        List<StoreTableFindDtoWithOrderFindDto> allStoreTableFindDtoWithOrderFindDtoByStoreId = storeTableService.findAllStoreTableFindDtoWithOrderFindDtoByStoreId(storeTestId);

        //then
        assertThat(allStoreTableFindDtoWithOrderFindDtoByStoreId.size()).isEqualTo(3);

//        //when
//        List<StoreTableFindDto> allStoreTableFindDtoByStoreId = storeTableService.findAllStoreTableFindDtoByStoreId(storeTestId);

//        //then
//        assertThat(allStoreTableFindDtoByStoreId.size()).isEqualTo(3);
    }

    @Test
    @DisplayName("테이블 생성")
    void createStoreTable() {
        //given
        storeTableService.createStoreTable(storeTestId);
        storeTableService.createStoreTable(storeTestId);
        storeTableService.createStoreTable(storeTestId);

        //영속성 컨텍스트 초기화
        em.flush();
        em.clear();

        //when
        List<StoreTableFindDto> allStoreTableFindDtoByStoreId = storeTableService.findAllStoreTableFindDtoByStoreId(storeTestId);

        //then
        assertThat(allStoreTableFindDtoByStoreId.size()).isEqualTo(4);
    }
}
