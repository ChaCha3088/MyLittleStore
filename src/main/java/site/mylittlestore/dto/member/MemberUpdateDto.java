package site.mylittlestore.dto.member;

import com.querydsl.core.annotations.QueryProjection;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import site.mylittlestore.domain.Address;
import site.mylittlestore.domain.Store;

import java.util.List;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class MemberUpdateDto {

    private Long id;

    private String name;

    private Address address;

    @Builder
    @QueryProjection
    public MemberUpdateDto(Long id, String name, Address address) {
        this.id = id;
        this.name = name;
        this.address = address;
    }
}
