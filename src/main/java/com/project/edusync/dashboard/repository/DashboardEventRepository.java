package com.project.edusync.dashboard.repository;

import com.project.edusync.dashboard.model.DashboardEvent;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public interface DashboardEventRepository extends JpaRepository<DashboardEvent, UUID> {
    
    Page<DashboardEvent> findByCreatedAtAfterOrderByCreatedAtDesc(LocalDateTime after, Pageable pageable);
    
    Page<DashboardEvent> findByTypeAndCreatedAtAfterOrderByCreatedAtDesc(String type, LocalDateTime after, Pageable pageable);
    
    @Query("SELECT COUNT(e) FROM DashboardEvent e WHERE e.isRead = false")
    int countUnread();
}
