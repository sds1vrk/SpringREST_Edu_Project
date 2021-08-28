package org.prms.kdt.customer;

import org.prms.kdt.JdbcCustomerRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.nio.ByteBuffer;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public class CustomerJdbcRepository implements CustomerRepository {


    private static final Logger logger= LoggerFactory.getLogger(CustomerJdbcRepository.class);

    private final DataSource dataSource;

    public CustomerJdbcRepository(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    public Customer insert(Customer customer) {
        try (
                // 원래 ID 패스워드는 들어가면 안됨 -> 설정 값으로 다 빼줘야 됨
                var connection=dataSource.getConnection();
                var statement=connection.prepareStatement("INSERT INTO customers(customer_id,name,email,created_at) VALUES (UUID_TO_BIN(?),?,?,?)");

        ){
            statement.setBytes(1,customer.getCustomerId().toString().getBytes());
            statement.setString(2,customer.getName());
            statement.setString(3,customer.getEmail());
            statement.setTimestamp(4, Timestamp.valueOf(customer.getCreatedAt()));
            var executeUpdate=statement.executeUpdate();

            if (executeUpdate!=1) {
                throw new RuntimeException("Noting was inserted");
            }

            return customer;

        }
        catch (SQLException sqlException) {
            logger.error("GOT error while createStatement connection",sqlException);
            throw new RuntimeException(sqlException);
        }

    }

    @Override
    public Customer update(Customer customer) {
        return null;
    }

    @Override
    public List<Customer> findAll() {
        List<Customer>allCustomers=new ArrayList<>();

        try (
                // 원래 ID 패스워드는 들어가면 안됨 -> 설정 값으로 다 빼줘야 됨
                var connection= dataSource.getConnection();
                var statement=connection.prepareStatement("select * from customers");
                var resultSet=statement.executeQuery();

        ){
            while (resultSet.next()) {
                mapToCustomer(allCustomers, resultSet);
            }

        }
        catch (SQLException sqlException) {
            sqlException.printStackTrace();
            logger.error("GOT error while createStatement connection",sqlException);
            throw new RuntimeException(sqlException);
        }
        return allCustomers;
    }

    private void mapToCustomer(List<Customer> allCustomers, ResultSet resultSet) throws SQLException {
        var customerName= resultSet.getString("name");
        var email= resultSet.getString("email");

        var lastLoginAt= resultSet.getTimestamp("last_login_at")!=null ?
                resultSet.getTimestamp("last_login_at").toLocalDateTime():null;
        var createdAt= resultSet.getTimestamp("created_at").toLocalDateTime();
        var customerID= toUUID(resultSet.getBytes("customer_id")); //UUID는 bytes로 가져온다.
        allCustomers.add(new Customer(customerID,customerName,email,lastLoginAt,createdAt));
    }

    @Override
    public Optional<Customer> findById(UUID customerId) {
        List<Customer>allCustomers=new ArrayList<>();

        try (
                // 원래 ID 패스워드는 들어가면 안됨 -> 설정 값으로 다 빼줘야 됨
                var connection=dataSource.getConnection();
                var statement=connection.prepareStatement("select * from customers WHERE customer_id=UUID_TO_BIN(?)");

        ){
            statement.setBytes(1,customerId.toString().getBytes());
            logger.info("statement-{}",statement);
            try( var resultSet=statement.executeQuery()) {
                while (resultSet.next()) {
                    mapToCustomer(allCustomers, resultSet);
                }
            }
        }
        catch (SQLException sqlException) {
            logger.error("GOT error while createStatement connection",sqlException);
            throw new RuntimeException(sqlException);
        }
        return allCustomers.stream().findFirst();
    }

    @Override
    public Optional<Customer> findByName(String name) {
        List<Customer>allCustomers=new ArrayList<>();

        try (
                // 원래 ID 패스워드는 들어가면 안됨 -> 설정 값으로 다 빼줘야 됨
                var connection=dataSource.getConnection();
                var statement=connection.prepareStatement("select * from customers WHERE name=?");

        ){
            statement.setString(1,name);
            logger.info("statement-{}",statement);
            try( var resultSet=statement.executeQuery()) {
                while (resultSet.next()) {
                    mapToCustomer(allCustomers, resultSet);
                }
            }
        }
        catch (SQLException sqlException) {
            logger.error("GOT error while createStatement connection",sqlException);
            throw new RuntimeException(sqlException);
        }
        return allCustomers.stream().findFirst();
    }

    @Override
    public Optional<Customer> findByEmail(String email) {
        List<Customer>allCustomers=new ArrayList<>();

        try (
                // 원래 ID 패스워드는 들어가면 안됨 -> 설정 값으로 다 빼줘야 됨
                var connection=dataSource.getConnection();
                var statement=connection.prepareStatement("select * from customers WHERE email=?");

        ){
            statement.setString(1,email);
            logger.info("statement-{}",statement);
            try( var resultSet=statement.executeQuery()) {
                while (resultSet.next()) {
                    mapToCustomer(allCustomers, resultSet);
                }
            }
        }
        catch (SQLException sqlException) {
            logger.error("GOT error while createStatement connection",sqlException);
            throw new RuntimeException(sqlException);
        }
        return allCustomers.stream().findFirst();
    }

    @Override
    public void deleteAll() {
        try (
                // 원래 ID 패스워드는 들어가면 안됨 -> 설정 값으로 다 빼줘야 됨
                var connection=dataSource.getConnection();
                var statement=connection.prepareStatement("DELETE FROM customers");
        ){
           statement.executeUpdate();
        }
        catch (SQLException sqlException) {
            sqlException.printStackTrace();
            logger.error("GOT error while createStatement connection",sqlException);
        }

    }


    static UUID toUUID(byte[] bytes) {
        var byteBuffer= ByteBuffer.wrap(bytes);
        return new UUID(byteBuffer.getLong(),byteBuffer.getLong());
    }

}
