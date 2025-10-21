package com.example.hacka.service;

import com.example.hacka.dto.UserLimitDTO;
import com.example.hacka.entity.User;
import com.example.hacka.entity.UserLimit;
import com.example.hacka.repository.UserLimitRepository;
import com.example.hacka.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserLimitService {

    @Autowired
    private UserLimitRepository userLimitRepository;

    @Autowired
    private UserRepository userRepository;

    @Transactional
    public UserLimitDTO assignLimit(UserLimitDTO dto) {
        User user = userRepository.findById(dto.getUserId()).orElse(null);
        if (user == null) return null;

        UserLimit limit = new UserLimit();
        limit.setUser(user);
        limit.setModelType(dto.getModelType());
        limit.setRequestLimitPerWindow(dto.getRequestLimitPerWindow());
        limit.setTokenLimitPerWindow(dto.getTokenLimitPerWindow());
        limit.setTimeWindowMinutes(dto.getTimeWindowMinutes());
        limit = userLimitRepository.save(limit);

        return toDTO(limit);
    }

    private UserLimitDTO toDTO(UserLimit limit) {
        UserLimitDTO dto = new UserLimitDTO();
        dto.setId(limit.getId());
        dto.setUserId(limit.getUser().getId());
        dto.setModelType(limit.getModelType());
        dto.setRequestLimitPerWindow(limit.getRequestLimitPerWindow());
        dto.setTokenLimitPerWindow(limit.getTokenLimitPerWindow());
        dto.setTimeWindowMinutes(limit.getTimeWindowMinutes());
        return dto;
    }
}
