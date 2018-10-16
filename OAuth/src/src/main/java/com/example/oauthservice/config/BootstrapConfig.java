package com.example.oauthservice.config;

import com.example.oauthservice.model.Role;
import com.example.oauthservice.model.User;
import com.example.oauthservice.repository.RoleRepository;
import com.example.oauthservice.repository.UserRepository;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Charsets;
import com.google.common.io.Resources;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import javax.annotation.PostConstruct;
import java.io.File;
import java.net.URL;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Data
@NoArgsConstructor
@AllArgsConstructor
class FileData {
    private List<Role> roles;
    private List<User> users;
}

@Configuration
public class BootstrapConfig {

    private final Logger logger = LoggerFactory.getLogger(BootstrapConfig.class);

    @Value("${bootstrap.enabled:false}")
    private boolean enabled;

    @Value("${bootstrap.save.enabled:false}")
    private boolean saveEnabled;

    @Value("${bootstrap.save.filepath:#{null}}")
    private String saveFilePath;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    public BCryptPasswordEncoder passwordEncoder;

    //@PostConstruct
    public void init(){
        if (enabled) {
            logger.info("Bootstrapping Oauth Server...");
            Role adminRole = getRoleOrCreate("ADMIN");
            Role userRole = getRoleOrCreate("USER");
            User userRoot = getUserOrCreate("root","password", "root@email.com",
                    Arrays.asList(adminRole,userRole));
            User userNormal = getUserOrCreate("user","password", "user@email.com",
                    Arrays.asList(userRole));
            if (saveEnabled){
                saveToFile(saveFilePath,
                        new FileData(Arrays.asList(adminRole,userRole),Arrays.asList(userRoot,userNormal)));
            }
        }
    }

    @PostConstruct
    public void initFromFile(){
        if (enabled) {
            logger.info("Bootstrapping Oauth Server...");
            try {
                URL url = Resources.getResource("initial-data.json");
                String json = Resources
                        .toString(url, Charsets.UTF_8)
                        .replaceAll("^\"|\"$|\\\\", "");
                ObjectMapper mapper = new ObjectMapper();
                mapper.enable(DeserializationFeature.ACCEPT_EMPTY_STRING_AS_NULL_OBJECT);
                FileData data = mapper.readValue(json, FileData.class);
                data.getRoles().stream().forEach(role -> createRole(role));
                data.getUsers().stream().forEach(user -> createUser(user));
            } catch (Exception e) {
                logger.error("Error parsing file for bootstrapping.", e);
            }
        }
    }

    private void saveToFile(String filepath, FileData data){
        File file = new File(filepath);
        ObjectMapper mapper = new ObjectMapper();
        try {
            mapper.writeValue(file, data);
        } catch (Exception e) {
            logger.error("Error saving the file used for bootstrapping.", e);
        }
    }

    private Role createRole(Role role){
        if (roleRepository.findByName(role.getName()) == null) {
            roleRepository.insert(role);
            logger.info(String.format("%s role has been created.", role.getName()));
        }
        return role;
    }

    private User createUser(User user) {
        if (userRepository.findByUsername(user.getUsername()) == null) {
            userRepository.insert(user);
            logger.info(String.format("%s user has been created.", user.getUsername()));
        }
        return user;
    }

    private Role getRoleOrCreate(String name){
        return createRole(new Role(null,name));
    }

    private User getUserOrCreate(String username, String password, String email, List<Role> roles){
        return createUser(new User(null,username,passwordEncoder.encode(password),email,
                roles.stream().map(x -> x.getId()).collect(Collectors.toList())));
    }
}
