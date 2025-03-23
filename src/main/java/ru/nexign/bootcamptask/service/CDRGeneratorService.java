package ru.nexign.bootcamptask.service;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.nexign.bootcamptask.model.CDR;
import ru.nexign.bootcamptask.model.Subscriber;
import ru.nexign.bootcamptask.repository.CDRRepository;
import ru.nexign.bootcamptask.repository.SubscriberRepository;

import java.time.LocalDateTime;
import java.util.*;

@Service
@RequiredArgsConstructor
public class CDRGeneratorService {

    private final SubscriberRepository subscriberRepository;
    private final CDRRepository cdrRepository;
    private final Random random = new Random();

    private final int subscriberCount = 10;
    private final int callsPerSubscriber = 100;

    public void generate(){
        generateSubscribers();
        generateCDRRecords();
    }

    private void generateSubscribers(){
        if (subscriberRepository.count() >= subscriberCount) return;

        for (int i = 0; i < subscriberCount; i++) {
            String msisdn = "79" + String.format("%09d", random.nextInt(1_000_000_000));
            subscriberRepository.save(Subscriber.builder().msisdn(msisdn).build());
        }
    }

    private void generateCDRRecords(){
        List<Subscriber> subs = subscriberRepository.findAll();
        List<CDR> allRecords = new ArrayList<>();

        for (Subscriber from : subs) {
            for (int i = 0; i < callsPerSubscriber; i++){
                Subscriber to;
                do {
                    to = subs.get(random.nextInt(subs.size()));
                } while (to.getMsisdn().equals(from.getMsisdn()));

                LocalDateTime start = randomDateInLastYear();
                LocalDateTime end = start.plusSeconds(30 + random.nextInt(300));

                String type = random.nextBoolean() ? "01" : "02";

                allRecords.add(CDR.builder()
                        .type(type)
                        .callerNumber(from.getMsisdn())
                        .calleeNumber(to.getMsisdn())
                        .startTime(start)
                        .endTime(end)
                        .build());
            }
        }

        cdrRepository.saveAll(allRecords);
    }

    private LocalDateTime randomDateInLastYear(){
        LocalDateTime now = LocalDateTime.now();
        return now.minusDays(random.nextInt(365)).withHour(random.nextInt(24)).withMinute(random.nextInt(60));
    }
}
