package com.treefinance.saas.taskcenter.dao.mapper;

import com.treefinance.saas.taskcenter.dao.entity.TaskLog;
import com.treefinance.saas.taskcenter.dao.entity.TaskLogCriteria;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.session.RowBounds;

import java.util.List;

public interface TaskLogMapper {
    /**
     * This method was generated by MyBatis Generator. This method corresponds to the database table task_log
     *
     * @mbg.generated Tue Sep 18 11:27:31 CST 2018
     */
    long countByExample(TaskLogCriteria example);

    /**
     * This method was generated by MyBatis Generator. This method corresponds to the database table task_log
     *
     * @mbg.generated Tue Sep 18 11:27:31 CST 2018
     */
    int deleteByExample(TaskLogCriteria example);

    /**
     * This method was generated by MyBatis Generator. This method corresponds to the database table task_log
     *
     * @mbg.generated Tue Sep 18 11:27:31 CST 2018
     */
    int deleteByPrimaryKey(Long id);

    /**
     * This method was generated by MyBatis Generator. This method corresponds to the database table task_log
     *
     * @mbg.generated Tue Sep 18 11:27:31 CST 2018
     */
    int insert(TaskLog record);

    /**
     * This method was generated by MyBatis Generator. This method corresponds to the database table task_log
     *
     * @mbg.generated Tue Sep 18 11:27:31 CST 2018
     */
    int insertSelective(TaskLog record);

    /**
     * This method was generated by MyBatis Generator. This method corresponds to the database table task_log
     *
     * @mbg.generated Tue Sep 18 11:27:31 CST 2018
     */
    List<TaskLog> selectByExampleWithRowbounds(TaskLogCriteria example, RowBounds rowBounds);

    /**
     * This method was generated by MyBatis Generator. This method corresponds to the database table task_log
     *
     * @mbg.generated Tue Sep 18 11:27:31 CST 2018
     */
    List<TaskLog> selectByExample(TaskLogCriteria example);

    /**
     * This method was generated by MyBatis Generator. This method corresponds to the database table task_log
     *
     * @mbg.generated Tue Sep 18 11:27:31 CST 2018
     */
    TaskLog selectByPrimaryKey(Long id);

    /**
     * This method was generated by MyBatis Generator. This method corresponds to the database table task_log
     *
     * @mbg.generated Tue Sep 18 11:27:31 CST 2018
     */
    int updateByExampleSelective(@Param("record") TaskLog record, @Param("example") TaskLogCriteria example);

    /**
     * This method was generated by MyBatis Generator. This method corresponds to the database table task_log
     *
     * @mbg.generated Tue Sep 18 11:27:31 CST 2018
     */
    int updateByExample(@Param("record") TaskLog record, @Param("example") TaskLogCriteria example);

    /**
     * This method was generated by MyBatis Generator. This method corresponds to the database table task_log
     *
     * @mbg.generated Tue Sep 18 11:27:31 CST 2018
     */
    int updateByPrimaryKeySelective(TaskLog record);

    /**
     * This method was generated by MyBatis Generator. This method corresponds to the database table task_log
     *
     * @mbg.generated Tue Sep 18 11:27:31 CST 2018
     */
    int updateByPrimaryKey(TaskLog record);
}