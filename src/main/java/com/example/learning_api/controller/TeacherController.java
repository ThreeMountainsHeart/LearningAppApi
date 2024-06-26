package com.example.learning_api.controller;

import com.example.learning_api.constant.StatusCode;
import com.example.learning_api.dto.request.teacher.CreateTeacherRequest;
import com.example.learning_api.dto.request.teacher.UpdateTeacherRequest;
import com.example.learning_api.dto.response.teacher.CreateTeacherResponse;
import com.example.learning_api.dto.response.teacher.GetTeachersResponse;
import com.example.learning_api.model.ResponseAPI;
import com.example.learning_api.service.core.ITeacherService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Date;

import static com.example.learning_api.constant.RouterConstant.*;

@RestController
@RequiredArgsConstructor
@RequestMapping(TEACHER_BASE_PATH)
@Slf4j
public class TeacherController {
    private final ITeacherService teacherService;

    @PostMapping(path = "")
    public ResponseEntity<ResponseAPI<CreateTeacherResponse>> createTeacher(@RequestBody @Valid CreateTeacherRequest body) {
        try{
            CreateTeacherResponse resDate = teacherService.createTeacher(body);
            ResponseAPI<CreateTeacherResponse> res = ResponseAPI.<CreateTeacherResponse>builder()
                    .timestamp(new Date())
                    .message("Create teacher successfully")
                    .data(resDate)
                    .build();
            return new ResponseEntity<>(res, StatusCode.CREATED);
        }
        catch (Exception e){
            ResponseAPI<CreateTeacherResponse> res = ResponseAPI.<CreateTeacherResponse>builder()
                    .timestamp(new Date())
                    .message(e.getMessage())
                    .build();
            return new ResponseEntity<>(res, StatusCode.BAD_REQUEST);
        }

    }
    @PatchMapping(path = "/{teacherId}")
    public ResponseEntity<ResponseAPI<String>> updateTeacher(@RequestBody @Valid UpdateTeacherRequest body, @PathVariable String teacherId) {
        try{
            body.setId(teacherId);
            teacherService.updateTeacher(body);
            ResponseAPI<String> res = ResponseAPI.<String>builder()
                    .timestamp(new Date())
                    .message("Update teacher successfully")
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
    @DeleteMapping(path = "/{teacherId}")
    public ResponseEntity<ResponseAPI<String>> deleteTeacher(@PathVariable String teacherId) {
        try{
            teacherService.deleteTeacher(teacherId);
            ResponseAPI<String> res = ResponseAPI.<String>builder()
                    .timestamp(new Date())
                    .message("Delete teacher successfully")
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
    public ResponseEntity<ResponseAPI<GetTeachersResponse>> getTeacher(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "") String search
    ) {
        try{
            GetTeachersResponse getTeachersResponse =  teacherService.getTeachers(page-1, size, search);
            ResponseAPI<GetTeachersResponse> res = ResponseAPI.<GetTeachersResponse>builder()
                    .timestamp(new Date())
                    .message("Get teacher successfully")
                    .data(getTeachersResponse)
                    .build();
            return new ResponseEntity<>(res, StatusCode.OK);
        }
        catch (Exception e){
            ResponseAPI<GetTeachersResponse> res = ResponseAPI.<GetTeachersResponse>builder()
                    .timestamp(new Date())
                    .message(e.getMessage())
                    .build();
            return new ResponseEntity<>(res, StatusCode.BAD_REQUEST);
        }

    }

    @PostMapping(path = "/{teacherId}/subject-specialization/{majorId}")
    public ResponseEntity<ResponseAPI<String>> addSubjectSpecialization(@PathVariable String teacherId, @PathVariable String majorId) {
        try{
            teacherService.addSubjectSpecialization(teacherId, majorId);
            ResponseAPI<String> res = ResponseAPI.<String>builder()
                    .timestamp(new Date())
                    .message("Add subject specialization successfully")
                    .build();
            return new ResponseEntity<>(res, StatusCode.CREATED);
        }
        catch (Exception e){
            ResponseAPI<String> res = ResponseAPI.<String>builder()
                    .timestamp(new Date())
                    .message(e.getMessage())
                    .build();
            return new ResponseEntity<>(res, StatusCode.BAD_REQUEST);
        }

    }

}
