package io.github.patrykkukula.diet_ms.repository;

import io.github.patrykkukula.diet_ms.model.OutboxEvent;
import io.github.patrykkukula.mealtrackingapp_common.events.OutboxEventStatus;
import jakarta.persistence.LockModeType;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;


import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface OutboxEventRepository extends JpaRepository<OutboxEvent, String> {

    @NativeQuery("""
            SELECT * FROM OutboxEvent 
            WHERE status = 'NEW' 
            FOR UPDATE SKIP LOCKED
            """)
    List<OutboxEvent> getUnsentEvents(Pageable pageable);

    @Query("""
            DELETE FROM OutboxEvent e 
            WHERE 
            e.status = :dead 
            OR 
            (
            e.status = :sent
            AND 
            e.sentAt < :delay
            )
            """
    )
    @Modifying
    int deleteSentEvents(@Param(value = "delay") LocalDateTime delay,
                         @Param(value = "dead") OutboxEventStatus statusDead,
                         @Param(value = "sent") OutboxEventStatus statusSent);
}
