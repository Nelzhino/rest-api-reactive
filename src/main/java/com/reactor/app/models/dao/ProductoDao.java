package com.reactor.app.models.dao;

import org.springframework.data.mongodb.repository.ReactiveMongoRepository;

import com.reactor.app.models.documents.Producto;

public interface ProductoDao extends ReactiveMongoRepository<Producto, String>{

}