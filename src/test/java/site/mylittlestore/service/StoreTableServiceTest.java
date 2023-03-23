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
import site.mylittlestore.dto.member.MemberCreationDto;
import site.mylittlestore.dto.store.StoreDto;
import site.mylittlestore.dto.store.StoreTableCreationDto;

import javax.persistence.EntityManager;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Transactional
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
    private EntityManager em;

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
    void createStoreTable(){
        //given
        storeTableService.createStoreTable(StoreTableCreationDto.builder()
                .storeId(storeTestId)
                .build());
        storeTableService.createStoreTable(StoreTableCreationDto.builder()
                .storeId(storeTestId)
                .build());
        storeTableService.createStoreTable(StoreTableCreationDto.builder()
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
