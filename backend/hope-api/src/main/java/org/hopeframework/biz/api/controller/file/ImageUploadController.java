package org.hopeframework.biz.api.controller.file;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.hopeframework.biz.api.auto.UserLoginToken;
import org.hopeframework.biz.api.entity.output.file.ImageUploadResponse;
import org.hopeframework.biz.api.service.file.IImageStorageService;
import org.hopeframework.core.response.RespBody;
import org.hopeframework.core.response.ResultUtil;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@Api(tags = "文件上传")
@UserLoginToken
@RestController
@RequestMapping("/api/v1/files")
public class ImageUploadController {
    private final IImageStorageService imageStorageService;

    public ImageUploadController(IImageStorageService imageStorageService) {
        this.imageStorageService = imageStorageService;
    }

    @ApiOperation("上传图片到阿里云 OSS")
    @PostMapping(value = "/images", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public RespBody<ImageUploadResponse> uploadImage(@RequestParam("file") MultipartFile file) {
        return ResultUtil.success(imageStorageService.upload(file));
    }
}
