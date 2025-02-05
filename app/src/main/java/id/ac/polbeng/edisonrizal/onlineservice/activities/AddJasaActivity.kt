package id.ac.polbeng.edisonrizal.onlineservice.activities

import android.net.Uri
import android.os.Bundle
import android.text.TextUtils
import android.view.View
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.bumptech.glide.Glide
import com.github.dhaval2404.imagepicker.ImagePicker
import id.ac.polbeng.edisonrizal.onlineservice.R
import id.ac.polbeng.edisonrizal.onlineservice.databinding.ActivityAddJasaBinding
import id.ac.polbeng.edisonrizal.onlineservice.helpers.SessionHandler
import id.ac.polbeng.edisonrizal.onlineservice.models.DefaultResponse
import id.ac.polbeng.edisonrizal.onlineservice.models.User
import id.ac.polbeng.edisonrizal.onlineservice.services.JasaService
import id.ac.polbeng.edisonrizal.onlineservice.services.ServiceBuilder
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import pub.devrel.easypermissions.EasyPermissions
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File

class AddJasaActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAddJasaBinding
    private var imageFile: MultipartBody.Part? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddJasaBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val sessionHandler = SessionHandler(applicationContext)
        val currentUser: User? = sessionHandler.getUser()

        binding.btnCari.setOnClickListener {
            ImagePicker.with(this)
                .compress(1024)
                .maxResultSize(1080, 1080)
                .createIntent { intent -> imagePickerResult.launch(intent) }
        }

        binding.btnAddJasa.setOnClickListener {
            val namaJasa = binding.etNamaJasa.text.toString()
            val deskripsiSingkat = binding.etDeskripsiSingkat.text.toString()
            val uraianDeskripsi = binding.etUraianDeskripsi.text.toString()
            val rating = binding.tvRating.text.toString()

            if (validasiInput(namaJasa, deskripsiSingkat, uraianDeskripsi, rating)) {
                val jasaService = ServiceBuilder.buildService(JasaService::class.java)

                val namaJasaBody = namaJasa.toRequestBody("multipart/form-data".toMediaTypeOrNull())
                val deskripsiSingkatBody = deskripsiSingkat.toRequestBody("multipart/form-data".toMediaTypeOrNull())
                val uraianDeskripsiBody = uraianDeskripsi.toRequestBody("multipart/form-data".toMediaTypeOrNull())
                val ratingBody = rating.toRequestBody("multipart/form-data".toMediaTypeOrNull())
                val userIdBody = currentUser?.id.toString().toRequestBody("multipart/form-data".toMediaTypeOrNull())

                if (imageFile == null) {
                    Toast.makeText(this, "Pilih gambar terlebih dahulu!", Toast.LENGTH_SHORT).show()
                    return@setOnClickListener
                }

                val requestCall = jasaService.addJasa(
                    imageFile!!,
                    userIdBody,
                    namaJasaBody,
                    deskripsiSingkatBody,
                    uraianDeskripsiBody,
                    ratingBody
                )

                requestCall.enqueue(object : Callback<DefaultResponse> {
                    override fun onResponse(call: Call<DefaultResponse>, response: Response<DefaultResponse>) {
                        if (response.body()?.error == false) {
                            Toast.makeText(this@AddJasaActivity, "Jasa berhasil ditambahkan!", Toast.LENGTH_SHORT).show()
                            finish()
                        } else {
                            Toast.makeText(
                                this@AddJasaActivity,
                                "Gagal menambahkan jasa: ${response.body()?.message}",
                                Toast.LENGTH_LONG
                            ).show()
                        }
                    }

                    override fun onFailure(call: Call<DefaultResponse>, t: Throwable) {
                        Toast.makeText(this@AddJasaActivity, "Terjadi kesalahan: ${t.message}", Toast.LENGTH_LONG).show()
                    }
                })
            }
        }
    }

    private val imagePickerResult =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == RESULT_OK && result.data != null) {
                val imageUri: Uri? = result.data?.data
                Glide.with(this).load(imageUri).into(binding.imgJasa)

                val file = File(imageUri?.path ?: "")
                val requestBody = file.asRequestBody("multipart/form-data".toMediaTypeOrNull())
                imageFile = MultipartBody.Part.createFormData("file", file.name, requestBody)
            } else if (result.resultCode == ImagePicker.RESULT_ERROR) {
                Toast.makeText(this, ImagePicker.getError(result.data), Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "Proses dibatalkan", Toast.LENGTH_SHORT).show()
            }
        }

    private fun validasiInput(
        namaJasa: String,
        deskripsiSingkat: String,
        uraianDeskripsi: String,
        rating: String
    ): Boolean {
        return when {
            namaJasa.isEmpty() -> {
                binding.etNamaJasa.error = "Nama jasa tidak boleh kosong!"
                false
            }
            deskripsiSingkat.isEmpty() -> {
                binding.etDeskripsiSingkat.error = "Deskripsi singkat tidak boleh kosong!"
                false
            }
            uraianDeskripsi.isEmpty() -> {
                binding.etUraianDeskripsi.error = "Uraian deskripsi tidak boleh kosong!"
                false
            }
            rating.isEmpty() -> {
                Toast.makeText(this, "Rating tidak boleh kosong!", Toast.LENGTH_SHORT).show()
                false
            }
            imageFile == null -> {
                Toast.makeText(this, "Pilih gambar terlebih dahulu!", Toast.LENGTH_SHORT).show()
                false
            }
            else -> true
        }
    }
}