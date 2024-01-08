package br.com.desafio.UserFieldVisibility.converter;

import br.com.desafio.UserFieldVisibility.DTO.request.UserFieldVisibilityRequest;
import br.com.desafio.UserFieldVisibility.DTO.response.UserFieldVisibilityResponse;
import br.com.desafio.UserFieldVisibility.entity.UserFieldVisibility;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class UserFieldVisibilityConverter {

    private final ModelMapper modelMapper;

    @Autowired
    public UserFieldVisibilityConverter(ModelMapper modelMapper) {
        this.modelMapper = modelMapper;
    }

    public UserFieldVisibility convertUserFieldVisibilityRequestToUserFieldVisibility(UserFieldVisibilityRequest userFieldVisibilityRequest) {
        return modelMapper.map(userFieldVisibilityRequest, UserFieldVisibility.class);
    }

    public UserFieldVisibilityResponse convertUserFieldVisibilityToUserFieldVisibilityResponse(UserFieldVisibility userFieldVisibility) {
        return modelMapper.map(userFieldVisibility, UserFieldVisibilityResponse.class);
    }

    public List<UserFieldVisibilityResponse> convertUserFieldVisibilityListToUserFieldVisibilityResponseList(List<UserFieldVisibility> userFieldVisibility) {
        return userFieldVisibility.stream().map(this::convertUserFieldVisibilityToUserFieldVisibilityResponse).collect(Collectors.toList());
    }
}
