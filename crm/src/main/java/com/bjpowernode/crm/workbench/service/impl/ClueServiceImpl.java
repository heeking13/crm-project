package com.bjpowernode.crm.workbench.service.impl;

import com.bjpowernode.crm.workbench.domain.Clue;
import com.bjpowernode.crm.workbench.mapper.ClueMapper;
import com.bjpowernode.crm.workbench.service.ClueService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service("clueService")
public class ClueServiceImpl implements ClueService {

    @Autowired
    private ClueMapper clueMapper;

    @Override
    public int insertClue(Clue clue) {
        return clueMapper.insertClue(clue);
    }

    @Override
    public List<Clue> queryClueByConditionForPage(Map<String, Object> map) {
        return clueMapper.queryClueByConditionForPage(map);
    }

    @Override
    public int queryCountOfClueByConditionForPage(Map<String, Object> map) {
        return clueMapper.queryCountOfClueByConditionForPage(map);
    }

    @Override
    public Clue queryClueDetail(String id) {
        return clueMapper.selectClueByIdForDetail(id);
    }
}
