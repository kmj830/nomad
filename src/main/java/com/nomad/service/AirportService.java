package com.nomad.service;

import com.nomad.domain.journey.Journey;
import com.nomad.domain.journey.JourneyRepository;
import com.nomad.dto.AirportDto;
import com.nomad.dto.FlightDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AirportService {

    private final JourneyRepository journeyRepository;
    private final FlightService flightService;

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    private static final DateTimeFormatter TIME_24_FORMATTER = DateTimeFormatter.ofPattern("HH:mm");
    private static final DateTimeFormatter TIME_12_FORMATTER = DateTimeFormatter.ofPattern("h:mm a", Locale.ENGLISH);

    public AirportDto.PickupScheduleResponse getPickupSchedule(Long journeyId) {
        Journey journey = null;
        if (journeyId != null) {
            journey = journeyRepository.findById(journeyId).orElse(null);
        }
        if (journey == null) {
            journey = journeyRepository.findAll().stream().findFirst().orElse(null);
        }

        LocalDateTime departure = (journey != null && journey.getDepartureDateTime() != null)
                ? journey.getDepartureDateTime()
                : LocalDateTime.now().plusDays(2).withHour(19).withMinute(35);

        String pnr = (journey != null && journey.getPnr() != null) ? journey.getPnr() : "HST777";
        FlightDto.FlightInfoResponse flightInfo = flightService.getFlightInfo(pnr);

        String terminal = flightInfo.getOriginTerminal() != null ? flightInfo.getOriginTerminal() : "인천공항 제2여객터미널";
        String originCode = flightInfo.getOriginCode() != null ? flightInfo.getOriginCode() : "ICN";
        String airportName = "인천국제공항 (" + originCode + ")";
        String flightNumber = flightInfo.getFlightNumber() != null ? flightInfo.getFlightNumber() : pnr;

        // 1. Month calculation (prev, current, next)
        int curMonth = departure.getMonthValue();
        int prevMonth = departure.minusMonths(1).getMonthValue();
        int nextMonth = departure.plusMonths(1).getMonthValue();
        List<String> months = List.of(prevMonth + "월", curMonth + "월", nextMonth + "월");
        String defaultMonth = curMonth + "월";

        // 2. Day calculation (prev day, current day, next day)
        int curDay = departure.getDayOfMonth();
        int prevDay = departure.minusDays(1).getDayOfMonth();
        int nextDay = departure.plusDays(1).getDayOfMonth();
        List<String> days = List.of(prevDay + "일", curDay + "일", nextDay + "일");
        String defaultDay = curDay + "일";

        // 3. Time calculation (4h, 3h, 2h before departure)
        LocalDateTime slot1 = departure.minusHours(4);
        LocalDateTime slot2 = departure.minusHours(3);
        LocalDateTime slot3 = departure.minusHours(2);

        String time1 = slot1.format(TIME_12_FORMATTER);
        String time2 = slot2.format(TIME_12_FORMATTER);
        String time3 = slot3.format(TIME_12_FORMATTER);
        List<String> times = List.of(time1, time2, time3);
        String defaultTime = time3; // 2 hours before flight departure (standard duty-free pickup recommendation)

        String pickupDeskLocation = String.format("인천국제공항 %s 3층 면세구역 250번 게이트 앞 Herstory VIP Care & Pick-up Desk", terminal);
        String recommendedNotice = String.format("출국 2시간 전(%s) 수령 시 가장 여유롭게 탑승하실 수 있습니다.", defaultTime);

        return AirportDto.PickupScheduleResponse.builder()
                .journeyId(journey != null ? journey.getId() : 1L)
                .pnr(pnr)
                .flightNumber(flightNumber)
                .airportName(airportName)
                .terminal(terminal)
                .departureDateTime(departure)
                .departureDate(departure.format(DATE_FORMATTER))
                .departureTime(departure.format(TIME_24_FORMATTER))
                .pickupDeskLocation(pickupDeskLocation)
                .months(months)
                .days(days)
                .times(times)
                .defaultMonth(defaultMonth)
                .defaultDay(defaultDay)
                .defaultTime(defaultTime)
                .recommendedNotice(recommendedNotice)
                .build();
    }
}
