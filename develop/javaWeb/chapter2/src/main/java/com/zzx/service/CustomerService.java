package com.zzx.service;

import com.zzx.helper.DatabaseHelper;
import com.zzx.model.Customer;
import com.zzx.util.PropsUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Properties;

public class CustomerService {
    private static final Logger LOGGER = LoggerFactory.getLogger(CustomerService.class);

    /* 获取客户列表 */
    public List<Customer> getCustomerList() {
        String sql = "SELECT * FROM customer";
        return DatabaseHelper.queryEntityList(Customer.class, sql);
        /* *(优化一)Connection conn = DatabaseHelper.getConnection(); */
        /* *(优化一)try { */
            /*List<Customer> customerList = new ArrayList<Customer>();
            String sql = "SELECT * FROM customer";
            conn = DatabaseHelper.getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql);
            ResultSet rs =  stmt.executeQuery();
            while (rs.next()) {
                Customer customer = new Customer();
                customer.setId(rs.getLong("id"));
                customer.setName(rs.getString("name"));
                customer.setContact(rs.getString("contact"));
                customer.setTelephone(rs.getString("telephone"));
                customer.setEmail(rs.getString("email"));
                customer.setRemark(rs.getString("remark"));
                customerList.add(customer);
            }
            return customerList;*/
            /* *(优化一)String sql = "SELECT * FROM customer";
            return DatabaseHelper.queryEntityList(Customer.class, conn, sql);*/
        /* *(优化一)} finally {
            DatabaseHelper.closeConnection(conn);
        }*/
    }
    /* 获取单个客户 */
    public Customer getCustomer(long id) {
        String sql = "SELECT * FROM customer WHERE id = " + id + ";";
        return DatabaseHelper.queryEntity(Customer.class, sql);
    }
    /* 创建客户 */
    public boolean createCustomer(Map<String, Object> fieldMap) {
        return DatabaseHelper.insertEntity(Customer.class, fieldMap);
    }
    /* 更新客户 */
    public boolean updateCustomer(long id, Map<String, Object> fieldMap) {
        return DatabaseHelper.updateEntity(Customer.class, id, fieldMap);
    }
    /* 删除客户 */
    public boolean deleteCustomer(long id) {
        return DatabaseHelper.deleteEntity(Customer.class, id);
    }
}
