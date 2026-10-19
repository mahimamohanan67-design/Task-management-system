package com.atelier.tasks.dto;

import com.atelier.tasks.entity.*;import jakarta.validation.constraints.*;import java.time.*;import java.util.*;
public final class ApiDtos {
 private ApiDtos(){}
 public record UserView(Long id,String name,String email,String profileImageUrl,Instant createdAt) { public static UserView of(AppUser u){return new UserView(u.id,u.name,u.email,u.profileImageUrl,u.createdAt);} }
 public record AuthView(String token,UserView user){}
 public record TaskInput(@NotBlank @Size(max=180) String title,@Size(max=3000) String description,@Size(max=80) String category,
   Task.Priority priority,Task.TaskStatus status,LocalDate startDate,LocalDate dueDate,@Size(max=24) String timeOfDay,
   @Size(max=20) List<@NotBlank @Size(max=60) String> tags){@jakarta.validation.constraints.AssertTrue(message="dueDate must be on or after startDate") public boolean isDateRangeValid(){return startDate==null||dueDate==null||!dueDate.isBefore(startDate);}}
 public record TaskView(Long id,String title,String description,String category,Long categoryId,Task.Priority priority,Task.TaskStatus status,
   LocalDate startDate,LocalDate dueDate,Instant completedAt,Instant createdAt,Instant updatedAt,String timeOfDay,List<String> tags) {}
 public record CategoryInput(@NotBlank @Size(max=80) String name,@Size(max=500) String description,@NotBlank @Size(max=32) String color,@NotBlank @Size(max=64) String icon){}
 public record CategoryView(Long id,String name,String description,String color,String icon,Instant createdAt,long taskCount){}
 public record NotificationView(Long id,Long taskId,String title,String message,String type,boolean read,Instant createdAt){}
 public record ActivityView(Long id,Long taskId,String taskTitle,String action,String details,Instant createdAt){}
 public record Stats(long total,long completed,long pending,long inProgress,long upcomingDeadlines,double completionRate){}
 public record PageView<T>(List<T> content,int page,int size,long totalElements,int totalPages,boolean first,boolean last){}
}
