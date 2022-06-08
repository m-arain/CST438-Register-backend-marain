package com.cst438.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;

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
	public void updateCourseGrades( @RequestBody CourseDTOG courseDTOG, @PathVariable("course_id") int course_id,  @AuthenticationPrincipal OAuth2User principal) {
		
		//TODO  complete this method in homework 4
		// find course
				Course course = courseRepository.findByCourse_id(course_id);
				if (course == null) {
					throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Course ID is invalid: '" + course_id + "'.");
				}

				String instructorEmail = principal.getAttribute("email");
				if (!course.getInstructor().equals(instructorEmail)) {
					throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "You are not authorized to access this resource.");
				}

				// update grades in enrollment table
				for (CourseDTOG.GradeDTO gradeDTO : courseDTOG.grades) {
					// find student
					Student student = studentRepository.findByEmail(gradeDTO.student_email);
					if (student == null) {
						throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Student email is invalid: '" + gradeDTO.student_email + "'.");
					}
					// create enrollment
					Enrollment enrollment = new Enrollment();
					enrollment.setStudent(student);
					enrollment.setCourse(course);
					enrollment.setCourseGrade(gradeDTO.grade);
					enrollment.setSemester(course.getSemester());
					// save enrollment to DB
					enrollmentRepository.save(enrollment);
				}
				
			}

}
