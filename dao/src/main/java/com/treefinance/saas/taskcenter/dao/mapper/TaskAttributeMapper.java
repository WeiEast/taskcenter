package com.treefinance.saas.taskcenter.dao.mapper;

import com.treefinance.saas.taskcenter.dao.entity.TaskAttribute;
import com.treefinance.saas.taskcenter.dao.entity.TaskAttributeCriteria;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.session.RowBounds;

import java.util.List;

public interface TaskAttributeMapper {
    /**
     * This method was generated by MyBatis Generator. This method corresponds to the database table task_attribute
     *
     * @mbg.generated Tue Sep 18 11:27:31 CST 2018
     */
    long countByExample(TaskAttributeCriteria example);

    /**
     * This method was generated by MyBatis Generator. This method corresponds to the database table task_attribute
     *
     * @mbg.generated Tue Sep 18 11:27:31 CST 2018
     */
    int deleteByExample(TaskAttributeCriteria example);

    /**
     * This method was generated by MyBatis Generator. This method corresponds to the database table task_attribute
     *
     * @mbg.generated Tue Sep 18 11:27:31 CST 2018
     */
    int deleteByPrimaryKey(Long id);

    /**
     * This method was generated by MyBatis Generator. This method corresponds to the database table task_attribute
     *
     * @mbg.generated Tue Sep 18 11:27:31 CST 2018
     */
    int insert(TaskAttribute record);

    /**
     * This method was generated by MyBatis Generator. This method corresponds to the database table task_attribute
     *
     * @mbg.generated Tue Sep 18 11:27:31 CST 2018
     */
    int insertSelective(TaskAttribute record);

    /**
     * This method was generated by MyBatis Generator. This method corresponds to the database table task_attribute
     *
     * @mbg.generated Tue Sep 18 11:27:31 CST 2018
     */
    List<TaskAttribute> selectByExampleWithRowbounds(TaskAttributeCriteria example, RowBounds rowBounds);

    /**
     * This method was generated by MyBatis Generator. This method corresponds to the database table task_attribute
     *
     * @mbg.generated Tue Sep 18 11:27:31 CST 2018
     */
    List<TaskAttribute> selectByExample(TaskAttributeCriteria example);

    /**
     * This method was generated by MyBatis Generator. This method corresponds to the database table task_attribute
     *
     * @mbg.generated Tue Sep 18 11:27:31 CST 2018
     */
    TaskAttribute selectByPrimaryKey(Long id);

    /**
     * This method was generated by MyBatis Generator. This method corresponds to the database table task_attribute
     *
     * @mbg.generated Tue Sep 18 11:27:31 CST 2018
     */
    int updateByExampleSelective(@Param("record") TaskAttribute record, @Param("example") TaskAttributeCriteria example);

    /**
     * This method was generated by MyBatis Generator. This method corresponds to the database table task_attribute
     *
     * @mbg.generated Tue Sep 18 11:27:31 CST 2018
     */
    int updateByExample(@Param("record") TaskAttribute record, @Param("example") TaskAttributeCriteria example);

    /**
     * This method was generated by MyBatis Generator. This method corresponds to the database table task_attribute
     *
     * @mbg.generated Tue Sep 18 11:27:31 CST 2018
     */
    int updateByPrimaryKeySelective(TaskAttribute record);

    /**
     * This method was generated by MyBatis Generator. This method corresponds to the database table task_attribute
     *
     * @mbg.generated Tue Sep 18 11:27:31 CST 2018
     */
    int updateByPrimaryKey(TaskAttribute record);
}