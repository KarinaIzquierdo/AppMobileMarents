    package com.marents.app

import retrofit2.Call
import retrofit2.http.*

interface ApiService {

    @FormUrlEncoded
    @POST("productos")
    fun crearProductoSimple(@FieldMap params: Map<String, String>): Call<Producto>

    @GET("productos")
    fun getProductos(): Call<List<Producto>>

    @GET("productos/categoria/{categoriaNombre}")
    fun getProductosPorCategoria(@Path("categoriaNombre") categoriaNombre: String): Call<List<Producto>>

    @GET("productos/{id}")
    fun getProducto(@Path("id") id: Int): Call<Producto>

    @POST("productos")
    fun crearProducto(@Body producto: Producto): Call<Producto>

    @FormUrlEncoded
    @PUT("productos/{id}")
    fun actualizarProducto(@Path("id") id: Int, @FieldMap params: Map<String, String>): Call<Map<String, Any>>

    @DELETE("productos/{id}")
    fun eliminarProducto(@Path("id") id: Int): Call<Void>

    @GET("categorias")
    fun getCategorias(): Call<List<Categoria>>

    @GET("modelos")
    fun getModelos(): Call<List<Modelo>>

    @POST("login")
    fun login(@Body loginRequest: LoginRequest): Call<LoginResponse>

    @POST("register")
    fun register(@Body registerRequest: RegisterRequest): Call<RegisterResponse>

    @GET("users")
    fun getUsuarios(): Call<List<User>>

    @FormUrlEncoded
    @PUT("users/{id}")
    fun actualizarUsuario(
        @Path("id") id: Int,
        @Field("nombres") nombres: String,
        @Field("apellidos") apellidos: String,
        @Field("email") email: String,
        @Field("documento") documento: String?,
        @Field("celular") celular: String?,
        @Field("rol") rol: String,
        @Field("password") password: String?
    ): Call<Map<String, Any>>

    @DELETE("users/{id}")
    fun eliminarUsuario(@Path("id") id: Int): Call<Void>

    @GET("cart")
    fun getCart(@Query("user_id") userId: Int): Call<List<CartItem>>

    @POST("cart/add")
    fun addToCart(@Body request: AddToCartRequest): Call<CartResponse>

    @DELETE("cart/item/{itemId}")
    fun removeFromCart(@Path("itemId") itemId: Int): Call<CartResponse>

    @GET("admin/stats")
    fun getAdminStats(): Call<AdminStatsResponse>
}

data class AdminStatsResponse(
    val pendientes: Int,
    val ventas: Int,
    val stock_bajo: Int,
    val completados: Int,
    val pedidos_recientes: List<PedidoResumenResponse>? = null
)

data class PedidoResumenResponse(
    val id: Int,
    val cliente: String,
    val total: Double,
    val estado: String
)