package com.example.learning_api.service.core.Impl;

import com.example.learning_api.dto.request.section.CreateSectionRequest;
import com.example.learning_api.dto.request.section.DeleteSectionRequest;
import com.example.learning_api.dto.request.section.UpdateSectionRequest;
import com.example.learning_api.dto.response.section.CreateSectionResponse;
import com.example.learning_api.dto.response.section.GetSectionsResponse;
import com.example.learning_api.entity.sql.database.SectionEntity;
import com.example.learning_api.repository.database.ClassRoomRepository;
import com.example.learning_api.repository.database.SectionRepository;
import com.example.learning_api.service.common.ModelMapperService;
import com.example.learning_api.service.core.ISectionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class SectionService implements ISectionService {
    private final ModelMapperService modelMapperService;
    private final SectionRepository sectionRepository;
    private final ClassRoomRepository classRoomRepository;
    @Override
    public CreateSectionResponse createSection(CreateSectionRequest body) {
        try {
            if (body.getName()==null){
                throw new IllegalArgumentException("Name is required");
            }
            if (body.getClassRoomId()==null){
                throw new IllegalArgumentException("ClassRoomId is required");
            }
            if (classRoomRepository.findById(body.getClassRoomId()).isEmpty()){
                throw new IllegalArgumentException("ClassRoomId is not found");
            }
            SectionEntity sectionEntity = modelMapperService.mapClass(body, SectionEntity.class);
            CreateSectionResponse resData = new CreateSectionResponse();
            sectionRepository.save(sectionEntity);
            resData.setName(sectionEntity.getName());
            resData.setDescription(sectionEntity.getDescription());
            resData.setClassRoomId(sectionEntity.getClassRoomId());
            resData.setId(sectionEntity.getId());
            return resData;
        }
        catch (Exception e){
            throw new IllegalArgumentException(e.getMessage());
        }
    }

    @Override
    public void updateSection(UpdateSectionRequest body) {
        try{
            SectionEntity sectionEntity = sectionRepository.findById(body.getId())
                    .orElseThrow(() -> new IllegalArgumentException("Section not found"));
            if (body.getName()!=null){
                sectionEntity.setName(body.getName());
            }
            if (body.getDescription()!=null){

                sectionEntity.setDescription(body.getDescription());
            }
            sectionRepository.save(sectionEntity);

        }
        catch (Exception e){
            throw new IllegalArgumentException(e.getMessage());
        }

    }

    @Override
    public void deleteSection(DeleteSectionRequest id) {
        try{
            sectionRepository.deleteById(id.getId());
        }
        catch (Exception e){
            throw new IllegalArgumentException(e.getMessage());
        }

    }

    @Override
    public GetSectionsResponse getSections(int page, int size, String search) {
        Pageable pageAble = PageRequest.of(page, size);
        Page<SectionEntity> sectionEntities = sectionRepository.findByClassRoomId(search, pageAble);
        List<GetSectionsResponse.SectionResponse> sectionResponses = modelMapperService.mapList(sectionEntities.getContent(), GetSectionsResponse.SectionResponse.class);
        GetSectionsResponse resData = new GetSectionsResponse();
        resData.setTotalPage(sectionEntities.getTotalPages());
        resData.setTotalElements(sectionEntities.getTotalElements());
        resData.setSections(sectionResponses);
        return resData;
    }
}
