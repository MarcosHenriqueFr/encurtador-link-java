package com.example.encurtadorlink.repositories;

import com.example.encurtadorlink.model.LogAccess;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LogAccessRepository extends JpaRepository<LogAccess, Long> {

}
