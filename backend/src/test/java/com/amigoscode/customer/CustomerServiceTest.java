package com.amigoscode.customer;

import com.amigoscode.customer.exception.DuplicateResource;
import com.amigoscode.customer.exception.RequestValidationException;
import com.amigoscode.customer.exception.ResourceNotFound;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CustomerServiceTest {

   @Mock
   private CustomerDao customerDao;
   private CustomerService underTest;

   @BeforeEach
   void setUp() {
      underTest = new CustomerService(customerDao);
   }

   @Test
   void getAllCustomers() {
      //When
      underTest.getAllCustomers();

      //Then
      verify(customerDao).selectAllCustomers();
   }

   @Test
   void canGetCustomer() {
      //Given
      int id = 10;
      Customer customer = new Customer(id, "Alex", "alex@gmail.com", 19);

      when(customerDao.selectCustomerById(id)).thenReturn(Optional.of(customer));

      //When
      Customer actual = underTest.getCustomer(id);

      //Then
      assertThat(actual).isEqualTo(customer);
   }

   @Test
   void willThrowWhenGetCustomerReturnsEmptyOptional() {
      //Given
      int id = 10;

      when(customerDao.selectCustomerById(id)).thenReturn(Optional.empty());

      //When

      //Then
      assertThatThrownBy(() -> underTest.getCustomer(id))
            .isInstanceOf(ResourceNotFound.class)
            .hasMessage("Customer with id [%s] not found".formatted(id));
   }

   @Test
   void addCustomer() {
      //Given
      String email = "alex@gmail.com";
      when(customerDao.existsPersonWithEmail(email)).thenReturn(false);

      CustomerRegistrationRequest request = new CustomerRegistrationRequest(
            "Alex", email, 19
      );

      //When
      underTest.addCustomer(request);

      //Then
      ArgumentCaptor<Customer> customerArgumentCaptor = ArgumentCaptor.forClass(
            Customer.class
      );
      verify(customerDao).insertCustomer(customerArgumentCaptor.capture());

      Customer capturedCustomer = customerArgumentCaptor.getValue();

      assertThat(capturedCustomer.getId()).isNull();
      assertThat(capturedCustomer.getName()).isEqualTo(request.name());
      assertThat(capturedCustomer.getEmail()).isEqualTo(request.email());
      assertThat(capturedCustomer.getAge()).isEqualTo(request.age());
   }

   @Test
   void willThrowWhenEmailExistsWhileAddingCustomer() {
      //Given
      String email = "alex@gmail.com";
      when(customerDao.existsPersonWithEmail(email)).thenReturn(true);

      CustomerRegistrationRequest request = new CustomerRegistrationRequest(
            "Alex", email, 19
      );

      //When
      assertThatThrownBy(() -> underTest.addCustomer(request))
            .isInstanceOf(DuplicateResource.class)
            .hasMessage("Email already taken");

      verify(customerDao, never()).insertCustomer(any());
   }

   @Test
   void deleteCustomer() {
      //Given
      int id = 10;

      when(customerDao.existsPersonWithId(id)).thenReturn(true);

      //When
      underTest.deleteCustomer(id);

      //Then
      verify(customerDao).deleteCustomerById(id);
   }

   @Test
   void willThrowWhenDeleteCustomerByIdNotExists() {
      //Given
      int id = 10;

      when(customerDao.existsPersonWithId(id)).thenReturn(false);

      //When
      assertThatThrownBy(() -> underTest.deleteCustomer(id))
            .isInstanceOf(ResourceNotFound.class)
            .hasMessage("customer with id [%s] not found".formatted(id));

      //Then
      verify(customerDao, never()).deleteCustomerById(id);
   }

   @Test
   void canUpdateAllCustomerProperties() {
      //Given
      int id = 10;
      Customer customer = new Customer(id, "Alex", "alex@gmail.com", 19);

      when(customerDao.selectCustomerById(id)).thenReturn(Optional.of(customer));

      String newEmail = "alexandro@gmail.com";
      CustomerUpdateRequest update = new CustomerUpdateRequest("Alexandro", newEmail, 23);

      when(customerDao.existsPersonWithEmail(newEmail)).thenReturn(false);

      //When
      underTest.updateCustomer(id, update);

      //Then
      ArgumentCaptor<Customer> customerArgumentCaptor = ArgumentCaptor.forClass(Customer.class);

      verify(customerDao).updateCustomer(customerArgumentCaptor.capture());
      Customer capturedCustomer = customerArgumentCaptor.getValue();

      assertThat(capturedCustomer.getName()).isEqualTo(update.name());
      assertThat(capturedCustomer.getEmail()).isEqualTo(update.email());
      assertThat(capturedCustomer.getAge()).isEqualTo(update.age());
   }

   @Test
   void canUpdateOnlyCustomerName() {
      //Given
      int id = 10;
      Customer customer = new Customer(id, "Alex", "alex@gmail.com", 19);

      when(customerDao.selectCustomerById(id)).thenReturn(Optional.of(customer));

      CustomerUpdateRequest update = new CustomerUpdateRequest("Alexandro", null, null);

      //When
      underTest.updateCustomer(id, update);

      //Then
      ArgumentCaptor<Customer> customerArgumentCaptor = ArgumentCaptor.forClass(Customer.class);

      verify(customerDao).updateCustomer(customerArgumentCaptor.capture());
      Customer capturedCustomer = customerArgumentCaptor.getValue();

      assertThat(capturedCustomer.getName()).isEqualTo(update.name());
      assertThat(capturedCustomer.getEmail()).isEqualTo(customer.getEmail());
      assertThat(capturedCustomer.getAge()).isEqualTo(customer.getAge());
   }

   @Test
   void canUpdateOnlyCustomerEmail() {
      //Given
      int id = 10;
      Customer customer = new Customer(id, "Alex", "alex@gmail.com", 19);

      when(customerDao.selectCustomerById(id)).thenReturn(Optional.of(customer));

      String newEmail = "alexandro@gmail.com";
      CustomerUpdateRequest update = new CustomerUpdateRequest(null, newEmail, null);

      when(customerDao.existsPersonWithEmail(newEmail)).thenReturn(false);

      //When
      underTest.updateCustomer(id, update);

      //Then
      ArgumentCaptor<Customer> customerArgumentCaptor = ArgumentCaptor.forClass(Customer.class);

      verify(customerDao).updateCustomer(customerArgumentCaptor.capture());
      Customer capturedCustomer = customerArgumentCaptor.getValue();

      assertThat(capturedCustomer.getName()).isEqualTo(customer.getName());
      assertThat(capturedCustomer.getEmail()).isEqualTo(newEmail);
      assertThat(capturedCustomer.getAge()).isEqualTo(customer.getAge());
   }

   @Test
   void canUpdateOnlyCustomerAge() {
      //Given
      int id = 10;
      Customer customer = new Customer(id, "Alex", "alex@gmail.com", 19);

      when(customerDao.selectCustomerById(id)).thenReturn(Optional.of(customer));

      CustomerUpdateRequest update = new CustomerUpdateRequest(null, null, 22);

      //When
      underTest.updateCustomer(id, update);

      //Then
      ArgumentCaptor<Customer> customerArgumentCaptor = ArgumentCaptor.forClass(Customer.class);

      verify(customerDao).updateCustomer(customerArgumentCaptor.capture());
      Customer capturedCustomer = customerArgumentCaptor.getValue();

      assertThat(capturedCustomer.getName()).isEqualTo(customer.getName());
      assertThat(capturedCustomer.getEmail()).isEqualTo(customer.getEmail());
      assertThat(capturedCustomer.getAge()).isEqualTo(update.age());
   }

   @Test
   void willThrowWhenTryingToUpdateCustomerEmailWhenAlreadyTaken() {
      //Given
      int id = 10;
      Customer customer = new Customer(id, "Alex", "alex@gmail.com", 19);

      when(customerDao.selectCustomerById(id)).thenReturn(Optional.of(customer));

      String newEmail = "alexandro@gmail.com";
      CustomerUpdateRequest update = new CustomerUpdateRequest(null, newEmail, null);

      when(customerDao.existsPersonWithEmail(newEmail)).thenReturn(true);

      //When
      assertThatThrownBy(() -> underTest.updateCustomer(id, update))
            .isInstanceOf(DuplicateResource.class)
            .hasMessage("Email already taken");

      //Then
      verify(customerDao, never()).updateCustomer(any());
   }

   @Test
   void willThrowWhenCustomerUpdateHasNoChanges() {
      //Given
      int id = 10;
      Customer customer = new Customer(id, "Alex", "alex@gmail.com", 19);

      when(customerDao.selectCustomerById(id)).thenReturn(Optional.of(customer));

      CustomerUpdateRequest update = new CustomerUpdateRequest(
            customer.getName(), customer.getEmail(), customer.getAge()
      );

      //When
      assertThatThrownBy(() -> underTest.updateCustomer(id, update))
            .isInstanceOf(RequestValidationException.class)
            .hasMessage("no data changes found");

      //Then
      verify(customerDao, never()).updateCustomer(any());
   }
}