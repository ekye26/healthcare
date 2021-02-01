package com.example.mit_healthcare.mainhealthcare

import android.content.Intent
import android.os.Bundle
import android.os.StrictMode
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.example.mit_healthcare.R
import java.sql.DriverManager
import java.sql.PreparedStatement
import java.sql.SQLException


class Health_signUp : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.health_signup)


        val button1: Button = findViewById(R.id.signup_ok)
        val button2: Button = findViewById(R.id.id_check_btn)

        val sign_id: EditText = findViewById(R.id.sign_up_id)
        val sign_pw: EditText = findViewById(R.id.sign_up_pw)
        val sign_pw_check: EditText = findViewById(R.id.sign_up_pw_check)
        val sign_name: EditText = findViewById(R.id.name)
        val sign_birt: EditText = findViewById(R.id.birth)

//        val PW: String = sign_pw.text.toString()
//        val PW_CHECK = sign_pw_check.text.toString().trim()


        // 아이디 중복 체크 구현
        button2.setOnClickListener {
            val ID: String = sign_id.text.toString()
            id_check("$ID")
        }

        //postgreSQL로 입력값 전달
        button1.setOnClickListener {
//            val radioGroup : RadioGroup = findViewById(R.id.RadioGroup)
//            val male : RadioButton = findViewById(R.id.male)
//            val female : RadioButton = findViewById(R.id.female)

            val ID: String = sign_id.text.toString()
            val PW: String = sign_pw.text.toString()
            val PW_CHECK = sign_pw_check.text.toString()
            val NAME: String = sign_name.text.toString()
            val BIRTH: String = sign_birt.text.toString()
            //val GENDER : String =

            connect("$ID", "$PW", "$NAME", "$BIRTH")
        }
    }


    /** postgreSQL 연결 및 botton1 클릭 시 값 전달 */
    fun connect(ID: String, PW: String, NAME: String, BIRTH: String) {

        //이 부분 없으면 오류 이유 파익 x
        val policy = StrictMode.ThreadPolicy.Builder().permitAll().build()
        StrictMode.setThreadPolicy(policy)

        val jdbcURL = "jdbc:postgresql://localhost:5432/server" //서버 주소
        val username = "postgres" // 유저 이름
        val password = "150526" // 비번

        try {
            val connection = DriverManager.getConnection(jdbcURL, username, password) //연결한다,
            println("Connected to PostgreSQL server")

            /** 입력 */
            // 쿼리에 입력한다.
            var sql = "INSERT INTO register (아이디, 패스워드, 이름, 생년월일)" + " VALUES (?,?,?,?)"

            val statement: PreparedStatement = connection.prepareStatement(sql)

            // 이 값을 테이블에 넣음
            statement.setString(1, "$ID")
            statement.setString(2, "$PW")
            statement.setString(3, "$NAME")
            statement.setString(4, "$BIRTH")

            val rows = statement.executeUpdate()


            if (rows > 0) {
                println("A new contact has been inserted.")
                val intent = Intent(this, Health_data::class.java)
                Toast.makeText(this, " 회원가입 완료입니다.", Toast.LENGTH_SHORT).show()
                startActivity(intent)
            }
            connection.close()

        } catch (e: SQLException) {
            println("Error in connected to PostgreSQL server")
            e.printStackTrace()
            Toast.makeText(this, " 회원가입 실패입니다.", Toast.LENGTH_SHORT).show()
        }
    }

    fun id_check (ID : String) {

        val policy = StrictMode.ThreadPolicy.Builder().permitAll().build()
        StrictMode.setThreadPolicy(policy)

        val jdbcURL = "jdbc:postgresql://lcoalhost:5432/server" //서버 주소
        val username = "postgres" // 유저 이름
        val password = "150526" // 비번

        try {
            val connection = DriverManager.getConnection(jdbcURL, username, password) //연결한다,
            println("Connected to PostgreSQL server")

            /** 입력 */
            // 쿼리에 입력한다.
            var sql = "SELECT EXISTS (SELECT * FROM register WHERE 아이디 = '$ID') AS success;"
            val statement = connection.createStatement()
            val result = statement.executeQuery(sql)

            while (result.next()) {
                val output = result.getBoolean("success")

                if (output == true) {
                    Toast.makeText(this, "아이디가 중복됩니다.", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(this, "아이디 사용이 가능합니다.", Toast.LENGTH_SHORT).show()
                }
            }
            connection.close()
        } catch (e: SQLException) {
            println("Error in connected to PostgreSQL server")
            e.printStackTrace()
        }

    }
}