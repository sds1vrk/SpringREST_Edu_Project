package org.prms.kdt;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.ByteBuffer;
import java.sql.*;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class JdbcCustomerRepository {

    private static final Logger logger= LoggerFactory.getLogger(JdbcCustomerRepository.class);
    private final String SELECT_BY_NAME_SQL= "select * from customers WHERE name=?";
    private final String SELECT_ALL_SQL= "select * from customers";
    private final String INSERT_SQL= "INSERT INTO customers(customer_id,name,email) VALUES (UUID_TO_BIN(?),?,?)";
    private final String DELETE_ALL_SQL= "DELETE FROM customers";
    private final String UPDATE_BY_ID_SQL="UPDATE customers SET name=? WHERE customer_id=UUID_TO_BIN(?)";

    public List<String> findNames(String name) {
        List<String>names=new ArrayList<>();

        try (
                // 원래 ID 패스워드는 들어가면 안됨 -> 설정 값으로 다 빼줘야 됨
                var connection=DriverManager.getConnection("jdbc:mysql://localhost/order_mgmt","root","root1234!");
                var statement=connection.prepareStatement(SELECT_BY_NAME_SQL);

        ){
            statement.setString(1,name);
            logger.info("statement-{}",statement);
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
    public List<String> findAllNames() {
        List<String>names=new ArrayList<>();

        try (
                // 원래 ID 패스워드는 들어가면 안됨 -> 설정 값으로 다 빼줘야 됨
                var connection=DriverManager.getConnection("jdbc:mysql://localhost/order_mgmt","root","root1234!");
                var statement=connection.prepareStatement(SELECT_ALL_SQL);
                var resultSet=statement.executeQuery();

        ){
                while (resultSet.next()) {
                    var customerName=resultSet.getString("name");
                    var customerID= UUID.nameUUIDFromBytes(resultSet.getBytes("customer_id")); //UUID는 bytes로 가져온다.
                    var createdAt=resultSet.getTimestamp("created_at").toLocalDateTime();
                    names.add(customerName);
                }

        }
        catch (SQLException sqlException) {
            sqlException.printStackTrace();
            logger.error("GOT error while createStatement connection",sqlException);
        }
        return names;
    }
    public List<UUID> findAllIds() {
        List<UUID>uuids=new ArrayList<>();

        try (
                // 원래 ID 패스워드는 들어가면 안됨 -> 설정 값으로 다 빼줘야 됨
                var connection=DriverManager.getConnection("jdbc:mysql://localhost/order_mgmt","root","root1234!");
                var statement=connection.prepareStatement(SELECT_ALL_SQL);
                var resultSet=statement.executeQuery();

        ){
            while (resultSet.next()) {
                var customerName=resultSet.getString("name");
                var customerID= toUUID(resultSet.getBytes("customer_id"));
                var createdAt=resultSet.getTimestamp("created_at").toLocalDateTime();
                uuids.add(customerID);
            }

        }
        catch (SQLException sqlException) {
            sqlException.printStackTrace();
            logger.error("GOT error while createStatement connection",sqlException);
        }
        return uuids;
    }

    public int insertCustomer(UUID customerId, String name, String email) {


        try (
                // 원래 ID 패스워드는 들어가면 안됨 -> 설정 값으로 다 빼줘야 됨
                var connection=DriverManager.getConnection("jdbc:mysql://localhost/order_mgmt","root","root1234!");
                var statement=connection.prepareStatement(INSERT_SQL);

        ){
            statement.setBytes(1,customerId.toString().getBytes());
            statement.setString(2,name);
            statement.setString(3,email);

            return statement.executeUpdate();

        }
        catch (SQLException sqlException) {
            sqlException.printStackTrace();
            logger.error("GOT error while createStatement connection",sqlException);
        }

        return 0;

    }
    public int deleteAllCustomers() {
        try (
                // 원래 ID 패스워드는 들어가면 안됨 -> 설정 값으로 다 빼줘야 됨
                var connection=DriverManager.getConnection("jdbc:mysql://localhost/order_mgmt","root","root1234!");
                var statement=connection.prepareStatement(DELETE_ALL_SQL);
        ){
            return statement.executeUpdate();
        }
        catch (SQLException sqlException) {
            sqlException.printStackTrace();
            logger.error("GOT error while createStatement connection",sqlException);
        }
        return 0;
    }
    public int updateCustomerName(UUID customerId, String name) {


        try (
                // 원래 ID 패스워드는 들어가면 안됨 -> 설정 값으로 다 빼줘야 됨
                var connection=DriverManager.getConnection("jdbc:mysql://localhost/order_mgmt","root","root1234!");
                var statement=connection.prepareStatement(UPDATE_BY_ID_SQL);
        ){
            statement.setString(1,name);
            statement.setBytes(2,customerId.toString().getBytes());
            return statement.executeUpdate();

        }
        catch (SQLException sqlException) {
            sqlException.printStackTrace();
            logger.error("GOT error while createStatement connection",sqlException);
        }

        return 0;

    }


    static UUID toUUID(byte[] bytes) {
        var byteBuffer=ByteBuffer.wrap(bytes);
        return new UUID(byteBuffer.getLong(),byteBuffer.getLong());
    }


    public static void main(String[] args) {
        var customerRepository=new JdbcCustomerRepository();
        var count=customerRepository.deleteAllCustomers();
        logger.info("deleted count->{}",count);


        var customerID=UUID.randomUUID();
        logger.info("created customerID -> {}",customerID);
        logger.info("created UUID version -> {}",customerID.version());

        customerRepository.insertCustomer(customerID,"new-user","new-user@gmail.com");
        customerRepository.findAllIds().forEach(v->logger.info("Found customerId:{} and version: {}",v,v.version()));

    }
}
