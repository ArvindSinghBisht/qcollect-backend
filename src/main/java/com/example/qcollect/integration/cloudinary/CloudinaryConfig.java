package com.example.qcollect.integration.cloudinary;

import com.cloudinary.Cloudinary;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.StringUtils;

@Configuration
public class CloudinaryConfig {

    @Bean
    public Cloudinary cloudinary(
            @Value("${cloudinary.url:}") String cloudinaryUrl
    ) {
        if (!StringUtils.hasText(cloudinaryUrl)) {
            throw new IllegalStateException(
                    "Cloudinary is not configured. Set the CLOUDINARY_URL environment variable."
            );
        }

        return new Cloudinary(cloudinaryUrl.trim());
    }
}
