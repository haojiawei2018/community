package org.hopeframework.biz.api.controller.xiaosongtv;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.hopeframework.biz.api.auto.PassToken;
import org.hopeframework.core.response.RespBody;
import org.hopeframework.core.response.ResultUtil;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.LinkedHashMap;
import java.util.Map;

/** 只提供小松电视机 Android 的资源更新元数据，不承载安装包文件。 */
@Api(tags = "小松的电视机更新")
@RestController
@RequestMapping("/api/v1/xiaosong-tv/update")
public class XiaosongTvUpdateController {
    @Value("${xiaosong-tv.update.android.wgt-version:}")
    private String wgtVersion;

    @Value("${xiaosong-tv.update.android.wgt-sha1:}")
    private String wgtSha1;

    @Value("${xiaosong-tv.update.android.wgt-url:http://161.189.5.95/xiaosong.wgt}")
    private String wgtUrl;

    @Value("${xiaosong-tv.update.android.min-apk-version-code:104}")
    private int minApkVersionCode;

    @PassToken
    @ApiOperation("检查 Android WGT 资源更新")
    @GetMapping("/android")
    public RespBody<Map<String, Object>> android() {
        Map<String, Object> update = new LinkedHashMap<>();
        boolean enabled = wgtVersion != null && wgtVersion.matches("[0-9]+(\\.[0-9]+)+")
                && wgtSha1 != null && wgtSha1.matches("(?i)[0-9a-f]{40}")
                && wgtUrl != null && wgtUrl.matches("https?://[^\\s]+\\.wgt(?:\\?[^\\s]*)?");
        update.put("enabled", enabled);
        if (enabled) {
            update.put("version", wgtVersion);
            update.put("url", wgtUrl);
            update.put("sha1", wgtSha1.toLowerCase());
            update.put("minApkVersionCode", minApkVersionCode);
        }
        return ResultUtil.success(update);
    }
}
