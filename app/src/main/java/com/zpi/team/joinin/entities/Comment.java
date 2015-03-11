package com.zpi.team.joinin.entities;

import java.util.Date;
import java.util.List;

/**
 * Created by MK on 2015-03-11.
 */
public class Comment {

    private int id;
    private Date timestamp;
    private String commentBody;

    private User author;
    private Comment parentComment;
    private List<Comment> childrenComments;
    private Event commentedEvent;

    public Comment(int id, Date timestamp, String commentBody) {
        this.id = id;
        this.timestamp = timestamp;
        this.commentBody = commentBody;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Date getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Date timestamp) {
        this.timestamp = timestamp;
    }

    public String getCommentBody() {
        return commentBody;
    }

    public void setCommentBody(String commentBody) {
        this.commentBody = commentBody;
    }

    public User getAuthor() {
        return author;
    }

    public void setAuthor(User author) {
        this.author = author;
    }

    public Comment getParentComment() {
        return parentComment;
    }

    public void setParentComment(Comment parentComment) {
        this.parentComment = parentComment;
    }

    public List<Comment> getChildrenComments() {
        return childrenComments;
    }

    public void setChildrenComments(List<Comment> childrenComments) {
        this.childrenComments = childrenComments;
    }

    public Event getCommentedEvent() {
        return commentedEvent;
    }

    public void setCommentedEvent(Event commentedEvent) {
        this.commentedEvent = commentedEvent;
    }
}
