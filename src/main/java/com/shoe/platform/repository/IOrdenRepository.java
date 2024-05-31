package com.shoe.platform.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.shoe.platform.model.Orden;

@Repository
public interface IOrdenRepository extends JpaRepository<Orden, Integer>{
	
}
