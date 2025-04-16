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

    @PUT("api/Userrs/{id}")
    Call<User> updateUser(@Path("id") int id, @Body User user);

    @POST("api/CatalogProducts")
    Call<Product> createProduct(@Body Product product);

    @PUT("api/CatalogProducts/{id}")
    Call<Product> updateProduct(@Path("id") int id, @Body Product product);

    @DELETE("api/CatalogProducts/{id}")
    Call<Void> deleteProduct(@Path("id") int id);

    @GET("api/Categories")
    Call<List<Categorie>> getAllCategories();

    @GET("api/Brands")
    Call<List<Brand>> getAllBrands();

    @GET("api/CatalogProducts")
    Call<List<Product>> getAllProducts();

    @GET("api/Categories/{id}")
    Call<Categorie> getCategory(@Path("id") int id);

    @GET("api/Brands/{id}")
    Call<Brand> getBrand(@Path("id") int id);

    @POST("api/Categories")
    Call<Categorie> createCategory(@Body Categorie category);

    @POST("api/Brands")
    Call<Brand> createBrand(@Body Brand brand);

    @PUT("api/Categories/{id}")
    Call<Categorie> updateCategory(@Path("id") int id, @Body Categorie category);

    @PUT("api/Brands/{id}")
    Call<Brand> updateBrand(@Path("id") int id, @Body Brand brand);

    @DELETE("api/Categories/{id}")
    Call<Void> deleteCategory(@Path("id") int id);

    @DELETE("api/Brands/{id}")
    Call<Void> deleteBrand(@Path("id") int id);

    @GET("api/Rolees")
    Call<List<Role>> getAllRoles();

    @GET("api/Rolees/{id}")
    Call<Role> getRole(@Path("id") int id);

    @POST("api/Favorites")
    Call<Favorite> addFavorite(@Body Favorite favorite);

    @GET("api/Favorites/{userId}")
    Call<List<Favorite>> getFavorites(@Path("userId") String userId);

    @DELETE("api/Favorites/{id}")
    Call<Void> deleteFavorite(@Path("id") int id);
}