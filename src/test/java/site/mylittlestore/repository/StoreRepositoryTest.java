package site.mylittlestore.repository;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.transaction.annotation.Transactional;
import site.mylittlestore.domain.Address;
import site.mylittlestore.domain.Store;
import site.mylittlestore.dto.member.MemberCreationDto;
import site.mylittlestore.dto.store.StoreDto;
import site.mylittlestore.repository.member.MemberRepository;
import site.mylittlestore.repository.store.StoreRepository;
import site.mylittlestore.service.MemberService;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Transactional
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
class StoreRepositoryTest {

    @Autowired
    StoreRepository storeRepository;
    @Autowired
    MemberRepository memberRepository;
    @Autowired
    private MemberService memberService;

    @PersistenceContext
    EntityManager em;

    private Long memberTestId;
    private Long storeTestId;

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

        memberTestId = newMemberId;
        storeTestId = newStoreId;
    }

    @Test
    void findStoreById() {
        //조회
        Optional<Store> findStore = storeRepository.findById(storeTestId);
        assertThat(findStore.get().getName()).isEqualTo("storeTest");
    }

    @Test
    void findStoreByName() {
        //조회
        Optional<Store> findStore = storeRepository.findStoreByName("storeTest");

        //검증
        assertThat(findStore.get().getName()).isEqualTo("storeTest");
    }
}