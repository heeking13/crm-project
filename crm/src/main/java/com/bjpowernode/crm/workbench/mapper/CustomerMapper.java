package com.bjpowernode.crm.workbench.mapper;

import com.bjpowernode.crm.workbench.domain.Customer;

public interface CustomerMapper {
    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table tbl_customer
     *
     * @mbggenerated Wed Aug 30 14:03:20 CST 2023
     */
    int deleteByPrimaryKey(String id);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table tbl_customer
     *
     * @mbggenerated Wed Aug 30 14:03:20 CST 2023
     */
    int insert(Customer record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table tbl_customer
     *
     * @mbggenerated Wed Aug 30 14:03:20 CST 2023
     */
    int insertSelective(Customer record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table tbl_customer
     *
     * @mbggenerated Wed Aug 30 14:03:20 CST 2023
     */
    Customer selectByPrimaryKey(String id);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table tbl_customer
     *
     * @mbggenerated Wed Aug 30 14:03:20 CST 2023
     */
    int updateByPrimaryKeySelective(Customer record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table tbl_customer
     *
     * @mbggenerated Wed Aug 30 14:03:20 CST 2023
     */
    int updateByPrimaryKey(Customer record);

    //保存创建的客户
    int insertCustomer(Customer customer);
}