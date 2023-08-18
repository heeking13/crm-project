package com.bjpowernode.crm.workbench.service;

import com.bjpowernode.crm.workbench.domain.ClueRemark;
import com.bjpowernode.crm.workbench.mapper.ClueRemarkMapper;
import org.springframework.stereotype.Service;

import java.util.List;


public interface ClueRemarkService {
    List<ClueRemark> queryClueRemarkById(String id);
}
