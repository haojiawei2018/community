package org.hopeframework.biz.api.service.impl.petsnack;

import com.qcloud.cos.COSClient;
import com.qcloud.cos.ClientConfig;
import com.qcloud.cos.auth.BasicCOSCredentials;
import com.qcloud.cos.model.ObjectMetadata;
import com.qcloud.cos.model.PutObjectRequest;
import com.qcloud.cos.region.Region;
import org.hopeframework.biz.api.config.petsnack.PetSnackCosProperties;
import org.hopeframework.biz.api.entity.output.file.ImageUploadResponse;
import org.hopeframework.biz.api.service.petsnack.IPetSnackImageStorageService;
import org.hopeframework.core.exception.HopeException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;
import java.io.InputStream;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Locale;
import java.util.Set;
import java.util.UUID;

@Service
public class PetSnackCosImageStorageService implements IPetSnackImageStorageService {
    private static final Set<String> EXT = new HashSet<>(Arrays.asList("jpg","jpeg","png","gif","webp","bmp"));
    private final PetSnackCosProperties properties;
    public PetSnackCosImageStorageService(PetSnackCosProperties properties){this.properties=properties;}

    public ImageUploadResponse upload(MultipartFile file){
        validate(file);String original=StringUtils.cleanPath(file.getOriginalFilename());
        String extension=StringUtils.getFilenameExtension(original).toLowerCase(Locale.ROOT);
        String prefix=properties.getObjectPrefix().replaceAll("^/+|/+$","");
        String objectName=prefix+"/"+LocalDate.now().toString().replace("-","/")+"/"+UUID.randomUUID().toString().replace("-","")+"."+extension;
        ObjectMetadata metadata=new ObjectMetadata();metadata.setContentLength(file.getSize());metadata.setContentType(file.getContentType());
        COSClient client=null;
        try(InputStream input=file.getInputStream()){
            client=new COSClient(new BasicCOSCredentials(properties.getSecretId(),properties.getSecretKey()),new ClientConfig(new Region(properties.getRegion())));
            client.putObject(new PutObjectRequest(properties.getBucketName(),objectName,input,metadata));
        }catch(Exception e){throw new HopeException(HttpStatus.BAD_GATEWAY.value(),"宠物商城图片上传到腾讯云COS失败",e);}finally{if(client!=null)client.shutdown();}
        String domain=StringUtils.hasText(properties.getPublicDomain())?properties.getPublicDomain():"https://"+properties.getBucketName()+".cos."+properties.getRegion()+".myqcloud.com";
        return new ImageUploadResponse(domain.replaceAll("/+$","")+"/"+objectName,objectName,original,file.getSize());
    }
    private void validate(MultipartFile file){
        if(!StringUtils.hasText(properties.getSecretId())||!StringUtils.hasText(properties.getSecretKey())||!StringUtils.hasText(properties.getRegion())||!StringUtils.hasText(properties.getBucketName()))throw new HopeException(503,"宠物商城腾讯云COS尚未配置");
        if(file==null||file.isEmpty())throw new HopeException(400,"请选择图片");
        String ext=StringUtils.getFilenameExtension(StringUtils.cleanPath(file.getOriginalFilename()));ext=ext==null?"":ext.toLowerCase(Locale.ROOT);
        if(!EXT.contains(ext)||file.getContentType()==null||!file.getContentType().startsWith("image/"))throw new HopeException(400,"仅支持jpg、png、gif、webp、bmp图片");
        if(file.getSize()>properties.getMaxImageSize().toBytes())throw new HopeException(400,"图片不能超过"+properties.getMaxImageSize());
    }
}
