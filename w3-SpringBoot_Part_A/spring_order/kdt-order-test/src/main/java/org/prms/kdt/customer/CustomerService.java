package org.prms.kdt.customer;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface CustomerService {
    void createCustomers(List<Customer> customers);

    List<Customer> getAllCustomers();

    Customer createCustomer(String email, String name);


    // Customer가 없을수도 있으므로 Optional로 처리
    Optional<Customer>getCustomer(UUID customerId);


}
