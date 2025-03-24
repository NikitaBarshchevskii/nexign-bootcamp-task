package ru.nexign.bootcamptask.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.nexign.bootcamptask.dto.UDRResponse;
import ru.nexign.bootcamptask.model.CDR;
import ru.nexign.bootcamptask.repository.CDRRepository;
import ru.nexign.bootcamptask.repository.SubscriberRepository;

import java.time.Duration;
import java.time.YearMonth;
import java.util.List;
import java.util.stream.Collectors;
import java.time.LocalDateTime;

/**
 * Сервис для формирования UDR (Usage Detail Record) по абонентам.
 * Расчитывает суммарную длительность входящих и исходящих звонков
 * за указанный месяц или за весь период.
 */
@Service
@RequiredArgsConstructor
public class UDRGeneratorService {

    private final CDRRepository cdrRepository;
    private final SubscriberRepository subscriberRepository;

    /**
     * Генерирует UDR-запись для заданного абонента.
     *
     * @param msisdn номер абонента
     * @param month  месяц (может быть null)
     * @return объект UDRResponse
     */
    public UDRResponse generateForMsisdn(String msisdn, YearMonth month) {
        List<CDR> calls;

        if (month != null) {
            LocalDateTime start = month.atDay(1).atStartOfDay();
            LocalDateTime end = month.atEndOfMonth().atTime(23, 59, 59);
            calls = cdrRepository.findByCallerNumberOrCalleeNumberAndStartTimeBetween(msisdn, msisdn, start, end);
        } else {
            calls = cdrRepository.findByCallerNumberOrCalleeNumber(msisdn, msisdn);
        }

        long incomingSeconds = 0;
        long outgoingSeconds = 0;

        for (CDR call : calls) {
            long duration = Duration.between(call.getStartTime(), call.getEndTime()).getSeconds();
            if (msisdn.equals(call.getCalleeNumber())) {
                incomingSeconds += duration;
            } else if (msisdn.equals(call.getCallerNumber())) {
                outgoingSeconds += duration;
            }
        }

        return UDRResponse.builder()
                .msisdn(msisdn)
                .incomingCall(new UDRResponse.CallInfo(formatDuration(incomingSeconds)))
                .outcomingCall(new UDRResponse.CallInfo(formatDuration(outgoingSeconds)))
                .build();
    }

    /**
     * Генерирует UDR-отчёты по всем абонентам за указанный месяц.
     *
     * @param month месяц
     * @return список отчётов
     */
    public List<UDRResponse> generateAllForMonth(YearMonth month) {
        return subscriberRepository.findAll().stream()
                .map(subscriber -> generateForMsisdn(subscriber.getMsisdn(), month))
                .collect(Collectors.toList());
    }

    /**
     * Форматирует длительность (в секундах) в строку формата HH:mm:ss.
     *
     * @param totalSeconds длительность в секундах
     * @return строка в формате HH:mm:ss
     */
    private String formatDuration(long totalSeconds) {
        long hours = totalSeconds / 3600;
        long minutes = (totalSeconds % 3600) / 60;
        long seconds = totalSeconds % 60;
        return String.format("%02d:%02d:%02d", hours, minutes, seconds);
    }
}
