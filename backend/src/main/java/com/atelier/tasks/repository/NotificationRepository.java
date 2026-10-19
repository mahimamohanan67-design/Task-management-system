package com.atelier.tasks.repository;
import com.atelier.tasks.entity.Notification;import org.springframework.data.jpa.repository.JpaRepository;import java.util.*;
public interface NotificationRepository extends JpaRepository<Notification,Long>{List<Notification> findAllByOwnerIdOrderByCreatedAtDesc(Long ownerId);Optional<Notification> findByIdAndOwnerId(Long id,Long ownerId);long countByOwnerIdAndReadFalse(Long ownerId);}
