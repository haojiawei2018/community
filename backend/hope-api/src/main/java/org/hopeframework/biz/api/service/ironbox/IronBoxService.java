package org.hopeframework.biz.api.service.ironbox;

import com.alibaba.fastjson.JSON;
import com.qcloud.cos.COSClient;
import com.qcloud.cos.ClientConfig;
import com.qcloud.cos.auth.BasicCOSCredentials;
import com.qcloud.cos.http.HttpMethodName;
import com.qcloud.cos.model.GeneratePresignedUrlRequest;
import com.qcloud.cos.model.CannedAccessControlList;
import com.qcloud.cos.model.ObjectMetadata;
import com.qcloud.cos.region.Region;
import org.hopeframework.biz.api.common.security.AuthContext;
import org.hopeframework.biz.api.common.security.AuthPrincipal;
import org.hopeframework.biz.api.common.security.IronBoxAccessTokenService;
import org.hopeframework.biz.api.common.tenant.TenantContext;
import org.hopeframework.biz.api.config.cos.IronBoxCosProperties;
import org.hopeframework.core.exception.HopeException;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.http.HttpStatus;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.net.URL;
import java.sql.PreparedStatement;
import java.sql.Statement;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.HashSet;
import java.util.UUID;

/** AI 铁盒独立业务层。所有 SQL 显式使用服务端租户 ID 和专用表。 */
@Service
public class IronBoxService {
    private static final Set<String> CATEGORIES = new HashSet<>(Arrays.asList(
            "career", "life", "study", "travel", "interest", "emotion", "other"));
    private static final Set<String> MIME_TYPES = new HashSet<>(Arrays.asList(
            "image/jpeg", "image/png", "image/webp"));
    private static final long MAX_FILE_SIZE = 15L * 1024 * 1024;
    private final JdbcTemplate jdbc;
    private final IronBoxAccessTokenService tokens;
    private final IronBoxCosProperties cos;
    private final BCryptPasswordEncoder passwords = new BCryptPasswordEncoder();

    public IronBoxService(JdbcTemplate jdbc, IronBoxAccessTokenService tokens, IronBoxCosProperties cos) {
        this.jdbc = jdbc;
        this.tokens = tokens;
        this.cos = cos;
    }

    @Transactional(rollbackFor = Exception.class)
    public Map<String, Object> register(Map<String, Object> input) {
        String username = string(input, "username").toLowerCase(Locale.ROOT);
        String password = string(input, "password");
        String nickname = string(input, "nickname");
        if (!username.matches("[a-z0-9_]{4,32}")) throw bad("用户名须为4至32位字母、数字或下划线");
        if (password.length() < 8 || password.length() > 72) throw bad("密码长度须为8至72位");
        if (nickname.isEmpty()) nickname = username;
        if (nickname.codePointCount(0, nickname.length()) > 20) throw bad("昵称最多20个字符");
        final String name = nickname;
        KeyHolder key = new GeneratedKeyHolder();
        try {
            jdbc.update(connection -> {
                PreparedStatement statement = connection.prepareStatement(
                        "INSERT INTO ironbox_user(tenant_id, username, password_hash, nickname, status) VALUES(?,?,?,?, 'ACTIVE')",
                        Statement.RETURN_GENERATED_KEYS);
                statement.setLong(1, tenant()); statement.setString(2, username);
                statement.setString(3, passwords.encode(password)); statement.setString(4, name);
                return statement;
            }, key);
        } catch (DuplicateKeyException exception) {
            throw new HopeException(HttpStatus.CONFLICT.value(), "用户名已注册");
        }
        return session(key.getKey().longValue());
    }

