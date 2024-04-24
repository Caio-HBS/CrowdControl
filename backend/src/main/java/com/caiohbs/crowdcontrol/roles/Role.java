package com.caiohbs.crowdcontrol.roles;

import com.caiohbs.crowdcontrol.user.User;
import jakarta.persistence.*;

import java.util.EnumSet;
import java.util.List;

import static jakarta.persistence.FetchType.EAGER;

@Entity
public class Role {

    @Id
    @GeneratedValue
    private Long roleId;
    private String roleName;
    private int maxNumberOfUsers;
    private double salary;
    @ElementCollection(fetch=EAGER)
    @Enumerated(EnumType.STRING)
    @CollectionTable(name = "role_permissions")
    @Column(name = "permission")
    private List<String> permissions;
    @OneToMany(mappedBy="userId", fetch=EAGER)
    private List<User> users;

    public Role() {
    }

    public Role(Long roleId, String roleName, int maxNumberOfUsers, double salary, List<String> permissions, List<User> users) {
        this.roleId = roleId;
        this.roleName = roleName;
        this.maxNumberOfUsers = maxNumberOfUsers;
        this.salary = salary;
        this.permissions = permissions;
        this.users = users;
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

    public void setSalary(int salary) {
        this.salary = salary;
    }

    public List<String> getPermissions() {
        return permissions;
    }

    public void setPermissions(List<String> permissions) {
//        for (String permission : permissions) {
//            if (!EnumSet.allOf(Permission.class).contains(permission)) {
//                throw new IllegalArgumentException("Permission " + permission + " is not valid");
//            }
//        }
        this.permissions = permissions;
    }

    public List<User> getUsers() {
        return users;
    }

    public void setUser(List<User> users) {
        this.users = users;
    }

    @Override
    public String toString() {
        return "Role{" +
               "roleId=" + roleId +
               ", roleName='" + roleName + '\'' +
               ", maxNumberOfUsers=" + maxNumberOfUsers +
               ", salary=" + salary +
               ", permissions=" + permissions +
               ", user=" + users +
               '}';
    }
}
