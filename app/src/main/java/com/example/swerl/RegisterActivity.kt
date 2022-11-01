package com.example.swerl

import android.content.ContentValues.TAG
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.Toast
import androidx.core.widget.doOnTextChanged
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import com.example.swerl.databinding.ActivityRegisterBinding
import com.example.swerl.ui.login.LoginActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.activity_login.*

class RegisterActivity : AppCompatActivity() {
    private lateinit var binding: ActivityRegisterBinding
    private  lateinit var  auth: FirebaseAuth
    private  val emailLiveData=MutableLiveData<String>()
    private val usernameLiveData=MutableLiveData<String>()
    private val passwordLiveData=MutableLiveData<String>()
    private  val confirmPasswordLiveData=MutableLiveData<String>()
    private  val isValidLiveData=MediatorLiveData<Boolean>().apply {

        this.value=false

        addSource(emailLiveData){ email ->
            val password=passwordLiveData.value
            val confirmPassword=confirmPasswordLiveData.value
            this.value=validateForm(email,password,confirmPassword)
        }
        addSource(passwordLiveData ){ password->
            val email=emailLiveData.value
            val confirmPassword=confirmPasswordLiveData.value
            this.value=validateForm(email,password,confirmPassword)
        }
        addSource(confirmPasswordLiveData ){ confirmPassword->
            val email=emailLiveData.value
            val password=passwordLiveData.value
            this.value=validateForm(email,password,confirmPassword)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
binding= ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)
        /*Initialize firebase authentication*/
auth=Firebase.auth
        /*Buttons*/
        val haveAccount=findViewById<Button>(R.id.haveAccount)
        val signUpButton=findViewById<Button>(R.id.signUp)
       /*Edittext*/
        val emailLayout=findViewById<EditText>(R.id.EmailAddress)
        val userNameLayout=findViewById<EditText>(R.id.username)
        val passwordLayout=findViewById<EditText>(R.id.password)
        val confirmPasswordLayout=findViewById<EditText>(R.id.confirm_Password)
/*progress bar*/
val loading=findViewById<ProgressBar>(R.id.loading)
        emailLayout.doOnTextChanged { text, _, _, _ ->
                emailLiveData.value=text.toString()
            }
        userNameLayout.doOnTextChanged { text, _, _, _ ->
            usernameLiveData.value=text.toString()
        }
        passwordLayout.doOnTextChanged { text, _, _, _ ->
            passwordLiveData.value=text.toString()
        }
        confirmPasswordLayout.doOnTextChanged { text, _, _, _ ->
           confirmPasswordLiveData.value=text.toString()
        }

        isValidLiveData.observe(this){isValid->
            signUpButton.isEnabled=isValid
        }

                signUpButton.setOnClickListener{
loading.visibility=View.VISIBLE
            createAccount(emailLayout.text.toString(),passwordLayout.text.toString())
        }
        haveAccount.setOnClickListener{
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
        }


    }

    private fun validateForm(email:String?,password:String?,confirmPassword:String?):Boolean{
        val isValidEmail=email!=null && email.isNotBlank() && email.contains("@")
        val isValidPassword=password!=null && password.isNotBlank()&& password.length>=6
        val isValidConfirmPassord=confirmPassword!=null&&confirmPassword.isNotBlank() && confirmPassword==password


        return isValidEmail && isValidPassword && isValidConfirmPassord
    }
    private fun createAccount(email: String, password: String) {
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    loading.visibility=View.INVISIBLE
                    val user = auth.currentUser
                    Log.d(TAG, "Welcome ")

                    val intent = Intent(this, MainActivity::class.java)
                    startActivity(intent)
                } else {
                    // If sign in fails, display a message to the user.
                    Log.w(TAG, "createUserWithEmail:failure", task.exception)
                    loading.visibility=View.INVISIBLE
                    Toast.makeText(baseContext, "Verification Error.",
                        Toast.LENGTH_SHORT).show()
                }
            }
    }

}