    public Map<String, Object> login(Map<String, Object> input) {
        String username = string(input, "username").toLowerCase(Locale.ROOT);
        String password = string(input, "password");
        List<Map<String, Object>> users = jdbc.queryForList(
                "SELECT id, password_hash, status FROM ironbox_user WHERE tenant_id=? AND username=? AND deleted_at IS NULL LIMIT 1",
                tenant(), username);
        if (users.isEmpty() || !passwords.matches(password, String.valueOf(users.get(0).get("password_hash")))) {
            throw new HopeException(HttpStatus.UNAUTHORIZED.value(), "用户名或密码错误");
        }
        if (!"ACTIVE".equals(users.get(0).get("status"))) throw new HopeException(403, "账号已停用");
        Long id = number(users.get(0).get("id"));
        jdbc.update("UPDATE ironbox_user SET last_login_at=NOW() WHERE tenant_id=? AND id=?", tenant(), id);
        return session(id);
    }

    public Map<String, Object> me() { return user(currentUserId()); }

    private Map<String, Object> session(Long userId) {
        Map<String, Object> result = new LinkedHashMap<>();
        result.put("tokenType", "Bearer");
        result.put("accessToken", tokens.create(userId, tenant()));
        result.put("expiresIn", 0);
        result.put("user", user(userId));
        return result;
    }

    public Map<String, Object> user(Long id) {
        List<Map<String, Object>> rows = jdbc.queryForList(
                "SELECT id, nickname, avatar_url FROM ironbox_user WHERE tenant_id=? AND id=? AND deleted_at IS NULL AND status='ACTIVE' LIMIT 1",
                tenant(), id);
        if (rows.isEmpty()) throw new HopeException(404, "用户不存在");
        Map<String, Object> row = rows.get(0);
        Map<String, Object> result = new LinkedHashMap<>();
        result.put("id", row.get("id")); result.put("nickname", row.get("nickname"));
        result.put("avatar", row.get("avatar_url"));
        String publicOnly = id.equals(optionalUserId()) ? "" : " AND visibility='public'";
        Map<String, Object> totals = jdbc.queryForMap(
                "SELECT COUNT(*) boxCount, COALESCE(SUM(open_count),0) openCount, COALESCE(SUM(like_count),0) likeCount " +
                        "FROM ironbox_box WHERE tenant_id=? AND user_id=? AND deleted_at IS NULL AND status='published'" + publicOnly,
                tenant(), id);
        result.putAll(totals);
        return result;
    }

    public Map<String, Object> stats() {
        return jdbc.queryForMap("SELECT COUNT(*) boxCount, COALESCE(SUM(open_count),0) openCount, " +
                "COUNT(DISTINCT user_id) creatorCount FROM ironbox_box WHERE tenant_id=? AND visibility='public' " +
                "AND status='published' AND deleted_at IS NULL", tenant());
    }

    public Map<String, Object> list(int page, int pageSize, String category, String sort, String keyword, Long ownerId) {
        return listInternal(page, pageSize, category, sort, keyword, ownerId, false);
    }

    public Map<String, Object> myBoxes(int page, int pageSize) {
        return listInternal(page, pageSize, null, "latest", null, currentUserId(), true);
    }

    public Map<String, Object> savedBoxes(String kind, int page, int pageSize) {
        String table = "like".equals(kind) ? "ironbox_like" : "ironbox_collect";
        Long userId = currentUserId();
        page = Math.max(1, page); pageSize = Math.max(1, Math.min(50, pageSize));
        String joined = " FROM " + table + " r JOIN ironbox_box b ON b.id=r.box_id AND b.tenant_id=r.tenant_id " +
                "JOIN ironbox_user u ON u.id=b.user_id AND u.tenant_id=b.tenant_id " +
                "WHERE r.tenant_id=? AND r.user_id=? AND b.visibility='public' AND b.status='published' AND b.deleted_at IS NULL ";
        long total = jdbc.queryForObject("SELECT COUNT(*)" + joined, new Object[]{tenant(), userId}, Long.class);
        List<Map<String, Object>> rows = jdbc.queryForList(
                "SELECT b.*,u.nickname author_nickname,u.avatar_url author_avatar" + joined +
                        "ORDER BY r.created_at DESC,b.id DESC LIMIT ? OFFSET ?",
                tenant(), userId, pageSize, (page - 1) * pageSize);
        List<Map<String, Object>> list = new ArrayList<>();
        for (Map<String, Object> row : rows) list.add(box(row));
        Map<String, Object> result = new LinkedHashMap<>();
        result.put("list", list); result.put("page", page); result.put("pageSize", pageSize);
        result.put("total", total); result.put("hasMore", (long) page * pageSize < total);
        return result;
    }

