package id.ac.polbeng.edisonrizal.onlineservice.activities

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import androidx.appcompat.app.AppCompatActivity
import id.ac.polbeng.edisonrizal.onlineservice.databinding.ActivitySplashScreenBinding
import id.ac.polbeng.edisonrizal.onlineservice.helpers.Config

class SplashScreenActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySplashScreenBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySplashScreenBinding.inflate(layoutInflater)
        setContentView(binding.root)
        Handler(mainLooper).postDelayed({
            startActivity(Intent(this@SplashScreenActivity,
                LoginActivity::class.java))
            finish()
        }, Config.SPLASH_SCREEN_DELAY)
    }
}

