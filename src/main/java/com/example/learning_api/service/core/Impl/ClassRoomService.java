package com.example.learning_api.service.core.Impl;

import com.example.learning_api.constant.CloudinaryConstant;
import com.example.learning_api.constant.ErrorConstant;
import com.example.learning_api.dto.request.classroom.CreateClassRoomRequest;
import com.example.learning_api.dto.request.classroom.UpdateClassRoomRequest;
import com.example.learning_api.dto.response.classroom.CreateClassRoomResponse;
import com.example.learning_api.dto.response.classroom.GetClassRoomsResponse;
import com.example.learning_api.dto.response.CloudinaryUploadResponse;
import com.example.learning_api.dto.response.section.GetSectionsResponse;
import com.example.learning_api.entity.sql.database.ClassRoomEntity;
import com.example.learning_api.entity.sql.database.SectionEntity;
import com.example.learning_api.model.CustomException;
import com.example.learning_api.repository.database.ClassRoomRepository;
import com.example.learning_api.repository.database.CourseRepository;
import com.example.learning_api.repository.database.SectionRepository;
import com.example.learning_api.repository.database.UserRepository;
import com.example.learning_api.service.common.CloudinaryService;
import com.example.learning_api.service.common.ModelMapperService;
import com.example.learning_api.service.core.IClassRoomService;
import com.example.learning_api.utils.ImageUtils;
import com.example.learning_api.utils.StringUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class ClassRoomService implements IClassRoomService {
    private final ModelMapperService modelMapperService;
    private final ClassRoomRepository classRoomRepository;
    private final CloudinaryService cloudinaryService;
    private final CourseRepository courseRepository;
    private final SectionRepository sectionRepository;
    @Override
    public CreateClassRoomResponse createClassRoom(CreateClassRoomRequest body) {
        try{
            if (!ImageUtils.isValidImageFile(body.getImage())&&body.getImage()!=null) {
                throw new CustomException(ErrorConstant.IMAGE_INVALID);
            }
            if (body.getName()==null){
                throw new IllegalArgumentException("Name is required");
            }
            if (body.getCourseId()==null){
                throw new IllegalArgumentException("CourseId is required");
            }
            if (courseRepository.findById(body.getCourseId()).isEmpty()){
                throw new IllegalArgumentException("CourseId is not found");
            }
            ClassRoomEntity classRoomEntity = modelMapperService.mapClass(body, ClassRoomEntity.class);
            classRoomEntity.setCourseId(body.getCourseId());
            classRoomEntity.setCreatedAt(new Date());
            classRoomEntity.setUpdatedAt(new Date());
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
            resData.setId(classRoomEntity.getId());
            resData.setName(classRoomEntity.getName());
            resData.setDescription(classRoomEntity.getDescription());
            resData.setImage(classRoomEntity.getImage());
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
            if(body.getName()!=null){
                classroom.setName(body.getName());
            }
            if(body.getDescription()!=null){
                classroom.setDescription(body.getDescription());
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


}
