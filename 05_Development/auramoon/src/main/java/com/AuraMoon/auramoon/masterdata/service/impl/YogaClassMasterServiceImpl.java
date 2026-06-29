package com.AuraMoon.auramoon.masterdata.service.impl;

import com.AuraMoon.auramoon.masterdata.service.YogaClassMasterService;
import com.AuraMoon.auramoon.yoga.entity.YogaClass;
import com.AuraMoon.auramoon.yoga.repository.YogaClassRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class YogaClassMasterServiceImpl implements YogaClassMasterService {

    private final YogaClassRepository yogaClassRepository;

    @Override
    public List<YogaClass> getAllYogaClasses() {
        return yogaClassRepository.findAll();
    }

    @Override
    public YogaClass getYogaClassById(Integer id) {
        return yogaClassRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Lớp Yoga không tồn tại với ID: " + id));
    }

    @Override
    @Transactional
    public YogaClass createYogaClass(YogaClass yogaClass) {
        yogaClass.setIsDelete(false);
        return yogaClassRepository.save(yogaClass);
    }

    @Override
    @Transactional
    public YogaClass updateYogaClass(Integer id, YogaClass yogaClass) {
        YogaClass existing = getYogaClassById(id);
        existing.setClassName(yogaClass.getClassName());
        existing.setDescription(yogaClass.getDescription());
        existing.setDurationMinutes(yogaClass.getDurationMinutes());
        existing.setImageUrl(yogaClass.getImageUrl());
        return yogaClassRepository.save(existing);
    }

    @Override
    @Transactional
    public void deleteYogaClass(Integer id) {
        YogaClass existing = getYogaClassById(id);
        existing.setIsDelete(true);
        yogaClassRepository.save(existing);
    }
}
