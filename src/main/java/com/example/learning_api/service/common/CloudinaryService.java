package com.example.learning_api.service.common;

import com.cloudinary.Cloudinary;
import com.cloudinary.Transformation;
import com.cloudinary.utils.ObjectUtils;
import com.example.learning_api.constant.ErrorConstant;
import com.example.learning_api.dto.response.CloudinaryUploadResponse;
import com.example.learning_api.model.CustomException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Map;

import static com.example.learning_api.constant.CloudinaryConstant.*;

@Component
@RequiredArgsConstructor
public class CloudinaryService {
    private final Cloudinary cloudinary;


    public void updateImage(String publicId) throws Exception {
        cloudinary.api().update(publicId,
                ObjectUtils.asMap(
                        "tags", "important",
                        "moderation_status", "approved"));
    }

    public String getImageUrl(String publicId) {
        try {
            return cloudinary.url().version(cloudinary.api().resource(publicId, null).get("version")).generate(publicId);
        } catch (Exception e) {
            throw new CustomException(ErrorConstant.NOT_FOUND);
        }
    }

    public String getThumbnailUrl(String publicId) {
        try {
            String quality = "auto:low";

            Transformation transformation = new Transformation().quality(quality).width(300).height(200);

            return cloudinary.url().transformation(transformation)
                    .version(cloudinary.api().resource(publicId, null).get("version"))
                    .generate(publicId);
        } catch (Exception e) {
            throw new CustomException(ErrorConstant.NOT_FOUND);
        }
    }

    public CloudinaryUploadResponse uploadFileToFolder(String pathName, String fileName, byte[] imageData) throws IOException {
        var file = cloudinary.uploader()
                .upload(imageData, Map.of(PUBLIC_ID, fileName, UPLOAD_PRESET, pathName, OVERWRITE, true));
        return CloudinaryUploadResponse.fromString(file.toString());

    }

    public void deleteImage(String publicId) throws IOException {
        // Xóa hình ảnh từ Cloudinary bằng cách sử dụng public ID
        Map<String, String> options = ObjectUtils.asMap("invalidate", true);
        cloudinary.uploader().destroy(publicId, options);
    }

}
