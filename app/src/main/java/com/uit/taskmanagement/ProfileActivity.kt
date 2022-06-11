package com.uit.taskmanagement

import android.content.ContentValues
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Html
import android.util.Log
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.activity_create_card.*
import kotlinx.android.synthetic.main.activity_profile.*
import kotlinx.android.synthetic.main.activity_update_card.*

class ProfileActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)
        var database: DatabaseReference = FirebaseDatabase.getInstance().reference
        val intentValue = intent
        val dataIdOfUser = intentValue.getStringExtra("dataIdOfUser")
        val dataDate = intentValue.getStringExtra("dataDate")

        // Truyền giá trị
        database.child("accounts").child("user"+dataIdOfUser.toString()).addValueEventListener(object :
            ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                txtNameProfile.text = dataSnapshot.child("name").value.toString()
                txtEmailProfile.text = dataSnapshot.child("email").value.toString()
            }
            override fun onCancelled(databaseError: DatabaseError) {
                // Getting Post failed, log a message
                Log.w(ContentValues.TAG, "loadPost:onCancelled", databaseError.toException())
                // ...
            }
        })
        // Truy cap Dang nhap activity
        btnLogout.setOnClickListener {
            val intent1 = Intent(this@ProfileActivity, MainActivity::class.java)
            startActivity(intent1)
        }
        // Truy cap Dashboard activity
        tvProfile.setOnClickListener {
            val intent2 = Intent(this@ProfileActivity, DashboardActivity::class.java)
            intent2.putExtra("dataIdOfUser", dataIdOfUser)
            intent2.putExtra("dataDate", dataDate)
            startActivity(intent2)
        }

        tvProfile.text = Html.fromHtml(" ‹ Profile") // Thay đổi TextView Profile
    }
}