package org.hopeframework.biz.api.service.petsnack;

import org.hopeframework.biz.api.entity.output.file.ImageUploadResponse;
import org.springframework.web.multipart.MultipartFile;

public interface IPetSnackImageStorageService {
    ImageUploadResponse upload(MultipartFile file);
}
