package com.amigoscode.journey;

import com.amigoscode.customer.Customer;
import com.amigoscode.customer.CustomerRegistrationRequest;
import com.amigoscode.customer.CustomerUpdateRequest;
import com.github.javafaker.Faker;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Random;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;

@SpringBootTest(webEnvironment = RANDOM_PORT)
public class CustomerIntegrationTest {

   @Autowired
   private WebTestClient webTestClient;

   private static final Random RANDOM = new Random();

   @Test
   void canRegisterCustomer() {
      //create a registration request
      Faker faker = new Faker();
      String name = faker.name().fullName();
      String email = faker.name().lastName() + "-" + UUID.randomUUID() + "@amigoscode.com";
      int age = RANDOM.nextInt(1, 100);
      CustomerRegistrationRequest request = new CustomerRegistrationRequest(name, email, age);

      //send a post request
      webTestClient.post()
            .uri("/api/v1/customers")
            .accept(MediaType.APPLICATION_JSON)
            .contentType(MediaType.APPLICATION_JSON)
            .body(Mono.just(request), CustomerRegistrationRequest.class)
            .exchange()
            .expectStatus()
            .isOk();

      //get all customers
      List<Customer> allCustomers = webTestClient.get()
            .uri("/api/v1/customers")
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isOk()
            .expectBodyList(new ParameterizedTypeReference<Customer>() {
            })
            .returnResult()
            .getResponseBody();

      //make sure customer is present
      Customer expected = new Customer(name, email, age);

      assertThat(allCustomers)
            .usingRecursiveFieldByFieldElementComparatorIgnoringFields("id")
            .contains(expected);

      //get customer by id
      int id = allCustomers.stream()
            .filter(c -> c.getEmail().equals(email))
            .map(Customer::getId)
            .findFirst()
            .orElseThrow();

      expected.setId(id);

      webTestClient.get()
            .uri("/api/v1/customers/{id}", id)
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isOk()
            .expectBody(new ParameterizedTypeReference<Customer>() {
            })
            .isEqualTo(expected);
   }

   @Test
   void canDeleteCustomer() {
      //create a registration request
      Faker faker = new Faker();
      String name = faker.name().fullName();
      String email = faker.name().lastName() + "-" + UUID.randomUUID() + "@amigoscode.com";
      int age = RANDOM.nextInt(1, 100);
      CustomerRegistrationRequest request = new CustomerRegistrationRequest(name, email, age);

      //send a post request
      webTestClient.post()
            .uri("/api/v1/customers")
            .accept(MediaType.APPLICATION_JSON)
            .contentType(MediaType.APPLICATION_JSON)
            .body(Mono.just(request), CustomerRegistrationRequest.class)
            .exchange()
            .expectStatus()
            .isOk();

      //get all customers
      List<Customer> allCustomers = webTestClient.get()
            .uri("/api/v1/customers")
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isOk()
            .expectBodyList(new ParameterizedTypeReference<Customer>() {
            })
            .returnResult()
            .getResponseBody();

      //get customer by id
      int id = allCustomers.stream()
            .filter(c -> c.getEmail().equals(email))
            .map(Customer::getId)
            .findFirst()
            .orElseThrow();

      //delete customer by id
      webTestClient.delete()
            .uri("/api/v1/customers/{id}", id)
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isOk();


      //get customer by id to make sure it's deleted
      webTestClient.get()
            .uri("/api/v1/customers/{id}", id)
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isNotFound();
   }

   @Test
   void canUpdateCustomer() {
      //create a registration request
      Faker faker = new Faker();
      String name = faker.name().fullName();
      String email = faker.name().lastName() + "-" + UUID.randomUUID() + "@amigoscode.com";
      int age = RANDOM.nextInt(1, 100);
      CustomerRegistrationRequest request = new CustomerRegistrationRequest(name, email, age);

      //send a post request
      webTestClient.post()
            .uri("/api/v1/customers")
            .accept(MediaType.APPLICATION_JSON)
            .contentType(MediaType.APPLICATION_JSON)
            .body(Mono.just(request), CustomerRegistrationRequest.class)
            .exchange()
            .expectStatus()
            .isOk();

      //get all customers
      List<Customer> allCustomers = webTestClient.get()
            .uri("/api/v1/customers")
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isOk()
            .expectBodyList(new ParameterizedTypeReference<Customer>() {
            })
            .returnResult()
            .getResponseBody();

      //get customer by id
      int id = allCustomers.stream()
            .filter(c -> c.getEmail().equals(email))
            .map(Customer::getId)
            .findFirst()
            .orElseThrow();

      //update the customer
      String newName = "Ali";
      CustomerUpdateRequest updateRequest = new CustomerUpdateRequest(newName, null, null);

      webTestClient.put()
            .uri("/api/v1/customers/{id}", id)
            .accept(MediaType.APPLICATION_JSON)
            .contentType(MediaType.APPLICATION_JSON)
            .body(Mono.just(updateRequest), CustomerUpdateRequest.class)
            .exchange()
            .expectStatus()
            .isOk();


      //get customer by id to make sure it's deleted
      Customer updatedCustomer = webTestClient.get()
            .uri("/api/v1/customers/{id}", id)
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isOk()
            .expectBody(Customer.class)
            .returnResult()
            .getResponseBody();

      Customer expected = new Customer(id, newName, email, age);

      assertThat(updatedCustomer).isEqualTo(expected )   ;
   }
}






























