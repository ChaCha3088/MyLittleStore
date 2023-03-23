package site.mylittlestore.dto.member;

import com.querydsl.core.annotations.QueryProjection;
import lombok.*;
import site.mylittlestore.domain.Address;
import site.mylittlestore.domain.Store;

import java.util.List;

@Getter
public class MemberCreationDto {

    private Long id;

    private String name;

    private String email;

    private String password;

    private Address address;

    private List<Store> stores;

    @Builder
    @QueryProjection
    public MemberCreationDto(Long id, String name, String email, String password, Address address, List<Store> stores) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.password = password;
        this.address = address;
        this.stores = stores;
    }
}
