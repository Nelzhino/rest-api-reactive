package com.reactor.app.controllers;

import java.io.File;
import java.net.URI;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.support.WebExchangeBindException;

import com.reactor.app.models.documents.Producto;
import com.reactor.app.models.services.ProductoService;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("api/product")
public class ProductoController {

	@Autowired
	private ProductoService service;
	
	@Value("${config.uploads.path}")
	private String path;
	
	
	@GetMapping
	public Mono<ResponseEntity<Flux<Producto>>> list() {
		return Mono.just(
				ResponseEntity.ok()
					.contentType(MediaType.APPLICATION_JSON)
					.body(service.findAll())
				).defaultIfEmpty(ResponseEntity.notFound().build());
	}
	
	
	@GetMapping("/{id}")
	public Mono<ResponseEntity<Producto>> show(@PathVariable String id) {
		return service.findById(id).map(p  -> ResponseEntity.ok()
				.contentType(null)
				.body(p))
				.defaultIfEmpty(ResponseEntity.notFound().build());
	}
	
	
	@PostMapping
	public Mono<ResponseEntity<Map<String, Object>>> create(@Valid @RequestBody Mono<Producto> monoProducto) {
		
		Map<String, Object> response = new HashMap<String, Object>();
		
		
		return monoProducto.flatMap( producto -> {
			
			if(producto.getCreateAt() == null) {
				producto.setCreateAt(new Date());
			}
			
			return service.save(producto).map( p -> {				
				response.put("producto", p);
				response.put("message", "Product created successfully!");
				response.put("timestamp", new Date());
				
				return ResponseEntity
						.created(URI.create("/api/producto/".concat(p.getId())))
						.contentType(MediaType.APPLICATION_JSON)
						.body(response);
				
			});
			
			
		}).onErrorResume( t -> {
			return Mono.just(t).cast(WebExchangeBindException.class)
					.flatMap(e -> Mono.just(e.getFieldErrors()))
					.flatMapMany(Flux::fromIterable)
					.map(fieldError -> String.format("The field %s %s", fieldError.getField(), fieldError.getDefaultMessage()))
					.collectList()
					.flatMap( list -> {
						response.put("errors", list);
						response.put("timestamp", new Date());
						response.put("status", HttpStatus.BAD_REQUEST.value());
						
						return Mono.just(ResponseEntity.badRequest().body(response));
					});
		});
		
	}
	
	
	@PutMapping
	public Mono<ResponseEntity<Producto>> update(@RequestBody Producto producto, @PathVariable String id) {
		
		return service.findById(id).flatMap(p -> {
			p.setNombre(producto.getNombre());
			p.setPrecio(producto.getPrecio());
			p.setCategoria(producto.getCategoria());
			return service.save(p);
		}).map( p -> 
		ResponseEntity.created(URI.create("/api/producto/".concat(p.getId())))
		.contentType(MediaType.APPLICATION_JSON)
		.body(p))
				.defaultIfEmpty(ResponseEntity.notFound().build());
		
	}
	
	
	@DeleteMapping("/{id}")
	public Mono<ResponseEntity<Void>> delete(@PathVariable String id) {
		return service.findById(id).flatMap( p -> {
			return service.delete(p).then(Mono.just(new ResponseEntity<Void>(HttpStatus.NO_CONTENT)));
		}).defaultIfEmpty(new ResponseEntity<Void>(HttpStatus.NOT_FOUND));
	}
	
	
	@PostMapping("/upload/{id}")
	public Mono<ResponseEntity<Producto>> upload(@PathVariable String id, @RequestPart FilePart file) {
		return service.findById(id).flatMap(p -> {
			p.setFoto(UUID.randomUUID().toString() + " - "+ file.filename()
			.replace(" ", "")
			.replace(".", "")
			.replace("\\", ""));
			
			return file.transferTo(new File(path + p.getFoto())).then(service.save(p));
		}).map(p -> ResponseEntity.ok(p))
		.defaultIfEmpty(ResponseEntity.notFound().build());
	}
	
	
	@PostMapping("/v2")
	public Mono<ResponseEntity<Producto>> create(Producto producto, @RequestPart FilePart file) {
		
		if(producto.getCreateAt() == null) {
			producto.setCreateAt(new Date());
		}
		
		producto.setFoto(UUID.randomUUID().toString() + "-" + file.filename()
			.replace(" ", "")
			.replace(":", "")
			.replace("\\", "")
		);
		
		return file.transferTo(new File(path + producto.getFoto())).then(service.save(producto))
				.map( p ->  ResponseEntity
						.created(URI.create("/api/producto/".concat(p.getId())))
						.contentType(MediaType.APPLICATION_JSON)
						.body(p));
	}
	
	
	
}
