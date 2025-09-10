package com.traffic.difflight.config;

import com.traffic.difflight.entity.Admin;
import com.traffic.difflight.entity.ParamOption;
import com.traffic.difflight.repository.AdminRepository;
import com.traffic.difflight.repository.ParamOptionRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

@Component
public class DataInitializer implements CommandLineRunner {

    private final AdminRepository adminRepository;
    private final ParamOptionRepository paramOptionRepository;
    private final PasswordEncoder passwordEncoder;

    public DataInitializer(AdminRepository adminRepository,
                          ParamOptionRepository paramOptionRepository,
                          PasswordEncoder passwordEncoder) {
        this.adminRepository = adminRepository;
        this.paramOptionRepository = paramOptionRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) throws Exception {
        initializeAdmin();
        initializeParameterOptions();
    }

    private void initializeAdmin() {
        if (!adminRepository.existsByUsername("admin")) {
            Admin admin = new Admin();
            admin.setUsername("admin");
            admin.setPasswordHash(passwordEncoder.encode("Admin@123"));
            admin.setPhone("13800000000");
            adminRepository.save(admin);
            System.out.println("初始管理员账号创建成功: admin / Admin@123");
        }
    }

    private void initializeParameterOptions() {
        if (paramOptionRepository.count() == 0) {
            List<ParamOption> options = Arrays.asList(
                // traffic_file
                createOption("traffic_file", "hangzhou"),
                createOption("traffic_file", "jinan"),
                
                // roadnet_file
                createOption("roadnet_file", "roadnet_3_4"),
                createOption("roadnet_file", "roadnet_4_4"),
                
                // NUM_INTERSECTIONS
                createOption("NUM_INTERSECTIONS", "16"),
                createOption("NUM_INTERSECTIONS", "12"),
                
                // missing_pattern
                createOption("missing_pattern", "random_missing"),
                createOption("missing_pattern", "kriging missing"),
                
                // missing_rate
                createOption("missing_rate", "10%"),
                createOption("missing_rate", "30%"),
                createOption("missing_rate", "50%"),
                createOption("missing_rate", "6.25%"),
                createOption("missing_rate", "12.5%"),
                createOption("missing_rate", "18.75%"),
                createOption("missing_rate", "8.33%"),
                createOption("missing_rate", "16.67%"),
                createOption("missing_rate", "25%")
            );
            
            paramOptionRepository.saveAll(options);
            System.out.println("参数选项初始化完成");
        }
    }

    private ParamOption createOption(String key, String value) {
        ParamOption option = new ParamOption();
        option.setParamKey(key);
        option.setParamValue(value);
        option.setUpdatedAt(LocalDateTime.now());
        return option;
    }
}