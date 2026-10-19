package com.atelier.tasks.entity;

import jakarta.persistence.*;
import java.time.*;
import java.util.*;

@Entity @Table(name="tasks",indexes={@Index(name="idx_tasks_user_status",columnList="user_id,status"),@Index(name="idx_tasks_user_due",columnList="user_id,due_date")})
public class Task {
 @Id @GeneratedValue(strategy=GenerationType.IDENTITY) public Long id;
 @ManyToOne(fetch=FetchType.LAZY,optional=false) @JoinColumn(name="user_id",nullable=false) public AppUser owner;
 @ManyToOne(fetch=FetchType.LAZY) @JoinColumn(name="category_id") public Category category;
 @Column(nullable=false,length=180) public String title;
 @Column(length=3000) public String description;
 @Enumerated(EnumType.STRING) @Column(nullable=false,length=20) public TaskStatus status=TaskStatus.TODO;
 @Enumerated(EnumType.STRING) @Column(nullable=false,length=20) public Priority priority=Priority.MEDIUM;
 @Column(name="start_date") public LocalDate startDate;
 @Column(name="due_date") public LocalDate dueDate;
 @Column(name="completed_at") public Instant completedAt;
 @Column(name="time_of_day",nullable=false,length=24) public String timeOfDay="Whenever";
 @Column(name="created_at",nullable=false) public Instant createdAt=Instant.now();
 @Column(name="updated_at",nullable=false) public Instant updatedAt=Instant.now();
 @OneToMany(mappedBy="task",cascade=CascadeType.ALL,orphanRemoval=true) @OrderBy("id ASC") public List<TaskTag> tags=new ArrayList<>();
 public enum Priority{LOW,MEDIUM,HIGH,URGENT}
 public enum TaskStatus{TODO,IN_PROGRESS,COMPLETED}
 @PreUpdate void touch(){updatedAt=Instant.now();}
}
