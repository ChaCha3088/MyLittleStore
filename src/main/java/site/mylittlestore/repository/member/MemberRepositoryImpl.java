package site.mylittlestore.repository.member;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.repository.query.Param;
import site.mylittlestore.domain.Member;
import site.mylittlestore.dto.member.MemberFindDto;
import site.mylittlestore.dto.member.QMemberFindDto;

import javax.persistence.EntityManager;
import java.util.List;
import java.util.Optional;

import static site.mylittlestore.domain.QMember.member;

@RequiredArgsConstructor
public class MemberRepositoryImpl implements MemberRepositoryQueryDsl {
    private final EntityManager em;

    @Override
    public Optional<MemberFindDto> findMemberFindDtoById(@Param("id") Long id) {
        return returnMemberFindDto(member.id.eq(id));
    }

    @Override
    public Optional<Member> findMemberByMemberIdAndStoreName(Long memberId, String storeName) {
        JPAQueryFactory queryFactory = new JPAQueryFactory(em);

        return Optional.ofNullable(
                queryFactory
                        .select(member)
                        .from(member)
                        .where(member.id.eq(memberId))
                        .where(member.stores.any().name.eq(storeName))
                        .fetchOne()
        );
    }

    @Override
    public Optional<MemberFindDto> findMemberFindDtoByEmail(@Param("email") String email) {
        return returnMemberFindDto(member.email.eq(email));
    }

    @Override
    public List<MemberFindDto> findAllMemberFindDto() {
        JPAQueryFactory queryFactory = new JPAQueryFactory(em);

        return queryFactory
                        .select(new QMemberFindDto(
                                member.id,
                                member.name,
                                member.email,
                                member.address
                        ))
                        .from(member)
                        .fetch();
    }

    private Optional<MemberFindDto> returnMemberFindDto(BooleanExpression booleanExpression) {
        JPAQueryFactory queryFactory = new JPAQueryFactory(em);

        return Optional.ofNullable(
                queryFactory
                        .select(new QMemberFindDto(
                                member.id,
                                member.name,
                                member.email,
                                member.address
                        ))
                        .from(member)
                        .where(booleanExpression)
                        .fetchOne()
        );
    }



}
