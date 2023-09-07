package com.example.demo;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;

import com.example.demo.model.persistence.Cart;
import com.example.demo.model.persistence.Item;
import com.example.demo.model.persistence.User;
import com.example.demo.model.persistence.UserOrder;
import com.example.demo.model.requests.CreateUserRequest;
import com.example.demo.model.requests.JwtRequest;
import com.example.demo.model.requests.ModifyCartRequest;
import com.example.demo.model.response.JwtResponse;
import com.fasterxml.jackson.databind.ObjectMapper;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
public class SareetaApplicationTests {

	@Autowired
	private TestRestTemplate restTemplate;

	@Test
	public void testCreateUser() throws Exception {
		CreateUserRequest userRequest = new CreateUserRequest();
		userRequest.setUsername("chiennv1");
		userRequest.setPassword("12345678");
		userRequest.setConfirmPassword("12345678");
		ResponseEntity<User> response = restTemplate.postForEntity("/api/user/create", userRequest, User.class);
		assertEquals(HttpStatus.OK, response.getStatusCode());
	}
	
	@Test
	public void testCreateUserInvalidData() throws Exception {
		CreateUserRequest userRequest = new CreateUserRequest();
		userRequest.setUsername("chiennv2");
		userRequest.setPassword("12345678");
		userRequest.setConfirmPassword("87654321");
		ResponseEntity<String> response = restTemplate.postForEntity("/api/user/create", userRequest, String.class);
		assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
	}
	
	@Test
	public void testCreateAuthenticationToken() throws Exception {
		String token = getToken();
		assertNotNull(token);
	}
	
	@Test
	public void testUserFindById() throws Exception {
		String token = getToken();
		HttpHeaders headers = new HttpHeaders();
		headers.add("Authorization", "Bearer " + token);
		HttpEntity<String> request = new HttpEntity<String>(headers);
		ResponseEntity<User> response = restTemplate.exchange("/api/user/id/1", HttpMethod.GET, request, User.class);
		assertEquals(HttpStatus.OK, response.getStatusCode());
	}
	
	@Test
	public void testUserFindByIdNotFound() throws Exception {
		String token = getToken();
		HttpHeaders headers = new HttpHeaders();
		headers.add("Authorization", "Bearer " + token);
		HttpEntity<String> request = new HttpEntity<String>(headers);
		ResponseEntity<User> response = restTemplate.exchange("/api/user/id/999", HttpMethod.GET, request, User.class);
		assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
	}
	
	@Test
	public void testUserFindByUserName() throws Exception {
		String token = getToken();
		HttpHeaders headers = new HttpHeaders();
		headers.add("Authorization", "Bearer " + token);
		HttpEntity<String> request = new HttpEntity<String>(headers);
		ResponseEntity<User> response = restTemplate.exchange("/api/user/chiennv", HttpMethod.GET, request, User.class);
		assertEquals(HttpStatus.OK, response.getStatusCode());
	}
	
	@Test
	public void testUserFindByUserNameNotFound() throws Exception {
		String token = getToken();
		HttpHeaders headers = new HttpHeaders();
		headers.add("Authorization", "Bearer " + token);
		HttpEntity<String> request = new HttpEntity<String>(headers);
		ResponseEntity<User> response = restTemplate.exchange("/api/user/abcdef", HttpMethod.GET, request, User.class);
		assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
	}

	@Test
	public void testAddTocartNotFoundUser() {
		String token = getToken();
		HttpHeaders headers = new HttpHeaders();
		headers.add("Authorization", "Bearer " + token);
		headers.set("Content-Type", MediaType.APPLICATION_JSON_VALUE);
		headers.set("Accept", MediaType.APPLICATION_JSON_VALUE);
		
		ModifyCartRequest cartRequest = new ModifyCartRequest();
		cartRequest.setUsername("testabc");
		cartRequest.setItemId(1);
		cartRequest.setQuantity(2);
		
		HttpEntity<String> request = new HttpEntity<String>(getBody(cartRequest), headers);
		ResponseEntity<Cart> response = restTemplate.exchange("/api/cart/addToCart", HttpMethod.POST, request, Cart.class);
		assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
	}
	
	@Test
	public void testAddTocart() {
		CreateUserRequest userRequest = new CreateUserRequest();
		userRequest.setUsername("chiennv2");
		userRequest.setPassword("12345678");
		userRequest.setConfirmPassword("12345678");
		ResponseEntity<User> responseUser = restTemplate.postForEntity("/api/user/create", userRequest, User.class);
		
		String token = getToken();
		HttpHeaders headers = new HttpHeaders();
		headers.add("Authorization", "Bearer " + token);
		headers.set("Content-Type", MediaType.APPLICATION_JSON_VALUE);
		headers.set("Accept", MediaType.APPLICATION_JSON_VALUE);
		
		ModifyCartRequest cartRequest = new ModifyCartRequest();
		cartRequest.setUsername("chiennv2");
		cartRequest.setItemId(1);
		cartRequest.setQuantity(1);
		
		HttpEntity<String> request = new HttpEntity<String>(getBody(cartRequest), headers);
		ResponseEntity<Cart> response = restTemplate.exchange("/api/cart/addToCart", HttpMethod.POST, request, Cart.class);
		assertEquals(HttpStatus.OK, response.getStatusCode());
	}
	
