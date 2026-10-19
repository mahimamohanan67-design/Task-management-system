package com.atelier.tasks.entity;
import jakarta.persistence.*;
@Entity @Table(name="task_tags")
public class TaskTag { @Id @GeneratedValue(strategy=GenerationType.IDENTITY) public Long id; @ManyToOne(fetch=FetchType.LAZY,optional=false) @JoinColumn(name="task_id",nullable=false) public Task task; @Column(nullable=false,length=60) public String name; public TaskTag(){} public TaskTag(Task task,String name){this.task=task;this.name=name;} }
