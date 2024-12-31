package id.ac.polbeng.edisonrizal.onlineservice.services

import id.ac.polbeng.edisonrizal.onlineservice.models.DefaultResponse
import id.ac.polbeng.edisonrizal.onlineservice.models.JasaResponse
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.http.DELETE
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Part
import retrofit2.http.Path

interface JasaService {
    @GET("services")
    fun getJasa() : Call<JasaResponse>

    @GET("userServices/{id}")
    fun getJasaUser(
        @Path("id") id: Int
    ) : Call<JasaResponse>

    @Multipart
    @POST("services")
    fun addJasa(
        @Part image: MultipartBody.Part,
        @Part("id_user") idUser: RequestBody,
        @Part("nama_jasa") namaJasa: RequestBody,
        @Part("deskripsi_singkat") deskripsiSingkat: RequestBody,
        @Part("uraian_deskripsi") uraianDeskripsi: RequestBody,
        @Part("rating") rating: RequestBody,
    ) : Call<DefaultResponse>

    @Multipart
    @PUT("services")
    fun editJasaReplaceImage(
        @Part image: MultipartBody.Part,
        @Part("id") idJasa: RequestBody,
        @Part("nama_jasa") namaJasa: RequestBody,
        @Part("deskripsi_singkat") deskripsiSingkat: RequestBody,
        @Part("uraian_deskripsi") uraianDeskripsi: RequestBody,
        @Part("file") gambar: RequestBody
    ) : Call<DefaultResponse>
    @FormUrlEncoded

    @PUT("services/{id}")
    fun editJasa(
        @Path("id") idJasa: Int,
        @Field("nama_jasa") namaJasa: String,
        @Field("deskripsi_singkat") deskripsiSingkat: String,
        @Field("uraian_deskripsi") uraianDeskripsi: String
    ): Call<DefaultResponse>

    @DELETE("services/{id}")
    fun deleteJasa(
        @Path("id") idJasa: Int
    ): Call<DefaultResponse>
}