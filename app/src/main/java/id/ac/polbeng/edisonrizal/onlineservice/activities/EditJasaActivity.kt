package id.ac.polbeng.edisonrizal.onlineservice.activities

import android.Manifest
import android.net.Uri
import android.os.Bundle
import android.text.TextUtils
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.github.dhaval2404.imagepicker.ImagePicker
import id.ac.polbeng.edisonrizal.onlineservice.R
import id.ac.polbeng.edisonrizal.onlineservice.databinding.ActivityEditJasaBinding
import id.ac.polbeng.edisonrizal.onlineservice.helpers.Config
import id.ac.polbeng.edisonrizal.onlineservice.helpers.Config.Companion.EXTRA_JASA
import id.ac.polbeng.edisonrizal.onlineservice.models.DefaultResponse
import id.ac.polbeng.edisonrizal.onlineservice.models.Jasa
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

class EditJasaActivity : AppCompatActivity() {
    private lateinit var binding: ActivityEditJasaBinding
    private var imageUri: Uri? = null
    private lateinit var imageFile: MultipartBody.Part

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEditJasaBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val receiveJasa = intent.getSerializableExtra(EXTRA_JASA) as? Jasa
        if (receiveJasa != null) {
            binding.etNamaJasa.setText(receiveJasa.namaJasa)
            binding.etDeskripsiSingkat.setText(receiveJasa.deskripsiSingkat)
            binding.etUraianDeskripsi.setText(receiveJasa.uraianDeskripsi)
            binding.tvRating.text = receiveJasa.rating.toString()
            binding.tvImage.text = receiveJasa.gambar
            Glide.with(this)
                .load(Config.IMAGE_URL + receiveJasa.gambar)
                .error(R.drawable.baseline_broken_image_24)
                .into(binding.imgJasa)
        }

        // Listener untuk tombol "Ubah" (btnUbah)
        binding.btnUbah.setOnClickListener {
            if (EasyPermissions.hasPermissions(
                    this,
                    android.Manifest.permission.READ_EXTERNAL_STORAGE
                )
            ) {
                ImagePicker.with(this)
                    .compress(512)
                    .maxResultSize(540, 540)
                    .createIntent { intent ->
                        startForProfileImageResult.launch(intent)
                    }
            } else {
                EasyPermissions.requestPermissions(
                    this,
                    "This application needs your permission to access the photo gallery.",
                    100,
                    android.Manifest.permission.READ_EXTERNAL_STORAGE
                )
            }
        }

