package com.atelier.tasks.entity;
import jakarta.persistence.*;import java.time.Instant;
@Entity @Table(name="notifications",indexes=@Index(name="idx_notifications_user_created",columnList="user_id,created_at"))
public class Notification {
 @Id @GeneratedValue(strategy=GenerationType.IDENTITY) public Long id;
 @ManyToOne(fetch=FetchType.LAZY,optional=false) @JoinColumn(name="user_id",nullable=false) public AppUser owner;
 @ManyToOne(fetch=FetchType.LAZY) @JoinColumn(name="task_id") public Task task;
 @Column(nullable=false,length=180) public String title; @Column(nullable=false,length=1000) public String message;
 @Column(nullable=false,length=32) public String type; @Column(name="is_read",nullable=false) public boolean read=false;
 @Column(name="created_at",nullable=false) public Instant createdAt=Instant.now();
}