    private Map<String, Object> listInternal(int page, int pageSize, String category, String sort,
                                              String keyword, Long ownerId, boolean includePrivate) {
        page = Math.max(1, page); pageSize = Math.max(1, Math.min(50, pageSize));
        StringBuilder where = new StringBuilder(" WHERE b.tenant_id=? AND b.status='published' AND b.deleted_at IS NULL ");
        List<Object> args = new ArrayList<>(); args.add(tenant());
        if (!includePrivate) where.append("AND b.visibility='public' ");
        if (ownerId != null) { where.append("AND b.user_id=? "); args.add(ownerId); }
        if (StringUtils.hasText(category) && CATEGORIES.contains(category)) {
            where.append("AND b.category=? "); args.add(category);
        }
        if (StringUtils.hasText(keyword)) {
            String term = "%" + keyword.trim().replace("%", "\\%").replace("_", "\\_") + "%";
            where.append("AND (b.title LIKE ? OR b.tags_json LIKE ? OR u.nickname LIKE ?) ");
            args.add(term); args.add(term); args.add(term);
        }
        String join = " FROM ironbox_box b JOIN ironbox_user u ON u.id=b.user_id AND u.tenant_id=b.tenant_id ";
        long total = jdbc.queryForObject("SELECT COUNT(*)" + join + where, args.toArray(), Long.class);
        String order = "hot".equals(sort) ? "b.like_count DESC, b.id DESC" :
                "recommended".equals(sort) ? "b.open_count DESC, b.id DESC" : "b.id DESC";
        List<Object> pagedArgs = new ArrayList<>(args); pagedArgs.add(pageSize); pagedArgs.add((page - 1) * pageSize);
        List<Map<String, Object>> rows = jdbc.queryForList(
                "SELECT b.*, u.nickname author_nickname, u.avatar_url author_avatar" + join + where +
                        "ORDER BY " + order + " LIMIT ? OFFSET ?", pagedArgs.toArray());
        List<Map<String, Object>> result = new ArrayList<>();
        for (Map<String, Object> row : rows) result.add(box(row));
        Map<String, Object> out = new LinkedHashMap<>();
        out.put("list", result); out.put("page", page); out.put("pageSize", pageSize);
        out.put("total", total); out.put("hasMore", (long) page * pageSize < total);
        return out;
    }

    public Map<String, Object> random() {
        List<Map<String, Object>> rows = jdbc.queryForList(
                "SELECT b.*, u.nickname author_nickname, u.avatar_url author_avatar FROM ironbox_box b " +
                        "JOIN ironbox_user u ON u.id=b.user_id AND u.tenant_id=b.tenant_id " +
                        "WHERE b.tenant_id=? AND b.visibility='public' AND b.status='published' " +
                        "AND b.deleted_at IS NULL ORDER BY RAND() LIMIT 1", tenant());
        if (rows.isEmpty()) throw new HopeException(404, "档案馆还没有铁盒");
        return box(rows.get(0));
    }

    public Map<String, Object> detail(Long id, boolean countView) {
        List<Map<String, Object>> rows = jdbc.queryForList(
                "SELECT b.*, u.nickname author_nickname, u.avatar_url author_avatar FROM ironbox_box b " +
                        "JOIN ironbox_user u ON u.id=b.user_id AND u.tenant_id=b.tenant_id " +
                        "WHERE b.tenant_id=? AND b.id=? AND b.deleted_at IS NULL AND b.status='published' LIMIT 1",
                tenant(), id);
        if (rows.isEmpty()) throw new HopeException(404, "铁盒不存在");
        Map<String, Object> row = rows.get(0);
        if (!"public".equals(row.get("visibility")) && !number(row.get("user_id")).equals(optionalUserId())) {
            throw new HopeException(404, "铁盒不存在");
        }
        if (countView) {
            jdbc.update("UPDATE ironbox_box SET view_count=view_count+1 WHERE tenant_id=? AND id=?", tenant(), id);
            row.put("view_count", number(row.get("view_count")) + 1);
        }
        return box(row);
    }

