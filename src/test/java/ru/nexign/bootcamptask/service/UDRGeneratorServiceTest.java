package ru.nexign.bootcamptask.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import ru.nexign.bootcamptask.dto.UDRResponse;
import ru.nexign.bootcamptask.model.CDR;
import ru.nexign.bootcamptask.repository.CDRRepository;
import ru.nexign.bootcamptask.repository.SubscriberRepository;

import java.time.LocalDateTime;
import java.time.YearMonth;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class UDRGeneratorServiceTest {

    private CDRRepository cdrRepository;
    private SubscriberRepository subscriberRepository;
    private UDRGeneratorService udrGeneratorService;

    @BeforeEach
    void setUp() {
        cdrRepository = mock(CDRRepository.class);
        subscriberRepository = mock(SubscriberRepository.class);
        udrGeneratorService = new UDRGeneratorService(cdrRepository, subscriberRepository);
    }

    @Test
    void shouldGenerateUDRForSingleMsisdn() {
        String msisdn = "79992221122";
        YearMonth month = YearMonth.of(2024, 10);

        CDR outgoing = new CDR(1L, "01", msisdn, "79991112233",
                LocalDateTime.of(2024, 10, 15, 12, 0),
                LocalDateTime.of(2024, 10, 15, 20, 5));

        CDR incoming = new CDR(2L, "02", "79991112233", msisdn,
                LocalDateTime.of(2024, 10, 16, 14, 0),
                LocalDateTime.of(2024, 10, 16, 14, 5));

        when(cdrRepository.findByCallerNumberOrCalleeNumberAndStartTimeBetween(
                eq(msisdn), eq(msisdn),
                any(LocalDateTime.class), any(LocalDateTime.class)))
                .thenReturn(List.of(outgoing, incoming));

        UDRResponse result = udrGeneratorService.generateForMsisdn(msisdn, month);

        assertEquals(msisdn, result.getMsisdn());
        assertEquals("08:05:00", result.getOutcomingCall().getTotalTime());
        assertEquals("00:05:00", result.getIncomingCall().getTotalTime());
    }

    @Test
    void shouldGenerateUDRForAllSubscribersInMonth() {
        String msisdn1 = "79992221122";
        String msisdn2 = "79993332211";
        YearMonth month = YearMonth.of(2024, 10);

        CDR call1 = new CDR(1L, "01", msisdn1, "79991112233",
                LocalDateTime.of(2024, 10, 15, 10, 0),
                LocalDateTime.of(2024, 10, 15, 10, 5));

        CDR call2 = new CDR(2L, "02", "79991112233", msisdn2,
                LocalDateTime.of(2024, 10, 16, 14, 0),
                LocalDateTime.of(2024, 10, 16, 14, 10));

        var subscriber1 = new ru.nexign.bootcamptask.model.Subscriber();
        subscriber1.setMsisdn(msisdn1);
        var subscriber2 = new ru.nexign.bootcamptask.model.Subscriber();
        subscriber2.setMsisdn(msisdn2);

        when(subscriberRepository.findAll()).thenReturn(List.of(subscriber1, subscriber2));

        when(cdrRepository.findByCallerNumberOrCalleeNumberAndStartTimeBetween(eq(msisdn1), eq(msisdn1), any(), any()))
                .thenReturn(List.of(call1));

        when(cdrRepository.findByCallerNumberOrCalleeNumberAndStartTimeBetween(eq(msisdn2), eq(msisdn2), any(), any()))
                .thenReturn(List.of(call2));

        List<UDRResponse> responses = udrGeneratorService.generateAllForMonth(month);

        assertEquals(2, responses.size());

        UDRResponse r1 = responses.stream().filter(r -> r.getMsisdn().equals(msisdn1)).findFirst().orElseThrow();
        UDRResponse r2 = responses.stream().filter(r -> r.getMsisdn().equals(msisdn2)).findFirst().orElseThrow();

        assertEquals("00:05:00", r1.getOutcomingCall().getTotalTime());
        assertEquals("00:00:00", r1.getIncomingCall().getTotalTime());

        assertEquals("00:10:00", r2.getIncomingCall().getTotalTime());
        assertEquals("00:00:00", r2.getOutcomingCall().getTotalTime());
    }
}
