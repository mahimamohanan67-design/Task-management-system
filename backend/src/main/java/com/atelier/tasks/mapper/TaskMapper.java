package com.atelier.tasks.mapper;
import com.atelier.tasks.dto.ApiDtos.TaskView;import com.atelier.tasks.entity.Task;
public final class TaskMapper {
 private TaskMapper(){}
 public static TaskView toView(Task t){return new TaskView(t.id,t.title,t.description,t.category==null?"Uncategorized":t.category.name,t.category==null?null:t.category.id,t.priority,t.status,t.startDate,t.dueDate,t.completedAt,t.createdAt,t.updatedAt,t.timeOfDay,t.tags.stream().map(x->x.name).toList());}
}
