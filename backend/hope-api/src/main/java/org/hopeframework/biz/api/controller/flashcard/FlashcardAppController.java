package org.hopeframework.biz.api.controller.flashcard;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.hopeframework.biz.api.auto.PassToken;
import org.hopeframework.biz.api.auto.UserLoginToken;
import org.hopeframework.biz.api.entity.PageResult;
import org.hopeframework.biz.api.entity.input.flashcard.CreateFlashcardGenerationRequest;
import org.hopeframework.biz.api.entity.input.flashcard.FlashcardDeviceLoginRequest;
import org.hopeframework.biz.api.entity.input.flashcard.FlashcardAppleLoginRequest;
import org.hopeframework.biz.api.entity.input.flashcard.FlashcardPhoneLoginRequest;
import org.hopeframework.biz.api.entity.input.flashcard.FlashcardPhoneCodeRequest;
import org.hopeframework.biz.api.entity.input.flashcard.FlashcardProfileUpdateRequest;
import org.hopeframework.biz.api.entity.output.auth.TokenResponse;
import org.hopeframework.biz.api.entity.output.flashcard.FlashcardCardResponse;
import org.hopeframework.biz.api.entity.output.flashcard.FlashcardCreateOptionsResponse;
import org.hopeframework.biz.api.entity.output.flashcard.FlashcardGenerationResponse;
import org.hopeframework.biz.api.entity.output.flashcard.FlashcardHomeResponse;
import org.hopeframework.biz.api.entity.output.flashcard.FlashcardImageUploadResponse;
import org.hopeframework.biz.api.entity.output.flashcard.FlashcardProfileResponse;
import org.hopeframework.biz.api.service.flashcard.IFlashcardAppImageService;
import org.hopeframework.biz.api.service.flashcard.IFlashcardAppService;
import org.hopeframework.biz.api.service.flashcard.IFlashcardPhoneCodeService;
import org.hopeframework.core.response.RespBody;
import org.hopeframework.core.response.ResultUtil;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

/**
 * 闪卡独立APP的唯一用户端入口。
 *
 * 路由、身份、上传记录和业务表均使用flashcard_app命名空间，禁止复用社区、预约APP的Controller接口。
 */
@Api(tags = "闪卡独立APP")
@RestController
@RequestMapping("/api/v1/flashcard-app")
public class FlashcardAppController {
    private final IFlashcardAppService flashcardAppService;
    private final IFlashcardAppImageService flashcardAppImageService;
    private final IFlashcardPhoneCodeService flashcardPhoneCodeService;

    public FlashcardAppController(IFlashcardAppService flashcardAppService,
                                  IFlashcardAppImageService flashcardAppImageService,
                                  IFlashcardPhoneCodeService flashcardPhoneCodeService) {
        this.flashcardAppService = flashcardAppService;
        this.flashcardAppImageService = flashcardAppImageService;
        this.flashcardPhoneCodeService = flashcardPhoneCodeService;
    }

    @PassToken
    @ApiOperation("闪卡APP设备无感登录")
    @PostMapping("/auth/device-login")
    public RespBody<TokenResponse> deviceLogin(@RequestBody FlashcardDeviceLoginRequest request) {
        return ResultUtil.success(flashcardAppService.deviceLogin(request));
    }

    @PassToken
    @ApiOperation("发送闪卡APP手机号登录验证码")
    @PostMapping("/auth/phone/send-code")
    public RespBody<Void> sendPhoneCode(@RequestBody FlashcardPhoneCodeRequest request) {
        flashcardPhoneCodeService.send(request == null ? null : request.getPhone());
        return ResultUtil.success(null);
    }

    @PassToken
    @ApiOperation("闪卡APP手机号验证码登录（未注册自动创建账号）")
    @PostMapping("/auth/phone/login")
    public RespBody<TokenResponse> phoneLogin(@RequestBody FlashcardPhoneLoginRequest request) {
        return ResultUtil.success(flashcardAppService.phoneLogin(request));
    }

