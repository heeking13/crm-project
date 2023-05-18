package com.bjpowernode.crm.settings.mapper;

import com.bjpowernode.crm.settings.domain.User;

import java.util.Map;

public interface UserMapper {
    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table tbl_user
     *
     * @mbggenerated Thu May 18 09:38:10 CST 2023
     */
    int deleteByPrimaryKey(String id);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table tbl_user
     *
     * @mbggenerated Thu May 18 09:38:10 CST 2023
     */
    int insert(User record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table tbl_user
     *
     * @mbggenerated Thu May 18 09:38:10 CST 2023
     */
    int insertSelective(User record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table tbl_user
     *
     * @mbggenerated Thu May 18 09:38:10 CST 2023
     */
    User selectByPrimaryKey(String id);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table tbl_user
     *
     * @mbggenerated Thu May 18 09:38:10 CST 2023
     */
    int updateByPrimaryKeySelective(User record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table tbl_user
     *
     * @mbggenerated Thu May 18 09:38:10 CST 2023
     */
    int updateByPrimaryKey(User record);


    /**
     * 根据账号和密码来查询用户
     * @param map
     * @return
     */
    User selectUserByLoginActAndPwd(Map<String,Object> map);
}