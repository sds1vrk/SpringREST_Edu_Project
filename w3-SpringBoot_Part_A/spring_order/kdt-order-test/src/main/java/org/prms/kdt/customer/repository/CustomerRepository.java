package org.prms.kdt.customer.repository;

import org.prms.kdt.customer.model.Customer;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface CustomerRepository {

    Customer insert(Customer customer);
    Customer update(Customer customer);

    int count();

    List<Customer> findAll();

    // 찾을려는게 없으면 Null처리를 위한 Optional을 사용
    Optional<Customer> findById(UUID customerId);
    Optional<Customer> findByName(String name);
    Optional<Customer> findByEmail(String email);


    void deleteAll();




}
