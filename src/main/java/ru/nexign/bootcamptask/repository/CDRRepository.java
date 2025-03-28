package ru.nexign.bootcamptask.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.nexign.bootcamptask.model.CDR;

import java.time.LocalDateTime;
import java.util.List;

public interface CDRRepository extends JpaRepository<CDR, Long>{
    List<CDR> findByCallerNumberOrCalleeNumber(String caller, String callee);

    List<CDR> findByCallerNumberOrCalleeNumberAndStartTimeBetween(
            String caller, String callee, LocalDateTime start, LocalDateTime end
    );
}
