package com.example.mytasks;

public class Task {
    private Integer mID;
    private String mName;
    private Integer mIsDone;
    private Integer mIsImportant;

    public Task() {
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
}
