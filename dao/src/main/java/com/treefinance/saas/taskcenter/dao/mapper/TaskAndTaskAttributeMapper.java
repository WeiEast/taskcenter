package com.treefinance.saas.taskcenter.dao.mapper;

import com.treefinance.saas.taskcenter.dao.entity.TaskAndTaskAttribute;

import java.util.List;
import java.util.Map;

/**
 * Created by haojiahong on 2017/12/27.
 */
public interface TaskAndTaskAttributeMapper {

    long countByExample(Map<String, Object> map);

    List<TaskAndTaskAttribute> getByExample(Map<String, Object> map);

}
