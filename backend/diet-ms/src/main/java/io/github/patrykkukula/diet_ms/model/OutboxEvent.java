package io.github.patrykkukula.diet_ms.model;

import io.github.patrykkukula.diet_ms.constants.OutboxEventStatus;
import io.github.patrykkukula.mealtrackingapp_common.events.EventType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Getter @Setter
@AllArgsConstructor @NoArgsConstructor
public class OutboxEvent {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    public Long outboxEventId;

    @Enumerated(value = EnumType.STRING)
    @Column(nullable = false)
    private OutboxEventStatus status;
    @Enumerated(value = EnumType.STRING)
    @Column(nullable = false)
    private EventType eventType;
    @Column(nullable = false)
    private String payload;
    @Column(nullable = false)
    private LocalDateTime createdAt;
    private LocalDateTime sentAt;
    @Column(nullable = false)
    private int retryCount;
}
