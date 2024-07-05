package com.caiohbs.crowdcontrol.model;

import jakarta.persistence.*;

import java.util.*;

import static jakarta.persistence.FetchType.EAGER;

@Entity
public class Role {

    @Id
    @GeneratedValue
    private Long roleId;
    @Column(unique=true)
    private String roleName;
    private int maxNumberOfUsers;
    private double salary;
    @ElementCollection(fetch=EAGER)
    @Enumerated(EnumType.STRING)
    @CollectionTable(name="role_permissions")
    @Column(name="permission")
    private List<String> permissions;

    public Role() {
    }

    public Role(Long roleId, String roleName, int maxNumberOfUsers,
                double salary, List<String> permissions
    ) {
        this.roleId = roleId;
        this.roleName = roleName;
        this.maxNumberOfUsers = maxNumberOfUsers;
        this.salary = salary;
        this.permissions = permissions;
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

    public double getSalary() {
        return salary;
    }

    public void setSalary(double salary) {
        this.salary = salary;
    }

    public List<String> getPermissions() {
        return permissions;
    }

    public void setPermissions(List<String> permissions) {
        List<String> validPermissions = new ArrayList<>();
        for (String permission : permissions) {
            try {
                Permission.valueOf(permission.toUpperCase());
                validPermissions.add(permission);
            } catch (IllegalArgumentException e) {
                throw new IllegalArgumentException("Invalid permission: " + permission);
            }
        }
        this.permissions = validPermissions;
    }

    @Override
    public String toString() {
        return "Role{" +
               "roleId=" + roleId +
               ", roleName='" + roleName + '\'' +
               ", maxNumberOfUsers=" + maxNumberOfUsers +
               ", salary=" + salary +
               ", permissions=" + permissions +
               '}';
    }
}
