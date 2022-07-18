package com.test.myapp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.test.myapp.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Example of a call to a native method
        binding.sampleText.text = managedFoo()
    }

    /**
     * A native method that is implemented by the 'myapp' native library,
     * which is packaged with this application.
     */
    private external fun managedFoo(): String

    companion object {
        // Used to load the 'myapp' library on application startup.
        init {
            System.loadLibrary("myapp")
        }
    }
}
