package site.mylittlestore.repository.member;

import lombok.RequiredArgsConstructor;

import javax.persistence.EntityManager;

@RequiredArgsConstructor
public class MemberRepositoryImpl implements MemberRepositoryQueryDsl {
    private final EntityManager em;
}
