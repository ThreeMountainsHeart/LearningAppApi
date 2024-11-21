package com.example.learning_api.dto.response.classroom;

import com.example.learning_api.entity.sql.database.DeadlineSubmissionsEntity;
import com.example.learning_api.entity.sql.database.TestResultEntity;
import com.example.learning_api.enums.StudentStatus;
import jakarta.persistence.Id;
import lombok.Data;

import java.util.List;

@Data
public class GetDetailStudentInClassResponse {
    @Id
    private String id;
    private String userId;
    private String gradeLevel;
    private String studentCode;
    private String gender;
    private String address;
    private String dateOfBirth;
    private String phone;
    private String schoolYear;
    private String academicYearId;
    private String majorId;
    private StudentStatus status;
    private String studentName;
    private String studentAvatar;

    List<TestResultEntity> testResults;
    List<DeadlineSubmissionsEntity> deadlineSubmissions;
}