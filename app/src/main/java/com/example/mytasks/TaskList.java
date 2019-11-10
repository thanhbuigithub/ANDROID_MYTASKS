package com.example.mytasks;

import java.util.ArrayList;
import java.util.List;

public class TaskList {
    private Integer mID;
    private String mName;
    private Integer mIcon;
    private List<Task> mListTasks = new ArrayList<>();

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

    public Integer getmIcon() {
        return mIcon;
    }

    public void setmIcon(Integer mIcon) {
        this.mIcon = mIcon;
    }

    public List<Task> getmListTasks() {
        return mListTasks;
    }

    public void setmListTasks(List<Task> mListTasks) {
        this.mListTasks = mListTasks;
    }
}
