package com.cst438.domain;

import org.springframework.data.repository.CrudRepository;
import java.util.List;

public interface StudentRepository extends CrudRepository <Student, Integer> {
	
	public List<Student> findAll();
	// declare the following method to return a single Student object
	// default JPA behavior that findBy methods return List<Student> except for findById.
	public Student findByEmail(String email);

}
