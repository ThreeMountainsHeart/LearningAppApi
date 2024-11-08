package com.example.learning_api.controller;

import com.example.learning_api.constant.StatusCode;
import com.example.learning_api.dto.request.classroom.CreateClassRoomRequest;

import com.example.learning_api.dto.request.classroom.ImportClassRoomRequest;
import com.example.learning_api.dto.request.classroom.UpdateClassRoomRequest;
import com.example.learning_api.dto.request.faculty.ImportFacultyRequest;
import com.example.learning_api.dto.response.classroom.*;
import com.example.learning_api.dto.response.section.GetSectionsResponse;
import com.example.learning_api.model.ResponseAPI;
import com.example.learning_api.repository.database.StudentRepository;
import com.example.learning_api.service.common.JwtService;
import com.example.learning_api.service.core.IClassRoomService;
import com.example.learning_api.service.core.IStudentService;
import com.example.learning_api.service.core.ITeacherService;
import com.example.learning_api.service.core.Impl.StudentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;

import static com.example.learning_api.constant.RouterConstant.*;

@RestController
@RequiredArgsConstructor
@RequestMapping(CLASSROOM_BASE_PATH)
@Slf4j
public class ClassRoomController {
    private final IClassRoomService classRoomService;
    private final JwtService jwtService;
    private final IStudentService studentService;
    private final ITeacherService teacherService;
    @PostMapping(path = "", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasAnyAuthority('ADMIN','TEACHER')")
    public ResponseEntity<ResponseAPI<CreateClassRoomResponse>> createClassRoom(@ModelAttribute @Valid CreateClassRoomRequest body,@CookieValue(name = "refreshToken", required = false) String refreshToken) {
        try{
            CreateClassRoomResponse resDate = classRoomService.createClassRoom(body);
            ResponseAPI<CreateClassRoomResponse> res = ResponseAPI.<CreateClassRoomResponse>builder()
                    .timestamp(new Date())
                    .message("Create class room successfully")
                    .data(resDate)
                    .build();
            return new ResponseEntity<>(res, StatusCode.CREATED);
        }
        catch (Exception e){
            ResponseAPI<CreateClassRoomResponse> res = ResponseAPI.<CreateClassRoomResponse>builder()
                    .timestamp(new Date())
                    .message(e.getMessage())
                    .build();
            return new ResponseEntity<>(res, StatusCode.BAD_REQUEST);
        }

    }

    @PatchMapping(path = "/{classroomId}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasAnyAuthority('ADMIN','TEACHER')")
    public ResponseEntity<ResponseAPI<String>> updateClassRoom(@ModelAttribute @Valid UpdateClassRoomRequest body, @PathVariable String classroomId) {
        try{
            body.setId(classroomId);
            classRoomService.updateClassRoom(body);
            ResponseAPI<String> res = ResponseAPI.<String>builder()
                    .timestamp(new Date())
                    .message("Update class room successfully")
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
    public ResponseEntity<ResponseAPI<GetClassRoomsResponse>> getClassRoom(
            @RequestParam(name="name",required = false,defaultValue = "") String search,
                                                            @RequestParam(name="page",required = false,defaultValue = "1") int page,
                                                            @RequestParam(name="size",required = false,defaultValue = "10") int size) {
        try{
            GetClassRoomsResponse resData = classRoomService.getClassRooms( page-1, size,search);
            ResponseAPI<GetClassRoomsResponse> res = ResponseAPI.<GetClassRoomsResponse>builder()
                    .timestamp(new Date())
                    .message("Get class room successfully")
                    .data(resData)
                    .build();
            return new ResponseEntity<>(res, StatusCode.OK);
        }
        catch (Exception e){
            ResponseAPI<GetClassRoomsResponse> res = ResponseAPI.<GetClassRoomsResponse>builder()
                    .timestamp(new Date())
                    .message(e.getMessage())
                    .build();
            return new ResponseEntity<>(res, StatusCode.BAD_REQUEST);
        }

    }
    @DeleteMapping(path = "/{classroomId}")
    @PreAuthorize("hasAnyAuthority('ADMIN')")
    public ResponseEntity<ResponseAPI<String>> deleteClassRoom(@PathVariable String classroomId) {
        try{
            classRoomService.deleteClassRoom(classroomId);
            ResponseAPI<String> res = ResponseAPI.<String>builder()
                    .timestamp(new Date())
                    .message("Delete class room successfully")
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

    @GetMapping(path = "/{classroomId}/sections")
    public ResponseEntity<ResponseAPI<GetSectionsResponse>> getSectionsByClassroomId(@PathVariable String classroomId
                                                                                     ) {
        try{
            GetSectionsResponse data= classRoomService.getSectionsByClassroomId(0,10,classroomId);
            ResponseAPI<GetSectionsResponse> res = ResponseAPI.<GetSectionsResponse>builder()
                    .timestamp(new Date())
                    .message("Get sections by classroomId successfully")
                    .data(data)
                    .build();
            return new ResponseEntity<>(res, StatusCode.OK);
        }
        catch (Exception e){
            ResponseAPI<GetSectionsResponse> res = ResponseAPI.<GetSectionsResponse>builder()
                    .timestamp(new Date())
                    .message(e.getMessage())
                    .build();
            return new ResponseEntity<>(res, StatusCode.BAD_REQUEST);
        }

    }


    @GetMapping(path = "/schedule-day/{studentId}")
    @PreAuthorize("hasAnyAuthority('USER')")
    public ResponseEntity<ResponseAPI<GetClassRoomsResponse>> getScheduleByDay(@PathVariable String studentId,
                                                                              @RequestParam(name="day",required = false,defaultValue = "") String day) {
        try{
            GetClassRoomsResponse data= classRoomService.getScheduleByDay(studentId,day);
            ResponseAPI<GetClassRoomsResponse> res = ResponseAPI.<GetClassRoomsResponse>builder()
                    .timestamp(new Date())
                    .message("Get schedule by day successfully")
                    .data(data)
                    .build();
            return new ResponseEntity<>(res, StatusCode.OK);
        }
        catch (Exception e){
            ResponseAPI<GetClassRoomsResponse> res = ResponseAPI.<GetClassRoomsResponse>builder()
                    .timestamp(new Date())
                    .message(e.getMessage())
                    .build();
            return new ResponseEntity<>(res, StatusCode.BAD_REQUEST);
        }

    }

    @GetMapping(path = "/schedule-week/{studentId}")
    @PreAuthorize("hasAnyAuthority('USER')")
    public ResponseEntity<ResponseAPI<List<GetScheduleResponse>>> getScheduleByStudentId(@PathVariable String studentId) {
        try{
            List<GetScheduleResponse> data= classRoomService.getScheduleByStudentId(studentId);
            ResponseAPI<List<GetScheduleResponse>> res = ResponseAPI.<List<GetScheduleResponse>>builder()
                    .timestamp(new Date())
                    .message("Get schedule by studentId successfully")
                    .data(data)
                    .build();
            return new ResponseEntity<>(res, StatusCode.OK);
        }
        catch (Exception e){
            ResponseAPI<List<GetScheduleResponse>> res = ResponseAPI.<List<GetScheduleResponse>>builder()
                    .timestamp(new Date())
                    .message(e.getMessage())
                    .build();
            return new ResponseEntity<>(res, StatusCode.BAD_REQUEST);
        }

    }

    @GetMapping(path = "/detail/{classroomId}")
    public ResponseEntity<ResponseAPI<GetClassRoomDetailResponse>> getClassRoomDetail(@PathVariable String classroomId) {
        try{
            GetClassRoomDetailResponse data= classRoomService.getClassRoomDetail(classroomId);
            ResponseAPI<GetClassRoomDetailResponse> res = ResponseAPI.<GetClassRoomDetailResponse>builder()
                    .timestamp(new Date())
                    .message("Get class room detail successfully")
                    .data(data)
                    .build();
            return new ResponseEntity<>(res, StatusCode.OK);
        }
        catch (Exception e){
            ResponseAPI<GetClassRoomDetailResponse> res = ResponseAPI.<GetClassRoomDetailResponse>builder()
                    .timestamp(new Date())
                    .message(e.getMessage())
                    .build();
            return new ResponseEntity<>(res, StatusCode.BAD_REQUEST);
        }

    }

    @GetMapping(path = "/invitation/{invitationCode}")
    public ResponseEntity<ResponseAPI<GetClassRoomDetailResponse>> getClassRoomByInvitationCode(@PathVariable String invitationCode) {
        try{
            GetClassRoomDetailResponse data= classRoomService.getClassRoomByInvitationCode(invitationCode);
            ResponseAPI<GetClassRoomDetailResponse> res = ResponseAPI.<GetClassRoomDetailResponse>builder()
                    .timestamp(new Date())
                    .message("Get class room by invitation code successfully")
                    .data(data)
                    .build();
            return new ResponseEntity<>(res, StatusCode.OK);
        }
        catch (Exception e){
            ResponseAPI<GetClassRoomDetailResponse> res = ResponseAPI.<GetClassRoomDetailResponse>builder()
                    .timestamp(new Date())
                    .message(e.getMessage())
                    .build();
            return new ResponseEntity<>(res, StatusCode.BAD_REQUEST);
        }

    }

    @GetMapping(path = "/invitation/{classroomId}/join")
    @PreAuthorize("hasAnyAuthority('USER')")
    public ResponseEntity<ResponseAPI<String>> joinClassRoom(@PathVariable String classroomId,
                                                             @RequestHeader(name = "Authorization") String authorizationHeader) {
        try {
            String accessToken = authorizationHeader.replace("Bearer ", "");
            String userId = jwtService.extractUserId(accessToken);
            String studentId = studentService.getStudentByUserId(userId).getId();
            classRoomService.joinClassRoom(classroomId, studentId);
            ResponseAPI<String> res = ResponseAPI.<String>builder()
                    .timestamp(new Date())
                    .message("Join class room successfully")
                    .build();
            return new ResponseEntity<>(res, StatusCode.OK);
        } catch (Exception e) {
            ResponseAPI<String> res = ResponseAPI.<String>builder()
                    .timestamp(new Date())
                    .message(e.getMessage())
                    .build();
            return new ResponseEntity<>(res, StatusCode.BAD_REQUEST);
        }
    }

    @GetMapping(path = "/invitation/{classroomId}/view-request")
    public ResponseEntity<ResponseAPI<GetJoinClassResponse>> getJoinClass(@PathVariable String classroomId,
                                                                          @RequestParam(name="page",required = false,defaultValue = "1") int page,
                                                                          @RequestParam(name="size",required = false,defaultValue = "10") int size,
                                                                          @RequestParam(name="email", required = false) String email,
                                                                          @RequestParam(name="name", required = false) String name,
                                                                          @RequestHeader(name = "Authorization") String authorizationHeader) {
        try {
            String accessToken = authorizationHeader.replace("Bearer ", "");
            String userId = jwtService.extractUserId(accessToken);
            String teacherId = teacherService.getTeacherByUserId(userId).getId();
            GetJoinClassResponse data = classRoomService.getJoinClassRequests( page - 1, size, classroomId, teacherId,email,name);
            ResponseAPI<GetJoinClassResponse> res = ResponseAPI.<GetJoinClassResponse>builder()
                    .timestamp(new Date())
                    .message("Get join class requests successfully")
                    .data(data)
                    .build();
            return new ResponseEntity<>(res, StatusCode.OK);
        } catch (Exception e) {
            ResponseAPI<GetJoinClassResponse> res = ResponseAPI.<GetJoinClassResponse>builder()
                    .timestamp(new Date())
                    .message(e.getMessage())
                    .build();
            return new ResponseEntity<>(res, StatusCode.BAD_REQUEST);
        }
    }

    @GetMapping(path = "/invitation/{classroomId}/accept-request/{studentId}")
    @PreAuthorize("hasAnyAuthority('TEACHER')")
    public ResponseEntity<ResponseAPI<String>> acceptJoinClass(@PathVariable String classroomId,
                                                               @PathVariable String studentId) {
        try {
            classRoomService.acceptJoinClass(classroomId, studentId);
            ResponseAPI<String> res = ResponseAPI.<String>builder()
                    .timestamp(new Date())
                    .message("Accept join class successfully")
                    .build();
            return new ResponseEntity<>(res, StatusCode.OK);
        } catch (Exception e) {
            ResponseAPI<String> res = ResponseAPI.<String>builder()
                    .timestamp(new Date())
                    .message(e.getMessage())
                    .build();
            return new ResponseEntity<>(res, StatusCode.BAD_REQUEST);
        }
    }

    @GetMapping(path = "/invitation/{classroomId}/reject-request/{studentId}")
    @PreAuthorize("hasAnyAuthority('TEACHER')")
    public ResponseEntity<ResponseAPI<String>> rejectJoinClass(@PathVariable String classroomId,
                                                               @PathVariable String studentId) {
        try {
            classRoomService.rejectJoinClass(classroomId, studentId);
            ResponseAPI<String> res = ResponseAPI.<String>builder()
                    .timestamp(new Date())
                    .message("Reject join class successfully")
                    .build();
            return new ResponseEntity<>(res, StatusCode.OK);
        } catch (Exception e) {
            ResponseAPI<String> res = ResponseAPI.<String>builder()
                    .timestamp(new Date())
                    .message(e.getMessage())
                    .build();
            return new ResponseEntity<>(res, StatusCode.BAD_REQUEST);
        }
    }

    @PostMapping(path = "/import", consumes = "multipart/form-data")
    @PreAuthorize("hasAnyAuthority('ADMIN')")
    public ResponseEntity<ResponseAPI<String>> importClass(@ModelAttribute @Valid ImportClassRoomRequest body) {
        try{
            classRoomService.importClassRoom(body);
            ResponseAPI<String> res = ResponseAPI.<String>builder()
                    .message("Import classrooms successfully")
                    .build();
            return ResponseEntity.ok(res);
        }
        catch (Exception e){
            log.error(e.getMessage());
            ResponseAPI<String> res = ResponseAPI.<String>builder()
                    .message(e.getMessage())
                    .build();
            return ResponseEntity.badRequest().body(res);
        }
    }

    @GetMapping(path = "/recent/student/{studentId}")
    @PreAuthorize("hasAnyAuthority('USER')")
    public ResponseEntity<ResponseAPI<GetClassRoomRecentResponse>> getRecentClassRooms(@RequestParam(name="page",required = false,defaultValue = "1") int page,
                                                                                       @RequestParam(name="size",required = false,defaultValue = "10") int size,
                                                                                       @PathVariable(name="studentId",required = true) String studentId) {
        try{
            GetClassRoomRecentResponse resData = classRoomService.getRecentClasses( page-1, size,studentId);
            ResponseAPI<GetClassRoomRecentResponse> res = ResponseAPI.<GetClassRoomRecentResponse>builder()
                    .timestamp(new Date())
                    .message("Get recent class room successfully")
                    .data(resData)
                    .build();
            return new ResponseEntity<>(res, StatusCode.OK);
        }
        catch (Exception e){
            ResponseAPI<GetClassRoomRecentResponse> res = ResponseAPI.<GetClassRoomRecentResponse>builder()
                    .timestamp(new Date())
                    .message(e.getMessage())
                    .build();
            return new ResponseEntity<>(res, StatusCode.BAD_REQUEST);
        }

    }

    @GetMapping(path = "/recent/teacher/{teacherId}")
    @PreAuthorize("hasAnyAuthority('TEACHER')")
    public ResponseEntity<ResponseAPI<GetClassRoomRecentResponse>> getRecentClassRoomsByTeacherId(@RequestParam(name="page",required = false,defaultValue = "1") int page,
                                                                                       @RequestParam(name="size",required = false,defaultValue = "10") int size,
                                                                                       @PathVariable(name="teacherId",required = true) String teacherId) {
        try{
            GetClassRoomRecentResponse resData = classRoomService.getRecentClassesByTeacherId( page-1, size,teacherId);
            ResponseAPI<GetClassRoomRecentResponse> res = ResponseAPI.<GetClassRoomRecentResponse>builder()
                    .timestamp(new Date())
                    .message("Get recent class room by teacherId successfully")
                    .data(resData)
                    .build();
            return new ResponseEntity<>(res, StatusCode.OK);
        }
        catch (Exception e){
            ResponseAPI<GetClassRoomRecentResponse> res = ResponseAPI.<GetClassRoomRecentResponse>builder()
                    .timestamp(new Date())
                    .message(e.getMessage())
                    .build();
            return new ResponseEntity<>(res, StatusCode.BAD_REQUEST);
        }

    }




}
