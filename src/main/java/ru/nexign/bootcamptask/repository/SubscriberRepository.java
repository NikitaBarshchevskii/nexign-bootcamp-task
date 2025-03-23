package ru.nexign.bootcamptask.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.nexign.bootcamptask.model.Subscriber;
public interface SubscriberRepository extends JpaRepository<Subscriber, String> {
}
