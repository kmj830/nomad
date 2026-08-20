package com.nomad.controller;

import com.nomad.dto.AuthDto;
import com.nomad.dto.MyPageDto;
import com.nomad.dto.PaymentMethodDto;
import com.nomad.service.MyPageService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "6. MyPage API (마이페이지 & 계정/설정)", description = "마이페이지 종합 요약 대시보드, 회원정보 수정, 비밀번호 변경, 여권 정보 및 결제 수단 관리 API")
@RestController
@RequestMapping({"/api/v1/mypage", "/api/v1/members"})
@RequiredArgsConstructor
public class MyPageController {

    private final MyPageService myPageService;

    @Operation(summary = "마이페이지 대시보드 종합 요약 조회", description = "상단 프로필, VIP 티어 승급 진행률, 보유 마일리지/쿠폰수/여정수 및 알림 설정을 한 번에 조회합니다.")
    @GetMapping({"/summary/{memberId}", "/{memberId}/mypage", "/{memberId}/summary"})
    public ResponseEntity<MyPageDto.SummaryResponse> getSummary(@PathVariable Long memberId) {
        return ResponseEntity.ok(myPageService.getSummary(memberId));
    }

    @Operation(summary = "마이페이지 대시보드 요약 조회 (쿼리 파라미터 호환)", description = "memberId 쿼리 파라미터로 마이페이지 종합 요약을 조회합니다.")
    @GetMapping("/summary")
    public ResponseEntity<MyPageDto.SummaryResponse> getSummaryByParam(@RequestParam(defaultValue = "1") Long memberId) {
        return ResponseEntity.ok(myPageService.getSummary(memberId));
    }

    @Operation(summary = "회원 프로필 상세 조회", description = "회원의 이름, 영문명, 이메일, 휴대폰 번호, 생년월일 정보를 조회합니다.")
    @GetMapping({"/{memberId}/profile", "/profile/{memberId}"})
    public ResponseEntity<MyPageDto.ProfileResponse> getProfile(@PathVariable Long memberId) {
        return ResponseEntity.ok(myPageService.getProfile(memberId));
    }

    @Operation(summary = "회원 프로필 수정", description = "회원의 영문 이름(여권용) 및 이메일 정보를 수정합니다.")
    @RequestMapping(value = {"/{memberId}/profile", "/profile/{memberId}"}, method = {RequestMethod.PATCH, RequestMethod.PUT, RequestMethod.POST})
    public ResponseEntity<MyPageDto.ProfileResponse> updateProfile(
            @PathVariable Long memberId,
            @RequestBody MyPageDto.UpdateProfileRequest request
    ) {
        return ResponseEntity.ok(myPageService.updateProfile(memberId, request));
    }

    @Operation(summary = "알림 및 마케팅 수신 설정 변경", description = "마일리지 알림, 여정 알림, 마케팅/프로모션 혜택 수신 동의 상태를 변경합니다.")
    @RequestMapping(value = {"/{memberId}/settings", "/settings/{memberId}"}, method = {RequestMethod.PATCH, RequestMethod.PUT, RequestMethod.POST})
    public ResponseEntity<MyPageDto.NotificationSettings> updateSettings(
            @PathVariable Long memberId,
            @RequestBody MyPageDto.UpdateSettingsRequest request
    ) {
        return ResponseEntity.ok(myPageService.updateSettings(memberId, request));
    }

    @Operation(summary = "로그인 회원 비밀번호 변경", description = "현재 비밀번호 확인 후 새로운 비밀번호로 재설정합니다.")
    @PostMapping({"/{memberId}/password/change", "/password/change/{memberId}"})
    public ResponseEntity<AuthDto.PasswordResponse> changePassword(
            @PathVariable Long memberId,
            @RequestBody MyPageDto.ChangePasswordRequest request
    ) {
        boolean success = myPageService.changePassword(memberId, request);
        return ResponseEntity.ok(AuthDto.PasswordResponse.builder()
                .success(success)
                .message("비밀번호가 성공적으로 변경되었습니다.")
                .build());
    }

    @Operation(summary = "여권 및 탑승객 정보 조회", description = "면세 구매 및 항공편 등록 시 자동 입력되는 여권 정보를 조회합니다.")
    @GetMapping({"/{memberId}/passport", "/passport/{memberId}"})
    public ResponseEntity<MyPageDto.PassportResponse> getPassport(@PathVariable Long memberId) {
        return ResponseEntity.ok(myPageService.getPassport(memberId));
    }

    @Operation(summary = "여권 정보 수정 및 자동 입력 설정", description = "여권 번호, 만료일자 및 면세 결제 시 자동 입력 여부를 수정합니다.")
    @RequestMapping(value = {"/{memberId}/passport", "/passport/{memberId}"}, method = {RequestMethod.PUT, RequestMethod.PATCH, RequestMethod.POST})
    public ResponseEntity<MyPageDto.PassportResponse> updatePassport(
            @PathVariable Long memberId,
            @RequestBody MyPageDto.UpdatePassportRequest request
    ) {
        return ResponseEntity.ok(myPageService.updatePassport(memberId, request));
    }

    @Operation(summary = "등록된 결제 수단(카드) 목록 조회", description = "면세 결제 및 빠른 체크아웃에 사용되는 등록 카드 목록을 조회합니다.")
    @GetMapping({"/{memberId}/payment-methods", "/payment-methods/{memberId}"})
    public ResponseEntity<List<PaymentMethodDto.PaymentMethodItem>> getPaymentMethods(@PathVariable Long memberId) {
        return ResponseEntity.ok(myPageService.getPaymentMethods(memberId));
    }

    @Operation(summary = "신규 결제 수단(카드) 등록", description = "신규 신용카드 및 결제 수단을 등록합니다.")
    @PostMapping({"/{memberId}/payment-methods", "/payment-methods/{memberId}"})
    public ResponseEntity<PaymentMethodDto.PaymentMethodItem> addPaymentMethod(
            @PathVariable Long memberId,
            @RequestBody PaymentMethodDto.AddCardRequest request
    ) {
        return ResponseEntity.ok(myPageService.addPaymentMethod(memberId, request));
    }

    @Operation(summary = "결제 수단 삭제", description = "등록된 결제 수단을 삭제합니다.")
    @DeleteMapping({"/{memberId}/payment-methods/{cardId}", "/payment-methods/{memberId}/{cardId}"})
    public ResponseEntity<AuthDto.PasswordResponse> deletePaymentMethod(
            @PathVariable Long memberId,
            @PathVariable Long cardId
    ) {
        myPageService.deletePaymentMethod(memberId, cardId);
        return ResponseEntity.ok(AuthDto.PasswordResponse.builder()
                .success(true)
                .message("결제 수단이 정상적으로 삭제되었습니다.")
                .build());
    }
}
