package com.nomad.domain.member;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface MemberRepository extends JpaRepository<Member, Long> {
    Optional<Member> findByEmail(String email);
    Optional<Member> findByPhone(String phone);
    Optional<Member> findByEmailAndPhone(String email, String phone);
}
