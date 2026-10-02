package org.hopeframework.biz.api.entity.output.flashcard;

import lombok.Data;

@Data
public class FlashcardImageUploadResponse {
    private Long fileId;
    private String url;
    private String objectName;
    private String originalName;
    private long originalSize;
    private long size;
    private int width;
    private int height;
    private String format;
    private boolean compressed;
}
