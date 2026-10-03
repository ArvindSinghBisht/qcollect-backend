package com.example.qcollect.integration.cloudinary;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import com.example.qcollect.common.exception.BadRequestException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Base64;
import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CloudinaryStorageService {

    private final Cloudinary cloudinary;

    @Value("${cloudinary.folder:qcollect}")
    private String rootFolder;

    @Value("${cloudinary.max-image-size-bytes:15728640}")
    private long maxImageSizeBytes;

    public CloudinaryUploadResult upload(
            MultipartFile file,
            UUID projectId,
            UUID submissionId
    ) {
        if (file == null || file.isEmpty()) {
            throw new BadRequestException("Image file is required.");
        }

        String contentType = file.getContentType();
        if (!StringUtils.hasText(contentType)
                || !contentType.toLowerCase().startsWith("image/")) {
            throw new BadRequestException("Only image files are allowed.");
        }

        validateSize(file.getSize());

        try {
            return uploadBytes(
                    file.getBytes(),
                    safeFileName(file.getOriginalFilename()),
                    projectId,
                    submissionId
            );
        } catch (IOException ex) {
            throw new IllegalStateException("Unable to read the uploaded image.", ex);
        }
    }

    public CloudinaryUploadResult uploadBase64(
            String encodedImage,
            UUID projectId,
            UUID submissionId,
            UUID mobilePhotoId
    ) {
        if (!StringUtils.hasText(encodedImage)) {
            throw new BadRequestException("Photo image data is required for synchronization.");
        }

        String payload = encodedImage.trim();
        String extension = "jpg";

        if (payload.startsWith("data:")) {
            int commaIndex = payload.indexOf(',');
            if (commaIndex < 0) {
                throw new BadRequestException("Invalid image data URI.");
            }

            String metadata = payload.substring(5, commaIndex).toLowerCase();
            if (!metadata.startsWith("image/") || !metadata.contains(";base64")) {
                throw new BadRequestException("Only base64 encoded images are allowed.");
            }

            String mimeType = metadata.substring(0, metadata.indexOf(';'));
            extension = extensionForMimeType(mimeType);
            payload = payload.substring(commaIndex + 1);
        }

        byte[] bytes;
        try {
            bytes = Base64.getDecoder().decode(payload.replaceAll("\\s", ""));
        } catch (IllegalArgumentException ex) {
            throw new BadRequestException("Invalid base64 image data.");
        }

        validateSize(bytes.length);

        return uploadBytes(
                bytes,
                "mobile-" + mobilePhotoId + "." + extension,
                projectId,
                submissionId
        );
    }

    public CloudinaryUploadResult uploadBytes(
            byte[] bytes,
            String fileName,
            UUID projectId,
            UUID submissionId
    ) {
        if (bytes == null || bytes.length == 0) {
            throw new BadRequestException("Image file is empty.");
        }

        validateSize(bytes.length);

        return doUpload(
                bytes,
                safeFileName(fileName),
                projectId,
                submissionId
        );
    }

    private CloudinaryUploadResult doUpload(
            byte[] bytes,
            String fileName,
            UUID projectId,
            UUID submissionId
    ) {
        try {
            Map<String, Object> result = cloudinary.uploader().upload(
                    bytes,
                    ObjectUtils.asMap(
                            "resource_type", "image",
                            "folder", buildFolder(projectId, submissionId),
                            "use_filename", true,
                            "unique_filename", true,
                            "overwrite", false
                    )
            );

            String publicId = value(result, "public_id");
            String secureUrl = value(result, "secure_url");

            if (!StringUtils.hasText(publicId) || !StringUtils.hasText(secureUrl)) {
                throw new IllegalStateException("Cloudinary returned an incomplete upload response.");
            }

            return new CloudinaryUploadResult(publicId, secureUrl, fileName);
        } catch (IOException ex) {
            throw new IllegalStateException("Unable to upload image to Cloudinary.", ex);
        }
    }

    public void delete(String publicId) {
        if (!StringUtils.hasText(publicId)) {
            return;
        }

        try {
            cloudinary.uploader().destroy(
                    publicId,
                    ObjectUtils.asMap(
                            "resource_type", "image",
                            "invalidate", true
                    )
            );
        } catch (IOException ex) {
            throw new IllegalStateException("Unable to delete image from Cloudinary.", ex);
        }
    }

    private String buildFolder(UUID projectId, UUID submissionId) {
        String normalizedRoot = StringUtils.hasText(rootFolder)
                ? rootFolder.trim().replaceAll("^/+|/+$", "")
                : "qcollect";

        return normalizedRoot
                + "/projects/" + projectId
                + "/submissions/" + submissionId;
    }

    private void validateSize(long size) {
        if (size <= 0) {
            throw new BadRequestException("Image file is empty.");
        }

        if (size > maxImageSizeBytes) {
            long maxMb = Math.max(1, maxImageSizeBytes / (1024 * 1024));
            throw new BadRequestException("Image must not exceed " + maxMb + " MB.");
        }
    }

    private String safeFileName(String originalFileName) {
        if (!StringUtils.hasText(originalFileName)) {
            return "photo-" + UUID.randomUUID() + ".jpg";
        }

        String clean = originalFileName.replace('\\', '/');
        clean = clean.substring(clean.lastIndexOf('/') + 1);
        clean = clean.replaceAll("[^A-Za-z0-9._-]", "_");

        return StringUtils.hasText(clean)
                ? clean
                : "photo-" + UUID.randomUUID() + ".jpg";
    }

    private String extensionForMimeType(String mimeType) {
        return switch (mimeType) {
            case "image/png" -> "png";
            case "image/webp" -> "webp";
            case "image/gif" -> "gif";
            case "image/heic", "image/heif" -> "heic";
            default -> "jpg";
        };
    }

    private String value(Map<String, Object> result, String key) {
        Object value = result.get(key);
        return value == null ? null : value.toString();
    }
}
