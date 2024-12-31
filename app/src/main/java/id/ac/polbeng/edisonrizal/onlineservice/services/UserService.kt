package id.ac.polbeng.edisonrizal.onlineservice.services

import id.ac.polbeng.edisonrizal.onlineservice.models.DefaultResponse
import id.ac.polbeng.edisonrizal.onlineservice.models.LoginResponse
import id.ac.polbeng.edisonrizal.onlineservice.models.User
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path
import retrofit2.http.QueryMap

interface UserService {
    @GET("login")
    fun loginUser(
        @QueryMap filter: HashMap<String, String>
    ): Call<LoginResponse>

    @POST("users")
    fun registerUser(
        @Body newUser: User
    ): Call<DefaultResponse>

    @PUT("users")
    fun updateUser(
        @Body updatedUser: User
    ): Call<DefaultResponse>

    @DELETE("users/{id}")
    fun deleteUser(
        @Path("id") id: Int
    ): Call<DefaultResponse>

    @DELETE("services/{id}")
    fun deleteService(
        @Path("id") idJasa: Int
    ) : Call<DefaultResponse>
}