package com.demo.database.entity;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
public class Site {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	private String latitude;
	private String longitude;
}
