package com.example.encurtadorlink.repositories;

import com.example.encurtadorlink.model.Link;
import com.example.encurtadorlink.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface LinkRepository extends JpaRepository<Link, Long> {
    Optional<Link> findByShortCode(String shortCode);
    List<Link> findByUser(User user);
}
