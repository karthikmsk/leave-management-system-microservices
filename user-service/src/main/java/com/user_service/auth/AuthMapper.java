package com.user_service.auth;

import org.mapstruct.Mapper;
import com.user_service.model.User;

@Mapper(componentModel = "spring")
public interface AuthMapper {

    LoginResponse toLoginResponse(User user);
}
