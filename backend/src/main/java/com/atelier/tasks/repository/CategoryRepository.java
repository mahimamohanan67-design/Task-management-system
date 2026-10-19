package com.atelier.tasks.repository;
import com.atelier.tasks.entity.Category;import org.springframework.data.jpa.repository.JpaRepository;import java.util.*;
public interface CategoryRepository extends JpaRepository<Category,Long>{List<Category> findAllByOwnerIdOrderByNameAsc(Long ownerId);Optional<Category> findByIdAndOwnerId(Long id,Long ownerId);Optional<Category> findByNameIgnoreCaseAndOwnerId(String name,Long ownerId);}
