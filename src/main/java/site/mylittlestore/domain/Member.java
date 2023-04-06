package site.mylittlestore.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;
import site.mylittlestore.dto.member.MemberFindDto;
import site.mylittlestore.entity.BaseEntity;

import javax.persistence.*;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Member extends BaseEntity {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "MEMBER_ID")
    private Long id;

    @NotBlank
    private String name;

    @NotBlank
    @Column(unique = true)
    @Email
    private String email;

    @NotBlank
    private String password;

    @NotNull
    @Embedded
    private Address address;

    @JsonIgnore
    @NotNull
    @OneToMany(mappedBy = "member")
    private List<Store> stores = new ArrayList<>();

    @Builder
    protected Member(String name, String email, String password, String city, String street, String zipcode) {
        this.name = name;
        this.email = email;
        this.password = password;
        this.address = Address.builder()
                .city(city)
                .street(street)
                .zipcode(zipcode)
            .build();
    }

    public void updateMemberName(String name) {
        this.name = name;
    }

    public void updateMemberPassword(String password) {
        this.password = password;
    }

    public void updateMemberAddress(Address address) {
        this.address = address;
    }

    //==연관관계 메소드==//
    public Member createStore(Store store) {
        stores.add(store);
        store.setMember(this);

        return this;
    }

//    public void updateStoreName(String originalName, String newStoreName) throws IllegalStateException {
//        stores.stream()
//                .filter(store -> store.getName().equals(originalName))
//                .findFirst()
//                .map(store -> store.updateStoreName(newStoreName))
//                .orElseThrow(() -> new IllegalStateException("가게 이름을 업데이트 할 수 없습니다."));
//    }

    //==DTO==//
    public MemberFindDto toMemberFindDto() {
        return MemberFindDto.builder()
                .id(id)
                .name(name)
                .email(email)
                .address(address)
                .build();
    }
}