	@Test
	public void testAddTocartNotFoundItem() {
		String token = getToken();
		HttpHeaders headers = new HttpHeaders();
		headers.add("Authorization", "Bearer " + token);
		headers.set("Content-Type", MediaType.APPLICATION_JSON_VALUE);
		headers.set("Accept", MediaType.APPLICATION_JSON_VALUE);
		
		ModifyCartRequest cartRequest = new ModifyCartRequest();
		cartRequest.setUsername("chiennv");
		cartRequest.setItemId(999);
		cartRequest.setQuantity(1);
		
		HttpEntity<String> request = new HttpEntity<String>(getBody(cartRequest), headers);
		ResponseEntity<String> response = restTemplate.exchange("/api/cart/addToCart", HttpMethod.POST, request, String.class);
		assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
	}
	
	@Test
	public void testRemoveFromCartNotFoundUser() {
		String token = getToken();
		HttpHeaders headers = new HttpHeaders();
		headers.add("Authorization", "Bearer " + token);
		headers.set("Content-Type", MediaType.APPLICATION_JSON_VALUE);
		headers.set("Accept", MediaType.APPLICATION_JSON_VALUE);
		
		ModifyCartRequest cartRequest = new ModifyCartRequest();
		cartRequest.setUsername("testabc");
		cartRequest.setItemId(1);
		cartRequest.setQuantity(1);
		
		HttpEntity<String> request = new HttpEntity<String>(getBody(cartRequest), headers);
		ResponseEntity<String> response = restTemplate.exchange("/api/cart/removeFromCart", HttpMethod.POST, request, String.class);
		assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
	}
	
	@Test
	public void testRemoveFromCartNotFoundItem() {
		String token = getToken();
		HttpHeaders headers = new HttpHeaders();
		headers.add("Authorization", "Bearer " + token);
		headers.set("Content-Type", MediaType.APPLICATION_JSON_VALUE);
		headers.set("Accept", MediaType.APPLICATION_JSON_VALUE);
		
		ModifyCartRequest cartRequest = new ModifyCartRequest();
		cartRequest.setUsername("chiennv");
		cartRequest.setItemId(999);
		cartRequest.setQuantity(1);
		
		HttpEntity<String> request = new HttpEntity<String>(getBody(cartRequest), headers);
		ResponseEntity<Void> response = restTemplate.exchange("/api/cart/removeFromCart", HttpMethod.POST, request, Void.class);
		assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
	}
	
	@Test
	public void testRemoveFromCart() {
		
		CreateUserRequest userRequest = new CreateUserRequest();
		userRequest.setUsername("chiennv3");
		userRequest.setPassword("12345678");
		userRequest.setConfirmPassword("12345678");
		restTemplate.postForEntity("/api/user/create", userRequest, User.class);
		
		String token = getToken();
		HttpHeaders headers = new HttpHeaders();
		headers.add("Authorization", "Bearer " + token);
		headers.set("Content-Type", MediaType.APPLICATION_JSON_VALUE);
		headers.set("Accept", MediaType.APPLICATION_JSON_VALUE);
		
		ModifyCartRequest cartRequest = new ModifyCartRequest();
		cartRequest.setUsername("chiennv3");
		cartRequest.setItemId(1);
		cartRequest.setQuantity(1);
		
		HttpEntity<String> request = new HttpEntity<String>(getBody(cartRequest), headers);
		ResponseEntity<Cart> response = restTemplate.exchange("/api/cart/removeFromCart", HttpMethod.POST, request, Cart.class);
		assertEquals(HttpStatus.OK, response.getStatusCode());
	}
	
	@Test
	public void testGetItems() {
		String token = getToken();
		HttpHeaders headers = new HttpHeaders();
		headers.add("Authorization", "Bearer " + token);
		HttpEntity<String> request = new HttpEntity<String>(headers);
		ResponseEntity<List> response = restTemplate.exchange("/api/item", HttpMethod.GET, request, List.class);
		assertEquals(HttpStatus.OK, response.getStatusCode());
		assertTrue(response.getBody().size() > 0);
	}
	
	@Test
	public void testGetItemById() {
		String token = getToken();
		HttpHeaders headers = new HttpHeaders();
		headers.add("Authorization", "Bearer " + token);
		HttpEntity<String> request = new HttpEntity<String>(headers);
		ResponseEntity<Item> response = restTemplate.exchange("/api/item/1", HttpMethod.GET, request, Item.class);
		assertEquals(HttpStatus.OK, response.getStatusCode());
	}
	
