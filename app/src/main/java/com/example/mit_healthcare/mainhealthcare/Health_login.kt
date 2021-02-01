package com.example.mit_healthcare.mainhealthcare

import android.content.Intent
import android.os.Bundle
import android.os.StrictMode
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.mit_healthcare.R
import java.sql.DriverManager
import java.sql.SQLException


class Health_login : AppCompatActivity() {
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
    }

    fun login(ID : String, PW : String) {

        val policy = StrictMode.ThreadPolicy.Builder().permitAll().build()
        StrictMode.setThreadPolicy(policy)

        val jdbcURL = "jdbc:postgresql://localhost:5432/server"
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
}
