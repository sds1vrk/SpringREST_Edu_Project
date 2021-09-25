package org.prms.kdt.customer.controller;

import org.prms.kdt.customer.service.CustomerService;
import org.prms.kdt.customer.model.Customer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Controller
public class CustomerController {

    private final CustomerService customerService;

    public CustomerController(CustomerService customerService) {
        this.customerService = customerService;
    }

    private static final Logger logger= LoggerFactory.getLogger(CustomerController.class);


    @GetMapping("/api/v1/customers")
    @ResponseBody
    public List<Customer> findCustomers() {
        return customerService.getAllCustomers();
    }

    @GetMapping("/api/v1/customers/{customerId}")
    @ResponseBody
    @CrossOrigin(origins ="*")
    public ResponseEntity<Customer> findCustomer(@PathVariable("customerId")UUID customerId) {
        var customer=customerService.getCustomer(customerId);
        // customer가 있으면 Map을 통해 페이지 반환 없으면 404 NOT FOUND
        return customer.map(v->ResponseEntity.ok(v)).orElse(ResponseEntity.notFound().build());
    }


    @PostMapping("/api/v1/customers/{customerId}")
    @ResponseBody
    public CustomerDto findCustomer(@PathVariable("customerId")UUID customerId, @RequestBody CustomerDto customer) {
        logger.info("GOT customer save Request {}",customer);
        return customer;
    }



    //    @RequestMapping(value = "/customers",method = RequestMethod.GET)
    @GetMapping("/customers")
    public String viewCustomersPage(Model model) {
        var allCustomers=customerService.getAllCustomers();

        model.addAttribute("serverTime",LocalDateTime.now());
        model.addAttribute("customers",allCustomers);
//        return "views/customers";
        return "views/customers";
    }


    @GetMapping("/customers/new")
    public String viewStringNewCustomerPage() {
        return "views/new-customers";
    }

    @PostMapping("/customers/new")
    public String addNewCustomer(CreateCustomerRequest createCustomerRequest) {
        // Customer의 생성을 Service에서 하게끔 전달한다. 이때 넘기는 값은 DTO로 전달
        customerService.createCustomer(createCustomerRequest.email(),createCustomerRequest.name());
        return "redirect:/customers";
    }


    // 상세 페이지 customer ID로 상세 페이지 조회
    // TODO : CustomerID로 조회하기
    @GetMapping("/customers/{customerId}")
//    @GetMapping("/{customerId}")
    public String findCustomer(@PathVariable("customerId") UUID customerId, Model model) { // @PathVariable : customerID -> UUID로 변경
        var maybeCustomer=customerService.getCustomer(customerId);

        System.err.println("customerID:"+customerId);

        System.err.println("customerGET:"+maybeCustomer.get());

        if (maybeCustomer.isPresent()) {
//            model.addAttribute("customer",maybeCustomer.get());
            model.addAttribute("customer",maybeCustomer.get());
            return "views/customer-details";
        }
        else {
            return "views/404";
        }

    }

}
