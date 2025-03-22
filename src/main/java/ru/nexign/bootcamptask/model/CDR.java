package ru.nexign.bootcamptask.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CDR {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String type;
    private String callerNumber;
    private String calleeNumber;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
}
