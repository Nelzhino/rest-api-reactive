package com.reactor.app;

import java.util.Collections;
import java.util.List;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.reactive.server.WebTestClient;

import com.reactor.app.models.documents.Categoria;
import com.reactor.app.models.documents.Producto;
import com.reactor.app.models.services.ProductoService;

import reactor.core.publisher.Mono;

@AutoConfigureWebTestClient
@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
class SpringWebfluxApirestApplicationTests {

	@Autowired
	private WebTestClient client;
	
	@Autowired
	private ProductoService productoService;
	
	
	
	@Test
	void listarTest() {
		
		client.get().uri("/api/v2/productos")
		.accept(MediaType.APPLICATION_JSON)
		.exchange()
		.expectStatus().isOk()
		.expectHeader().contentType(MediaType.APPLICATION_JSON)
		.expectBodyList(Producto.class)
		.consumeWith(response -> {
			List<Producto> productos = response.getResponseBody();
			productos.forEach( p -> {
				System.out.println(p.getNombre());
			});
			
			
			Assertions.assertTrue(productos.size() > 0);
			
		});
		// .hasSize(8);
		
	}
	
	
	
	@Test
	void verTest() {		
		Producto producto = productoService.findByNombre("TV Panasonic Pantalla LCD").block();
		
		client.get()
		.uri("/api/v2/productos/{id}", Collections.singletonMap("id", producto.getId()))
		.accept(MediaType.APPLICATION_JSON)
		.exchange()
		.expectStatus().isOk()
		.expectHeader().contentType(MediaType.APPLICATION_JSON)
		.expectBody(Producto.class)
		.consumeWith( response -> {
			
			Producto productoReponse = response.getResponseBody();
			Assertions.assertNotNull(productoReponse.getId());
			Assertions.assertEquals(productoReponse.getNombre(), "TV Panasonic Pantalla LCD");
			
			
		});
//		.expectBody()
//		.jsonPath("$.id").isNotEmpty()
//		.jsonPath("$.nombre").isEqualTo("TV Panasonic Pantalla LCD");
		
		
	}
	
	@Test
	void crearTest() {		
		
		Categoria categoria = productoService.findByCategoriaNombre("Muebles").block();
		
		Producto producto = new Producto("Mesa para portatil", 100.00, categoria);
		
		client.post()
		.uri("/api/v2/productos", Collections.singletonMap("id", producto.getId()))
		.contentType(MediaType.APPLICATION_JSON)
		.accept(MediaType.APPLICATION_JSON)
		.body(Mono.just(producto), Producto.class)
		.exchange()
		.expectStatus().isCreated()
		.expectHeader().contentType(MediaType.APPLICATION_JSON)
		.expectBody(Producto.class)
		.consumeWith( response -> {
			Producto productoResponse = response.getResponseBody();
			Assertions.assertNotNull(productoResponse.getId());
			Assertions.assertEquals(productoResponse.getNombre(), "Mesa para portatil");
			Assertions.assertEquals(productoResponse.getCategoria().getNombre(), "Muebles");
			
		});
//		.expectBody()
//		.jsonPath("$.id").isNotEmpty()
//		.jsonPath("$.nombre").isEqualTo("Mesa para portatil")
//		.jsonPath("$.categoria.nombre").isEqualTo("Muebles");
	}


	@Test
	void editarTest() {

		Producto findProducto = productoService.findByNombre("HP Notebook Omen 17").block();
		Categoria categoria = productoService.findByCategoriaNombre("Electrónico").block();
		Producto producto = new Producto("MacBook Air 13 M1", 1500.00, categoria);

		client.post()
				.uri("/api/v2/productos/{id}", Collections.singletonMap("id", producto.getId()))
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON)
				.body(Mono.just(producto), Producto.class)
				.exchange()
				.expectStatus().isCreated()
				.expectBody()
				.jsonPath("$.id").isNotEmpty()
				.jsonPath("$.nombre").isEqualTo("MacBook Air 13 M1")
				.jsonPath("$.categoria.nombre").isEqualTo("Electrónico");

	}

	@Test
	void eliminarTest() {
		Producto producto = productoService.findByNombre("Apple iPod").block();

		client.delete()
				.uri("/api/v2/productos/{id}", Collections.singletonMap("id", producto.getId()))
				.exchange()
				.expectStatus().isNoContent()
				.expectBody().isEmpty();

		client.get()
				.uri("/api/v2/productos/{id}", Collections.singletonMap("id", producto.getId()))
				.exchange()
				.expectStatus().isNotFound()
				.expectBody().isEmpty();
	}


	
}
