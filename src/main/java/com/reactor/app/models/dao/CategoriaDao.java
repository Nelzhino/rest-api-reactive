package com.reactor.app.models.dao;



import org.springframework.data.mongodb.repository.ReactiveMongoRepository;

import com.reactor.app.models.documents.Categoria;

import reactor.core.publisher.Mono;

public interface CategoriaDao extends ReactiveMongoRepository<Categoria, String>{
	
	Mono<Categoria> findByNombre(String nombre);

}
