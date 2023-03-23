package site.mylittlestore.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;
import site.mylittlestore.entity.BaseEntity;
import site.mylittlestore.enumstorage.status.StoreTableStatus;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

import static javax.persistence.FetchType.LAZY;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class StoreTable extends BaseEntity {

    @Id @GeneratedValue
    @Column(name = "TABLE_ID")
    private Long id;

    @NotNull
    @JsonIgnore
    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "STORE_ID")
    private Store store;

    @OneToOne(fetch = LAZY)
    @JoinColumn(name = "ORDER_ID")
    private Order order;

    private Long xCoordinate;

    private Long yCoordinate;

    @NotNull
    @Enumerated(EnumType.STRING)
    private StoreTableStatus storeTableStatus;

    @Builder
    protected StoreTable(Store store) {
        this.store = store;
        this.storeTableStatus = StoreTableStatus.EMPTY;
    }
}
