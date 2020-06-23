package com.example.todoandmemo

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import kotlinx.android.synthetic.main.activity_login.*

class LoginActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        loginButton.setOnClickListener {
            loginEmail()
        }

        GoSighUpActivityTextView.setOnClickListener {
            val intent = Intent(this, SignUpActivity::class.java)
            startActivity(intent)
            finish()
        }
    }

    private fun loginEmail() {
        val email = emailEditTextLogin.text.toString()
        val password = passwordEditTextLogin.text.toString()

        FirebaseAuth.getInstance().signInWithEmailAndPassword(email, password).addOnCompleteListener {
            if(it.isSuccessful) {
                moveNextPage()
                return@addOnCompleteListener
            }
        }
            .addOnFailureListener {
                Toast.makeText(this, "존재하지 않는 계정입니다.", Toast.LENGTH_LONG).show()
                Log.d("TAG", it.toString())
            }
    }

    private fun moveNextPage() {
        var currentUser = FirebaseAuth.getInstance().currentUser
        if(currentUser != null)
        {
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }
    }

    override fun onResume() {
        super.onResume()
        moveNextPage()
    }
}