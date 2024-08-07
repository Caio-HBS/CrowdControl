package com.caiohbs.crowdcontrol.dto.mapper;

import com.caiohbs.crowdcontrol.dto.UserInfoDTO;
import com.caiohbs.crowdcontrol.model.UserInfo;
import org.springframework.stereotype.Service;

import java.util.function.Function;

@Service
public class UserInfoDTOMapper implements Function<UserInfo, UserInfoDTO> {

    @Override
    public UserInfoDTO apply(UserInfo userInfo) {
        return new UserInfoDTO(
                userInfo.getUser().getUserId(),
                "/" + userInfo.getPfp(),
                userInfo.getPronouns(),
                userInfo.getBio(),
                userInfo.getNationality()
        );
    }
}
