package com.nomad.service;

import com.nomad.domain.member.Member;
import com.nomad.domain.member.MemberRepository;
import com.nomad.domain.member.VipTier;
import com.nomad.dto.AuthDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AuthService {

    private final MemberRepository memberRepository;

    @Transactional
    public AuthDto.LoginResponse login(AuthDto.LoginRequest request) {
        Member member = memberRepository.findByEmail(request.getEmail())
                .orElseGet(() -> memberRepository.save(
                        Member.builder()
                                .email(request.getEmail())
                                .name(request.getName() != null ? request.getName() : "신규 노마드 회원")
                                .vipTier(VipTier.SILVER)
                                .nomadMiles(1000L)
                                .build()
                ));

        return AuthDto.LoginResponse.builder()
                .memberId(member.getId())
                .email(member.getEmail())
                .name(member.getName())
                .vipTier(member.getVipTier())
                .nomadMiles(member.getNomadMiles())
                .message("MCM Nomad Hub에 성공적으로 접속되었습니다.")
                .build();
    }
}
