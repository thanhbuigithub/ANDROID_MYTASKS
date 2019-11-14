package com.example.mytasks;

public class Task {
    private Integer mID;
    private String mName;
    private Integer mIsDone;
    private Integer mIsImportant;
    private Integer mIDList;
    private String mRemind;
    private String mDeadline;
    private Integer mRepeat;
    private Integer mFile;
    private String mNote;

    public Task() {
        this.mName = "";
        this.mIsDone = 0;
        this.mIsImportant = 0;
        this.mIDList = -1;
        this.mRemind = "";
        this.mDeadline = "";
        this.mRepeat = 0;
        this.mFile = -1;
        this.mNote = "";
    }

    public Task(String mName) {
        this.mName = mName;
    }

    public Integer getmID() {
        return mID;
    }

    public void setmID(Integer mID) {
        this.mID = mID;
    }

    public String getmName() {
        return mName;
    }

    public void setmName(String mName) {
        this.mName = mName;
    }

    public Integer getmIsDone() {
        return mIsDone;
    }

    public void setmIsDone(Integer mIsDone) {
        this.mIsDone = mIsDone;
    }

    public Integer getmIsImportant() {
        return mIsImportant;
    }

    public void setmIsImportant(Integer mIsImportant) {
        this.mIsImportant = mIsImportant;
    }

    public Task(Integer mID, String mName, Integer mIsDone, Integer mIsImportant, Integer mIDList) {
        this.mID = mID;
        this.mName = mName;
        this.mIsDone = mIsDone;
        this.mIsImportant = mIsImportant;
        this.mIDList = mIDList;
    }

    public Integer getmIDList() {
        return mIDList;
    }

    public void setmIDList(Integer mIDList) {
        this.mIDList = mIDList;
    }

    public String getmRemind() {
        return mRemind;
    }

    public void setmRemind(String mRemind) {
        this.mRemind = mRemind;
    }

    public String getmDeadline() {
        return mDeadline;
    }

    public void setmDeadline(String mDeadline) {
        this.mDeadline = mDeadline;
    }

    public Integer getmRepeat() {
        return mRepeat;
    }

    public void setmRepeat(Integer mRepeat) {
        this.mRepeat = mRepeat;
    }

    public Integer getmFile() {
        return mFile;
    }

    public void setmFile(Integer mFile) {
        this.mFile = mFile;
    }

    public String getmNote() {
        return mNote;
    }

    public void setmNote(String mNote) {
        this.mNote = mNote;
    }
}
