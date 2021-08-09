package com.reactor.app.models.dao;



import org.springframework.data.mongodb.repository.ReactiveMongoRepository;

import com.reactor.app.models.documents.Categoria;

public interface CategoriaDao extends ReactiveMongoRepository<Categoria, String>{

}
