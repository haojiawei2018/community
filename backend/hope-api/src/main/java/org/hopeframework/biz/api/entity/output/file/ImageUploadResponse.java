package org.hopeframework.biz.api.entity.output.file;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ImageUploadResponse {
    private String url;
    private String objectName;
    private String originalName;
    private long size;
}
