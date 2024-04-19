package com.caiohbs.crowdcontrol.roles;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;

@Entity
public class Role {

    @Id
    @GeneratedValue
    private Long roleId;
    private String roleName;
    private int maxNumberOfUsers;
    private int salary;

    public Role() {
    }

    public Role(String roleName, int maxNumberOfUsers, int salary, Long roleId) {
        this.roleName = roleName;
        this.maxNumberOfUsers = maxNumberOfUsers;
        this.salary = salary;
        this.roleId = roleId;
    }

    public Long getRoleId() {
        return roleId;
    }

    public void setRoleId(Long roleId) {
        this.roleId = roleId;
    }

    public String getRoleName() {
        return roleName;
    }

    public void setRoleName(String roleName) {
        this.roleName = roleName;
    }

    public int getMaxNumberOfUsers() {
        return maxNumberOfUsers;
    }

    public void setMaxNumberOfUsers(int maxNumberOfUsers) {
        this.maxNumberOfUsers = maxNumberOfUsers;
    }

    public int getSalary() {
        return salary;
    }

    public void setSalary(int salary) {
        this.salary = salary;
    }

    @Override
    public String toString() {
        return "Role{" +
               "roleId=" + roleId +
               ", roleName='" + roleName + '\'' +
               ", maxNumberOfUsers=" + maxNumberOfUsers +
               ", sallary=" + salary +
               '}';
    }
}
