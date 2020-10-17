package com.company.test;

import com.company.main.data.CreateUserResponse;
import com.company.main.data.DeleteUserResponse;
import com.company.main.data.PetOrderStatus;
import com.company.main.entities.*;
import com.company.main.entities.Order;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.jupiter.api.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

import static io.restassured.RestAssured.given;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class restAssuredApi {
    @BeforeEach
    public void setUp() {
        RestAssured.baseURI = "https://petstore.swagger.io/v2";
    }

    @AfterEach
    public void tearDown() {
    }

    @Test
    public void addUserToTheStore() {
        //Preparing test data

        //just for pleasure
        //LocalDateTime now = LocalDateTime.now();
        //System.out.println(now);
        // boolean var = PetOrderStatus.APPROVED.equals(PetOrderStatus.APPROVED);


        User newUser = new User(
                100000 + (long) (Math.random() * 999999),
                "user" + RandomStringUtils.randomAlphabetic(5),
                "firstName" + RandomStringUtils.randomAlphabetic(5),
                "lastName" + RandomStringUtils.randomAlphabetic(5),
                RandomStringUtils.randomAlphabetic(5) + "@test.com",
                RandomStringUtils.random(10, 0, 123, true, true),
                "+38097" + RandomStringUtils.randomNumeric(7),
                1000 + (int) (Math.random() * 999));

//        Tests
        Response responseCreateUser = given()
                .basePath("/user")
                .contentType(ContentType.JSON)
                .body(newUser)
                .post();

        assertEquals(200, responseCreateUser.getStatusCode());
        System.out.println("Response for adding a new user: \n" + responseCreateUser.asString() + "\n"); // log info
        CreateUserResponse createdUser = responseCreateUser.as(CreateUserResponse.class);

        User foundUserByUserName = given()
                .pathParam("userName", newUser.getUsername())
                .basePath("/user/{userName}")
                .accept("application/json")
                .when()
                .get()
                .as(User.class);
        System.out.println("Response for getting user by userName: \n" + foundUserByUserName.toString()); // log info

        // final assert
        assertEquals(newUser.getEmail(), foundUserByUserName.getEmail());


        // удалить своего юзера в конце теста, чтобы не засорять базу
        Response deleteResponse =
                given()
                        .pathParam("userName", newUser.getUsername())
                        .basePath("/user/{userName}")
                        .accept("application/json")
                        .when()
                        .delete();
        System.out.println(deleteResponse.asString());
        DeleteUserResponse deleteResponseAsClass = deleteResponse.as(DeleteUserResponse.class);
        assertEquals(200, deleteResponseAsClass.getCode());
        assertNotNull(deleteResponseAsClass.getType());
        System.out.println(deleteResponseAsClass.getType());
        assertEquals(foundUserByUserName.getUsername(), deleteResponseAsClass.getMessage());
    }

    //place an order test
    @Test
    public void placeAnOrderToTheStore() {
        //Preparing test data
        Order newOrder = new Order(
                (long) (1 + (Math.random() * 9)),
                (int) (1 + (Math.random() * 9)),
                (int) (1 + (Math.random() * 3)),
                LocalDateTime.now().toString(),
                PetOrderStatus.PLACED.name(),
                false);
        //System.out.println(newOrder.toString());

        //        Tests
        Response responsePlaceAnOrder = given()
                .basePath("/store/order")
                .contentType(ContentType.JSON)
                .body(newOrder)
                .post();

        assertEquals(200, responsePlaceAnOrder.getStatusCode());
        System.out.println("Response for placing a new order: \n" + responsePlaceAnOrder.asString() + "\n");

        Order foundOrderByOrderId = given()
                .pathParam("orderId", newOrder.getId())
                .basePath("/store/order/{orderId}")
                .accept("application/json")
                .when()
                .get()
                .as(Order.class);
        System.out.println("Response for getting order by orderId: \n" + foundOrderByOrderId.toString());
        assertEquals(newOrder.getQuantity(), foundOrderByOrderId.getQuantity());
        assertEquals(newOrder.getPetId(), foundOrderByOrderId.getPetId());

        // удалить своего юзера в конце теста, чтобы не засорять базу
        Response deleteResponse =
                given()
                        .pathParam("orderId", newOrder.getId())
                        .basePath("/store/order/{orderId}")
                        .accept("application/json")
                        .when()
                        .delete();
        System.out.println(deleteResponse.asString());
        DeleteUserResponse deleteResponseAsClass = deleteResponse.as(DeleteUserResponse.class);
        assertEquals(200, deleteResponseAsClass.getCode());
        assertNotNull(deleteResponseAsClass.getType());
        System.out.println(deleteResponseAsClass.getType());
        assertEquals(String.valueOf(foundOrderByOrderId.getId()), deleteResponseAsClass.getMessage());
    }

}
