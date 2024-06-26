package com.example.learning_api.controller;

import com.example.learning_api.constant.StatusCode;
import com.example.learning_api.dto.request.student.CreateStudentRequest;
import com.example.learning_api.dto.request.student.UpdateStudentRequest;
import com.example.learning_api.dto.response.student.CreateStudentResponse;
import com.example.learning_api.dto.response.student.GetStudentsResponse;
import com.example.learning_api.model.ResponseAPI;
import com.example.learning_api.service.core.IStudentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Date;

import static com.example.learning_api.constant.RouterConstant.*;

@RestController
@RequiredArgsConstructor
@RequestMapping(STUDENT_BASE_PATH)
@Slf4j
public class StudentController {
    private final IStudentService studentService;
    @PostMapping(path = "")
    public ResponseEntity<ResponseAPI<CreateStudentResponse>> createStudent(@RequestBody @Valid CreateStudentRequest body) {
        try{
            CreateStudentResponse resDate = studentService.createStudent(body);
            ResponseAPI<CreateStudentResponse> res = ResponseAPI.<CreateStudentResponse>builder()
                    .timestamp(new Date())
                    .message("Create student successfully")
                    .data(resDate)
                    .build();
            return new ResponseEntity<>(res, StatusCode.CREATED);
        }
        catch (Exception e){
            ResponseAPI<CreateStudentResponse> res = ResponseAPI.<CreateStudentResponse>builder()
                    .timestamp(new Date())
                    .message(e.getMessage())
                    .build();
            return new ResponseEntity<>(res, StatusCode.BAD_REQUEST);
        }

    }
    @PatchMapping(path = "/{studentId}")
    public ResponseEntity<ResponseAPI<String>> updateStudent(@RequestBody @Valid UpdateStudentRequest body, @PathVariable String studentId) {
        try{
            body.setId(studentId);
            studentService.updateStudent(body);
            ResponseAPI<String> res = ResponseAPI.<String>builder()
                    .timestamp(new Date())
                    .message("Update student successfully")
                    .build();
            return new ResponseEntity<>(res, StatusCode.OK);
        }
        catch (Exception e){
            ResponseAPI<String> res = ResponseAPI.<String>builder()
                    .timestamp(new Date())
                    .message(e.getMessage())
                    .build();
            return new ResponseEntity<>(res, StatusCode.BAD_REQUEST);
        }

    }
    @DeleteMapping(path = "/{studentId}")
    @PreAuthorize("hasAnyAuthority('ADMIN')")
    public ResponseEntity<ResponseAPI<String>> deleteStudent(@PathVariable String studentId) {
        try{
            studentService.deleteStudent(studentId);
            ResponseAPI<String> res = ResponseAPI.<String>builder()
                    .timestamp(new Date())
                    .message("Delete student successfully")
                    .build();
            return new ResponseEntity<>(res, StatusCode.OK);
        }
        catch (Exception e){
            ResponseAPI<String> res = ResponseAPI.<String>builder()
                    .timestamp(new Date())
                    .message(e.getMessage())
                    .build();
            return new ResponseEntity<>(res, StatusCode.BAD_REQUEST);
        }

    }
    @GetMapping(path = "")
    public ResponseEntity<ResponseAPI<GetStudentsResponse>> getStudents(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "") String search
    ) {
        try{
            GetStudentsResponse studentsResponse = studentService.getStudents(page-1, size, search);
            ResponseAPI<GetStudentsResponse> res = ResponseAPI.<GetStudentsResponse>builder()
                    .timestamp(new Date())
                    .message("Get students successfully")
                    .data(studentsResponse)
                    .build();
            return new ResponseEntity<>(res, StatusCode.OK);
        }
        catch (Exception e){
            ResponseAPI<GetStudentsResponse> res = ResponseAPI.<GetStudentsResponse>builder()
                    .timestamp(new Date())
                    .message(e.getMessage())
                    .build();
            return new ResponseEntity<>(res, StatusCode.BAD_REQUEST);
        }

    }
}
