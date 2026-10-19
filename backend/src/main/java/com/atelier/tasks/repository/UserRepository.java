package com.atelier.tasks.repository;
import com.atelier.tasks.entity.AppUser;import org.springframework.data.jpa.repository.JpaRepository;import java.util.*;
public interface UserRepository extends JpaRepository<AppUser,Long>{Optional<AppUser> findByEmailIgnoreCase(String email);}
