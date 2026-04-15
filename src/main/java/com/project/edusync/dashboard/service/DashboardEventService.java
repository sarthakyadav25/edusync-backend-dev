package com.project.edusync.dashboard.service;

import com.project.edusync.dashboard.dto.DashboardEventDTO;
import com.project.edusync.dashboard.model.DashboardEvent;
import com.project.edusync.dashboard.repository.DashboardEventRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

@Service
@RequiredArgsConstructor
@Slf4j
public class DashboardEventService {

    private final DashboardEventRepository repository;
    
    // In-memory store of active EventSource connections
    private final List<SseEmitter> emitters = new CopyOnWriteArrayList<>();

    public SseEmitter register() {
        SseEmitter emitter = new SseEmitter(Long.MAX_VALUE); // infinite timeout

        emitters.add(emitter);

        emitter.onCompletion(() -> removeEmitter(emitter));
        emitter.onTimeout(() -> removeEmitter(emitter));
        emitter.onError((e) -> removeEmitter(emitter));

        // Send an initial ping to ensure connection works
        try {
            emitter.send(SseEmitter.event().name("init").data("connected"));
        } catch (Exception e) {
            removeEmitter(emitter);
        }

        return emitter;
    }

    private void removeEmitter(SseEmitter emitter) {
        emitters.remove(emitter);
    }

    @Transactional
    public void pushEvent(DashboardEvent event) {
        DashboardEvent savedEvent = repository.save(event);
        DashboardEventDTO dto = mapToDTO(savedEvent);

        if (!emitters.isEmpty()) {
            for (SseEmitter emitter : emitters) {
                try {
                    emitter.send(SseEmitter.event()
                            .id(savedEvent.getId().toString())
                            .name("dashboard-event")
                            .data(dto));
                } catch (Exception e) {
                    // removing broken emitters
                    emitters.remove(emitter);
                }
            }
        }
    }

    public Page<DashboardEventDTO> getEvents(int page, int size, Instant since, String type) {
        Pageable pageable = PageRequest.of(page, size);
        LocalDateTime after = since != null ? LocalDateTime.ofInstant(since, ZoneOffset.UTC) : LocalDateTime.of(2000, 1, 1, 0, 0);

        Page<DashboardEvent> eventsPage;
        if (type != null && !type.isBlank()) {
            eventsPage = repository.findByTypeAndCreatedAtAfterOrderByCreatedAtDesc(type, after, pageable);
        } else {
            eventsPage = repository.findByCreatedAtAfterOrderByCreatedAtDesc(after, pageable);
        }
        
        return eventsPage.map(this::mapToDTO);
    }

    @Transactional
    public void markAsRead(List<UUID> eventIds) {
        List<DashboardEvent> events = repository.findAllById(eventIds);
        events.forEach(e -> e.setIsRead(true));
        repository.saveAll(events);
    }

    public int getUnreadCount() {
        return repository.countUnread();
    }

    private DashboardEventDTO mapToDTO(DashboardEvent event) {
        return new DashboardEventDTO(
                event.getId(),
                event.getType(),
                event.getSeverity(),
                event.getTitle(),
                event.getMessage(),
                event.getActionUrl(),
                event.getIsRead(),
                event.getMetadata(),
                event.getCreatedAt()
        );
    }
}
