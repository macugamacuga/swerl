package com.example.swerl.ui.login

import android.content.ContentValues.TAG
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.doOnTextChanged
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import com.example.swerl.MainActivity
import com.example.swerl.databinding.ActivityLoginBinding

import com.example.swerl.R
import com.example.swerl.RegisterActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthException
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.ktx.auth
import com.google.firebase.auth.ktx.oAuthCredential
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.activity_login.*
import kotlinx.android.synthetic.main.activity_login.view.*

class LoginActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLoginBinding
    private  lateinit var  auth: FirebaseAuth
    private  val emailLiveData= MutableLiveData<String>()
    private val passwordLiveData= MutableLiveData<String>()
    private  val isValidLiveData= MediatorLiveData<Boolean>().apply {

        this.value=false

        addSource(emailLiveData){ email ->
            val password=passwordLiveData.value
            this.value=validateForm(email,password)
        }
        addSource(passwordLiveData ){ password->
            val email=emailLiveData.value
            this.value=validateForm(email,password)
        }

    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        auth = Firebase.auth
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val username = binding.username
        val password = binding.password
        val login = binding.login
        val loading = binding.loading
        val errorMessages=binding.errorMessage
        username.doOnTextChanged { text, _, _, _ ->
            emailLiveData.value=text.toString()
        }
        password.doOnTextChanged { text, _, _, _ ->
            passwordLiveData.value=text.toString()
        }
        isValidLiveData.observe(this){isValid->
            login.isEnabled=isValid
        }

        binding.RegisterPage?.setOnClickListener{
            val intent = Intent(this, RegisterActivity::class.java)
            startActivity(intent)}
            login.setOnClickListener{
                loading.visibility=View.VISIBLE
                logIn(username.text.toString(),password.text.toString())


            }

        fun onStart() {
            super.onStart()
            var currentUser=auth.currentUser
            if(currentUser!=null){
                setContentView(R.layout.activity_main)
            }



        }

}
    private fun validateForm(email:String?,password:String?):Boolean{
        val isValidEmail=email!=null && email.isNotBlank() && email.contains("@")
        val isValidPassword=password!=null && password.isNotBlank()&& password.length>=6



        return isValidEmail && isValidPassword
    }
private fun logIn(email: String,password: String){

        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    Log.d(TAG, "signInWithEmail:success")
                    val user = auth.currentUser
                    loading.visibility=View.INVISIBLE
                    Toast.makeText(baseContext, "Welcome ",
                        Toast.LENGTH_SHORT).show()
                    val intent = Intent(this,   MainActivity ::class.java)
                    startActivity(intent)
                } else {
                    // If sign in fails, display a message to the user.
                    Log.w(TAG, "signInWithEmail:failure", task.exception)

                    loading.visibility=View.INVISIBLE
                    error_message.text=""
                    error_message.text= task.exception?.message.toString()
                    Toast.makeText(baseContext, "Validation Error.",
                        Toast.LENGTH_SHORT).show()

                }
            }
    }
}
