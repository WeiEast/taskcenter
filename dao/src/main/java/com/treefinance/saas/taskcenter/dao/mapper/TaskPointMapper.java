package com.treefinance.saas.taskcenter.dao.mapper;

import com.treefinance.saas.taskcenter.dao.entity.TaskPoint;
import com.treefinance.saas.taskcenter.dao.entity.TaskPointCriteria;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.session.RowBounds;

import java.util.List;

public interface TaskPointMapper {
    /**
     * This method was generated by MyBatis Generator. This method corresponds to the database table task_point
     *
     * @mbg.generated Tue Feb 26 14:18:47 CST 2019
     */
    long countByExample(TaskPointCriteria example);

    /**
     * This method was generated by MyBatis Generator. This method corresponds to the database table task_point
     *
     * @mbg.generated Tue Feb 26 14:18:47 CST 2019
     */
    int deleteByExample(TaskPointCriteria example);

    /**
     * This method was generated by MyBatis Generator. This method corresponds to the database table task_point
     *
     * @mbg.generated Tue Feb 26 14:18:47 CST 2019
     */
    int deleteByPrimaryKey(Long id);

    /**
     * This method was generated by MyBatis Generator. This method corresponds to the database table task_point
     *
     * @mbg.generated Tue Feb 26 14:18:47 CST 2019
     */
    int insert(TaskPoint record);

    /**
     * This method was generated by MyBatis Generator. This method corresponds to the database table task_point
     *
     * @mbg.generated Tue Feb 26 14:18:47 CST 2019
     */
    int insertSelective(TaskPoint record);

    /**
     * This method was generated by MyBatis Generator. This method corresponds to the database table task_point
     *
     * @mbg.generated Tue Feb 26 14:18:47 CST 2019
     */
    List<TaskPoint> selectByExampleWithRowbounds(TaskPointCriteria example, RowBounds rowBounds);

    /**
     * This method was generated by MyBatis Generator. This method corresponds to the database table task_point
     *
     * @mbg.generated Tue Feb 26 14:18:47 CST 2019
     */
    List<TaskPoint> selectByExample(TaskPointCriteria example);

    /**
     * This method was generated by MyBatis Generator. This method corresponds to the database table task_point
     *
     * @mbg.generated Tue Feb 26 14:18:47 CST 2019
     */
    TaskPoint selectByPrimaryKey(Long id);

    /**
     * This method was generated by MyBatis Generator. This method corresponds to the database table task_point
     *
     * @mbg.generated Tue Feb 26 14:18:47 CST 2019
     */
    int updateByExampleSelective(@Param("record") TaskPoint record, @Param("example") TaskPointCriteria example);

    /**
     * This method was generated by MyBatis Generator. This method corresponds to the database table task_point
     *
     * @mbg.generated Tue Feb 26 14:18:47 CST 2019
     */
    int updateByExample(@Param("record") TaskPoint record, @Param("example") TaskPointCriteria example);

    /**
     * This method was generated by MyBatis Generator. This method corresponds to the database table task_point
     *
     * @mbg.generated Tue Feb 26 14:18:47 CST 2019
     */
    int updateByPrimaryKeySelective(TaskPoint record);

    /**
     * This method was generated by MyBatis Generator. This method corresponds to the database table task_point
     *
     * @mbg.generated Tue Feb 26 14:18:47 CST 2019
     */
    int updateByPrimaryKey(TaskPoint record);
}