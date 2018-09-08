package com.ibrickedlabs.drops.data;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created by RajeshAatrayan on 27-08-2018.
 */

public class Exam extends RealmObject{
    private String what;//what exactly we enter
    @PrimaryKey
    private long added;//which we consider as primary key ie..input time which will be unique
    private long when;//the inputed date
    private boolean completed;//wether the action completed or not for [mark]

    public Exam() {

    }

    public Exam(String what, long added, long when, boolean completed) {
        this.what = what;
        this.added = added;
        this.when = when;
        this.completed = completed;
    }

    public String getWhat() {
        return what;
    }

    public void setWhat(String what) {
        this.what = what;
    }

    public long getAdded() {
        return added;
    }

    public void setAdded(long added) {
        this.added = added;
    }

    public long getWhen() {
        return when;
    }

    public void setWhen(long when) {
        this.when = when;
    }

    public boolean isCompleted() {
        return completed;
    }

    public void setCompleted(boolean completed) {
        this.completed = completed;
    }
}
