// package com.AuraMoon.auramoon.auth.config;

// import com.AuraMoon.auramoon.auth.entity.Role;
// import com.AuraMoon.auramoon.auth.entity.User;
// import com.AuraMoon.auramoon.auth.repository.IRoleRepository;
// import com.AuraMoon.auramoon.auth.repository.IUserRepository;
// import org.springframework.beans.factory.annotation.Autowired;
// import org.springframework.boot.CommandLineRunner;
// import org.springframework.security.crypto.password.PasswordEncoder;
// import org.springframework.stereotype.Component;
// import com.AuraMoon.auramoon.spa.entity.Therapist;
// import com.AuraMoon.auramoon.spa.repository.TherapistRepository;

// import java.util.Arrays;
// import java.util.List;

// @Component
// public class DataInitial implements CommandLineRunner {

// @Autowired
// private IRoleRepository roleRepository;

// @Autowired
// private IUserRepository userRepository;

// @Autowired
// private PasswordEncoder passwordEncoder;

// @Autowired
// private TherapistRepository therapistRepository;

// @Override
// public void run(String... args) throws Exception {
// // 1. Initialize Roles
// List<String> roleNames = Arrays.asList("GUEST", "ADMIN", "RECEPTIONIST",
// "THERAPIST", "CHEFF", "MANAGER");
// for (String roleName : roleNames) {
// if (roleRepository.findByRoleName(roleName).isEmpty()) {
// Role role = new Role();
// role.setRoleName(roleName);
// roleRepository.save(role);
// }
// }

// // 2. Initialize Users (one user per role for testing authorization)
// createInitialUser("guest@xoai-aura.com", "Guest User", "GUEST");
// createInitialUser("admin@xoai-aura.com", "Admin User", "ADMIN");
// createInitialUser("receptionist@xoai-aura.com", "Receptionist User",
// "RECEPTIONIST");
// createInitialUser("therapist@xoai-aura.com", "Therapist User", "THERAPIST");
// createInitialUser("cheff@xoai-aura.com", "Cheff User", "CHEFF");
// createInitialUser("manager@xoai-aura.com", "Manager User", "MANAGER");
// }

// private void createInitialUser(String email, String fullName, String
// roleName) {
// User user;
// if (!userRepository.existsByEmail(email)) {
// Role role = roleRepository.findByRoleName(roleName)
// .orElseThrow(() -> new IllegalStateException("Role " + roleName + " not
// found"));
// user = new User();
// user.setEmail(email);
// user.setPasswordHash(passwordEncoder.encode("password123")); // Default test
// password
// user.setFullName(fullName);
// user.setRole(role);
// user.setStatus("ACTIVE");
// user = userRepository.save(user);
// } else {
// user = userRepository.findByEmail(email);
// }

// if ("THERAPIST".equals(roleName) && user != null) {
// if (!therapistRepository.existsById(user.getId())) {
// Therapist therapist = new Therapist();
// therapist.setId(user.getId());
// therapist.setTherapistCode("NV00" + user.getId());
// therapist.setStatus("AVAILABLE");
// therapistRepository.save(therapist);
// }
// }
// }
// }
