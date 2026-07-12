package com.example.app.viewmodel;

import com.example.app.model.Employee;
import javafx.beans.property.*;
import lombok.Getter;

public class EmployeeDetailViewModel extends ViewModelBase {

    @Getter
    private final ObjectProperty<Employee> employee = new SimpleObjectProperty<>();

    @Getter
    private final StringProperty name = new SimpleStringProperty();

    @Getter
    private final StringProperty gender = new SimpleStringProperty();

    @Getter
    private final StringProperty phone = new SimpleStringProperty();

    @Getter
    private final StringProperty email = new SimpleStringProperty();

    @Getter
    private final StringProperty orgName = new SimpleStringProperty();

    @Getter
    private final StringProperty hireDate = new SimpleStringProperty();

    @Getter
    private final StringProperty birthday = new SimpleStringProperty();

    @Getter
    private final StringProperty status = new SimpleStringProperty();

    @Getter
    private final StringProperty employeeId = new SimpleStringProperty();

    public ObjectProperty<Employee> employeeProperty() { return employee; }
    public StringProperty nameProperty() { return name; }
    public StringProperty genderProperty() { return gender; }
    public StringProperty phoneProperty() { return phone; }
    public StringProperty emailProperty() { return email; }
    public StringProperty orgNameProperty() { return orgName; }
    public StringProperty hireDateProperty() { return hireDate; }
    public StringProperty birthdayProperty() { return birthday; }
    public StringProperty statusProperty() { return status; }
    public StringProperty employeeIdProperty() { return employeeId; }

    public void setEmployee(Employee emp) {
        employee.set(emp);
        if (emp != null) {
            employeeId.set(emp.getId() != null ? String.valueOf(emp.getId()) : "");
            name.set(emp.getName() != null ? emp.getName() : "");
            gender.set(emp.getGender() != null ? emp.getGender() : "");
            phone.set(emp.getPhone() != null ? emp.getPhone() : "");
            email.set(emp.getEmail() != null ? emp.getEmail() : "");
            orgName.set(emp.getOrgName() != null ? emp.getOrgName() : "");
            hireDate.set(emp.getHireDate() != null ? emp.getHireDate() : "");
            birthday.set(emp.getBirthday() != null ? emp.getBirthday() : "");
            status.set(emp.getStatus() != null ? (emp.getStatus() == 1 ? "在职" : "离职") : "");
        }
    }
}
