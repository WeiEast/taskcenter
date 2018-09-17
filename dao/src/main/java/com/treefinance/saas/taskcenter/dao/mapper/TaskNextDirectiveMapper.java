package com.treefinance.saas.taskcenter.dao.mapper;

import com.treefinance.saas.taskcenter.dao.entity.TaskNextDirective;
import com.treefinance.saas.taskcenter.dao.entity.TaskNextDirectiveCriteria;
import java.util.List;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.session.RowBounds;

public interface TaskNextDirectiveMapper {
    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table task_next_directive
     *
     * @mbg.generated Mon Sep 17 17:19:56 CST 2018
     */
    long countByExample(TaskNextDirectiveCriteria example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table task_next_directive
     *
     * @mbg.generated Mon Sep 17 17:19:56 CST 2018
     */
    int deleteByExample(TaskNextDirectiveCriteria example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table task_next_directive
     *
     * @mbg.generated Mon Sep 17 17:19:56 CST 2018
     */
    int deleteByPrimaryKey(Long id);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table task_next_directive
     *
     * @mbg.generated Mon Sep 17 17:19:56 CST 2018
     */
    int insert(TaskNextDirective record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table task_next_directive
     *
     * @mbg.generated Mon Sep 17 17:19:56 CST 2018
     */
    int insertSelective(TaskNextDirective record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table task_next_directive
     *
     * @mbg.generated Mon Sep 17 17:19:56 CST 2018
     */
    List<TaskNextDirective> selectByExampleWithRowbounds(TaskNextDirectiveCriteria example, RowBounds rowBounds);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table task_next_directive
     *
     * @mbg.generated Mon Sep 17 17:19:56 CST 2018
     */
    List<TaskNextDirective> selectByExample(TaskNextDirectiveCriteria example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table task_next_directive
     *
     * @mbg.generated Mon Sep 17 17:19:56 CST 2018
     */
    TaskNextDirective selectByPrimaryKey(Long id);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table task_next_directive
     *
     * @mbg.generated Mon Sep 17 17:19:56 CST 2018
     */
    int updateByExampleSelective(@Param("record") TaskNextDirective record, @Param("example") TaskNextDirectiveCriteria example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table task_next_directive
     *
     * @mbg.generated Mon Sep 17 17:19:56 CST 2018
     */
    int updateByExample(@Param("record") TaskNextDirective record, @Param("example") TaskNextDirectiveCriteria example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table task_next_directive
     *
     * @mbg.generated Mon Sep 17 17:19:56 CST 2018
     */
    int updateByPrimaryKeySelective(TaskNextDirective record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table task_next_directive
     *
     * @mbg.generated Mon Sep 17 17:19:56 CST 2018
     */
    int updateByPrimaryKey(TaskNextDirective record);
}