        // Listener untuk tombol "Edit Jasa" (btnEditJasa)
        binding.btnEditJasa.setOnClickListener {
            val idJasa = receiveJasa?.idJasa.toString()
            val namaJasa = binding.etNamaJasa.text.toString()
            val deskripsiSingkat = binding.etDeskripsiSingkat.text.toString()
            val uraianDeskripsi = binding.etUraianDeskripsi.text.toString()

            if (TextUtils.isEmpty(namaJasa)) {
                binding.etNamaJasa.error = "Nama jasa tidak boleh kosong!"
                binding.etNamaJasa.requestFocus()
                return@setOnClickListener
            }
            if (TextUtils.isEmpty(deskripsiSingkat)) {
                binding.etDeskripsiSingkat.error = "Deskripsi singkat tidak boleh kosong!"
                binding.etDeskripsiSingkat.requestFocus()
                return@setOnClickListener
            }
            if (TextUtils.isEmpty(uraianDeskripsi)) {
                binding.etUraianDeskripsi.error = "Uraian deskripsi tidak boleh kosong!"
                binding.etUraianDeskripsi.requestFocus()
                return@setOnClickListener
            }

            val jasaService: JasaService = ServiceBuilder.buildService(JasaService::class.java)
            val requestCall: Call<DefaultResponse> = jasaService.editJasa(
                idJasa.toInt(),
                namaJasa,
                deskripsiSingkat,
                uraianDeskripsi
            )

            showLoading(true)
            requestCall.enqueue(object : retrofit2.Callback<DefaultResponse> {
                override fun onFailure(call: Call<DefaultResponse>, t: Throwable) {
                    showLoading(false)
                    Toast.makeText(
                        this@EditJasaActivity,
                        "Error terjadi: ${t.message}",
                        Toast.LENGTH_LONG
                    ).show()
                }

                override fun onResponse(
                    call: Call<DefaultResponse>,
                    response: Response<DefaultResponse>
                ) {
                    showLoading(false)
                    if (response.body()?.error == false) {
                        Toast.makeText(
                            this@EditJasaActivity,
                            response.body()?.message,
                            Toast.LENGTH_LONG
                        ).show()
                        finish()
                    } else {
                        Toast.makeText(
                            this@EditJasaActivity,
                            "Gagal: ${response.body()?.message}",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                }
            })
        }

        // Listener untuk tombol "Hapus Jasa" (btnHapusJasa)
        binding.btnHapusJasa.setOnClickListener {
            val builder = AlertDialog.Builder(this)
            builder.setTitle("Hapus Jasa")
            builder.setMessage("Apakah anda yakin menghapus jasa?\n${receiveJasa?.namaJasa}")
            builder.setIcon(R.drawable.baseline_delete_forever_24)
            builder.setPositiveButton("Ya") { dialog, _ ->
                val jasaService: JasaService = ServiceBuilder.buildService(JasaService::class.java)
                val requestCall: Call<DefaultResponse> =
                    jasaService.deleteJasa(receiveJasa?.idJasa!!)//Disini saya merubah dari deletejasa menjadi deleteservice
                requestCall.enqueue(object : retrofit2.Callback<DefaultResponse> {
                    override fun onFailure(call: Call<DefaultResponse>, t: Throwable) {
                        Toast.makeText(
                            this@EditJasaActivity,
                            "Error terjadi ketika sedang menghapus jasa: $t",
                            Toast.LENGTH_LONG
                        ).show()
                    }

                    override fun onResponse(
                        call: Call<DefaultResponse>,
                        response: Response<DefaultResponse>
                    ) {
                        if (!response.body()?.error!!) {
                            val defaultResponse: DefaultResponse = response.body()!!
                            defaultResponse.let {
                                Toast.makeText(
                                    this@EditJasaActivity,
                                    defaultResponse.message,
                                    Toast.LENGTH_LONG
                                ).show()
                                finish()
                            }
                        } else {
                            Toast.makeText(
                                this@EditJasaActivity,
                                "Gagal menghapus jasa: ${response.body()?.message}",
                                Toast.LENGTH_LONG
                            ).show()
                        }
                    }
                })
                dialog.dismiss()
            }
            builder.setNegativeButton("Tidak") { dialog, _ ->
                dialog.dismiss()
            }
            val alertDialog: AlertDialog = builder.create()
            alertDialog.show()
        }
    }

    private val startForProfileImageResult =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            val resultCode = it.resultCode
            val data = it.data
            if (resultCode == RESULT_OK && data != null) {
                imageUri = data.data
                Glide.with(this)
                    .load(imageUri)
                    .into(binding.imgJasa)
                binding.tvImage.text = imageUri?.lastPathSegment
                val file = File(imageUri?.path)
                val requestBody = file.asRequestBody("multipart/form-data".toMediaTypeOrNull())
                imageFile = MultipartBody.Part.createFormData("file", file.name, requestBody)
            } else if (resultCode == ImagePicker.RESULT_ERROR) {
                Toast.makeText(this, ImagePicker.getError(data), Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "Task Cancelled", Toast.LENGTH_SHORT).show()
            }
        }

    private fun showLoading(isLoading: Boolean) {
        binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
    }

    override fun onSupportNavigateUp(): Boolean {
        finish()
        return true
    }
}