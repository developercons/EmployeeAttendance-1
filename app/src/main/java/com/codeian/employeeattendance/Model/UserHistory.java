package com.codeian.employeeattendance.Model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class UserHistory {

    @SerializedName("date")
    @Expose
    private String date;
    @SerializedName("checked_in")
    @Expose
    private String checkedIn;
    @SerializedName("checked_out")
    @Expose
    private String checkedOut;

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getCheckedIn() {
        return checkedIn;
    }

    public void setCheckedIn(String checkedIn) {
        this.checkedIn = checkedIn;
    }

    public String getCheckedOut() {
        return checkedOut;
    }

    public void setCheckedOut(String checkedOut) {
        this.checkedOut = checkedOut;
    }

}