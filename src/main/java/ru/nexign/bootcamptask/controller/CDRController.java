package ru.nexign.bootcamptask.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;
import ru.nexign.bootcamptask.dto.UDRResponse;
import ru.nexign.bootcamptask.service.UDRGeneratorService;

import java.time.YearMonth;
import java.util.List;

@RestController
@RequestMapping("/api/cdr")
@RequiredArgsConstructor
public class CDRController {

    private final UDRGeneratorService udrGeneratorService;

    @GetMapping("/udr/{msisdn}")
    public UDRResponse getUDRByMsisdn(
            @PathVariable String msisdn,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM") YearMonth month
    ) {
        return udrGeneratorService.generateForMsisdn(msisdn, month);
    }

    @GetMapping("/udr/all")
    public List<UDRResponse> getUDRsByMonth(
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM") YearMonth month) {
        return udrGeneratorService.generateAllForMonth(month);
    }
}
