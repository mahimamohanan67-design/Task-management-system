package com.atelier.tasks.service;
import com.atelier.tasks.dto.ApiDtos.*;import com.atelier.tasks.entity.*;import com.atelier.tasks.repository.*;import org.springframework.http.HttpStatus;import org.springframework.stereotype.Service;import org.springframework.transaction.annotation.Transactional;import org.springframework.web.server.ResponseStatusException;import java.util.*;
@Service @Transactional public class CategoryService {
 private final CategoryRepository categories;private final TaskRepository tasks;public CategoryService(CategoryRepository c,TaskRepository t){categories=c;tasks=t;}
 public List<CategoryView> list(AppUser user){return categories.findAllByOwnerIdOrderByNameAsc(user.id).stream().map(this::view).toList();}
 public CategoryView create(AppUser user,CategoryInput in){if(categories.findByNameIgnoreCaseAndOwnerId(in.name().trim(),user.id).isPresent())throw new ResponseStatusException(HttpStatus.CONFLICT,"Category already exists");Category c=new Category();c.owner=user;apply(c,in);return view(categories.save(c));}
 public CategoryView update(AppUser user,Long id,CategoryInput in){Category c=owned(user,id);categories.findByNameIgnoreCaseAndOwnerId(in.name().trim(),user.id).filter(other->!other.id.equals(id)).ifPresent(x->{throw new ResponseStatusException(HttpStatus.CONFLICT,"Category already exists");});apply(c,in);return view(c);}
 public void delete(AppUser user,Long id){Category c=owned(user,id);categories.delete(c);}
 private Category owned(AppUser u,Long id){return categories.findByIdAndOwnerId(id,u.id).orElseThrow(()->new ResponseStatusException(HttpStatus.NOT_FOUND,"Category not found"));}
 private void apply(Category c,CategoryInput in){c.name=in.name().trim();c.description=in.description();c.color=in.color();c.icon=in.icon();}
 private CategoryView view(Category c){return new CategoryView(c.id,c.name,c.description,c.color,c.icon,c.createdAt,tasks.countByCategoryId(c.id));}
}
