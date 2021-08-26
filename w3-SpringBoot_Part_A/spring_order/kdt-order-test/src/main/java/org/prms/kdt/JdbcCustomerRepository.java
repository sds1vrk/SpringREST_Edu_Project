package org.prms.kdt;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;
import java.util.UUID;

public class JdbcCustomerRepository {

    private static final Logger logger= LoggerFactory.getLogger(JdbcCustomerRepository.class);
    public static void main(String[] args) {
        // 원래 ID 패스워드는 들어가면 안됨 -> 설정 값으로 다 빼줘야 됨
        Connection connection=null;
        Statement statement=null;
        ResultSet resultSet=null;
        try {
            connection=DriverManager.getConnection("jdbc:mysql://localhost/order_mgmt","root","root1234!");
            statement=connection.createStatement();
            resultSet=statement.executeQuery("select * from customers");

            while (resultSet.next()) {
                var name=resultSet.getString("name");
                var customerID= UUID.nameUUIDFromBytes(resultSet.getBytes("customer_id")); //UUID는 bytes로 가져온다.
                logger.info("customers name->{}, id->{}",name,customerID);

            }

        } catch (SQLException sqlException) {
            sqlException.printStackTrace();
            logger.error("GOT error while createStatement connection",sqlException);
        }
        finally {
            // 반드시 닫아줘야 함.
            try {
                if (connection!=null) connection.close();
                if (statement!=null) statement.close();
                if (resultSet!=null) resultSet.close();
            }catch (SQLException sqlException) {
                logger.error("GOT error while closing connection",sqlException);
            }
        }

    }
}
