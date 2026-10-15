package com.example.qcollect.project.service;

import com.example.qcollect.user.entity.User;
import com.example.qcollect.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ProjectUserAccountWriter {

    private final UserRepository userRepository;

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public User insertNew(User user) {
        return userRepository.saveAndFlush(user);
    }
}
