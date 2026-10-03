package com.example.qcollect.integration.cloudinary;

public record CloudinaryUploadResult(
        String publicId,
        String secureUrl,
        String originalFileName
) {
}