    @PassToken
    @ApiOperation("闪卡APP Apple登录（首次自动注册）")
    @PostMapping("/auth/apple/login")
    public RespBody<TokenResponse> appleLogin(@RequestBody FlashcardAppleLoginRequest request) {
        return ResultUtil.success(flashcardAppService.appleLogin(request));
    }

    @ApiOperation("查询闪卡广场首页")
    @GetMapping("/home")
    public RespBody<FlashcardHomeResponse> home(
            @RequestParam(value = "keyword", required = false) String keyword,
            @RequestParam(value = "categoryCode", required = false) String categoryCode,
            @RequestParam(value = "page", defaultValue = "1") long page,
            @RequestParam(value = "pageSize", defaultValue = "20") long pageSize) {
        return ResultUtil.success(flashcardAppService.home(keyword, categoryCode, page, pageSize));
    }

    @ApiOperation("查询闪卡详情")
    @GetMapping("/cards/{cardId}")
    public RespBody<FlashcardCardResponse> cardDetail(@PathVariable Long cardId) {
        return ResultUtil.success(flashcardAppService.cardDetail(cardId));
    }

    @PassToken
    @ApiOperation("查询闪卡制作选项")
    @GetMapping("/create-options")
    public RespBody<FlashcardCreateOptionsResponse> createOptions() {
        return ResultUtil.success(flashcardAppService.createOptions());
    }

    @UserLoginToken
    @ApiOperation("上传并压缩闪卡图片到阿里云OSS")
    @PostMapping(value = "/files/images", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public RespBody<FlashcardImageUploadResponse> uploadImage(
            @RequestParam("file") MultipartFile file,
            @RequestParam(value = "layerType", defaultValue = "SUBJECT") String layerType) {
        return ResultUtil.success(flashcardAppImageService.upload(file, layerType));
    }

    @UserLoginToken
    @ApiOperation("提交闪卡生成任务")
    @PostMapping("/generations")
    public RespBody<FlashcardGenerationResponse> createGeneration(
            @RequestBody CreateFlashcardGenerationRequest request) {
        return ResultUtil.success(flashcardAppService.createGeneration(request));
    }

    @UserLoginToken
    @ApiOperation("查询闪卡生成任务")
    @GetMapping("/generations/{taskId}")
    public RespBody<FlashcardGenerationResponse> generation(@PathVariable String taskId) {
        return ResultUtil.success(flashcardAppService.generation(taskId));
    }

    @UserLoginToken
    @ApiOperation("查询闪卡APP当前用户资料")
    @GetMapping("/me")
    public RespBody<FlashcardProfileResponse> profile() {
        return ResultUtil.success(flashcardAppService.profile());
    }

    @UserLoginToken
    @ApiOperation("修改闪卡APP当前用户资料")
    @PutMapping("/me")
    public RespBody<FlashcardProfileResponse> updateProfile(
            @RequestBody FlashcardProfileUpdateRequest request) {
        return ResultUtil.success(flashcardAppService.updateProfile(request));
    }

    @UserLoginToken
    @ApiOperation("分页查询当前用户卡册")
    @GetMapping("/me/cards")
    public RespBody<PageResult<FlashcardCardResponse>> myCards(
            @RequestParam(value = "type", defaultValue = "ALL") String type,
            @RequestParam(value = "rarity", required = false) String rarity,
            @RequestParam(value = "page", defaultValue = "1") long page,
            @RequestParam(value = "pageSize", defaultValue = "20") long pageSize) {
        return ResultUtil.success(flashcardAppService.myCards(type, rarity, page, pageSize));
    }

    @UserLoginToken
    @ApiOperation("收藏闪卡")
    @PutMapping("/cards/{cardId}/favorite")
    public RespBody<FlashcardCardResponse> favorite(@PathVariable Long cardId) {
        return ResultUtil.success(flashcardAppService.favorite(cardId));
    }

    @UserLoginToken
    @ApiOperation("取消收藏闪卡")
    @DeleteMapping("/cards/{cardId}/favorite")
    public RespBody<FlashcardCardResponse> unfavorite(@PathVariable Long cardId) {
        return ResultUtil.success(flashcardAppService.unfavorite(cardId));
    }
}
