package com.project.repository;

import java.util.List;

import com.project.module.Admin;
import com.project.module.Provider;
import com.project.module.User;

public interface AdminRepository {
    List<Admin> getAllAdmins();
    Admin getAdminById(int id);
    Admin createAdmin(Admin admin);
    Admin updateAdmin(int id, Admin admin);
    void deleteAdmin(int id);
    List<User> getAllUsers();
    List<Provider> getAllProviders();
}
