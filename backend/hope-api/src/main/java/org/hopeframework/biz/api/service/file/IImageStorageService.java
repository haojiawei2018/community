package org.hopeframework.biz.api.service.file;

import org.hopeframework.biz.api.entity.output.file.ImageUploadResponse;
import org.springframework.web.multipart.MultipartFile;

public interface IImageStorageService {
    ImageUploadResponse upload(MultipartFile file);
}
