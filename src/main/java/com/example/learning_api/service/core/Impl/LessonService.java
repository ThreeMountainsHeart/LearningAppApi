package com.example.learning_api.service.core.Impl;

import com.example.learning_api.dto.request.lesson.CreateLessonRequest;
import com.example.learning_api.dto.request.lesson.UpdateLessonRequest;
import com.example.learning_api.dto.response.lesson.GetLessonDetailResponse;
import com.example.learning_api.entity.sql.database.*;
import com.example.learning_api.enums.*;
import com.example.learning_api.repository.database.*;
import com.example.learning_api.service.common.ModelMapperService;
import com.example.learning_api.service.core.ILessonService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class LessonService implements ILessonService {
    private final LessonRepository lessonRepository;
    private final ModelMapperService modelMapperService;
    private final SectionRepository sectionRepository;
    private final TestRepository testRepository;
    private final ClassRoomRepository classRoomRepository;
    private final ProgressRepository progressRepository;
    private final MediaRepository mediaRepository;
    private final SubstanceRepository substanceRepository;
    private final ResourceRepository resourceRepository;
    private final DeadlineRepository deadlineRepository;

    @Override
    public void createLesson(CreateLessonRequest createLessonRequest) {
        try{
            if (createLessonRequest.getName()==null){
                throw new IllegalArgumentException("Name is required");
            }
            if (createLessonRequest.getSectionId()==null){
                throw new IllegalArgumentException("SectionId is required");
            }
            if (sectionRepository.findById(createLessonRequest.getSectionId()).isEmpty()){
                throw new IllegalArgumentException("SectionId is not found");
            }
            if (createLessonRequest.getStatus()==null){
                createLessonRequest.setStatus(SectionStatus.PUBLIC.toString());
            }
            if (createLessonRequest.getType()==null){
                throw new IllegalArgumentException("Type is required");
            }

            LessonEntity lessonEntity = modelMapperService.mapClass(createLessonRequest, LessonEntity.class);
            Integer index = lessonRepository.findMaxIndexBySectionId(createLessonRequest.getSectionId());

            lessonEntity.setIndex(index==null?0:index+1);
            lessonEntity.setCreatedAt(String.valueOf(System.currentTimeMillis()));
            lessonEntity.setUpdatedAt(String.valueOf(System.currentTimeMillis()));
            lessonRepository.save(lessonEntity);
            SectionEntity sectionEntity = sectionRepository.findById(createLessonRequest.getSectionId()).get();
            ClassRoomEntity classRoomEntity = classRoomRepository.findById(sectionEntity.getClassRoomId()).get();
            if (createLessonRequest.getType().equals(LessonType.MEDIA.toString())){
                MediaEntity mediaEntity = new MediaEntity();
                mediaEntity.setLessonId(lessonEntity.getId());
                mediaEntity.setName(createLessonRequest.getName());
                mediaEntity.setDescription(createLessonRequest.getDescription());
                mediaEntity.setCreatedAt(String.valueOf(System.currentTimeMillis()));
                mediaEntity.setUpdatedAt(String.valueOf(System.currentTimeMillis()));
                if (classRoomEntity.getTotalVideo()==null)
                    classRoomEntity.setTotalVideo(0);
                classRoomEntity.setTotalVideo(classRoomEntity.getTotalVideo()+1);
                mediaRepository.save(mediaEntity);
            }else if (createLessonRequest.getType().equals(LessonType.TEST.toString())){
                TestEntity testEntity = new TestEntity();
                testEntity.setLessonId(lessonEntity.getId());
                testEntity.setClassroomId(classRoomEntity.getId());
                testEntity.setTeacherId(classRoomEntity.getTeacherId());
                testEntity.setName(createLessonRequest.getName());
                testEntity.setDescription(createLessonRequest.getDescription());
                testEntity.setStatus(TestStatus.ONGOING);
                testEntity.setCreatedAt(String.valueOf(System.currentTimeMillis()));
                testEntity.setUpdatedAt(String.valueOf(System.currentTimeMillis()));
                testRepository.save(testEntity);
            }
            else if (createLessonRequest.getType().equals(LessonType.SUBSTANCE.toString())) {
                SubstanceEntity substanceEntity = new SubstanceEntity();
                substanceEntity.setLessonId(lessonEntity.getId());
                substanceEntity.setName(createLessonRequest.getName());
                substanceEntity.setCreatedAt(String.valueOf(System.currentTimeMillis()));
                substanceEntity.setUpdatedAt(String.valueOf(System.currentTimeMillis()));
                if (classRoomEntity.getTotalDocument()==null)
                    classRoomEntity.setTotalDocument(0);
                classRoomEntity.setTotalDocument(classRoomEntity.getTotalDocument()+1);
                substanceRepository.save(substanceEntity);
            }
            else if (createLessonRequest.getType().equals(LessonType.DEADLINE.toString())) {
                DeadlineEntity deadlineEntity = new DeadlineEntity();
                deadlineEntity.setLessonId(lessonEntity.getId());
                deadlineEntity.setTitle(createLessonRequest.getName());
                deadlineEntity.setDescription(createLessonRequest.getDescription());
                deadlineEntity.setClassroomId(classRoomEntity.getId());
                deadlineEntity.setStatus(DeadlineStatus.ONGOING);
                deadlineEntity.setCreatedAt(String.valueOf(System.currentTimeMillis()));
                deadlineEntity.setUpdatedAt(String.valueOf(System.currentTimeMillis()));
                if (classRoomEntity.getTotalAssignment()==null)
                    classRoomEntity.setTotalAssignment(0);
                classRoomEntity.setTotalAssignment(classRoomEntity.getTotalAssignment()+1);
                deadlineRepository.save(deadlineEntity);
            }
            classRoomRepository.save(classRoomEntity);
        }

        catch (Exception e){
            log.error(e.getMessage());
            throw new IllegalArgumentException(e.getMessage());
        }
    }

    @Override
    public void updateLesson(UpdateLessonRequest updateLessonRequest) {
        try {
            LessonEntity lessonEntity = lessonRepository.findById(updateLessonRequest.getId()).orElseThrow(()->new IllegalArgumentException("Lesson not found"));
            if (updateLessonRequest.getName()!=null)
                lessonEntity.setName(updateLessonRequest.getName());
            if (updateLessonRequest.getDescription()!=null)
                lessonEntity.setDescription(updateLessonRequest.getDescription());
            if (updateLessonRequest.getStatus()!=null)
                lessonEntity.setStatus(SectionStatus.valueOf(updateLessonRequest.getStatus()));
            if (updateLessonRequest.getIndex()!=null)
                lessonEntity.setIndex(updateLessonRequest.getIndex());
               SectionEntity sectionEntity = sectionRepository.findById(lessonEntity.getSectionId()).get();
               ClassRoomEntity classRoomEntity = classRoomRepository.findById(sectionEntity.getClassRoomId()).get();
           if (updateLessonRequest.getType()!=null){
               if (lessonEntity.getType().equals(LessonType.MEDIA)){
                    mediaRepository.deleteByLessonId(lessonEntity.getId());
                    classRoomEntity.setTotalVideo(classRoomEntity.getTotalVideo()-1);
               }else if (lessonEntity.getType().equals(LessonType.TEST)){
                   TestEntity testEntity = testRepository.findByLessonId(lessonEntity.getId()).get(0);
                   if (classRoomEntity.getTotalQuiz()==null)
                          classRoomEntity.setTotalQuiz(1);
                   if (classRoomEntity.getTotalExam()==null)
                          classRoomEntity.setTotalExam(1);
                   if (testEntity.getShowResultType().equals(TestShowResultType.SHOW_RESULT_IMMEDIATELY)) {
                       classRoomEntity.setTotalQuiz(classRoomEntity.getTotalQuiz()-1);
                   }else if (testEntity.getShowResultType().equals(TestShowResultType.SHOW_RESULT_AFTER_TEST)) {
                       classRoomEntity.setTotalExam(classRoomEntity.getTotalExam() - 1);
                   }
                     testRepository.deleteByLessonId(lessonEntity.getId());
               }
               else if (lessonEntity.getType().equals(LessonType.SUBSTANCE)) {
                        if (classRoomEntity.getTotalDocument()==null)
                            classRoomEntity.setTotalDocument(1);
                        substanceRepository.deleteByLessonId(lessonEntity.getId());
                        classRoomEntity.setTotalDocument(classRoomEntity.getTotalDocument()-1);
               }
               else if (lessonEntity.getType().equals(LessonType.DEADLINE)) {
                        if (classRoomEntity.getTotalAssignment()==null)
                            classRoomEntity.setTotalAssignment(1);
                     deadlineRepository.deleteByLessonId(lessonEntity.getId());
                        classRoomEntity.setTotalAssignment(classRoomEntity.getTotalAssignment()-1);
               }
               lessonEntity.setType(LessonType.valueOf(updateLessonRequest.getType()));
               if (lessonEntity.getType().equals(LessonType.MEDIA)){
                   MediaEntity mediaEntity = new MediaEntity();
                   mediaEntity.setLessonId(lessonEntity.getId());
                   mediaEntity.setName(lessonEntity.getName());
                   mediaEntity.setDescription(lessonEntity.getDescription());
                   mediaEntity.setCreatedAt(String.valueOf(System.currentTimeMillis()));
                   mediaEntity.setUpdatedAt(String.valueOf(System.currentTimeMillis()));
                   mediaRepository.save(mediaEntity);
                   if (classRoomEntity.getTotalVideo()==null)
                       classRoomEntity.setTotalVideo(0);
                   classRoomEntity.setTotalVideo(classRoomEntity.getTotalVideo()+1);

               }else if (lessonEntity.getType().equals(LessonType.TEST)){

                   TestEntity testEntity = new TestEntity();
                   testEntity.setLessonId(lessonEntity.getId());
                   testEntity.setClassroomId(classRoomEntity.getId());
                   testEntity.setTeacherId(classRoomEntity.getTeacherId());
                   testEntity.setName(lessonEntity.getName());
                   testEntity.setDescription(lessonEntity.getDescription());
                   testEntity.setStatus(TestStatus.ONGOING);
                   testEntity.setCreatedAt(String.valueOf(System.currentTimeMillis()));
                   testEntity.setUpdatedAt(String.valueOf(System.currentTimeMillis()));
                   if (classRoomEntity.getTotalQuiz()==null)
                       classRoomEntity.setTotalQuiz(0);
                   if (classRoomEntity.getTotalExam()==null)
                       classRoomEntity.setTotalExam(0);
                   testRepository.save(testEntity);

               }else if (lessonEntity.getType().equals(LessonType.RESOURCE)) {
                   ResourceEntity resourceEntity = new ResourceEntity();
                   resourceEntity.setLessonId(lessonEntity.getId());
                   resourceEntity.setName(lessonEntity.getName());
                   resourceEntity.setDescription(lessonEntity.getDescription());
                   resourceEntity.setCreatedAt(new Date());
                   resourceEntity.setUpdatedAt(new Date());
                   resourceRepository.save(resourceEntity);
                   if (classRoomEntity.getTotalResource()==null)
                       classRoomEntity.setTotalResource(0);
                   classRoomEntity.setTotalResource(classRoomEntity.getTotalResource()+1);
               }
               else if (lessonEntity.getType().equals(LessonType.SUBSTANCE)) {
                   SubstanceEntity substanceEntity = new SubstanceEntity();
                   substanceEntity.setLessonId(lessonEntity.getId());
                   substanceEntity.setName(lessonEntity.getName());
                   substanceEntity.setCreatedAt(String.valueOf(System.currentTimeMillis()));
                   substanceEntity.setUpdatedAt(String.valueOf(System.currentTimeMillis()));
                   substanceRepository.save(substanceEntity);
                   if (classRoomEntity.getTotalDocument()==null)
                       classRoomEntity.setTotalDocument(0);
                   classRoomEntity.setTotalDocument(classRoomEntity.getTotalDocument()+1);
               }
               else if (lessonEntity.getType().equals(LessonType.DEADLINE)) {
                   DeadlineEntity deadlineEntity = new DeadlineEntity();
                   deadlineEntity.setLessonId(lessonEntity.getId());
                   deadlineEntity.setTitle(lessonEntity.getName());
                   deadlineEntity.setDescription(lessonEntity.getDescription());
                   deadlineEntity.setStatus(DeadlineStatus.ONGOING);
                   deadlineEntity.setCreatedAt(String.valueOf(System.currentTimeMillis()));
                   deadlineEntity.setUpdatedAt(String.valueOf(System.currentTimeMillis()));
                   deadlineRepository.save(deadlineEntity);
                   if (classRoomEntity.getTotalAssignment()==null)
                       classRoomEntity.setTotalAssignment(0);
                   classRoomEntity.setTotalAssignment(classRoomEntity.getTotalAssignment()+1);
               }
           }

            lessonEntity.setUpdatedAt(String.valueOf(System.currentTimeMillis()));
            lessonRepository.save(lessonEntity);
            classRoomRepository.save(classRoomEntity);
        }
        catch (Exception e){
            log.error(e.getMessage());
            throw new IllegalArgumentException(e.getMessage());
        }
    }

    @Override
    public void deleteLesson(String id) {
        try {
            lessonRepository.deleteById(id);
        }
        catch (Exception e){
            log.error(e.getMessage());
            throw new IllegalArgumentException(e.getMessage());
        }
    }

    @Override
    public GetLessonDetailResponse getLessonWithResourcesAndMediaAndSubstances(String id) {
        try {
            GetLessonDetailResponse getLessonDetailResponse = lessonRepository.getLessonWithResourcesAndMediaAndSubstances(id);
            List<TestEntity> testEntities = testRepository.findByLessonId(id);
            if (getLessonDetailResponse==null){
                throw new IllegalArgumentException("Lesson not found");
            }
            getLessonDetailResponse.setTests(testEntities);
            return getLessonDetailResponse;
        }
        catch (Exception e){
            log.error(e.getMessage());
            throw new IllegalArgumentException(e.getMessage());
        }
    }

    @Override
    public List<GetLessonDetailResponse> getLessonBySectionId(String sectionId,String role,String userId) {
        try{
            List<String> statuses = new ArrayList<>();
            if (role.equals("TEACHER")){
                statuses.add(SectionStatus.PUBLIC.toString());
                statuses.add(SectionStatus.PRIVATE.toString());
            }
            else {
                statuses.add(SectionStatus.PUBLIC.toString());
            }
            List<LessonEntity> lessonEntities = lessonRepository.findBySectionId(sectionId, Sort.by(Sort.Direction.ASC, "index"),statuses);
            List<GetLessonDetailResponse> getLessonDetailResponses = new ArrayList<>();
            for (LessonEntity lessonEntity: lessonEntities){
                GetLessonDetailResponse getLessonDetailResponse = lessonRepository.getLessonWithResourcesAndMediaAndSubstances(lessonEntity.getId());
                if (getLessonDetailResponse.getType().equals(LessonType.TEST.toString())){
                    List<TestEntity> testEntities = testRepository.findByLessonId(lessonEntity.getId());
                    getLessonDetailResponse.setTests(testEntities);
                }
                LessonEntity previousLesson = lessonRepository.findBySectionIdAndIndex(sectionId,lessonEntity.getIndex()-1);
                Optional<SectionEntity> sectionEntity = sectionRepository.findById(sectionId);
                if (lessonEntity.getIndex()==0||role.equals("TEACHER")){
                    getLessonDetailResponse.setCanAccess(true);
                } else if (previousLesson==null) {
                    getLessonDetailResponse.setCanAccess(false);

                }else{
                    getLessonDetailResponse.setCanAccess(progressRepository.existsByStudentIdAndClassroomIdAndLessonIdAndCompleted(userId,sectionEntity.get().getClassRoomId(),previousLesson.getId(),true));
                }
                getLessonDetailResponse.setType(lessonEntity.getType().toString());

                getLessonDetailResponse.setIsComplete(progressRepository.existsByStudentIdAndClassroomIdAndLessonIdAndCompleted(userId,sectionEntity.get().getClassRoomId(),lessonEntity.getId(),true));

                getLessonDetailResponses.add(getLessonDetailResponse);
            }
            return getLessonDetailResponses;
        }
        catch (Exception e){
            log.error(e.getMessage());
            throw new IllegalArgumentException(e.getMessage());
        }
    }


}
