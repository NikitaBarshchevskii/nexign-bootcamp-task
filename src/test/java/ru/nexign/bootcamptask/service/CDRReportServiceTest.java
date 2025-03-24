package ru.nexign.bootcamptask.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.nexign.bootcamptask.model.CDR;
import ru.nexign.bootcamptask.repository.CDRRepository;
import ru.nexign.bootcamptask.repository.SubscriberRepository;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class CDRReportServiceTest {

    private CDRRepository cdrRepository;
    private SubscriberRepository subscriberRepository;
    private CDRReportService cdrReportService;

    @BeforeEach
    void setUp() {
        cdrRepository = mock(CDRRepository.class);
        subscriberRepository = mock(SubscriberRepository.class);
        cdrReportService = new CDRReportService(cdrRepository, subscriberRepository);
    }

    @Test
    void shouldGenerateCsvReportForValidMsisdn() throws IOException {
        String msisdn = "79990001122";
        LocalDateTime from = LocalDateTime.of(2024, 10, 1, 0, 0);
        LocalDateTime to = LocalDateTime.of(2024, 10, 31, 23, 59);

        CDR cdr = new CDR(1L, "01", msisdn, "79991112233",
                from, from.plusMinutes(5));

        when(subscriberRepository.existsById(msisdn)).thenReturn(true);
        when(cdrRepository.findByCallerNumberOrCalleeNumberAndStartTimeBetween(msisdn, msisdn, from, to))
                .thenReturn(List.of(cdr));

        UUID uuid = cdrReportService.generateCSVReport(msisdn, from, to);

        Path reportPath = Path.of("reports", msisdn + "_" + uuid + ".csv");
        assertTrue(Files.exists(reportPath));

        String content = Files.readString(reportPath);
        assertTrue(content.contains("01," + msisdn + ",79991112233"));

        Files.deleteIfExists(reportPath);
    }

    @Test
    void shouldThrowExceptionForInvalidMsisdn() {
        String msisdn = "79991112233";
        LocalDateTime from = LocalDateTime.of(2024, 10, 1, 0, 0);
        LocalDateTime to = LocalDateTime.of(2024, 10, 31, 23, 59);

        when(subscriberRepository.existsById(msisdn)).thenReturn(false);

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
                cdrReportService.generateCSVReport(msisdn, from, to)
        );

        assertEquals("Абонент с номером " + msisdn + " не найден", exception.getMessage());
    }

    @Test
    void shouldGenerateEmptyCsvWhenNoCdrRecords() throws IOException {
        String msisdn = "79991112233";
        LocalDateTime from = LocalDateTime.of(2024, 10, 1, 0, 0);
        LocalDateTime to = LocalDateTime.of(2024, 10, 31, 23, 59);

        when(subscriberRepository.existsById(msisdn)).thenReturn(true);
        when(cdrRepository.findByCallerNumberOrCalleeNumberAndStartTimeBetween(msisdn, msisdn, from, to))
                .thenReturn(List.of());

        UUID reportId = cdrReportService.generateCSVReport(msisdn, from, to);

        Path reportPath = Path.of("reports", msisdn + "_" + reportId + ".csv");
        assertTrue(Files.exists(reportPath));

        List<String> lines = Files.readAllLines(reportPath);
        assertTrue(lines.isEmpty(), "Ожидался пустой CSV файл");
    }
}
