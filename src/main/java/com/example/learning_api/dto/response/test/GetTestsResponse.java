package com.example.learning_api.dto.response.test;

import com.example.learning_api.entity.sql.database.QuestionEntity;
import com.example.learning_api.entity.sql.database.TestEntity;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
public class GetTestsResponse {
    private Integer totalPage;
    private Long totalElements;
    private List<TestResponse> tests;
    @Data
    public static class TestResponse {
        private String id;
        private String name;
        private String description;
        private int duration;
        private List<QuestionEntity> questions;
        private String startTime;
        private String endTime;
        private String classroomId;
        private String showResultType;
        private String createdBy;
        private String createdAt;
        private String updatedAt;


        public static TestResponse fromTestEntity(TestEntity testEntity){
            TestResponse testResponse = new TestResponse();
            testResponse.setId(testEntity.getId());
            testResponse.setName(testEntity.getName());
            testResponse.setDescription(testEntity.getDescription());
            testResponse.setQuestions(testEntity.getQuestions());
            testResponse.setDuration(testEntity.getDuration());
            testResponse.setCreatedBy(testEntity.getCreatedBy());
            testResponse.setCreatedAt(testEntity.getCreatedAt().toString());
            testResponse.setUpdatedAt(testEntity.getUpdatedAt().toString());
            testResponse.setStartTime(testEntity.getStartTime().toString());
            testResponse.setEndTime(testEntity.getEndTime().toString());
            testResponse.setClassroomId(testEntity.getClassroomId());
            testResponse.setShowResultType(testEntity.getShowResultType().toString());

            return testResponse;
        }
    }
}
