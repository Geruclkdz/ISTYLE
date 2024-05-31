package com.istyle.backend.repository;

import com.istyle.backend.api.internal.Type;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository
public interface TypesRepository extends JpaRepository<Type, Integer> {
}