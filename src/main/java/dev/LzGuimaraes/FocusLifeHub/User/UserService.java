package dev.LzGuimaraes.FocusLifeHub.User;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import dev.LzGuimaraes.FocusLifeHub.Exceptions.ResourceNotFoundException;
import dev.LzGuimaraes.FocusLifeHub.User.dto.request.UserUpdateDTO;
import dev.LzGuimaraes.FocusLifeHub.User.dto.response.UserResponseDTO;
import dev.LzGuimaraes.FocusLifeHub.config.JWTUserData;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;

    public UserService(UserRepository userRepository, UserMapper userMapper) {
        this.userRepository = userRepository;
        this.userMapper = userMapper;
    }

    public UserResponseDTO getMe() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        JWTUserData jwtData = (JWTUserData) authentication.getPrincipal();

        UserModel user = userRepository.findById(jwtData.userId()) 
                .orElseThrow(() -> new ResourceNotFoundException("Usuário com ID " + jwtData.userId() + " não encontrado"));

        return userMapper.toResponse(user);
    }


    public UserResponseDTO updateMe(UserUpdateDTO dto) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        JWTUserData jwtData = (JWTUserData) authentication.getPrincipal();

        UserModel user = userRepository.findById(jwtData.userId())
                .orElseThrow(() -> new ResourceNotFoundException("Usuário " + jwtData.userId() + " não encontrado para atualizar"));

        user.setNome(dto.nome());
        user.setEmail(dto.email());

        UserModel updatedUser = userRepository.save(user);

        return userMapper.toResponse(updatedUser);
    }
}