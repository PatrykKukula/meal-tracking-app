package io.github.patrykkukula.product_ms.repository;

import io.github.patrykkukula.mealtrackingapp_common.events.OutboxEventStatus;
import io.github.patrykkukula.product_ms.model.OutboxEvent;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.NativeQuery;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface OutboxEventRepository extends JpaRepository<OutboxEvent, String> {

    @NativeQuery("""
            SELECT * FROM outbox_event 
            WHERE status = 'NEW' 
            ORDER BY
            created_at 
            ASC 
            FOR UPDATE SKIP LOCKED
            LIMIT 100
            """)
    List<OutboxEvent> getUnsentEvents();

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
