package com.atelier.tasks.repository;
import com.atelier.tasks.entity.TaskActivity;import org.springframework.data.jpa.repository.JpaRepository;import java.util.*;
public interface TaskActivityRepository extends JpaRepository<TaskActivity,Long>{List<TaskActivity> findTop20ByUserIdOrderByCreatedAtDesc(Long userId);List<TaskActivity> findAllByTaskIdAndUserIdOrderByCreatedAtDesc(Long taskId,Long userId);}
