package com.amigoscode.customer;

import com.amigoscode.exception.DuplicateResource;
import com.amigoscode.exception.RequestValidationException;
import com.amigoscode.exception.ResourceNotFound;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CustomerService {

   private final CustomerDao customerDao;

   public CustomerService(@Qualifier("jdbc") CustomerDao customerDao) {
      this.customerDao = customerDao;
   }

   public List<Customer> getAllCustomers() {
      return customerDao.selectAllCustomers();
   }

   public Customer getCustomer(Integer id) {
      return customerDao.selectCustomerById(id).orElseThrow(() ->
            new ResourceNotFound("Customer with id [%s] not found".formatted(id)));
   }

   public void addCustomer(CustomerRegistrationRequest customerRegistrationRequest) {
      String email = customerRegistrationRequest.email();
      if (customerDao.existsPersonWithEmail(email)) {
         throw new DuplicateResource("Email already taken");
      } else {
         Customer customer = new Customer(
               customerRegistrationRequest.name(),
               customerRegistrationRequest.email(),
               customerRegistrationRequest.age());
         customerDao.insertCustomer(customer);
      }
   }

   public void deleteCustomer(Integer id) {
      if (!customerDao.existsPersonWithId(id)) {
         throw new ResourceNotFound("customer with id [%s] not found".formatted(id));
      }

      customerDao.deleteCustomerById(id);
   }

   public void updateCustomer(Integer id, CustomerUpdateRequest updateRequest) {
      Customer customer = getCustomer(id);

      boolean changes = false;

      if (updateRequest.name() != null && !updateRequest.name().equals(customer.getName())) {
         customer.setName(updateRequest.name());
         changes = true;
      }

      if (updateRequest.email() != null && !updateRequest.email().equals(customer.getEmail())) {
         if (customerDao.existsPersonWithEmail(updateRequest.email())) {
            throw new DuplicateResource("Email already taken");
         }
         customer.setEmail(updateRequest.email());
         changes = true;
      }

      if (updateRequest.age() != null && !updateRequest.age().equals(customer.getAge())) {
         customer.setAge(updateRequest.age());
         changes = true;
      }

      if (!changes) {
         throw new RequestValidationException("no data changes found");
      }

      customerDao.updateCustomer(customer);
   }
}
