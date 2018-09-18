package com.treefinance.saas.taskcenter.dao.mapper;

import com.treefinance.saas.taskcenter.dao.entity.Task;
import com.treefinance.saas.taskcenter.dao.entity.TaskCriteria;
import java.util.List;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.session.RowBounds;

public interface TaskMapper {
    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table task
     *
     * @mbg.generated Tue Sep 18 11:27:31 CST 2018
     */
    long countByExample(TaskCriteria example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table task
     *
     * @mbg.generated Tue Sep 18 11:27:31 CST 2018
     */
    int deleteByExample(TaskCriteria example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table task
     *
     * @mbg.generated Tue Sep 18 11:27:31 CST 2018
     */
    int deleteByPrimaryKey(Long id);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table task
     *
     * @mbg.generated Tue Sep 18 11:27:31 CST 2018
     */
    int insert(Task record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table task
     *
     * @mbg.generated Tue Sep 18 11:27:31 CST 2018
     */
    int insertSelective(Task record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table task
     *
     * @mbg.generated Tue Sep 18 11:27:31 CST 2018
     */
    List<Task> selectByExampleWithRowbounds(TaskCriteria example, RowBounds rowBounds);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table task
     *
     * @mbg.generated Tue Sep 18 11:27:31 CST 2018
     */
    List<Task> selectByExample(TaskCriteria example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table task
     *
     * @mbg.generated Tue Sep 18 11:27:31 CST 2018
     */
    List<Task> selectPaginationByExample(TaskCriteria example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table task
     *
     * @mbg.generated Tue Sep 18 11:27:31 CST 2018
     */
    void batchInsert(List<Task> list);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table task
     *
     * @mbg.generated Tue Sep 18 11:27:31 CST 2018
     */
    void batchUpdateByPrimaryKey(List<Task> list);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table task
     *
     * @mbg.generated Tue Sep 18 11:27:31 CST 2018
     */
    void batchUpdateByPrimaryKeySelective(List<Task> list);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table task
     *
     * @mbg.generated Tue Sep 18 11:27:31 CST 2018
     */
    Task selectByPrimaryKey(Long id);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table task
     *
     * @mbg.generated Tue Sep 18 11:27:31 CST 2018
     */
    int updateByExampleSelective(@Param("record") Task record, @Param("example") TaskCriteria example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table task
     *
     * @mbg.generated Tue Sep 18 11:27:31 CST 2018
     */
    int updateByExample(@Param("record") Task record, @Param("example") TaskCriteria example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table task
     *
     * @mbg.generated Tue Sep 18 11:27:31 CST 2018
     */
    int updateByPrimaryKeySelective(Task record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table task
     *
     * @mbg.generated Tue Sep 18 11:27:31 CST 2018
     */
    int updateByPrimaryKey(Task record);
}