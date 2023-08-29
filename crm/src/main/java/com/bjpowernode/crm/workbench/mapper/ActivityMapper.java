package com.bjpowernode.crm.workbench.mapper;

import com.bjpowernode.crm.workbench.domain.Activity;

import java.util.List;
import java.util.Map;

public interface ActivityMapper {
    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table tbl_activity
     *
     * @mbggenerated Thu Jun 01 10:12:04 CST 2023
     */
    int deleteByPrimaryKey(String id);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table tbl_activity
     *
     * @mbggenerated Thu Jun 01 10:12:04 CST 2023
     */
    int insert(Activity record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table tbl_activity
     *
     * @mbggenerated Thu Jun 01 10:12:04 CST 2023
     */
    int insertSelective(Activity record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table tbl_activity
     *
     * @mbggenerated Thu Jun 01 10:12:04 CST 2023
     */
    Activity selectByPrimaryKey(String id);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table tbl_activity
     *
     * @mbggenerated Thu Jun 01 10:12:04 CST 2023
     */
    int updateByPrimaryKeySelective(Activity record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table tbl_activity
     *
     * @mbggenerated Thu Jun 01 10:12:04 CST 2023
     */
    int updateByPrimaryKey(Activity record);


    //添加市场活动
    int insertActivity(Activity activity);

    /**
     * 根据条件分页查询活动市场列表
     *
     * @param map
     * @return
     */
    List<Activity> selectActivityByConditionForPage(Map<String, Object> map);

    /**
     * 根据条件查询市场活动的总条数
     *
     * @param map
     * @return
     */
    int selectCountOfActivityByCondition(Map<String, Object> map);

    //批量删除市场活动
    int deleteActivityByIds(String[] ids);

    //根据id查询活动，填写修改活动窗口
    Activity selectActivityById(String id);

    //修改市场活动信息
    int updateActivity(Activity activity);

    //查询所有的市场活动导出
    List<Activity> selectAllActivities();

    //查询选择的市场活动导出
    List<Activity> selectActivitiesByChoose(String[] ids);

    //批量保存市场活动
    int insertActivityByList(List<Activity> activityList);

    //根据id查询市场活动的信息
    Activity selectActivityForDetailByActivityId(String id);

    //根据clueid查询相关联的市场活动的
    List<Activity> selectActivityForDetailByClueId(String id);

    //根据名字，模糊查询活动展示关联,并且把已经关联过的市场活动排除
    List<Activity> selectActivityForDetailByNameClueId(Map<String, Object> map);

    //根据id查询活动信息显示
    List<Activity> selectActivityForDetailByIds(String[] ids);

    //根据名称和线索id模糊查询线索
    List<Activity> selectActivityForConvertByNameClueId(Map<String, Object> map);
}