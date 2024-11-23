package com.example.learning_api.service.core;

import com.example.learning_api.dto.request.forum.*;
import com.example.learning_api.dto.response.forum.GetForumCommentResponse;
import com.example.learning_api.dto.response.forum.GetForumDetailResponse;
import com.example.learning_api.dto.response.forum.GetForumsResponse;
import com.example.learning_api.entity.sql.database.TagEntity;

import java.util.List;

public interface IForumService {
    void createForum(CreateForumRequest request);
    void updateForum(UpdateForumRequest request);
    void deleteForum(String id);
    void voteForum(VoteRequest request);
    GetForumsResponse getForums(int page, int size , String search);
    GetForumDetailResponse getForumDetail(String id);
    GetForumsResponse getForumByAuthor(String authorId, int page, int size);
    GetForumsResponse getForumByTag(List<String> tags, int page, int size);

    void createForumComment(CreateForumCommentRequest request);
    void updateForumComment(UpdateForumCommentRequest request);
    void deleteForumComment(String id);
    GetForumCommentResponse getReplyComments(String parentIdm, int page, int size);


    void createTag(TagEntity request);
    void updateTag(TagEntity request);
    void deleteTag(String id);

}