    public void opened(Long id) {
        int changed = jdbc.update("UPDATE ironbox_box SET open_count=open_count+1 WHERE tenant_id=? AND id=? " +
                "AND visibility='public' AND status='published' AND deleted_at IS NULL", tenant(), id);
        if (changed == 0) throw new HopeException(404, "铁盒不存在");
    }

    @Transactional(rollbackFor = Exception.class)
    public Map<String, Object> create(Map<String, Object> input) {
        Long userId = currentUserId();
        String title = string(input, "title");
        String description = string(input, "description");
        String category = string(input, "category");
        String visibility = string(input, "visibility");
        String key = string(input, "key");
        if (title.isEmpty() || title.codePointCount(0, title.length()) > 30) throw bad("标题须为1至30字");
        if (description.codePointCount(0, description.length()) > 300) throw bad("描述最多300字");
        if (!CATEGORIES.contains(category)) throw bad("请选择有效分类");
        if (!"public".equals(visibility) && !"private".equals(visibility)) throw bad("公开范围不正确");
        List<String> tags = tags(input.get("tags"));
        String prefix = keyPrefix(userId);
        if (!key.startsWith(prefix) || !key.matches("[A-Za-z0-9/_\\.-]+")) throw bad("上传图片不属于当前账号");
        ObjectMetadata metadata = imageMetadata(key);
        if (metadata.getContentLength() < 1 || metadata.getContentLength() > MAX_FILE_SIZE ||
                !MIME_TYPES.contains(metadata.getContentType())) throw bad("图片格式或大小不符合要求");
        String mediumKey = key + ".medium.webp";
        String thumbnailKey = key + ".thumb.webp";
        for (String variant : Arrays.asList(mediumKey, thumbnailKey)) {
            ObjectMetadata variantMetadata = imageMetadata(variant);
            if (variantMetadata.getContentLength() < 1 || variantMetadata.getContentLength() > MAX_FILE_SIZE ||
                    !"image/webp".equals(variantMetadata.getContentType())) throw bad("压缩图片格式或大小不符合要求");
        }
        COSClient client = cosClient();
        try {
            CannedAccessControlList acl = "private".equals(visibility) ?
                    CannedAccessControlList.Private : CannedAccessControlList.PublicRead;
            for (String objectKey : Arrays.asList(key, mediumKey, thumbnailKey))
                client.setObjectAcl(cos.getBucketName(), objectKey, acl);
        } catch (Exception exception) {
            throw new HopeException(502, "无法设置图片访问权限，请稍后重试", exception);
        } finally { client.shutdown(); }
        String original = publicUrl(key);
        String medium = publicUrl(mediumKey);
        String thumbnail = publicUrl(thumbnailKey);
        int width = intValue(input.get("width")); int height = intValue(input.get("height"));
        KeyHolder id = new GeneratedKeyHolder();
        try {
            jdbc.update(connection -> {
                PreparedStatement statement = connection.prepareStatement(
                        "INSERT INTO ironbox_box(tenant_id,user_id,title,description,category,tags_json,original_url,medium_url,thumbnail_url," +
                                "object_key,width,height,file_size,mime_type,visibility,status) VALUES(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,'published')",
                        Statement.RETURN_GENERATED_KEYS);
                statement.setLong(1, tenant()); statement.setLong(2, userId); statement.setString(3, title);
                statement.setString(4, description); statement.setString(5, category);
                statement.setString(6, JSON.toJSONString(tags)); statement.setString(7, original);
                statement.setString(8, medium); statement.setString(9, thumbnail); statement.setString(10, key);
                statement.setInt(11, width); statement.setInt(12, height);
                statement.setLong(13, metadata.getContentLength()); statement.setString(14, metadata.getContentType());
                statement.setString(15, visibility); return statement;
            }, id);
        } catch (DuplicateKeyException exception) {
            throw new HopeException(409, "这张图片已经发布过");
        }
        Map<String, Object> result = new LinkedHashMap<>();
        result.put("id", id.getKey().longValue()); result.put("status", "published");
        return result;
    }

