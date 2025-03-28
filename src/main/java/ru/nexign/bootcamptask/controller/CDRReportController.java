package ru.nexign.bootcamptask.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.nexign.bootcamptask.service.CDRReportService;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * REST-контроллер для генерации CDR-отчётов в формате CSV.
 */
@RestController
@RequestMapping("/api/report")
@RequiredArgsConstructor
public class CDRReportController {

    private final CDRReportService reportService;

    /**
     * Генерирует CSV-отчёт по абоненту за указанный период.
     *
     * @param msisdn номер абонента
     * @param from   начало периода
     * @param to     конец периода
     * @return UUID созданного отчёта
     */
    @PostMapping("/{msisdn}")
    public ResponseEntity<String> generateReport(
            @PathVariable String msisdn,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime from,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime to
    ) throws IOException {
        UUID uuid = reportService.generateCSVReport(msisdn, from, to);
        return ResponseEntity.ok("Report generated. UUID: " + uuid);
    }
}


