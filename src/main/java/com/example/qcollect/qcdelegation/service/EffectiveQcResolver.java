package com.example.qcollect.qcdelegation.service;

import com.example.qcollect.submission.entity.Submission;
import com.example.qcollect.user.entity.User;

public interface EffectiveQcResolver {

    User resolve(Submission submission);

}