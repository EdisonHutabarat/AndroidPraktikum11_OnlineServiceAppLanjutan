package id.ac.polbeng.edisonrizal.onlineservice.fragments

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import id.ac.polbeng.edisonrizal.onlineservice.R
import id.ac.polbeng.edisonrizal.onlineservice.activities.EditProfileActivity
import id.ac.polbeng.edisonrizal.onlineservice.activities.LoginActivity
import id.ac.polbeng.edisonrizal.onlineservice.databinding.FragmentProfileBinding
import id.ac.polbeng.edisonrizal.onlineservice.helpers.Config
import id.ac.polbeng.edisonrizal.onlineservice.helpers.SessionHandler
import id.ac.polbeng.edisonrizal.onlineservice.models.DefaultResponse
import id.ac.polbeng.edisonrizal.onlineservice.models.User
import id.ac.polbeng.edisonrizal.onlineservice.services.ServiceBuilder
import id.ac.polbeng.edisonrizal.onlineservice.services.UserService
import id.ac.polbeng.edisonrizal.onlineservice.viewmodel.ProfileViewModel
import retrofit2.Call
import retrofit2.Response


class ProfileFragment : Fragment() {
    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentProfileBinding.inflate(inflater, container, false)
        val root: View = binding.root

        val session = SessionHandler(requireContext())
        val user: User? = session.getUser()
        val titikDua = ": "

        if (user != null) {
            val url = Config.PROFILE_IMAGE_URL + user.gambar
            Glide.with(requireContext())
                .load(url)
                .apply(RequestOptions().placeholder(R.drawable.user).error(R.drawable.user))
                .into(binding.imgLogo)

            binding.tvNama.text = titikDua + user.nama
            binding.tvTanggalLahir.text = titikDua + user.tanggalLahir
            binding.tvJenisKelamin.text = titikDua + user.jenisKelamin
            binding.tvNomorHP.text = titikDua + user.nomorHP
            binding.tvAlamat.text = titikDua + user.alamat
            binding.tvEmail.text = titikDua + user.email
            binding.tvWaktuSesi.text = titikDua + session.getExpiredTime()

            binding.btnEditProfil.setOnClickListener {
                startActivity(Intent(context, EditProfileActivity::class.java))
            }

            binding.btnHapusUser.setOnClickListener {
                val builder = AlertDialog.Builder(requireContext())
                builder.setTitle("Hapus Akun")
                    .setMessage("Apakah anda yakin menghapus akun? Anda tidak akan bisa lagi login ke akun.")
                    .setIcon(R.drawable.baseline_delete_forever_24)
                    .setPositiveButton("Ya") { dialog, _ ->
                        val userService: UserService =
                            ServiceBuilder.buildService(UserService::class.java)
                        val userId = user.id ?: return@setPositiveButton
                        val requestCall: Call<DefaultResponse> = userService.deleteUser(userId)
                        showLoading(true)
                        requestCall.enqueue(object : retrofit2.Callback<DefaultResponse> {
                            override fun onFailure(call: Call<DefaultResponse>, t: Throwable) {
                                showLoading(false)
                                Toast.makeText(context, "Error: $t", Toast.LENGTH_LONG).show()
                            }

                            override fun onResponse(
                                call: Call<DefaultResponse>,
                                response: Response<DefaultResponse>
                            ) {
                                showLoading(false)
                                if (response.body()?.error == false) {
                                    session.removeUser()
                                    Toast.makeText(context, response.body()?.message, Toast.LENGTH_LONG).show()
                                    startActivity(Intent(context, LoginActivity::class.java).apply {
                                        flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                                    })
                                } else {
                                    Toast.makeText(context, "Gagal: ${response.body()?.message}", Toast.LENGTH_LONG).show()
                                }
                            }
                        })
                        dialog.dismiss()
                    }
                    .setNegativeButton("Tidak") { dialog, _ -> dialog.dismiss() }
                builder.create().show()
            }
        }

        return root
    }

    private fun showLoading(isLoading: Boolean) {
        binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
