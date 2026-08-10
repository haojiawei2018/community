package org.hopeframework.biz.api.module.auth.service;

import org.hopeframework.biz.api.module.auth.dto.LoginRequest;
import org.hopeframework.biz.api.module.auth.dto.LogoutRequest;
import org.hopeframework.biz.api.module.auth.dto.RefreshRequest;
import org.hopeframework.biz.api.module.auth.dto.RegisterRequest;
import org.hopeframework.biz.api.module.auth.dto.TokenResponse;
import org.hopeframework.biz.api.module.auth.dto.UserSessionResponse;
import org.hopeframework.biz.api.module.user.dto.UpdateProfileRequest;

public interface AuthService {
    TokenResponse register(RegisterRequest request, String ip);
    TokenResponse login(LoginRequest request, String ip);
    TokenResponse refresh(RefreshRequest request);
    void logout(LogoutRequest request);
    UserSessionResponse currentUser();
    UserSessionResponse updateProfile(UpdateProfileRequest request);
}
