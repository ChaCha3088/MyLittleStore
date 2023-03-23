package site.mylittlestore.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;
import site.mylittlestore.domain.item.Item;
import site.mylittlestore.dto.store.StoreOnlyDto;
import site.mylittlestore.enumstorage.status.StoreStatus;
import site.mylittlestore.dto.store.StoreDto;
import site.mylittlestore.entity.BaseEntity;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;

import static javax.persistence.FetchType.LAZY;

@Entity
@Getter
@Table(name = "STORES")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Store extends BaseEntity {

    @Id @GeneratedValue
    @Column(name = "STORE_ID")
    private Long id;

    @NotNull
    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "MEMBER_ID")
    private Member member;

    @NotBlank
    @Column(unique = true)
    private String name;

    @NotNull
    @Embedded
    private Address address;

    @NotNull
    @Enumerated(EnumType.STRING)
    private StoreStatus storeStatus = StoreStatus.CLOSE;

    @JsonIgnore
    @OneToMany(mappedBy = "store")
    private List<StoreTable> storeTables;

    @JsonIgnore
    @OneToMany(mappedBy = "store")
    private List<Item> items;

    @Builder
    protected Store(Member member, String name, Address address) {
        this.member = member;
        this.name = name;
        this.address = address;
        this.storeTables = new ArrayList<>();
        this.items = new ArrayList<>();
        this.storeStatus = StoreStatus.CLOSE;
    }

    public StoreTable createStoreTable() {
        StoreTable storeTable = StoreTable.builder()
                .store(this)
                .build();
        this.storeTables.add(storeTable);

        return storeTable;
    }

    public Store createItem(Item item) {
        this.items.add(item);
        item.setStore(this);

        return this;
    }

    public void updateStoreName(String newStoreName) {
        this.name = newStoreName;
    }

    public void updateStoreAddress(Address address) {
        this.address = address;
    }

    public void changeStoreStatus(StoreStatus storeStatus) {
        this.storeStatus = storeStatus;
    }

    //==연관관계 메소드==//
    public void setMember(Member member) {
        this.member = member;
    }

    //==DTO==//
    public StoreDto toStoreDto() {
        return StoreDto.builder()
                .id(this.id)
                .memberId(this.member.getId())
                .name(this.name)
                .address(this.address)
                .storeStatus(this.storeStatus)
                .storeTables(this.storeTables)
//                .orders(this.orders.stream().map(Order::getId).collect(Collectors.toList()))
//                .items(this.items.stream().map(Item::getId).collect(Collectors.toList()))
                .items(this.items)
                .build();
    }

    public StoreOnlyDto toStoreOnlyDto() {
        return StoreOnlyDto.builder()
                .id(this.id)
                .memberId(this.member.getId())
                .name(this.name)
                .address(this.address)
                .storeStatus(this.storeStatus)
                .build();
    }
}
