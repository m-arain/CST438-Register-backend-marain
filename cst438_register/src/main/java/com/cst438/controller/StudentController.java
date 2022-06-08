package com.cst438.controller;

import com.cst438.domain.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import java.util.ArrayList;
import java.util.List;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;

@RestController
@CrossOrigin(origins = "http://localhost:3000")
public class StudentController {

    @Autowired
    StudentRepository studentRepository;
    
    @Autowired
    CourseRepository courseRepository;

    @GetMapping("/student")
    @Transactional
    public List<StudentDTO> getStudents() {
        List<Student> students =studentRepository.findAll();

        List<StudentDTO> studentsDTO = new ArrayList<StudentDTO>();

        for (Student s:
             students) {
            studentsDTO.add(createStudentDTO(s));
        }

        return studentsDTO;
    }
    
    @PostMapping("/student")
    @Transactional
    public StudentDTO addStudent(@RequestBody StudentDTO studentDTO, @AuthenticationPrincipal OAuth2User principal) {
        String adminEmail = principal.getAttribute("email");

        if (!isEmailAdmin(adminEmail)) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "You are not authorized to access this resource.");
        }

        Student existingStudent = studentRepository.findByEmail(studentDTO.email);

        if (existingStudent == null) {
            Student student = new Student();
            student.setName(studentDTO.name);
            student.setEmail(studentDTO.email);
            student.setStatus(studentDTO.status);
            student.setStatusCode(studentDTO.status_code);
            Student savedStudent = studentRepository.save(student);

            return createStudentDTO(savedStudent);
        } else {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Student email is already registered: '" + studentDTO.email + "'.");
        }
    }

    @PostMapping("/student/placeHold")
    @Transactional
    public StudentDTO placeStudentHold(@RequestBody StudentDTO studentDTO, @AuthenticationPrincipal OAuth2User principal) {
        String adminEmail = principal.getAttribute("email");
        if (!isEmailAdmin(adminEmail)) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "You are not authorized to access this resource.");
        }
        Student student = studentRepository.findByEmail(studentDTO.email);

        if (student != null) {
            student.setStatusCode(1);
            studentRepository.save(student);

            return createStudentDTO(student);
        } else {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Student email is invalid: '" + studentDTO.email + "'.");
        }
    }

    @PostMapping("/student/releaseHold")
    @Transactional
    public StudentDTO releaseStudentHold(@RequestBody StudentDTO studentDTO, @AuthenticationPrincipal OAuth2User principal) {
        String adminEmail = principal.getAttribute("email");
        if (!isEmailAdmin(adminEmail)) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "You are not authorized to access this resource.");
        }
        Student student = studentRepository.findByEmail(studentDTO.email);

        if (student != null) {
            student.setStatusCode(0);
            studentRepository.save(student);

            return createStudentDTO(student);
        } else {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Student email is invalid: '" + studentDTO.email + "'.");
        }
    }

    private StudentDTO createStudentDTO(Student s) {
        StudentDTO studentDTO = new StudentDTO();
        studentDTO.student_id = String.valueOf(s.getStudent_id());
        studentDTO.name = s.getName();
        studentDTO.email = s.getEmail();
        studentDTO.status = s.getStatus();
        studentDTO.status_code = s.getStatusCode();

        return studentDTO;
    }
    
    private boolean isEmailAdmin(String email) {
        Student student = studentRepository.findByEmail(email);
        if (student != null) {
            return false;
        }

        Course course = courseRepository.findByInstructor(email);
        if (course != null) {
            return false;
        }

        return true;
    }
}