package ru.nexign.bootcamptask.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.nexign.bootcamptask.model.CDR;
import ru.nexign.bootcamptask.repository.CDRRepository;
import ru.nexign.bootcamptask.repository.SubscriberRepository;

import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;

/**
 * Сервис для генерации CDR-отчёта в формате CSV по абоненту и заданному периоду.
 */
@Service
@RequiredArgsConstructor
public class CDRReportService {

    private final CDRRepository cdrRepository;
    private final SubscriberRepository subscriberRepository;

    /**
     * Генерирует CSV-отчёт по звонкам абонента за указанный период.
     *
     * @param msisdn номер абонента
     * @param from   начало периода
     * @param to     конец периода
     * @return UUID сгенерированного отчёта
     * @throws IOException если произошла ошибка при записи файла
     */
    public UUID generateCSVReport(String msisdn, LocalDateTime from, LocalDateTime to) throws IOException {
        if (!subscriberRepository.existsById(msisdn)) {
            throw new IllegalArgumentException("Абонент с номером " + msisdn + " не найден");
        }

        List<CDR> records = cdrRepository
                .findByCallerNumberOrCalleeNumberAndStartTimeBetween(msisdn, msisdn, from, to)
                .stream()
                .sorted(Comparator.comparing(CDR::getStartTime))
                .toList();

        UUID uuid = UUID.randomUUID();
        String fileName = msisdn + "_" + uuid + ".csv";

        Path reportsDir = Path.of("reports");
        if (!Files.exists(reportsDir)) {
            Files.createDirectories(reportsDir);
        }

        Path filePath = reportsDir.resolve(fileName);

        try (FileWriter writer = new FileWriter(filePath.toFile())) {
            for (CDR cdr : records) {
                writer.write(String.format("%s,%s,%s,%s,%s\n",
                        cdr.getType(),
                        cdr.getCallerNumber(),
                        cdr.getCalleeNumber(),
                        cdr.getStartTime(),
                        cdr.getEndTime()
                ));
            }
        }

        return uuid;
    }
}