    public void delete(Long id) {
        int changed = jdbc.update("UPDATE ironbox_box SET status='deleted',deleted_at=NOW() WHERE tenant_id=? AND id=? " +
                "AND user_id=? AND deleted_at IS NULL", tenant(), id, currentUserId());
        if (changed == 0) throw new HopeException(404, "铁盒不存在或不属于你");
    }

    @Transactional(rollbackFor = Exception.class)
    public Map<String, Object> reaction(Long boxId, String kind, boolean add) {
        String table = "like".equals(kind) ? "ironbox_like" : "ironbox_collect";
        String count = "like".equals(kind) ? "like_count" : "collect_count";
        Long userId = currentUserId();
        detail(boxId, false);
        int changed = add ? jdbc.update("INSERT IGNORE INTO " + table + "(tenant_id,user_id,box_id) VALUES(?,?,?)",
                tenant(), userId, boxId) : jdbc.update("DELETE FROM " + table + " WHERE tenant_id=? AND user_id=? AND box_id=?",
                tenant(), userId, boxId);
        if (changed > 0) jdbc.update("UPDATE ironbox_box SET " + count + "=GREATEST(0," + count + (add ? "+1" : "-1") +
                ") WHERE tenant_id=? AND id=?", tenant(), boxId);
        Map<String, Object> result = new HashMap<>(); result.put("active", add); return result;
    }

    public Map<String, Object> comments(Long boxId, int page, int pageSize) {
        detail(boxId, false); page = Math.max(1, page); pageSize = Math.max(1, Math.min(50, pageSize));
        List<Map<String, Object>> rows = jdbc.queryForList(
                "SELECT c.id,c.content,c.created_at,u.id author_id,u.nickname author_nickname,u.avatar_url author_avatar " +
                        "FROM ironbox_comment c JOIN ironbox_user u ON u.id=c.user_id AND u.tenant_id=c.tenant_id " +
                        "WHERE c.tenant_id=? AND c.box_id=? AND c.deleted_at IS NULL ORDER BY c.id DESC LIMIT ? OFFSET ?",
                tenant(), boxId, pageSize, (page - 1) * pageSize);
        List<Map<String, Object>> list = new ArrayList<>();
        for (Map<String, Object> row : rows) {
            Map<String, Object> comment = new LinkedHashMap<>();
            comment.put("id", row.get("id")); comment.put("content", row.get("content"));
            comment.put("createdAt", row.get("created_at"));
            comment.put("author", author(row)); list.add(comment);
        }
        Map<String, Object> result = new LinkedHashMap<>(); result.put("list", list); result.put("page", page);
        result.put("hasMore", list.size() == pageSize); return result;
    }

    @Transactional(rollbackFor = Exception.class)
    public Map<String, Object> comment(Long boxId, Map<String, Object> input) {
        Long userId = currentUserId(); detail(boxId, false);
        String content = string(input, "content");
        if (content.isEmpty() || content.codePointCount(0, content.length()) > 500) throw bad("评论须为1至500字");
        KeyHolder id = new GeneratedKeyHolder();
        jdbc.update(connection -> {
            PreparedStatement statement = connection.prepareStatement(
                    "INSERT INTO ironbox_comment(tenant_id,box_id,user_id,content) VALUES(?,?,?,?)",
                    Statement.RETURN_GENERATED_KEYS);
            statement.setLong(1, tenant()); statement.setLong(2, boxId); statement.setLong(3, userId);
            statement.setString(4, content); return statement;
        }, id);
        jdbc.update("UPDATE ironbox_box SET comment_count=comment_count+1 WHERE tenant_id=? AND id=?", tenant(), boxId);
        Map<String, Object> result = new HashMap<>(); result.put("id", id.getKey().longValue()); return result;
    }

