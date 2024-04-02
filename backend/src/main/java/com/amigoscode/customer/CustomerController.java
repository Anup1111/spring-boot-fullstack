package com.amigoscode.customer;

import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("api/v1/customers")
public class CustomerController {

   private final CustomerService customerService;

   public CustomerController(CustomerService customerService) {
      this.customerService = customerService;
   }

   @GetMapping
   public List<Customer> getCustomers() {
      return customerService.getAllCustomers();
   }

   @GetMapping("{id}")
   public Customer getCustomer(@PathVariable("id") Integer id) {
      return customerService.getCustomer(id);
   }

   @PostMapping
   public void registerCustomer(@RequestBody CustomerRegistrationRequest customerRegistrationRequest) {
      customerService.addCustomer(customerRegistrationRequest);
   }

   @DeleteMapping("{id}")
   public void deleteCustomer(@PathVariable("id") Integer id) {
      customerService.deleteCustomer(id);
   }

   @PutMapping("{id}")
   public void updateCustomer(@PathVariable("id") Integer id,
                              @RequestBody CustomerUpdateRequest customerUpdateRequest) {
      customerService.updateCustomer(id, customerUpdateRequest);
   }
}
