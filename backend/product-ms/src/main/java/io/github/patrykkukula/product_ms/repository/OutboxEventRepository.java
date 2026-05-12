package io.github.patrykkukula.product_ms.repository;

import io.github.patrykkukula.product_ms.model.OutboxEvent;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface OutboxEventRepository extends JpaRepository<OutboxEvent, String> {

    @Query("""
            SELECT e FROM OutboxEvent e 
            WHERE e.status = io.github.patrykkukula.mealtrackingapp_common.events.OutboxEventStatus.NEW 
            OR 
            e.status = io.github.patrykkukula.mealtrackingapp_common.events.OutboxEventStatus.FAILED
    """)
    List<OutboxEvent> getUnsentEvents(Pageable pageable);

    @Query("""
            DELETE FROM OutboxEvent e WHERE 
            (e.status = io.github.patrykkukula.mealtrackingapp_common.events.OutboxEventStatus.DEAD) 
            OR 
            (
            e.status = io.github.patrykkukula.mealtrackingapp_common.events.OutboxEventStatus.SENT 
            AND 
            e.sentAt < :delay"
            )
            """
    )
    @Modifying
    int deleteSentEvents(@Param(value = "delay") LocalDateTime delay);
}
