package ru.nexign.bootcamptask.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;
import ru.nexign.bootcamptask.dto.UDRResponse;
import ru.nexign.bootcamptask.service.UDRGeneratorService;

import java.time.YearMonth;
import java.util.List;

/**
 * REST-контроллер для получения UDR-отчётов.
 */
@RestController
@RequestMapping("/api/udr")
@RequiredArgsConstructor
public class UDRController {

    private final UDRGeneratorService udrGeneratorService;

    /**
     * Возвращает UDR по заданному номеру абонента и (опционально) месяцу.
     *
     * @param msisdn номер абонента
     * @param month  месяц в формате yyyy-MM (может быть null)
     * @return объект UDRResponse
     */
    @GetMapping("/{msisdn}")
    public UDRResponse getUDRByMsisdn(
            @PathVariable String msisdn,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM") YearMonth month
    ) {
        return udrGeneratorService.generateForMsisdn(msisdn, month);
    }

    /**
     * Возвращает UDR по всем абонентам за указанный месяц.
     *
     * @param month месяц в формате yyyy-MM
     * @return список объектов UDRResponse
     */
    @GetMapping("/all")
    public List<UDRResponse> getUDRsByMonth(
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM") YearMonth month) {
        return udrGeneratorService.generateAllForMonth(month);
    }
}

