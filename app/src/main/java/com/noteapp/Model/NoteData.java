package com.noteapp.Model;

public class NoteData {




    private String userId;
    private String noteId;
    private String title;
    private String content;
    private String lastUpdateDate;
    private String creationDate;
    private String snap;
    private String color;

    public NoteData() {
    }

    public NoteData(String userId, String title, String content , String lastUpdateDate, String creationDate,String snap,String color) {
        this.userId = userId;
        this.title = title;
        this.content = content;
        this.lastUpdateDate = lastUpdateDate;
        this.creationDate = creationDate;
        this.snap = snap;
        this.color = color;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public String getSnap() {
        return snap;
    }

    public void setSnap(String snap) {
        this.snap = snap;
    }

    public String getUserId() {
        return userId;
    }

    public String getNoteId() {
        return noteId;
    }

    public void setNoteId(String noteId) {
        this.noteId = noteId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }



    public String getLastUpdateDate() {
        return lastUpdateDate;
    }

    public void setLastUpdateDate(String lastUpdateDate) {
        this.lastUpdateDate = lastUpdateDate;
    }

    public String getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(String creationDate) {
        this.creationDate = creationDate;
    }
}
