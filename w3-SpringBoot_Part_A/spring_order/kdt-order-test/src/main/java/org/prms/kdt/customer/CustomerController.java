package org.prms.kdt.customer;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Controller
public class CustomerController {

    private final CustomerService customerService;

    public CustomerController(CustomerService customerService) {
        this.customerService = customerService;
    }


    @GetMapping("/api/v1/customers")
    @ResponseBody
    public List<Customer> findCustomers() {
        return customerService.getAllCustomers();

    }


//    @RequestMapping(value = "/customers",method = RequestMethod.GET)
    @GetMapping("/customers")
    public String viewCustomersPage(Model model) {
        var allCustomers=customerService.getAllCustomers();

        model.addAttribute("serverTime",LocalDateTime.now());
        model.addAttribute("customers",allCustomers);
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
    public String findCustomer(@PathVariable("customerId") UUID customerId, Model model) { // @PathVariable : customerID -> UUID로 변경
        var maybeCustomer=customerService.getCustomer(customerId);

        if (maybeCustomer.isPresent()) {
            model.addAttribute("customer",maybeCustomer.get());
            return "views/customer-details";
        }
        else {
            return "views/404";
        }

    }

}