    public Map<String, Object> uploadToken(Map<String, Object> input) {
        Long userId = currentUserId(); requireCos();
        String mime = string(input, "mimeType"); long size = longValue(input.get("fileSize"));
        if (!MIME_TYPES.contains(mime) || size < 1 || size > MAX_FILE_SIZE) throw bad("仅支持15MB以内的JPG、PNG或WebP图片");
        String extension = "image/png".equals(mime) ? "png" : "image/webp".equals(mime) ? "webp" : "jpg";
        String key = keyPrefix(userId) + LocalDate.now().toString().replace('-', '/') + "/" +
                UUID.randomUUID().toString().replace("-", "") + "." + extension;
        Date expiry = new Date(System.currentTimeMillis() + 10 * 60 * 1000);
        COSClient client = cosClient();
        try {
            Map<String, Object> result = new LinkedHashMap<>();
            result.put("uploadUrl", signedPutUrl(client, key, expiry)); result.put("key", key);
            result.put("mediumUploadUrl", signedPutUrl(client, key + ".medium.webp", expiry));
            result.put("thumbnailUploadUrl", signedPutUrl(client, key + ".thumb.webp", expiry));
            result.put("originalUrl", publicUrl(key)); result.put("expireAt", expiry.getTime() / 1000);
            return result;
        } finally { client.shutdown(); }
    }

    private String signedPutUrl(COSClient client, String key, Date expiry) {
        GeneratePresignedUrlRequest request = new GeneratePresignedUrlRequest(cos.getBucketName(), key, HttpMethodName.PUT);
        request.setExpiration(expiry);
        return client.generatePresignedUrl(request).toString();
    }

    private ObjectMetadata imageMetadata(String key) {
        requireCos(); COSClient client = cosClient();
        try { return client.getObjectMetadata(cos.getBucketName(), key); }
        catch (Exception exception) { throw bad("上传图片不存在或尚未完成上传"); }
        finally { client.shutdown(); }
    }

    private COSClient cosClient() {
        return new COSClient(new BasicCOSCredentials(cos.getSecretId(), cos.getSecretKey()),
                new ClientConfig(new Region(cos.getRegion())));
    }

    private void requireCos() {
        if (!StringUtils.hasText(cos.getSecretId()) || !StringUtils.hasText(cos.getSecretKey()) ||
                !StringUtils.hasText(cos.getRegion()) || !StringUtils.hasText(cos.getBucketName())) {
            throw new HopeException(503, "AI铁盒对象存储尚未配置");
        }
    }

    private String publicUrl(String key) {
        String domain = StringUtils.hasText(cos.getPublicDomain()) ? cos.getPublicDomain() :
                "https://" + cos.getBucketName() + ".cos." + cos.getRegion() + ".myqcloud.com";
        return domain.replaceAll("/+$", "") + "/" + key;
    }

    private String keyPrefix(Long userId) { return "boxes/" + tenant() + "/" + userId + "/"; }

    private Map<String, Object> box(Map<String, Object> row) {
        Map<String, Object> result = new LinkedHashMap<>();
        for (String field : Arrays.asList("id", "title", "description", "category", "visibility", "status"))
            result.put(field, row.get(field));
        if ("private".equals(row.get("visibility"))) {
            String key = String.valueOf(row.get("object_key"));
            result.put("imageUrl", signedReadUrl(key + ".medium.webp"));
            result.put("mediumUrl", signedReadUrl(key + ".medium.webp"));
            result.put("thumbnailUrl", signedReadUrl(key + ".thumb.webp"));
            result.put("originalUrl", signedReadUrl(key));
        } else {
            result.put("imageUrl", row.get("medium_url")); result.put("mediumUrl", row.get("medium_url"));
            result.put("thumbnailUrl", row.get("thumbnail_url")); result.put("originalUrl", row.get("original_url"));
        }
        result.put("width", row.get("width")); result.put("height", row.get("height"));
        result.put("tags", JSON.parseArray(String.valueOf(row.get("tags_json")), String.class));
        result.put("author", author(row));
        for (String field : Arrays.asList("like_count", "collect_count", "comment_count", "view_count", "open_count"))
            result.put(camel(field), row.get(field));
        result.put("createdAt", row.get("created_at"));
        Long me = optionalUserId();
        if (me != null) {
            result.put("liked", exists("ironbox_like", me, number(row.get("id"))));
            result.put("collected", exists("ironbox_collect", me, number(row.get("id"))));
        } else { result.put("liked", false); result.put("collected", false); }
        return result;
    }

