package com.example.learning_api.service.core;

import com.example.learning_api.dto.request.test.CreateTestRequest;
import com.example.learning_api.dto.request.test.ImportTestRequest;
import com.example.learning_api.dto.request.test.TestSubmitRequest;
import com.example.learning_api.dto.request.test.UpdateTestRequest;
import com.example.learning_api.dto.response.question.GetQuestionsResponse;
import com.example.learning_api.dto.response.test.*;

import java.util.List;

public interface ITestService {
    CreateTestResponse createTest(CreateTestRequest body);
    void updateTest(UpdateTestRequest body);
    void deleteTest(String id);
    GetTestsResponse getTests(int page, int size,String search);
    void importTest(ImportTestRequest body);
    GetTestDetailResponse getTestDetail(String id);
    GetTestsResponse getTestsByClassroomId(int page, int size,String classroomId);
    GetTestInProgress getTestInProgress(int page,int size,String studentId);
    GetTestInProgress getTestOnSpecificDayByStudentId(String studentId,String date,int page,int size);
    TestSubmitResponse submitTest( TestSubmitRequest body);
    List<TestResultResponse> getTestResult(String studentId, String testId);
    List<GetQuestionsResponse.QuestionResponse> getProgress(String testResultId);

}
