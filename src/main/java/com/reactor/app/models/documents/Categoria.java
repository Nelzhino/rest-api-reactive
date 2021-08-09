package com.reactor.app.models.documents;


import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@NoArgsConstructor
@Getter
@Setter
@Document(collection = "categorias")
public class Categoria {
	
	@Id
	@NotEmpty
	private String id;
	
	@NotBlank
	@Min(value = 3)
	private String nombre;
	
	public Categoria(String nombre) {
		this.nombre = nombre;
	}

	

}
