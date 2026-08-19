package com.nomad.domain.mileage;

public enum MileageType {
    EARNED_PURCHASE,    // 면세점 구매 적립
    EARNED_FLIGHT,      // 항공편 등록 적립
    EARNED_STAMP,       // 시티 패스포트 스탬프 적립
    USED_BENEFIT,       // 라운지/피팅 혜택 교환 사용
    USED_CHECKOUT,      // 결제 시 마일리지 차감 할인
    TRANSFERRED_OUT,    // 타 회원 양도 (출금)
    TRANSFERRED_IN      // 타 회원으로부터 양도 수신 (입금)
}
