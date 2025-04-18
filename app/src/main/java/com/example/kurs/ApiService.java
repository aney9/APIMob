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
    // Методы для пользователей
    @GET("api/Userrs")
    Call<List<User>> getAllUsers();

    @GET("api/Userrs/{id}")
    Call<User> getUser(@Path("id") int id);

    @PUT("api/Userrs/{id}")
    Call<User> updateUser(@Path("id") int id, @Body User user);

    @DELETE("api/Userrs/{id}")
    Call<Void> deleteUser(@Path("id") int id);

    // Методы для продуктов
    @GET("api/CatalogProducts")
    Call<List<Product>> getAllProducts();

    @GET("api/CatalogProducts/{id}")
    Call<Product> getProduct(@Path("id") int id);

    @POST("api/CatalogProducts")
    Call<Product> createProduct(@Body Product product);

    @PUT("api/CatalogProducts/{id}")
    Call<Product> updateProduct(@Path("id") int id, @Body Product product);

    @DELETE("api/CatalogProducts/{id}")
    Call<Void> deleteProduct(@Path("id") int id);

    // Методы для категорий
    @GET("api/Categories")
    Call<List<Categorie>> getAllCategories();

    @GET("api/Categories/{id}")
    Call<Categorie> getCategory(@Path("id") int id);

    @POST("api/Categories")
    Call<Categorie> createCategory(@Body Categorie category);

    @PUT("api/Categories/{id}")
    Call<Categorie> updateCategory(@Path("id") int id, @Body Categorie category);

    @DELETE("api/Categories/{id}")
    Call<Void> deleteCategory(@Path("id") int id);

    // Методы для брендов
    @GET("api/Brands")
    Call<List<Brand>> getAllBrands();

    @GET("api/Brands/{id}")
    Call<Brand> getBrand(@Path("id") int id);

    @POST("api/Brands")
    Call<Brand> createBrand(@Body Brand brand);

    @PUT("api/Brands/{id}")
    Call<Brand> updateBrand(@Path("id") int id, @Body Brand brand);

    @DELETE("api/Brands/{id}")
    Call<Void> deleteBrand(@Path("id") int id);

    // Методы для ролей
    @GET("api/Rolees")
    Call<List<Role>> getAllRoles();

    @GET("api/Rolees/{id}")
    Call<Role> getRole(@Path("id") int id);

    // Методы для избранного
    @GET("api/Favorites")
    Call<List<Favorite>> getAllFavorites();

    @POST("api/Favorites")
    Call<Favorite> addFavorite(@Body FavoriteWrapper favoriteWrapper);

    @DELETE("api/Favorites/{id}")
    Call<Void> deleteFavorite(@Path("id") int id);

    // Методы для корзины
    @GET("api/Carts")
    Call<List<Cart>> getAllCarts();

    // Метод для добавления в корзину
    @POST("api/Carts")
    Call<Cart> addToCart(@Body Cart cart);

    // Метод для обновления корзины
    @PUT("api/Carts/{id}")
    Call<Cart> updateCart(@Path("id") int id, @Body CartWrapper cartWrapper);

    // Метод для удаления из корзины
    @DELETE("api/Carts/{id}")
    Call<Void> deleteCart(@Path("id") int id);

    // Методы для элементов заказа
    @GET("api/OrderItems")
    Call<List<OrderItem>> getAllOrderItems();

    @GET("api/OrderItems/{id}")
    Call<OrderItem> getOrderItem(@Path("id") int id);

    @POST("api/OrderItems")
    Call<OrderItem> createOrderItem(@Body OrderItem orderItem);

    @PUT("api/OrderItems/{id}")
    Call<OrderItem> updateOrderItem(@Path("id") int id, @Body OrderItem orderItem);

    @DELETE("api/OrderItems/{id}")
    Call<Void> deleteOrderItem(@Path("id") int id);

    // Методы для заказов
    @GET("api/Orders")
    Call<List<Order>> getAllOrders();

    @GET("api/Orders/{id}")
    Call<Order> getOrder(@Path("id") int id);

    @POST("api/Orders")
    Call<Order> createOrder(@Body Order order);

    @PUT("api/Orders/{id}")
    Call<Order> updateOrder(@Path("id") int id, @Body Order order);

    @DELETE("api/Orders/{id}")
    Call<Void> deleteOrder(@Path("id") int id);

    // Методы для отзывов
    @GET("api/Reviews")
    Call<List<Review>> getAllReviews();

    @GET("api/Reviews/{id}")
    Call<Review> getReview(@Path("id") int id);

    @POST("api/Reviews")
    Call<Review> createReview(@Body Review review);

    @PUT("api/Reviews/{id}")
    Call<Review> updateReview(@Path("id") int id, @Body Review review);

    @DELETE("api/Reviews/{id}")
    Call<Void> deleteReview(@Path("id") int id);
}