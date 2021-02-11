package com.example.mit_healthcare.mainhealthcare

import android.content.Intent
import android.os.Bundle
import android.os.StrictMode
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.mit_healthcare.R
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.SignInButton
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import java.sql.DriverManager
import java.sql.SQLException


class Health_login : AppCompatActivity() {

    var auth: FirebaseAuth? = null
    val GOOGLE_REQUEST_CODE = 99
    val TAG = "googleLogin"
    private lateinit var googleSignInClient: GoogleSignInClient


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.health_login)

        val button1 : Button = findViewById<Button>(R.id.login_btn)
        val button2 : Button = findViewById(R.id.sign_up_btn)
        val login_id : EditText = findViewById(R.id.login_id)
        val login_pw: EditText = findViewById(R.id.login_pw)

        button2.setOnClickListener {
            val nextsignup = Intent(this, Health_signUp::class.java)
            startActivity(nextsignup)
        }

        button1.setOnClickListener {
            val PW = login_pw.text.toString()
            val ID = login_id.text.toString()
            login("$ID", "$PW")

        }

        //구글연동
        auth = FirebaseAuth.getInstance()
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()
        googleSignInClient = GoogleSignIn.getClient(this,gso)

        val google_btn : SignInButton = findViewById(R.id.google_btn)
        google_btn.setOnClickListener { signIn() }
        // 여기까지

    }

    private fun login(ID : String, PW : String) {

        val policy = StrictMode.ThreadPolicy.Builder().permitAll().build()
        StrictMode.setThreadPolicy(policy)

        val jdbcURL = "jdbc:postgresql://:5432/server"
        val username = "postgres"
        val password = "150526"

        try {
            val connection = DriverManager.getConnection(jdbcURL, username, password)
            println("Connected to PostgreSQL server")
            val sql = "SELECT 패스워드 FROM register WHERE 아이디 = '$ID'"
            val statement = connection.createStatement()
            val result = statement.executeQuery(sql)

            while (result.next()) {
                val password = result.getString("패스워드")
                System.out.print("패스워드 : $password")

                if (PW == password) {
                    val login_next = Intent(this, Health_data::class.java)
                    Toast.makeText(this, " 로그인 완료입니다.", Toast.LENGTH_SHORT).show()
                    startActivity(login_next)
                } else {
                    Toast.makeText(this, " 로그인 실패했습니다.", Toast.LENGTH_SHORT).show()
                }
            }
            connection.close()
        } catch (e: SQLException) {
            println("Error in connected to PostgreSQL server")
            e.printStackTrace()
        }
    }

    //구글 연동
    private fun signIn() {
        val signInIntent = googleSignInClient.signInIntent
        startActivityForResult(signInIntent, GOOGLE_REQUEST_CODE)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == GOOGLE_REQUEST_CODE) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            try {
                // Google Sign In was successful, authenticate with Firebase
                val account = task.getResult(ApiException::class.java)!!
                Log.d(TAG, "firebaseAuthWithGoogle:" + account.id)
                firebaseAuthWithGoogle(account.idToken!!)
            } catch (e: ApiException) {
                // Google Sign In failed, update UI appropriately
                Log.w(TAG, "Google sign in failed", e)
                Toast.makeText(this, "로그인 실패", Toast.LENGTH_SHORT).show()
            }
        }
    }
    private fun firebaseAuthWithGoogle(idToken: String) {
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        auth?.signInWithCredential(credential)
            ?.addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    Log.d(TAG, "로그인 성공")
                    val user = auth!!.currentUser
                    loginSuccess()
                } else {
                    // If sign in fails, display a message to the user.
                    Log.w(TAG, "signInWithCredential:failure", task.exception)
                }
            }
    }
    private fun loginSuccess(){
        val intent = Intent(this, Health_data::class.java)
        Toast.makeText(this, "로그인을 성공했습니다.", Toast.LENGTH_SHORT).show()
        startActivity(intent)
        finish()
    }
    // 여기까지
}