	@Test
	public void testGetItemByIdNotFound() {
		String token = getToken();
		HttpHeaders headers = new HttpHeaders();
		headers.add("Authorization", "Bearer " + token);
		HttpEntity<String> request = new HttpEntity<String>(headers);
		ResponseEntity<Item> response = restTemplate.exchange("/api/item/999", HttpMethod.GET, request, Item.class);
		assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
	}
	
	@Test
	public void testGetItemsByName() {
		String token = getToken();
		HttpHeaders headers = new HttpHeaders();
		headers.add("Authorization", "Bearer " + token);
		HttpEntity<String> request = new HttpEntity<String>(headers);
		ResponseEntity<List> response = restTemplate.exchange("/api/item/name/Round Widget", HttpMethod.GET, request, List.class);
		assertEquals(HttpStatus.OK, response.getStatusCode());
		assertTrue(response.getBody().size() > 0);
	}
	
	@Test
	public void testGetItemsByNameNotFound() {
		String token = getToken();
		HttpHeaders headers = new HttpHeaders();
		headers.add("Authorization", "Bearer " + token);
		HttpEntity<String> request = new HttpEntity<String>(headers);
		ResponseEntity<List> response = restTemplate.exchange("/api/item/name/abcd123456", HttpMethod.GET, request, List.class);
		assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
	}
	
	@Test
	public void testSubmitNotFound() {
		String token = getToken();
		HttpHeaders headers = new HttpHeaders();
		headers.add("Authorization", "Bearer " + token);
		HttpEntity<String> request = new HttpEntity<String>(headers);
		ResponseEntity<UserOrder> response = restTemplate.exchange("/api/order/submit/abcd1234", HttpMethod.POST, request, UserOrder.class);
		assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
	}

	@Test
	public void testSubmit() {
		
		CreateUserRequest userRequest = new CreateUserRequest();
		userRequest.setUsername("chiennv5");
		userRequest.setPassword("12345678");
		userRequest.setConfirmPassword("12345678");
		restTemplate.postForEntity("/api/user/create", userRequest, User.class);
		
		String token = getToken();
		HttpHeaders headers = new HttpHeaders();
		headers.add("Authorization", "Bearer " + token);
		HttpEntity<String> request = new HttpEntity<String>(headers);
		ResponseEntity<UserOrder> response = restTemplate.exchange("/api/order/submit/chiennv5", HttpMethod.POST, request, UserOrder.class);
		assertEquals(HttpStatus.OK, response.getStatusCode());
	}

	@Test
	public void testGetOrdersForUser() {
		CreateUserRequest userRequest = new CreateUserRequest();
		userRequest.setUsername("chiennv6");
		userRequest.setPassword("12345678");
		userRequest.setConfirmPassword("12345678");
		restTemplate.postForEntity("/api/user/create", userRequest, User.class);
		
		String token = getToken();
		HttpHeaders headers = new HttpHeaders();
		headers.add("Authorization", "Bearer " + token);
		HttpEntity<String> request = new HttpEntity<String>(headers);
		ResponseEntity<UserOrder> response = restTemplate.exchange("/api/order/submit/chiennv6", HttpMethod.POST, request, UserOrder.class);
		
		ResponseEntity<List> responseHistory = restTemplate.exchange("/api/order/history/chiennv6", HttpMethod.GET, request, List.class);
		assertEquals(HttpStatus.OK, response.getStatusCode());
		assertTrue(responseHistory.getBody().size() > 0);
	}

	@Test
	public void testGetOrdersForUserNotFound() {
		String token = getToken();
		HttpHeaders headers = new HttpHeaders();
		headers.add("Authorization", "Bearer " + token);
		HttpEntity<String> request = new HttpEntity<String>(headers);
		ResponseEntity<List> responseHistory = restTemplate.exchange("/api/order/history/abcd1234", HttpMethod.GET, request, List.class);
		assertEquals(HttpStatus.NOT_FOUND, responseHistory.getStatusCode());
	}
	
	@Test
	public void testGetOrdersForUserEmpty() {
		String token = getToken();
		HttpHeaders headers = new HttpHeaders();
		headers.add("Authorization", "Bearer " + token);
		HttpEntity<String> request = new HttpEntity<String>(headers);
		ResponseEntity<List> responseHistory = restTemplate.exchange("/api/order/history/chiennv", HttpMethod.GET, request, List.class);
		assertEquals(HttpStatus.OK, responseHistory.getStatusCode());
		assertTrue(responseHistory.getBody().size() == 0);
	}
	
	public String getToken() {
		ResponseEntity<JwtResponse> response = restTemplate.postForEntity("/api/user/authenticate",
				new JwtRequest("chiennv", "12345678"), JwtResponse.class);
		return response.getBody().getJwttoken();
	}

	private static String getBody(Object object) {
		try {
			return new ObjectMapper().writeValueAsString(object);
		} catch (Exception e) {
			return null;
		}
	}
	
}
