package com.example.kurs;

import java.util.List;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;

public interface ApiService {
    @GET("api/Userrs")
    Call<List<User>> getAllUsers();

    @GET("api/CatalogProducts")
    Call<List<Product>> getProducts();

    @GET("api/Userrs/{id}")
    Call<User> getUser(@Path("id") int id);

    @POST("api/CatalogProducts")
    Call<Product> createProduct(@Body Product product);

    @PUT("api/CatalogProducts/{id}")
    Call<Product> updateProduct(@Path("id") int id, @Body Product product);

    @DELETE("api/CatalogProducts/{id}")
    Call<Void> deleteProduct(@Path("id") int id);
}