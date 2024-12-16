package com.ksk.chunkAndTasklet.model;

import java.io.Serializable;
import java.time.LocalDate;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Line implements Serializable{

	private String name;
	private LocalDate dob;
	private Long age;
}
