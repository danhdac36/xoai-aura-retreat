package com.AuraMoon.auramoon.masterdata.service;

import com.AuraMoon.auramoon.yoga.entity.YogaClass;
import java.util.List;

public interface YogaClassMasterService {
    List<YogaClass> getAllYogaClasses();
    YogaClass getYogaClassById(Integer id);
    YogaClass createYogaClass(YogaClass yogaClass);
    YogaClass updateYogaClass(Integer id, YogaClass yogaClass);
    void deleteYogaClass(Integer id);
}
