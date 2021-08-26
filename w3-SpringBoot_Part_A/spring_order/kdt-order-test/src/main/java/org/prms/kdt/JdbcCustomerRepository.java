package org.prms.kdt;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;
import java.util.UUID;

public class JdbcCustomerRepository {

    private static final Logger logger= LoggerFactory.getLogger(JdbcCustomerRepository.class);
    public static void main(String[] args) {
        // 원래 ID 패스워드는 들어가면 안됨 -> 설정 값으로 다 빼줘야 됨

        try (
            var connection=DriverManager.getConnection("jdbc:mysql://localhost/order_mgmt","root","root1234!");
            var statement=connection.createStatement();
            var resultSet=statement.executeQuery("select * from customers");
        )
            // Auto Close -> 블록이 끝났을때 알아서 클로스 
        {
            while (resultSet.next()) {
                var name=resultSet.getString("name");
                var customerID= UUID.nameUUIDFromBytes(resultSet.getBytes("customer_id")); //UUID는 bytes로 가져온다.
                logger.info("customers name->{}, id->{}",name,customerID);

            }
        }
        catch (SQLException sqlException) {
            sqlException.printStackTrace();
            logger.error("GOT error while createStatement connection",sqlException);
        }


    }
}
