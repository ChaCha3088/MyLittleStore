package site.mylittlestore.repository;

import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.transaction.annotation.Transactional;
import site.mylittlestore.domain.Address;
import site.mylittlestore.domain.Order;
import site.mylittlestore.dto.item.ItemCreationDto;
import site.mylittlestore.dto.member.MemberCreationDto;
import site.mylittlestore.dto.store.StoreDto;
import site.mylittlestore.dto.store.StoreTableCreationDto;
import site.mylittlestore.enumstorage.errormessage.OrderErrorMessage;
import site.mylittlestore.exception.store.NoSuchOrderException;
import site.mylittlestore.repository.item.ItemRepository;
import site.mylittlestore.repository.member.MemberRepository;
import site.mylittlestore.repository.store.StoreRepository;
import site.mylittlestore.repository.order.OrderRepository;
import site.mylittlestore.service.MemberService;
import site.mylittlestore.service.StoreService;
import site.mylittlestore.service.StoreTableService;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Sql(scripts = {"classpath:sql/test.sql"}, executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
class OrderRepositoryTest {
    @Autowired
    MemberRepository memberRepository;
    @Autowired
    StoreRepository storeRepository;
    @Autowired
    StoreTableService storeTableService;
    @Autowired
    ItemRepository itemRepository;
    @Autowired
    OrderRepository orderRepository;
    @Autowired
    MemberService memberService;
    @Autowired
    StoreService storeService;

    @PersistenceContext
    EntityManager em;

    private Long memberTestId;
    private Long storeTestId;
    private Long itemTestId;
    private Long orderTestId;

    @BeforeEach
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

        Long newOrderId = storeTableService.createStoreTable(newStoreId);

        memberTestId = newMemberId;
        storeTestId = newStoreId;
        itemTestId = newItemId;
        orderTestId = newOrderId;
    }

    @Test
    void findOrderById() {
        //조회
        Optional<Order> orderById = orderRepository.findById(orderTestId);

        //검증
        assertThat(orderById.orElseThrow(()
                -> new NoSuchOrderException(OrderErrorMessage.NO_SUCH_ORDER.getMessage())).getId())
                .isEqualTo(orderTestId);
    }
    
    @Test
    @DisplayName("storeId로 USING인 모든 주문 조회")
    void findAllUsingByStoreId() {
        //given
        
        
        //when
        
        
        //then
        
        assertThat(1).isEqualTo(2);
    }
}