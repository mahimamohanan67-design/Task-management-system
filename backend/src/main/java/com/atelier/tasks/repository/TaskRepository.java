package com.atelier.tasks.repository;
import com.atelier.tasks.entity.Task;import org.springframework.data.jpa.repository.*;import org.springframework.data.repository.query.Param;import java.time.LocalDate;import java.util.*;
public interface TaskRepository extends JpaRepository<Task,Long>,JpaSpecificationExecutor<Task>{
 Optional<Task> findByIdAndOwnerId(Long id,Long ownerId);
 long countByOwnerId(Long id); long countByOwnerIdAndStatus(Long id,Task.TaskStatus status);
 long countByOwnerIdAndDueDateBetweenAndStatusNot(Long id,LocalDate from,LocalDate to,Task.TaskStatus status);
 List<Task> findAllByOwnerIdAndDueDateBetweenOrderByDueDateAsc(Long id,LocalDate from,LocalDate to);
 List<Task> findTop8ByOwnerIdOrderByDueDateAsc(Long id);
 long countByCategoryId(Long categoryId);
}
