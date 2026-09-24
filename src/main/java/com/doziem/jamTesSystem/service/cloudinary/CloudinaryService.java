package com.doziem.jamTesSystem.service.cloudinary;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import com.doziem.jamTesSystem.exceptions.CustomException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class CloudinaryService {
    private final Cloudinary cloudinary;

    public Map<String, String> upload(MultipartFile file, String folder) {
        try {
            Map params = ObjectUtils.asMap("folder", folder, "resource_type", "image");

            @SuppressWarnings("unchecked")
            Map<String, Object> uploadResult = (Map<String, Object>) cloudinary.uploader().upload(file.getBytes(), params);

            return Map.of("url", uploadResult.get("secure_url").toString(), "publicId", uploadResult.get("public_id").toString());

        } catch (Exception e) {
            throw new CustomException("Upload failed: " + e.getMessage());
        }
    }

    public void delete(String publicId) {
        try {
            Map result = cloudinary.uploader().destroy(publicId, Map.of("invalidate", true));

            String status = (String) result.get("result");

            if (!"ok".equals(status) && !"not found".equals(status)) {
                throw new CustomException("Cloudinary deletion failed → status=" + status);
            }

        } catch (Exception ex) {
            log.error("Cloudinary deletion error: {}", ex.getMessage());
            throw new CustomException("Cloudinary deletion error → " + ex.getMessage());
        }
    }
}
