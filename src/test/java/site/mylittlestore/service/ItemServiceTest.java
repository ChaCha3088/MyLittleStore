package site.mylittlestore.service;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.transaction.annotation.Transactional;
import site.mylittlestore.domain.Address;
import site.mylittlestore.domain.item.Item;
import site.mylittlestore.dto.item.ItemCreationDto;
import site.mylittlestore.dto.item.ItemFindDto;
import site.mylittlestore.dto.member.MemberCreationDto;
import site.mylittlestore.dto.store.StoreDto;
import site.mylittlestore.enumstorage.errormessage.ItemErrorMessage;
import site.mylittlestore.enumstorage.status.ItemStatus;
import site.mylittlestore.exception.item.NoSuchItemException;
import site.mylittlestore.repository.item.ItemRepository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
@Sql(scripts = {"classpath:sql/test.sql"}, executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
class ItemServiceTest {

    @Autowired
    private ItemRepository itemRepository;

    @Autowired
    private ItemService itemService;

    @Autowired
    private MemberService memberService;

    @Autowired
    private StoreService storeService;

    @PersistenceContext
    EntityManager em;

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

        storeTestId = newStoreId;
        itemTestId = newItemId;
    }

    /**
     * Id로 아이템을 찾는다.
     */
    @Test
    void findItemById() {
        //when
        ItemFindDto itemFindDto = itemService.findItemDtoById(itemTestId);

        System.out.println("itemDto = " + itemFindDto);

        //then
        assertThat(itemFindDto.getName()).isEqualTo("itemTest");
    }

    /**
     * 아이템이 없으면 예외가 발생한다.
     */
    @Test
    void findItemByIdException() {
        assertThatThrownBy(() -> {
            itemService.findItemDtoById(1234L);
        }).isInstanceOf(NoSuchItemException.class)
                .hasMessageContaining(ItemErrorMessage.NO_SUCH_ITEM.getMessage());
    }

    @Test
    void findAllByStoreId() {
        //given
        Long newItemId = storeService.createItem(ItemCreationDto.builder()
                .storeId(storeTestId)
                .name("newItemTest")
                .price(9999)
                .stock(99)
                .build());

        //when
        List<ItemFindDto> findAllByStoreId = itemService.findAllItemDtoByStoreId(storeTestId);

        //then
        assertThat(findAllByStoreId.size()).isEqualTo(2);
        findAllByStoreId.forEach(itemDto -> {
            assertThat(itemDto.getStoreId()).isEqualTo(storeTestId);
        });
    }

    @Test
    void deleteItemById() {
        //when
        itemService.deleteItemById(itemTestId);

        //영속성 컨텍스트 초기화
        em.flush();
        em.clear();

        //then
        assertThatThrownBy(() -> itemService.findItemDtoById(itemTestId))
                .isInstanceOf(NoSuchItemException.class)
                .hasMessageContaining(ItemErrorMessage.NO_SUCH_ITEM.getMessage());

        Optional<Item> findById = itemRepository.findById(itemTestId);

        assertThat(findById.get().getItemStatus()).isEqualTo(ItemStatus.DELETED);
    }
}