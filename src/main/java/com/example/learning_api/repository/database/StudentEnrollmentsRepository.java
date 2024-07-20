package com.example.learning_api.repository.database;

import com.example.learning_api.dto.response.classroom.GetClassRoomRecentResponse;
import com.example.learning_api.dto.response.classroom.GetScheduleResponse;
import com.example.learning_api.dto.response.deadline.UpcomingDeadlinesResponse;
import com.example.learning_api.entity.sql.database.ClassRoomEntity;
import com.example.learning_api.entity.sql.database.StudentEnrollmentsEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.repository.Aggregation;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import java.util.Date;
import java.util.List;

public interface StudentEnrollmentsRepository extends MongoRepository<StudentEnrollmentsEntity, String> {
    //    void deleteByStudentIdAndCourseId(String studentId, String courseId);
    @Query("{'studentId': ?0, 'courseId': ?1}")
    StudentEnrollmentsEntity findByStudentIdAndCourseId(String studentId, String courseId);

    @Query("{'studentId': ?0}")
    List<StudentEnrollmentsEntity> findByStudentId(String studentId);

    @Aggregation(pipeline = {
            "{$addFields: {_classroomId: {$toObjectId: '$classroomId'}}}",
            "{$match: {studentId: '?0'}}",
            "{$lookup: {from: 'classrooms', localField: '_classroomId', foreignField: '_id', as: 'classroom'}}",
            "{$unwind: '$classroom'}",
            "{$addFields: {'classroom.classroomId': {$toString: '$classroom._id'}}}",
            "{$lookup: {from: 'schedules', localField: 'classroom.classroomId', foreignField: 'classroomId', as: 'schedules'}}",
            "{$unwind: '$schedules'}",
            "{$group: {_id: {dayOfWeek: '$schedules.dayOfWeek'}, sessions: {$push: {startTime: '$schedules.startTime', endTime: '$schedules.endTime', className: '$classroom.name', classroomId: '$classroom.classroomId'}}}}",
            "{$project: {dayOfWeek: '$_id.dayOfWeek', sessions: 1, _id: 0}}",
            "{$sort: {dayOfWeek: 1}}"
    })
    AggregationResults<GetScheduleResponse> getWeeklySchedule(String studentId);
    @Aggregation(pipeline = {
            "{$addFields: {_classroomId: {$toObjectId: '$classroomId'}}}",
            "{$match: {studentId: '?0'}}",
            "{$lookup: {from: 'classrooms', localField: '_classroomId', foreignField: '_id', as: 'classroom'}}",
            "{$unwind: '$classroom'}",
            "{$lookup: {from: 'recent_class', let: {classroomId: '$classroomId', studentId: '$studentId'}, pipeline: [{$match: {$expr: {$and: [{$eq: ['$classroomId', '$$classroomId']}, {$eq: ['$studentId', '$$studentId']}]}}}], as: 'recentAccess'}}",
            "{$unwind: {path: '$recentAccess', preserveNullAndEmptyArrays: true}}",
            "{$project: {_id: '$classroom._id', name: '$classroom.name',image:'$classroom.image', description: '$classroom.description', courseId: '$classroom.courseId', termId: '$classroom.termId', teacherId: '$classroom.teacherId', createdAt: '$classroom.createdAt', updatedAt: '$classroom.updatedAt', enrolledAt: '$enrolledAt', lastAccessedAt: {$ifNull: ['$recentAccess.lastAccessedAt', null]}}}",
            "{$sort: {lastAccessedAt: -1}}"
    })
    Slice<GetClassRoomRecentResponse.ClassRoomResponse> getRecentClasses(String studentId, Pageable pageable);
    @Aggregation(pipeline = {
            "{ $match: { studentId: ?0 } }",
            "{ $lookup: { from: 'sections', localField: 'classroomId', foreignField: 'classRoomId', as: 'section' } }",
            "{ $unwind: '$section' }",
            "{ $lookup: { from: 'lessons', let: { sectionId: '$section._id' }, pipeline: [{ $match: { $expr: { $eq: ['$sectionId', { $toString: '$$sectionId' }] } } }], as: 'lessons' } }",
            "{ $unwind: '$lessons' }",
            "{ $lookup: { from: 'deadlines', let: { lessonId: '$lessons._id' }, pipeline: [{ $match: { $expr: { $and: [{ $eq: ['$lessonId', { $toString: '$$lessonId' }] }, { $eq: ['$status', 'UPCOMING'] }, { $gt: [{ $toLong: '$endDate' }, { $toLong: ?1 }] }] } } }], as: 'deadlines' } }",
            "{ $unwind: '$deadlines' }",
            "{ $project: { _id: '$deadlines._id', title: '$deadlines.title', description: '$deadlines.description', type: '$deadlines.type', status: '$deadlines.status', attachment: '$deadlines.attachment', startDate: { $toLong: '$deadlines.startDate' }, endDate: { $toLong: '$deadlines.endDate' }, lessonName: '$lessons.name', lessonDescription: '$lessons.description', sectionName: '$section.name', sectionDescription: '$section.description' } }"
    })
    List<UpcomingDeadlinesResponse> getUpcomingDeadlines(String studentId, String compareDate);


}