    private Map<String, Object> author(Map<String, Object> row) {
        Map<String, Object> author = new LinkedHashMap<>();
        author.put("id", row.get("author_id") == null ? row.get("user_id") : row.get("author_id"));
        author.put("nickname", row.get("author_nickname")); author.put("avatar", row.get("author_avatar"));
        return author;
    }

    private boolean exists(String table, Long userId, Long boxId) {
        return jdbc.queryForObject("SELECT COUNT(*) FROM " + table + " WHERE tenant_id=? AND user_id=? AND box_id=?",
                new Object[]{tenant(), userId, boxId}, Integer.class) > 0;
    }

    private String camel(String snake) {
        StringBuilder result = new StringBuilder(); boolean upper = false;
        for (char ch : snake.toCharArray()) {
            if (ch == '_') upper = true;
            else { result.append(upper ? Character.toUpperCase(ch) : ch); upper = false; }
        }
        return result.toString();
    }

    private List<String> tags(Object input) {
        if (!(input instanceof List)) return Collections.emptyList();
        List<?> values = (List<?>) input;
        if (values.size() > 5) throw bad("最多填写5个标签");
        List<String> result = new ArrayList<>();
        for (Object value : values) {
            String tag = String.valueOf(value).trim();
            if (tag.isEmpty() || tag.codePointCount(0, tag.length()) > 20) throw bad("标签长度须为1至20字");
            if (!result.contains(tag)) result.add(tag);
        }
        return result;
    }

    private Long currentUserId() {
        AuthPrincipal principal = AuthContext.require();
        if (!principal.isIronBoxUser() || !tenant().equals(principal.getTenantId())) throw new HopeException(403, "账号无权访问AI铁盒");
        List<Map<String, Object>> users = jdbc.queryForList(
                "SELECT id FROM ironbox_user WHERE tenant_id=? AND id=? AND status='ACTIVE' AND deleted_at IS NULL LIMIT 1",
                tenant(), principal.getUserId());
        if (users.isEmpty()) throw new HopeException(401, "用户不存在或已停用，请重新登录");
        return principal.getUserId();
    }

    private String signedReadUrl(String key) {
        requireCos(); COSClient client = cosClient();
        try {
            Date expiry = new Date(System.currentTimeMillis() + 10 * 60 * 1000);
            return client.generatePresignedUrl(cos.getBucketName(), key, expiry, HttpMethodName.GET).toString();
        } finally { client.shutdown(); }
    }

    private Long optionalUserId() {
        AuthPrincipal principal = AuthContext.current();
        return principal != null && principal.isIronBoxUser() && tenant().equals(principal.getTenantId()) ? principal.getUserId() : null;
    }

    private Long tenant() { return TenantContext.requireTenantId(); }
    private String string(Map<String, Object> input, String key) {
        Object value = input == null ? null : input.get(key);
        return value == null ? "" : String.valueOf(value).trim();
    }
    private int intValue(Object value) {
        try { return Math.max(0, Integer.parseInt(String.valueOf(value))); } catch (Exception exception) { return 0; }
    }
    private long longValue(Object value) {
        try { return Long.parseLong(String.valueOf(value)); } catch (Exception exception) { return 0; }
    }
    private Long number(Object value) { return ((Number) value).longValue(); }
    private HopeException bad(String message) { return new HopeException(400, message); }
}
