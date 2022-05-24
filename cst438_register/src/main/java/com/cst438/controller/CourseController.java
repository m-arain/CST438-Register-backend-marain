package com.cst438.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import com.cst438.domain.Course;
import com.cst438.domain.CourseDTOG;
import com.cst438.domain.CourseRepository;
import com.cst438.domain.Enrollment;
import com.cst438.domain.EnrollmentRepository;
import com.cst438.domain.Student;
import com.cst438.domain.StudentRepository;

@RestController
public class CourseController {
	
	@Autowired
	EnrollmentRepository enrollmentRepository;
	
	@Autowired
	StudentRepository studentRepository;

	@Autowired
	CourseRepository courseRepository;
	/*
	 * endpoint used by gradebook service to transfer final course grades
	 */
	@PutMapping("/course/{course_id}")
	@Transactional
	public void updateCourseGrades( @RequestBody CourseDTOG courseDTOG, @PathVariable("course_id") int course_id) {
		
		//TODO  complete this method in homework 4
		for (CourseDTOG.GradeDTO g : courseDTOG.grades) {
			Enrollment tempEnrollment = enrollmentRepository.findByEmailAndCourseId(g.student_email, course_id);

			if(tempEnrollment != null) {
				tempEnrollment.setCourseGrade(g.grade);
			}
			else {
				throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Could not find enrollment record.");
			}
		}
	}

}
