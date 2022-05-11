package com.cst438.controller;

import com.cst438.domain.Student;
import com.cst438.domain.StudentDTO;
import com.cst438.domain.StudentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

@RestController
@CrossOrigin(origins = "http://localhost:3000")
public class StudentController {

    @Autowired
    StudentRepository studentRepository;

    @PostMapping("/student")
    @Transactional
    public StudentDTO addStudent(@RequestBody StudentDTO studentDTO) {
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
    public StudentDTO placeStudentHold(@RequestBody StudentDTO studentDTO) {
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
    public StudentDTO releaseStudentHold(@RequestBody StudentDTO studentDTO) {
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
}