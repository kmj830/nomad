package com.nomad.service;

import com.nomad.dto.CareDto;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class GoogleMapsServiceTest {

    private final GoogleMapsService googleMapsService = new GoogleMapsService();

    @Test
    @DisplayName("Google Maps API 연동 및 매장 탐색 데이터 반환")
    void findMcmSpotsWithMaps_Success() {
        List<CareDto.VisetosSpot> spots = googleMapsService.findMcmSpotsWithMaps("Bangkok");

        assertThat(spots).isNotEmpty();
        assertThat(spots.get(0).getSpotName()).isNotEmpty();
        assertThat(spots.get(0).getBrand()).isNotNull();
        assertThat(spots.get(0).getLatitude()).isNotNull();
        assertThat(spots.get(0).getLongitude()).isNotNull();
    }
}
