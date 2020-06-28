package com.miku.item.service;

import com.miku.item.mapper.SpecGroupMapper;
import com.miku.item.mapper.SpecParamMapper;
import com.miku.item.pojo.SpecGroup;
import com.miku.item.pojo.SpecParam;
import com.sun.org.apache.xpath.internal.operations.Bool;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SpecificationService {

    @Autowired
    private SpecGroupMapper specGroupMapper;

    @Autowired
    private SpecParamMapper specParamMapper;

    public List<SpecGroup> queryGroupsByCid(Long cid){
        SpecGroup record = new SpecGroup();
        record.setCid(cid);
        return this.specGroupMapper.select(record);
    }

    public List<SpecParam> queryParamByGid(Long gid, Long cid, Boolean generic, Boolean searching){
        SpecParam record = new SpecParam();
        record.setGroupId(gid);
        record.setCid(cid);
        record.setGeneric(generic);
        record.setSearching(searching);
        return this.specParamMapper.select(record);

    }

    public List<SpecGroup> queryGroupsWithParam(Long cid) {

        List<SpecGroup> groups = this.queryGroupsByCid(cid);
        groups.forEach(specGroup -> {
            List<SpecParam> params = this.queryParamByGid(specGroup.getId(), null, null, null);
            specGroup.setParams(params);
        });
        return groups;
    }
}
