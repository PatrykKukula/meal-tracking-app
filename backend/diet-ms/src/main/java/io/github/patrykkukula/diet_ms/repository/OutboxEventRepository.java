package io.github.patrykkukula.diet_ms.repository;

import io.github.patrykkukula.diet_ms.model.OutboxEvent;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;


import java.util.List;

@Repository
public interface OutboxEventRepository extends JpaRepository<OutboxEvent, Long> {

    @Query("SELECT e FROM OutboxEvent WHERE " +
            "e.status = io.github.patrykkukula.diet_ms.constants.OutboxEventStatus.NEW " +
            "OR " +
            "e.status = io.github.patrykkukula.diet_ms.constants.OutboxEventStatus.FAILED")
    List<OutboxEvent> getUnsentEvents(Pageable pageable);

    @Query("DELETE e FROM OutboxEvent WHERE " +
            "e.status = io.github.patrykkukula.diet_ms.constants.OutboxEventStatus.DEAD" +
            "OR" +
            "io.github.patrykkukula.diet_ms.constants.OutboxEventStatus.SENT")
    @Modifying
    void deleteSentEvents();
}
