package com.user_service.mapper;

import java.util.List;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import com.user_service.dto.UserRequest;
import com.user_service.dto.UserResponse;
import com.user_service.model.User;

@Mapper(componentModel = "spring")
public interface UserMapper {
    
    UserResponse toUserResponse(User user);

    List<UserResponse> toUserResponses(List<User> users);

    @Mapping(target = "id", ignore = true)
    User toUser(UserRequest userRequest);

}
