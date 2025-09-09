package com.dm.password_generator

import android.content.ClipData
import android.content.ClipboardManager
import android.os.Build
import android.os.Bundle
import android.widget.SeekBar
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.dm.password_generator.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            window.statusBarColor = ContextCompat.getColor(this, R.color.colorPrimary)
        }

        binding.seekBar.max = 30
        binding.seekBar.progress = 10

        binding.password.text = getString(R.string.length_label, binding.seekBar.progress)

        binding.seekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                binding.password.text = getString(R.string.length_label, progress)
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {}
            override fun onStopTrackingTouch(seekBar: SeekBar?) {}
        })

        binding.generate.setOnClickListener {
            val length = binding.seekBar.progress
            val includeUppercase = binding.uppercase.isChecked
            val includeNumbers = binding.numbers.isChecked
            val includeSpecialChars = binding.specialCharacter.isChecked
            val excludeSimilarChars = binding.excludeSimilar.isChecked

            val password = generatePassword(
                length,
                includeUppercase,
                includeNumbers,
                includeSpecialChars,
                excludeSimilarChars
            )
            binding.password.text = password
        }

        binding.copy.setOnClickListener {
            val password = binding.password.text.toString()
            copyToClipboard(password)
            Toast.makeText(this, "Senha copiada para a área de transferência", Toast.LENGTH_SHORT).show()
        }
    }

    private fun generatePassword(
        length: Int,
        includeUppercase: Boolean,
        includeNumbers: Boolean,
        includeSpecialChars: Boolean,
        excludeSimilarChars: Boolean
    ): String {
        val lowercaseChars = "abcdefghijklmnopqrstuvwxyz"
        val uppercaseChars = "ABCDEFGHIJKLMNOPQRSTUVWXYZ"
        val numberChars = "0123456789"
        val specialChars = "!@#\$%^&*()-_=+[]{}|;:'\",.<>?/`~"
        val similarChars = "il1Lo0O"

        var charPool = buildString {
            append(lowercaseChars)
            if (includeUppercase) append(uppercaseChars)
            if (includeNumbers) append(numberChars)
            if (includeSpecialChars) append(specialChars)
        }

        if (excludeSimilarChars) {
            charPool = charPool.filterNot { it in similarChars }
        }

        return (1..length).map { charPool.random() }.joinToString("")
    }

    private fun copyToClipboard(text: String) {
        val clipboard = getSystemService(CLIPBOARD_SERVICE) as ClipboardManager
        val clip = ClipData.newPlainText("password", text)
        clipboard.setPrimaryClip(clip)
    }
}