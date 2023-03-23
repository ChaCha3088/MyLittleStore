package site.mylittlestore.dto.member;

import com.querydsl.core.annotations.QueryProjection;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import site.mylittlestore.domain.Address;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class MemberFindDto {

    private Long id;

    private String name;

    private String email;

    private Address address;

    @Builder
    @QueryProjection
    public MemberFindDto(Long id, String name, String email, Address address) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.address = address;
    }
}
