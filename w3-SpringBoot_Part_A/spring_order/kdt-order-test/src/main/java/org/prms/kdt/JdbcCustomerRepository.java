package org.prms.kdt;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class JdbcCustomerRepository {

    private static final Logger logger= LoggerFactory.getLogger(JdbcCustomerRepository.class);

    public List<String> findNames(String name) {
        var SELECT_SQL= "select * from customers WHERE name=?";
        List<String>names=new ArrayList<>();

        try (
                // 원래 ID 패스워드는 들어가면 안됨 -> 설정 값으로 다 빼줘야 됨
                var connection=DriverManager.getConnection("jdbc:mysql://localhost/order_mgmt","root","root1234!");
                var statement=connection.prepareStatement(SELECT_SQL);

        ){
            statement.setString(1,name);
            try( var resultSet=statement.executeQuery()) {
                while (resultSet.next()) {
                    var customerName=resultSet.getString("name");
                    var customerID= UUID.nameUUIDFromBytes(resultSet.getBytes("customer_id")); //UUID는 bytes로 가져온다.
                    var createdAt=resultSet.getTimestamp("created_at").toLocalDateTime();
                    logger.info("customers name->{}, id->{}, createdAt->{}",customerName,customerID,createdAt);
                    names.add(customerName);
                }
            }
        }
        catch (SQLException sqlException) {
            sqlException.printStackTrace();
            logger.error("GOT error while createStatement connection",sqlException);
        }
        return names;
    }

    public static void main(String[] args) {
        var names=new JdbcCustomerRepository().findNames("tester01' OR 'a'='a"); //sql Injection
        names.forEach(v->logger.info("Found name:{}",v));

    }
}
