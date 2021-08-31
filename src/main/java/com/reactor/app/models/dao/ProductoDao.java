package com.reactor.app.models.dao;

import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;

import com.reactor.app.models.documents.Producto;

import reactor.core.publisher.Mono;

public interface ProductoDao extends ReactiveMongoRepository<Producto, String>{

	
	Mono<Producto> findByNombre(String nombre);
	
	@Query("{ 'nombre': ?0 }")
	Mono<Producto> obtenerPorNombre(String nombre);
	
}
