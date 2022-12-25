package com.vcs.svtt.cryptographygallery.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.hanks.passcodeview.PasscodeView
import com.vcs.svtt.cryptographygallery.R
import kotlinx.android.synthetic.main.activity_passcode.*

class PasscodeActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_passcode)

        val passcodeview: PasscodeView = findViewById(R.id.passcodeview)
        passcodeview.setPasscodeLength(4).setLocalPasscode("1234")
            .setListener(object: PasscodeView.PasscodeViewListener{
                override fun onFail() {
                    Toast.makeText(baseContext, "Password is wrong!", Toast.LENGTH_SHORT).show()
                }

                override fun onSuccess(number: String?) {
                    val intent = Intent(baseContext,MainActivity::class.java)
                    startActivity(intent)
                }
            })
    }
}