package org.hopeframework.biz.api.service.auth;

import org.hopeframework.biz.api.entity.input.auth.LoginRequest;
import org.hopeframework.biz.api.entity.input.auth.LogoutRequest;
import org.hopeframework.biz.api.entity.input.auth.RefreshRequest;
import org.hopeframework.biz.api.entity.input.auth.RegisterRequest;
import org.hopeframework.biz.api.entity.output.auth.TokenResponse;
import org.hopeframework.biz.api.entity.output.auth.UserSessionResponse;
import org.hopeframework.biz.api.entity.input.user.UpdateProfileRequest;

public interface IAuthService {
    TokenResponse register(RegisterRequest request, String ip);
    TokenResponse login(LoginRequest request, String ip);
    TokenResponse refresh(RefreshRequest request);
    void logout(LogoutRequest request);
    UserSessionResponse currentUser();
    UserSessionResponse updateProfile(UpdateProfileRequest request);
}
