import com.qcloud.cos.COSClient;
import com.qcloud.cos.ClientConfig;
import com.qcloud.cos.auth.BasicCOSCredentials;
import com.qcloud.cos.model.BucketCrossOriginConfiguration;
import com.qcloud.cos.model.CORSRule;
import com.qcloud.cos.region.Region;
import java.util.ArrayList;
import java.util.List;

/** 一次性运维工具；只从环境变量取 AI 铁盒 COS 密钥。 */
public class IronBoxCosAdmin {
    public static void main(String[] args) {
        String id = System.getenv("IRONBOX_COS_SECRET_ID");
        String key = System.getenv("IRONBOX_COS_SECRET_KEY");
        if (id == null || key == null) throw new IllegalStateException("AI铁盒 COS 环境变量缺失");
        String bucket = "ironbox-1318651650";
        COSClient client = new COSClient(new BasicCOSCredentials(id, key), new ClientConfig(new Region("ap-beijing")));
        try {
            if (args.length > 0 && "cors".equals(args[0])) {
                BucketCrossOriginConfiguration config;
                try { config = client.getBucketCrossOriginConfiguration(bucket); }
                catch (com.qcloud.cos.exception.CosServiceException missing) {
                    if (missing.getStatusCode() != 404) throw missing;
                    config = new BucketCrossOriginConfiguration();
                }
                if (config == null) config = new BucketCrossOriginConfiguration();
                List<CORSRule> rules = config.getRules() == null ? new ArrayList<CORSRule>() :
                        new ArrayList<CORSRule>(config.getRules());
                rules.removeIf(rule -> "ai-ironbox-web-upload".equals(rule.getId()));
                CORSRule rule = new CORSRule();
                rule.setId("ai-ironbox-web-upload");
                rule.setAllowedMethods(CORSRule.AllowedMethods.PUT, CORSRule.AllowedMethods.GET, CORSRule.AllowedMethods.HEAD);
                rule.setAllowedHeaders("*");
                rule.setAllowedOrigins("*");
                rule.setExposedHeaders("ETag", "Content-Length");
                rule.setMaxAgeSeconds(600);
                rules.add(rule);
                config.setRules(rules);
                client.setBucketCrossOriginConfiguration(bucket, config);
                System.out.println("AI铁盒存储桶 CORS 已配置，规则数=" + client.getBucketCrossOriginConfiguration(bucket).getRules().size());
            } else if (args.length > 1 && "delete".equals(args[0])) {
                for (int i = 1; i < args.length; i++) client.deleteObject(bucket, args[i]);
                System.out.println("已清理测试对象 " + (args.length - 1) + " 个");
            } else throw new IllegalArgumentException("usage: cors | delete objectKey...");
        } finally { client.shutdown(); }
    }
}
