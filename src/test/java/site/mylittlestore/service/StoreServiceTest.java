package site.mylittlestore.service;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.transaction.annotation.Transactional;
import site.mylittlestore.domain.Address;
import site.mylittlestore.dto.item.ItemCreationDto;
import site.mylittlestore.dto.item.ItemFindDto;
import site.mylittlestore.dto.item.ItemUpdateDto;
import site.mylittlestore.dto.member.MemberCreationDto;
import site.mylittlestore.dto.store.StoreDto;
import site.mylittlestore.dto.order.OrderDto;
import site.mylittlestore.enumstorage.errormessage.StoreErrorMessage;
import site.mylittlestore.exception.store.NoSuchStoreException;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;

@SpringBootTest
@Transactional
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
class StoreServiceTest {
    @Autowired
    private ItemService itemService;

    @Autowired
    private MemberService memberService;

    @Autowired
    private StoreService storeService;

    @PersistenceContext
    EntityManager em;

    private Long memberTestId;
    private Long storeTestId;
    private Long itemTestId;

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

        memberTestId = newMemberId;
        storeTestId = newStoreId;
        itemTestId = newItemId;
    }

    @Test
    void findStoreDtoById() {
        //when
        StoreDto findStoreById = storeService.findStoreDtoById(storeTestId);

        //then
        assertThat(findStoreById.getName()).isEqualTo("storeTest");
    }

    @Test
    void findStoreByIdException() {
        //then
        assertThatThrownBy(() -> storeService.findStoreDtoById(123456789L))
                .isInstanceOf(NoSuchStoreException.class)
                .hasMessageContaining(StoreErrorMessage.NO_SUCH_STORE.getMessage());

    }

    @Test
    public void findAllStoreByMemberId() {
        //given
        Long savedStoreId = memberService.createStore(StoreDto.builder()
                .memberId(memberTestId)
                .name("newStoreTest")
                .address(Address.builder()
                        .city("city")
                        .street("street")
                        .zipcode("zipcode")
                        .build())
                .build());

        //영속성 컨텍스트 초기화
        em.flush();
        em.clear();

        //when
        List<StoreDto> findAllStoreByMemberId = storeService.findAllStoreDtoByMemberId(memberTestId);

        //then
        assertThat(findAllStoreByMemberId.size()).isEqualTo(2);
        assertThat(findAllStoreByMemberId.stream()
                .filter(s -> s.getId().equals(savedStoreId))
                .findFirst()
                .get()).isNotNull();
    }

    @Test
    public void createItem() {
        //given
        Long newItemTestId = storeService.createItem(ItemCreationDto.builder()
                .storeId(storeTestId)
                .name("newItemTest")
                .price(9999)
                .stock(99)
                .build());

        //영속성 컨텍스트 초기화
        em.flush();
        em.clear();

        //when
        Long findNewItemTestId = itemService.findItemDtoById(newItemTestId).getId();

        //then
        assertThat(findNewItemTestId).isEqualTo(newItemTestId);
    }

    @Test
    public void updateItem() {
        //when
        storeService.updateItem(ItemUpdateDto.builder()
                        .id(itemTestId)
                        .storeId(storeTestId)
                        .newItemName("newItemTest")
                        .newPrice(9999)
                        .newStock(99)
                .build());

        //영속성 컨텍스트 초기화
        em.flush();
        em.clear();

        //then
        //아이템을 업데이트하면 store에서 item을 찾았을 때, 업데이트된 아이템이 나와야 한다.
//        Long findItemId = storeService.findStoreDtoById(storeTestId).getItems().stream()
        Long findItemId = storeService.findStoreDtoById(storeTestId).getItems().stream()
                .filter(i -> i.getId().equals(itemTestId))
                .findFirst()
                .get().getId();
//                .orElseThrow(() -> new NoSuchItemException(ItemErrorMessageEnum.NO_SUCH_ITEM.getMessage()));
        ItemFindDto findItemFindDtoById = itemService.findItemDtoById(findItemId);
        assertThat(findItemFindDtoById.getName()).isEqualTo("newItemTest");
        assertThat(findItemFindDtoById.getPrice()).isEqualTo(9999);
        assertThat(findItemFindDtoById.getStock()).isEqualTo(99);
    }

    @Test
    public void updateItemPartially(){
        //when
        storeService.updateItem(ItemUpdateDto.builder()
                .id(itemTestId)
                .storeId(storeTestId)
                .newItemName("itemTest")
                .newPrice(9999)
                .newStock(99)
                .build());

        //영속성 컨텍스트 초기화
        em.flush();
        em.clear();

        //then
        ItemFindDto findItemFindDtoById = itemService.findItemDtoById(itemTestId);
        assertThat(findItemFindDtoById.getName()).isEqualTo("itemTest");
        assertThat(findItemFindDtoById.getPrice()).isEqualTo(9999);
        assertThat(findItemFindDtoById.getStock()).isEqualTo(99);
    }

    @Test
    public void createOrder(){
        //given
        storeService.createStoreTable(OrderDto.builder()
                        .storeId(storeTestId)
                        .build());
        storeService.createStoreTable(OrderDto.builder()
                .storeId(storeTestId)
                .build());
        storeService.createStoreTable(OrderDto.builder()
                .storeId(storeTestId)
                .build());

        //영속성 컨텍스트 초기화
        em.flush();
        em.clear();

        //when
        StoreDto findStoreById = storeService.findStoreDtoById(storeTestId);

        //then
        assertThat(findStoreById.getStoreTables().size()).isEqualTo(3);
    }
}