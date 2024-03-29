package com.bjpowernode.crm.workbench.service;

import com.bjpowernode.crm.workbench.domain.ActivityRemark;

import java.util.List;

public interface ActivityRemarkService {
    List<ActivityRemark> queryActivityRemarkForDetailByActivityId(String id);

    int saveCreateActivityRemark(ActivityRemark activityRemark);

    int deleteActivityRemarkById(String id);

    int updateActivityRemark(ActivityRemark activityRemark);
}
