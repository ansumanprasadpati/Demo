package eCommerceTestAPI;
//import org.testng.annotations.DataProvider;
//import org.testng.annotations.Test;
//import io.restassured.RestAssured;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.http.ContentType;
import io.restassured.path.json.JsonPath;
//import io.restassured.response.Response;
//import io.restassured.path.json.JsonPath;
import io.restassured.specification.RequestSpecification;
import pojo.OrderDetails;
import pojo.Orders;
import pojo.requestLogin;
import pojo.responseLogin;

import static io.restassured.RestAssured.*;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.testng.Assert;

public class eCommerceAPITest {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		
		//Login user
		RequestSpecification req_main= new RequestSpecBuilder().setBaseUri("https://rahulshettyacademy.com")
		.setContentType(ContentType.JSON).build();
	//new pojo class created and called here by creating the object.
		requestLogin req_Login= new requestLogin();
		req_Login.setUserEmail("ansuman.pati@prodapt.com");
		req_Login.setUserPassword("Ansu@123");
	
		RequestSpecification reqLogin=given().log().all().spec(req_main).body(req_Login);
		
		responseLogin res_Login=
		reqLogin.when().post("/api/ecom/auth/login").then().log().all().extract().response()
		.as(responseLogin.class);
		System.out.println(res_Login.getToken());
		String token= res_Login.getToken();
		System.out.println(res_Login.getUserId());
		String UserId=res_Login.getUserId();
		System.out.println(res_Login.getMessage());
		
		//Add Product
		RequestSpecification req_Addproduct=new RequestSpecBuilder().setBaseUri("https://rahulshettyacademy.com")
		.addHeader("authorization",token).build();
		RequestSpecification req=given().log().all().spec(req_Addproduct).param("productName", "Coke")
		.param("productAddedBy", UserId).param("productCategory","fashion")
		.param("productSubCategory", "shirts").param("productPrice", "11500")
		.param("productDescription", "Addias Originals").param("productFor", "women")
		.multiPart("productImage",new File("C:\\Users\\ansuman.pati\\Downloads\\coca-cola.png"));
		
		String res_addProduct=req.when().post("/api/ecom/product/add-product")
		.then().log().all().assertThat().statusCode(201).extract().response().asString();
		
		JsonPath js=new JsonPath(res_addProduct);
		String productID=js.get("productId");
		js.get("message");
		System.out.println(productID);
		
		
		//Create Order
		RequestSpecification req_CreateOrder= new RequestSpecBuilder().setBaseUri("https://rahulshettyacademy.com")
				.addHeader("authorization", token).setContentType(ContentType.JSON).build();
		OrderDetails od_de= new OrderDetails();
		od_de.setCountry("India");
		od_de.setProductOrderedId(productID);
		
		List<OrderDetails> orderDetailsList= new ArrayList<OrderDetails>();
		orderDetailsList.add(od_de);
		
		Orders od=new Orders();
		od.setOrders(orderDetailsList);
		
		
		RequestSpecification createOrder_req=given().log().all().spec(req_CreateOrder).body(od);
		
		String createOrder_res=createOrder_req.when().post("/api/ecom/order/create-order").then()
		.log().all().extract().response().asString();
		System.out.println(createOrder_res);
	
		//Delete Order
		RequestSpecification req_DeleteOrder= new RequestSpecBuilder().setBaseUri("https://rahulshettyacademy.com")
				.addHeader("authorization", token).setContentType(ContentType.JSON).build();
		RequestSpecification actual_req= given().spec(req_DeleteOrder).pathParam("productID", productID);
		String res_DeleteOrder= actual_req.when().delete("/api/ecom/product/delete-product/{productID}")
				.then().log().all().extract().response().asString();
		
		JsonPath js1=new JsonPath(res_DeleteOrder);
		Assert.assertEquals("Product Deleted Successfully", js1.get("message"));
	}

}
