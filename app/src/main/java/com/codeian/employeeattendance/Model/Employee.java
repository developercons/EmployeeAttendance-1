package com.codeian.employeeattendance.Model;

import java.util.List;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Employee {

    @SerializedName("success")
    @Expose
    private Boolean success;
    @SerializedName("employee_list")
    @Expose
    private List<EmployeeList> employeeList = null;

    public Boolean getSuccess() {
        return success;
    }

    public void setSuccess(Boolean success) {
        this.success = success;
    }

    public List<EmployeeList> getEmployeeList() {
        return employeeList;
    }

    public void setEmployeeList(List<EmployeeList> employeeList) {
        this.employeeList = employeeList;
    }

}