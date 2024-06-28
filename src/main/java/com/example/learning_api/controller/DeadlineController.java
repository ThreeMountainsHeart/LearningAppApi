package com.example.learning_api.controller;

import com.example.learning_api.constant.StatusCode;
import com.example.learning_api.dto.request.deadline.CreateDeadlineRequest;
import com.example.learning_api.dto.request.deadline.CreateDeadlineSubmissionsRequest;
import com.example.learning_api.dto.request.deadline.UpdateDeadlineRequest;
import com.example.learning_api.dto.request.deadline.UpdateDeadlineSubmissionsRequest;
import com.example.learning_api.dto.response.deadline.GetDeadlineSubmissionsResponse;
import com.example.learning_api.dto.response.deadline.GetDeadlinesResponse;
import com.example.learning_api.entity.sql.database.DeadlineEntity;
import com.example.learning_api.entity.sql.database.DeadlineSubmissionsEntity;
import com.example.learning_api.model.ResponseAPI;
import com.example.learning_api.service.core.IDeadlineService;
import com.example.learning_api.service.core.IDeadlineSubmissionsService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Date;

import static com.example.learning_api.constant.RouterConstant.*;

@RestController
@RequiredArgsConstructor
@RequestMapping(DEADLINE_BASE_PATH)
@Slf4j
public class DeadlineController {
    private final IDeadlineService deadlineService;
    private final IDeadlineSubmissionsService deadlineSubmissionsService;
    @PostMapping(path = "", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasAnyAuthority('ADMIN','TEACHER')")
    public ResponseEntity<ResponseAPI<String>> createDeadline(@ModelAttribute @Valid CreateDeadlineRequest body) {
        try{
            deadlineService.createDeadline(body);
            ResponseAPI<String> res = ResponseAPI.<String>builder()
                    .timestamp(new Date())
                    .message("Create deadline successfully")
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

    @PatchMapping(path = "/{deadlineId}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasAnyAuthority('ADMIN','TEACHER')")
    public ResponseEntity<ResponseAPI<String>> updateDeadline(@ModelAttribute @Valid UpdateDeadlineRequest body, @PathVariable String deadlineId) {
        try{
            body.setId(deadlineId);
            deadlineService.updateDeadline(body);
            ResponseAPI<String> res = ResponseAPI.<String>builder()
                    .timestamp(new Date())
                    .message("Update deadline successfully")
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


    @DeleteMapping(path = "/{deadlineId}")
    @PreAuthorize("hasAnyAuthority('ADMIN','TEACHER')")
    public ResponseEntity<ResponseAPI<String>> deleteDeadline(@PathVariable String deadlineId) {
        try{
             deadlineService.deleteDeadline(deadlineId);
            ResponseAPI<String> res = ResponseAPI.<String>builder()
                    .timestamp(new Date())
                    .message("Delete deadline successfully")
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

    @GetMapping(path = "/{deadlineId}")
    public ResponseEntity<ResponseAPI<DeadlineEntity>> getDeadline(@PathVariable String deadlineId) {
        try{
           DeadlineEntity data =  deadlineService.getDeadline(deadlineId);
            ResponseAPI<DeadlineEntity> res = ResponseAPI.<DeadlineEntity>builder()
                    .timestamp(new Date())
                    .message("Get deadline successfully")
                    .data(data)
                    .build();
            return new ResponseEntity<>(res, StatusCode.OK);
        }
        catch (Exception e){
            ResponseAPI<DeadlineEntity> res = ResponseAPI.<DeadlineEntity>builder()
                    .timestamp(new Date())
                    .message(e.getMessage())
                    .build();
            return new ResponseEntity<>(res, StatusCode.BAD_REQUEST);
        }

    }

    @GetMapping(path = "/lesson/{lessonId}")
    public ResponseEntity<ResponseAPI<GetDeadlinesResponse>> getDeadlines(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            @PathVariable String lessonId)  {
        try{
            GetDeadlinesResponse data =  deadlineService.getDeadlinesByClassroomId(lessonId, page-1, size);
            ResponseAPI<GetDeadlinesResponse> res = ResponseAPI.<GetDeadlinesResponse>builder()
                    .timestamp(new Date())
                    .message("Get deadlines successfully")
                    .data(data)
                    .build();
            return new ResponseEntity<>(res, StatusCode.OK);
        }
        catch (Exception e){
            ResponseAPI<GetDeadlinesResponse> res = ResponseAPI.<GetDeadlinesResponse>builder()
                    .timestamp(new Date())
                    .message(e.getMessage())
                    .build();
            return new ResponseEntity<>(res, StatusCode.BAD_REQUEST);
        }

    }

    @PostMapping(path = "/{deadlineId}/submissions", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasAnyAuthority('USER')")
    public ResponseEntity<ResponseAPI<String>> createDeadlineSubmissions(@ModelAttribute @Valid CreateDeadlineSubmissionsRequest body){
        try{
            body.setDeadlineId(body.getDeadlineId());
            deadlineSubmissionsService.CreateDeadlineSubmissions(body);
            ResponseAPI<String> res = ResponseAPI.<String>builder()
                    .timestamp(new Date())
                    .message("Create deadline submissions successfully")
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

    @PatchMapping(path = "/{deadlineId}/submissions/{submissionId}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ResponseAPI<String>> updateDeadlineSubmissions(@ModelAttribute @Valid UpdateDeadlineSubmissionsRequest body, @PathVariable String submissionId) {
        try{
            body.setId(submissionId);
            deadlineSubmissionsService.UpdateDeadlineSubmissions(body);
            ResponseAPI<String> res = ResponseAPI.<String>builder()
                    .timestamp(new Date())
                    .message("Update deadline submissions successfully")
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

    @DeleteMapping(path = "/{deadlineId}/submissions/{submissionId}")
    public ResponseEntity<ResponseAPI<String>> deleteDeadlineSubmissions(@PathVariable String submissionId) {
        try{
            deadlineSubmissionsService.DeleteDeadlineSubmissions(submissionId);
            ResponseAPI<String> res = ResponseAPI.<String>builder()
                    .timestamp(new Date())
                    .message("Delete deadline submissions successfully")
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

    @GetMapping(path = "/{deadlineId}/submissions/{submissionId}")
    public ResponseEntity<ResponseAPI<DeadlineSubmissionsEntity>> getDeadlineSubmissions(@PathVariable String submissionId) {
        try{
            DeadlineSubmissionsEntity data =  deadlineSubmissionsService.GetDeadlineSubmissions(submissionId);
            ResponseAPI<DeadlineSubmissionsEntity> res = ResponseAPI.<DeadlineSubmissionsEntity>builder()
                    .timestamp(new Date())
                    .message("Get deadline submissions successfully")
                    .data(data)
                    .build();
            return new ResponseEntity<>(res, StatusCode.OK);
        }
        catch (Exception e){
            ResponseAPI<DeadlineSubmissionsEntity> res = ResponseAPI.<DeadlineSubmissionsEntity>builder()
                    .timestamp(new Date())
                    .message(e.getMessage())
                    .build();
            return new ResponseEntity<>(res, StatusCode.BAD_REQUEST);
        }

    }

    @GetMapping(path = "/{deadlineId}/submissions")
    public ResponseEntity<ResponseAPI<GetDeadlineSubmissionsResponse>> getDeadlineSubmissionsByDeadlineId(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            @PathVariable String deadlineId)  {
        try{
            GetDeadlineSubmissionsResponse data =  deadlineSubmissionsService.GetDeadlineSubmissionsByDeadlineId(deadlineId, page-1, size);
            ResponseAPI<GetDeadlineSubmissionsResponse> res = ResponseAPI.<GetDeadlineSubmissionsResponse>builder()
                    .timestamp(new Date())
                    .message("Get deadline submissions by deadlineId successfully")
                    .data(data)
                    .build();
            return new ResponseEntity<>(res, StatusCode.OK);
        }
        catch (Exception e){
            ResponseAPI<GetDeadlineSubmissionsResponse> res = ResponseAPI.<GetDeadlineSubmissionsResponse>builder()
                    .timestamp(new Date())
                    .message(e.getMessage())
                    .build();
            return new ResponseEntity<>(res, StatusCode.BAD_REQUEST);
        }

    }
}
