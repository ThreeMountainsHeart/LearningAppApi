package com.example.learning_api.service.core.Impl;

import com.example.learning_api.constant.CloudinaryConstant;
import com.example.learning_api.constant.ErrorConstant;
import com.example.learning_api.dto.request.classroom.ClassSessionRequest;
import com.example.learning_api.dto.request.classroom.CreateClassRoomRequest;
import com.example.learning_api.dto.request.classroom.ImportClassRoomRequest;
import com.example.learning_api.dto.request.classroom.UpdateClassRoomRequest;
import com.example.learning_api.dto.response.classroom.*;
import com.example.learning_api.dto.response.CloudinaryUploadResponse;
import com.example.learning_api.dto.response.lesson.GetLessonDetailResponse;
import com.example.learning_api.dto.response.section.GetSectionsResponse;
import com.example.learning_api.entity.sql.database.*;
import com.example.learning_api.model.CustomException;
import com.example.learning_api.repository.database.*;
import com.example.learning_api.service.common.CloudinaryService;
import com.example.learning_api.service.common.ExcelReader;
import com.example.learning_api.service.common.ModelMapperService;
import com.example.learning_api.service.core.IClassRoomService;
import com.example.learning_api.utils.ImageUtils;
import com.example.learning_api.utils.StringUtils;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.bson.Document;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class ClassRoomService implements IClassRoomService {
    private final ModelMapperService modelMapperService;
    private final ClassRoomRepository classRoomRepository;
    private final CloudinaryService cloudinaryService;
    private final CourseRepository courseRepository;
    private final SectionRepository sectionRepository;
    private final TeacherRepository teacherRepository;
    private final StudentRepository studentRepository;
    private final StudentEnrollmentsRepository studentEnrollmentsRepository;
    private final LessonRepository lessonRepository;
    private final ScheduleRepository scheduleRepository;
    private final TermsRepository termRepository;
    private final FacultyRepository facultyRepository;
    private final ExcelReader excelReader;
    private final RecentClassRepository recentClassRepository;

    @Override
    public CreateClassRoomResponse createClassRoom(CreateClassRoomRequest body) {
        try{

            if (body.getName()==null){
                throw new IllegalArgumentException("Name is required");
            }
            if (body.getCourseId()==null){
                throw new IllegalArgumentException("CourseId is required");
            }
            if (courseRepository.findById(body.getCourseId()).isEmpty()){
                throw new IllegalArgumentException("CourseId is not found");
            }
            if (body.getTeacherId()==null){
                throw new IllegalArgumentException("TeacherId is required");
            }
            if (teacherRepository.findById(body.getTeacherId()).isEmpty()){
                throw new IllegalArgumentException("TeacherId is not found");
            }
            if (body.getTermId()==null){
                throw new IllegalArgumentException("TermId is required");
            }
            if (termRepository.findById(body.getTermId()).isEmpty()){
                throw new IllegalArgumentException("TermId is not found");
            }
            if (body.getFacultyId()==null){
                throw new IllegalArgumentException("FacultyId is required");
            }
            if (facultyRepository.findById(body.getFacultyId()).isEmpty()){
                throw new IllegalArgumentException("FacultyId is not found");
            }
            if (body.getEnrollmentCapacity()==null){
                throw new IllegalArgumentException("EnrollmentCapacity is required");
            }
            ClassRoomEntity classRoomEntity = modelMapperService.mapClass(body, ClassRoomEntity.class);
            classRoomEntity.setFacultyId(body.getFacultyId());
            classRoomEntity.setCurrentEnrollment(0);
            classRoomEntity.setCourseId(body.getCourseId());
            classRoomEntity.setCreatedAt(String.valueOf(System.currentTimeMillis()));
            classRoomEntity.setUpdatedAt(String.valueOf(System.currentTimeMillis()));
            List<ScheduleEntity> schedules = new ArrayList<>();

            CreateClassRoomResponse resData = new CreateClassRoomResponse();
            if (body.getImage()!=null){
                byte[] originalImage = new byte[0];
                originalImage = body.getImage().getBytes();
                byte[] newImage = ImageUtils.resizeImage(originalImage, 200, 200);
                CloudinaryUploadResponse imageUploaded = cloudinaryService.uploadFileToFolder(
                        CloudinaryConstant.CLASSROOM_PATH,
                        StringUtils.generateFileName(body.getName(), "classroom"),
                        newImage,
                        "image"
                );
                classRoomEntity.setImage(imageUploaded.getUrl());
            }

            classRoomRepository.save(classRoomEntity);
            if (body.getSessions()!=null){
                for (ClassSessionRequest session : body.getSessions()){
                    ScheduleEntity schedule = new ScheduleEntity();
                    schedule.setClassroomId(classRoomEntity.getId());
                    schedule.setDayOfWeek(session.getDayOfWeek());
                    schedule.setStartTime(session.getStartTime());
                    schedule.setEndTime(session.getEndTime());
                    scheduleRepository.save(schedule);
                }

            }
            else{
                throw new IllegalArgumentException("Sessions is required");
            }
            resData.setId(classRoomEntity.getId());
            resData.setName(classRoomEntity.getName());
            resData.setDescription(classRoomEntity.getDescription());
            resData.setImage(classRoomEntity.getImage());
            resData.setCourseId(classRoomEntity.getCourseId());
            resData.setTeacherId(classRoomEntity.getTeacherId());
            resData.setTermId(classRoomEntity.getTermId());
            resData.setFacultyId(classRoomEntity.getFacultyId());
            resData.setEnrollmentCapacity(classRoomEntity.getEnrollmentCapacity());
            resData.setCurrentEnrollment(classRoomEntity.getCurrentEnrollment());
            resData.setCredits(classRoomEntity.getCredits());
            resData.setStatus(classRoomEntity.getStatus().toString());
            resData.setCreatedAt(classRoomEntity.getCreatedAt().toString());
            resData.setUpdatedAt(classRoomEntity.getUpdatedAt().toString());
            resData.setSchedules(schedules);
            return resData;
        }
        catch (Exception e){
            log.error(e.getMessage());
            throw new IllegalArgumentException(e.getMessage());
        }
    }

    @Override
    public void updateClassRoom(UpdateClassRoomRequest body) {
        try {
            ClassRoomEntity classroom = classRoomRepository.findById(body.getId())
                    .orElseThrow(() -> new CustomException(ErrorConstant.NOT_FOUND));
            if(body.getImage()!=null){
                byte[] originalImage = new byte[0];
                originalImage = body.getImage().getBytes();
                byte[] newImage = ImageUtils.resizeImage(originalImage, 200, 200);
                CloudinaryUploadResponse imageUploaded = cloudinaryService.uploadFileToFolder(
                        CloudinaryConstant.CLASSROOM_PATH,
                        StringUtils.generateFileName(body.getName(), "classroom"),
                        newImage,
                        "image"
                );
                classroom.setImage(imageUploaded.getUrl());
            }
            if (body.getName()!=null){
                classroom.setName(body.getName());
            }
            if (body.getDescription()!=null){
                classroom.setDescription(body.getDescription());
            }
            if (body.getCourseId()!=null){
                if (courseRepository.findById(body.getCourseId()).isEmpty()){
                    throw new IllegalArgumentException("CourseId is not found");
                }
                classroom.setCourseId(body.getCourseId());
            }
            if (body.getTeacherId()!=null){
                if (teacherRepository.findById(body.getTeacherId()).isEmpty()){
                    throw new IllegalArgumentException("TeacherId is not found");
                }
                classroom.setTeacherId(body.getTeacherId());
            }
            if (body.getTermId()!=null){
                if (termRepository.findById(body.getTermId()).isEmpty()){
                    throw new IllegalArgumentException("TermId is not found");
                }
                classroom.setTermId(body.getTermId());
            }
            if (body.getFacultyId()!=null){
                if (facultyRepository.findById(body.getFacultyId()).isEmpty()){
                    throw new IllegalArgumentException("FacultyId is not found");
                }
                classroom.setFacultyId(body.getFacultyId());
            }
            if (body.getEnrollmentCapacity()!=null){
                classroom.setEnrollmentCapacity(body.getEnrollmentCapacity());
            }
            if (body.getCredits()!=null){
                classroom.setCredits(body.getCredits());
            }
            if (body.getStatus()!=null){
                classroom.setStatus(body.getStatus());
            }
            classRoomRepository.save(classroom);
        }
        catch (Exception e){
            throw new IllegalArgumentException(e.getMessage());
        }

    }


    @Override
    public void deleteClassRoom(String classroomId) {
        try {
             ClassRoomEntity classroom = classRoomRepository.findById(classroomId)
                    .orElseThrow(() -> new CustomException(ErrorConstant.NOT_FOUND));
            classRoomRepository.delete(classroom);
        }
        catch (Exception e){
            throw new IllegalArgumentException(e.getMessage());
        }
    }

    @Override
    public GetClassRoomsResponse getClassRooms(int page, int size, String search) {
        try{
            Pageable pageAble = PageRequest.of(page, size);
            Page<ClassRoomEntity> classRooms = classRoomRepository.findByNameContaining(search, pageAble);
            List<GetClassRoomsResponse.ClassRoomResponse> resData = new ArrayList<>();
            for (ClassRoomEntity classRoom : classRooms){
                log.info("classRoom: {}", classRoom);
                GetClassRoomsResponse.ClassRoomResponse classRoomResponse = modelMapperService.mapClass(classRoom, GetClassRoomsResponse.ClassRoomResponse.class);
                resData.add(classRoomResponse);
            }
            GetClassRoomsResponse res = new GetClassRoomsResponse();
            res.setClassRooms(resData);
            res.setTotalPage(classRooms.getTotalPages());
            res.setTotalElements(classRooms.getTotalElements());
            return res;
        }
        catch (Exception e){
            throw new IllegalArgumentException(e.getMessage());
        }

    }

    @Override
    public GetSectionsResponse getSectionsByClassroomId(int page, int size, String classroomId) {
        try{
            Pageable pageAble = PageRequest.of(page, size);
            Page<SectionEntity> sectionEntities = sectionRepository.findByClassRoomId(classroomId, pageAble);
            List<GetSectionsResponse.SectionResponse> sectionResponses = modelMapperService.mapList(sectionEntities.getContent(), GetSectionsResponse.SectionResponse.class);
            GetSectionsResponse resData = new GetSectionsResponse();
            resData.setTotalPage(sectionEntities.getTotalPages());
            resData.setTotalElements(sectionEntities.getTotalElements());
            resData.setSections(sectionResponses);
            return resData;
        }
        catch (Exception e){
            throw new IllegalArgumentException(e.getMessage());
        }

    }

    @Override
    public GetClassRoomsResponse getScheduleByDay(String studentId, String day) {
        try{

            List<ClassRoomEntity> classRooms = classRoomRepository.findStudentScheduleByDayAndStudentId(day,studentId);
            int start = 0;
            int end = Math.min(start + 10, classRooms.size());
            List<ClassRoomEntity> pagedCourses = classRooms.subList(start, end);

            List<GetClassRoomsResponse.ClassRoomResponse> resData = pagedCourses.stream()
                    .map(classRoom -> modelMapperService.mapClass(classRoom,GetClassRoomsResponse.ClassRoomResponse.class))
                    .collect(Collectors.toList());
            GetClassRoomsResponse res = new GetClassRoomsResponse();
            res.setClassRooms(resData);
            res.setTotalPage((int) Math.ceil((double) classRooms.size() / 10));
            res.setTotalElements((long) classRooms.size());
            return res;

        }
        catch (Exception e){
            throw new IllegalArgumentException(e.getMessage());
        }
    }
    @Override
    public List<GetScheduleResponse> getScheduleByStudentId(String studentId) {
        try {
            if (studentRepository.findById(studentId).isEmpty()) {
                throw new IllegalArgumentException("StudentId is not found");
            }
            AggregationResults<GetScheduleResponse> results = studentEnrollmentsRepository.getWeeklySchedule(studentId);
            List<GetScheduleResponse> resData = results.getMappedResults();

            // Create a default schedule with all days of the week
            List<GetScheduleResponse> defaultSchedule = Arrays.asList(
                    new GetScheduleResponse("Monday", new ArrayList<>()),
                        new GetScheduleResponse("Tuesday", new ArrayList<>()),
                    new GetScheduleResponse("Wednesday", new ArrayList<>()),
                    new GetScheduleResponse("Thursday", new ArrayList<>()),
                    new GetScheduleResponse("Friday", new ArrayList<>()),
                    new GetScheduleResponse("Saturday", new ArrayList<>()),
                    new GetScheduleResponse("Sunday", new ArrayList<>())
            );

            // Replace the default schedule with the actual data where available
            for (GetScheduleResponse schedule : defaultSchedule) {
                GetScheduleResponse foundSchedule = resData.stream()
                        .filter(s -> s.getDayOfWeek().equals(schedule.getDayOfWeek()))
                        .findFirst()
                        .orElse(null);
                if (foundSchedule != null) {
                    schedule.setSessions(foundSchedule.getSessions());
                }
            }

            return defaultSchedule;
        } catch (Exception e) {
            throw new IllegalArgumentException(e.getMessage());
        }
    }

    @Override
    public GetClassRoomDetailResponse getClassRoomDetail(String classroomId) {
       try{
              ClassRoomEntity classRoomEntity = classRoomRepository.findById(classroomId)
                      .orElseThrow(() -> new CustomException(ErrorConstant.NOT_FOUND));
                GetClassRoomDetailResponse resData = new GetClassRoomDetailResponse();
                Pageable pageAble = PageRequest.of(0, 15);
                resData.setClassRoom(classRoomEntity);
                Page<SectionEntity> sectionEntities = sectionRepository.findByClassRoomId(classroomId,pageAble);
                List<GetClassRoomDetailResponse.Section> sections = new ArrayList<>();
                for (SectionEntity sectionEntity : sectionEntities){
                    GetClassRoomDetailResponse.Section section = new GetClassRoomDetailResponse.Section();
                    section.setId(sectionEntity.getId());
                    section.setName(sectionEntity.getName());
                    section.setDescription(sectionEntity.getDescription());
                    List<LessonEntity> lessons = lessonRepository.findBySectionId(sectionEntity.getId());
                    List<GetLessonDetailResponse> lessonDetails = new ArrayList<>();
                    for (LessonEntity lesson : lessons){
                        GetLessonDetailResponse lessonDetail = lessonRepository.getLessonWithResourcesAndMediaAndSubstances(lesson.getId());
                        lessonDetails.add(lessonDetail);
                    }
                    section.setLessons(lessonDetails);
                    sections.add(section);

                }
                resData.setSections(sections);
                return resData;

       }
         catch (Exception e){
              throw new IllegalArgumentException(e.getMessage());
         }
    }

    @Override
    public void importClassRoom(ImportClassRoomRequest body) {
        try{
            if (body.getFile()==null){
                throw new IllegalArgumentException(" File is required");
            }
            ObjectMapper mapper = new ObjectMapper();
            List<List<String>> data = excelReader.readExcel(body.getFile().getInputStream());
            for (int i = 1; i < data.size(); i++) {
                List<String> row = data.get(i);
                ClassRoomEntity classRoomEntity = new ClassRoomEntity();
                classRoomEntity.setName(row.get(0));
                classRoomEntity.setDescription(row.get(1));
                if (courseRepository.findById(row.get(2)).isEmpty()){
                    throw new IllegalArgumentException("CourseId is not found");
                }
                classRoomEntity.setCourseId(row.get(2));
                if (teacherRepository.findById(row.get(4)).isEmpty()){
                    throw new IllegalArgumentException("TeacherId is not found");
                }
                classRoomEntity.setTeacherId(row.get(4));
                if (termRepository.findById(row.get(3)).isEmpty()){
                    throw new IllegalArgumentException("TermId is not found");
                }
                classRoomEntity.setTermId(row.get(3));
                classRoomEntity.setCreatedAt(String.valueOf(System.currentTimeMillis()));
                classRoomEntity.setUpdatedAt(String.valueOf(System.currentTimeMillis()));
                classRoomRepository.save(classRoomEntity);
            }
        }
        catch (Exception e){
            throw new IllegalArgumentException(e.getMessage());
        }
    }

    @Override
    public GetClassRoomRecentResponse getRecentClasses(int page, int size, String studentId) {
        try{
            Pageable pageAble = PageRequest.of(page, size);
            Slice<GetClassRoomRecentResponse.ClassRoomResponse> classRooms = studentEnrollmentsRepository.getRecentClasses(studentId, pageAble);
            GetClassRoomRecentResponse resData = new GetClassRoomRecentResponse();
            resData.setTotalPage(classRooms.getSize()/size);
            resData.setTotalElements(classRooms.getNumberOfElements());
            resData.setClassRooms(classRooms.getContent());
            return resData;
        }
        catch (Exception e){
            throw new IllegalArgumentException(e.getMessage());
        }
    }

    @Override
    public GetClassRoomRecentResponse getRecentClassesByTeacherId(int page, int size, String teacherId) {
        try {
            int skip = page * size;
            List<RecentClassDTO> classRooms = recentClassRepository.findRecentClassesByTeacherId(teacherId, skip, size);

            long totalElements = recentClassRepository.countRecentClassesByTeacherId(teacherId);
            int totalPages = (int) Math.ceil((double) totalElements / size);

            GetClassRoomRecentResponse resData = new GetClassRoomRecentResponse();
            resData.setTotalElements((int) totalElements);
            resData.setTotalPage(totalPages);
            List<GetClassRoomRecentResponse.ClassRoomResponse> data = new ArrayList<>();
            for (RecentClassDTO classRoom : classRooms) {
                GetClassRoomRecentResponse.ClassRoomResponse classRoomResponse = modelMapperService.mapClass(classRoom, GetClassRoomRecentResponse.ClassRoomResponse.class);
                data.add(classRoomResponse);
            }
            resData.setClassRooms(data);
            return resData;
        } catch (Exception e) {
            throw new IllegalArgumentException(e.getMessage());
        }
    }

}
