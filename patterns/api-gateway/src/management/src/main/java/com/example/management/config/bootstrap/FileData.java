package com.example.management.config.bootstrap;

import com.example.management.model.Role;
import com.example.management.model.User;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
class FileData {
    private List<Role> roles;
    private List<User> users;
}
