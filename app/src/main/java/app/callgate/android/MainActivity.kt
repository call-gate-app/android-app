package app.callgate.android

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import app.callgate.android.databinding.ActivityMainBinding
import app.callgate.android.ui.HomeFragment

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportFragmentManager
            .beginTransaction()
            .replace(R.id.container, HomeFragment())
            .commit()
    }
}