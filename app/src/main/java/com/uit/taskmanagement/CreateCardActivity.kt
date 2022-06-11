package com.uit.taskmanagement

import android.app.AlertDialog
import android.app.DatePickerDialog
import android.content.ContentValues
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Html
import android.util.Log
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.activity_create_card.*
import kotlinx.android.synthetic.main.activity_main.*
import java.text.SimpleDateFormat
import java.util.*

class CreateCardActivity : AppCompatActivity() {
    var database: DatabaseReference = FirebaseDatabase.getInstance().reference // Tham chiếu database
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_card)

        val intentValue = intent
        val dataIdOfUser = intentValue.getStringExtra("dataIdOfUser")
        val dataDate = intentValue.getStringExtra("dataDate")

        txtPickDate.text = dataDate.toString()

        val myCalender = Calendar.getInstance()

        val datePicker = DatePickerDialog.OnDateSetListener { view, year, month, dayOfMonth ->
            myCalender.set(Calendar.YEAR, year)
            myCalender.set(Calendar.MONTH, month)
            myCalender.set(Calendar.DAY_OF_MONTH, dayOfMonth)
            updateLabel(myCalender)
        }

        imgDateAddTask.setOnClickListener {
            DatePickerDialog(this, datePicker, myCalender.get(Calendar.YEAR), myCalender.get(
                Calendar.MONTH)
                , myCalender.get(Calendar.DAY_OF_MONTH)).show()
        } // Click IMG Calendar
        btnLow.setOnClickListener {
            create_priority.text = "LOW"
        } // Click Low
        btnMedium.setOnClickListener {
            create_priority.text = "MEDIUM"
        } // Click Medium
        btnHigh.setOnClickListener {
            create_priority.text = "HIGH"
        } // Click High
        tvBackDashBoard.setOnClickListener {
            val intent = Intent(this@CreateCardActivity, DashboardActivity::class.java)
            intent.putExtra("dataIdOfUser", dataIdOfUser)
            intent.putExtra("dataDate", txtPickDate.text.toString())
            startActivity(intent)
        } // Click Back
        btnDone.setOnClickListener {
            if(create_title.text.toString().trim{it<=' '}.isNotEmpty()
                && create_priority.text.toString().trim{it<=' '}.isNotEmpty() ) {
                database.child("tasks").child("user"+dataIdOfUser.toString()).addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(dataSnapshot: DataSnapshot) {
                        var numOfTasksTemp = 1
                        for (postSnapshot in dataSnapshot.children) numOfTasksTemp++
                        val intent = Intent(this@CreateCardActivity, DashboardActivity::class.java)
                        database.child("tasks").child("user"+dataIdOfUser.toString()).child("task$numOfTasksTemp").child("done").setValue("NO")
                        database.child("tasks").child("user"+dataIdOfUser.toString()).child("task$numOfTasksTemp").child("icon").setValue("link:...")
                        database.child("tasks").child("user"+dataIdOfUser.toString()).child("task$numOfTasksTemp").child("priority").setValue(create_priority.text.toString())
                        database.child("tasks").child("user"+dataIdOfUser.toString()).child("task$numOfTasksTemp").child("title").setValue(create_title.text.toString())
                        database.child("tasks").child("user"+dataIdOfUser.toString()).child("task$numOfTasksTemp").child("date").setValue(txtPickDate.text.toString())
                        database.child("tasks").child("user"+dataIdOfUser.toString()).child("task$numOfTasksTemp").child("idUser").setValue("user"+dataIdOfUser.toString())
                        database.child("tasks").child("user"+dataIdOfUser.toString()).child("task$numOfTasksTemp").child("idTask").setValue("task$numOfTasksTemp")
                        showDefaultDialog("Thêm task thành công!")
                        intent.putExtra("dataIdOfUser", dataIdOfUser)
                        intent.putExtra("dataDate", txtPickDate.text.toString())
                        startActivity(intent)
                    }

                    override fun onCancelled(databaseError: DatabaseError) {
                        // Getting Post failed, log a message
                        Log.w(ContentValues.TAG, "loadPost:onCancelled", databaseError.toException())
                        // ...
                    }
                })
            } else { showDefaultDialog("Vui lòng điền đầy đủ các thông tin") }
        }
        tvBackDashBoard.text = Html.fromHtml(" ‹ Add Task") // Thay đổi TextView Add Task
    }
    private fun updateLabel(myCalendar: Calendar) {
        val myFormat = "dd-MM-yyyy"
        val sdf = SimpleDateFormat(myFormat)
        txtPickDate.text = sdf.format(myCalendar.time)
    }
    private fun showDefaultDialog(noiDung: String) {
        val alertDialog = AlertDialog.Builder(this)
        alertDialog.apply {
            setTitle(noiDung)
        }.create().show()
    } // Hiện dialog
}