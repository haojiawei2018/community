package org.hopeframework.biz.api.entity.output.flashcard;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class FlashcardCreateOptionsResponse {
    private List<StyleOption> styles = new ArrayList<>();
    private List<CodeNameOption> rarities = new ArrayList<>();
    private UploadConfig uploadConfig = new UploadConfig();

    @Data
    public static class StyleOption {
        private String code;
        private String name;
        private String imageUrl;

        public StyleOption(String code, String name, String imageUrl) {
            this.code = code;
            this.name = name;
            this.imageUrl = imageUrl;
        }
    }

    @Data
    public static class CodeNameOption {
        private String code;
        private String name;

        public CodeNameOption(String code, String name) {
            this.code = code;
            this.name = name;
        }
    }

    @Data
    public static class UploadConfig {
        private int maxLayerCount = 1;
        private long maxFileSize = 10L * 1024L * 1024L;
        private int maxImageSide = 2048;
        private int recommendedWidth = 1560;
        private int recommendedHeight = 2000;
        private String cardAspectRatio = "39:50";
        private List<String> allowedExtensions = new ArrayList<>();

        public UploadConfig() {
            allowedExtensions.add("jpg");
            allowedExtensions.add("jpeg");
            allowedExtensions.add("png");
        }
    }
}
