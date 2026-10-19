package com.atelier.tasks.entity;
import jakarta.persistence.*;import java.time.Instant;
@Entity @Table(name="task_activities",indexes=@Index(name="idx_activity_user_created",columnList="user_id,created_at"))
public class TaskActivity {
 @Id @GeneratedValue(strategy=GenerationType.IDENTITY) public Long id;
 @ManyToOne(fetch=FetchType.LAZY,optional=false) @JoinColumn(name="task_id",nullable=false) public Task task;
 @ManyToOne(fetch=FetchType.LAZY,optional=false) @JoinColumn(name="user_id",nullable=false) public AppUser user;
 @Column(nullable=false,length=80) public String action; @Column(length=1000) public String details;
 @Column(name="created_at",nullable=false) public Instant createdAt=Instant.now();
 public TaskActivity(){} public TaskActivity(Task task,AppUser user,String action,String details){this.task=task;this.user=user;this.action=action;this.details=details;}
}
