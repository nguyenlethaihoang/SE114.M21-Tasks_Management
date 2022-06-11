package com.uit.taskmanagement

import android.app.AlertDialog
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

class UpdateCardActivity : AppCompatActivity() {
    var database: DatabaseReference = FirebaseDatabase.getInstance().reference
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_update_card)

        val intentValue = intent
        val idUser = intentValue.getStringExtra("idUser")
        val idTask = intentValue.getStringExtra("idTask")
        val dataDate = intentValue.getStringExtra("dataDate")

        // Truyền giá trị
        database.child("tasks").child(idUser.toString()).child(idTask.toString()).addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                txtTitleUpdate.setText(dataSnapshot.child("title").value.toString())
                txtPriorityUpdate.text = dataSnapshot.child("priority").value.toString()
                tvStatusUpdate.text = dataSnapshot.child("done").value.toString()
            }
            override fun onCancelled(databaseError: DatabaseError) {
                // Getting Post failed, log a message
                Log.w(ContentValues.TAG, "loadPost:onCancelled", databaseError.toException())
                // ...
            }
        })

        btnLow_update.setOnClickListener {
            txtPriorityUpdate.text = "LOW"
        }
        btnMedium_update.setOnClickListener {
            txtPriorityUpdate.text = "MEDIUM"
        }
        btnHigh_update.setOnClickListener {
            txtPriorityUpdate.text = "HIGH"
        }
        btnYes.setOnClickListener {
            tvStatusUpdate.text = "YES"
        }
        btnNo.setOnClickListener {
            tvStatusUpdate.text = "NO"
        }
        btnDoneUpdate.setOnClickListener {
            if (txtTitleUpdate.text.toString() != "") {
                database.child("tasks").child(idUser.toString()).child(idTask.toString()).child("title").setValue(txtTitleUpdate.text.toString())
                database.child("tasks").child(idUser.toString()).child(idTask.toString()).child("priority").setValue(txtPriorityUpdate.text.toString())
                database.child("tasks").child(idUser.toString()).child(idTask.toString()).child("done").setValue(tvStatusUpdate.text.toString())
                showDefaultDialog("Cập nhật task thành công!")
                val intent = Intent(this@UpdateCardActivity, DashboardActivity::class.java)
                var strTemp = ""
                var strIdUser = idUser.toString()
                for (i in 4 .. strIdUser.length-1)
                    strTemp += strIdUser[i]
                intent.putExtra("dataIdOfUser", strTemp)
                intent.putExtra("dataDate", dataDate)
                startActivity(intent)
            } else showDefaultDialog("Vui lòng nhập tên Task")

        }

        tvBackDashBoard1.setOnClickListener {
            val intent = Intent(this@UpdateCardActivity, DashboardActivity::class.java)
            var strTemp = ""
            var strIdUser = idUser.toString()
            for (i in 4 .. strIdUser.length-1)
                strTemp += strIdUser[i]
            intent.putExtra("dataIdOfUser", strTemp)
            intent.putExtra("dataDate", dataDate)
            startActivity(intent)
        }

        btnDeleteUpdate.setOnClickListener {
            database.child("tasks").child(idUser.toString()).child(idTask.toString()).removeValue()
            showDefaultDialog("Xoá task thành công!")
            val intent = Intent(this@UpdateCardActivity, DashboardActivity::class.java)
            var strTemp = ""
            var strIdUser = idUser.toString()
            for (i in 4 .. strIdUser.length-1)
                strTemp += strIdUser[i]
            intent.putExtra("dataIdOfUser", strTemp)
            intent.putExtra("dataDate", dataDate)
            startActivity(intent)
        }

        tvBackDashBoard1.text = Html.fromHtml(" ‹ Update Task") // Thay đổi TextView Add Task

    }
    private fun showDefaultDialog(noiDung: String) {
        val alertDialog = AlertDialog.Builder(this)
        alertDialog.apply {
            setTitle(noiDung)
        }.create().show()
    } // Hiện dialog
}