package com.uit.taskmanagement

import android.app.DatePickerDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.activity_dashboard.*
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class DashboardActivity : AppCompatActivity() {
    var database: DatabaseReference = FirebaseDatabase.getInstance().reference
    private lateinit var taskRecyclerView: RecyclerView
    private lateinit var taskArrayList: ArrayList<CardInfo>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dashboard)

        val intentValue = intent
        val dataIdOfUser = intentValue.getStringExtra("dataIdOfUser")
        val dataDate = intentValue.getStringExtra("dataDate")
        tvDate.text = dataDate.toString()

        taskRecyclerView = findViewById(R.id.taskList)
        taskRecyclerView.layoutManager = LinearLayoutManager(this)
        taskRecyclerView.setHasFixedSize(true)

        taskArrayList = arrayListOf<CardInfo>()
        getTaskData(dataIdOfUser, tvDate.text.toString())

        btAdd.setOnClickListener {
            val intent1 = Intent(this@DashboardActivity, CreateCardActivity::class.java)
            intent1.putExtra("dataIdOfUser", dataIdOfUser)
            intent1.putExtra("dataDate", tvDate.text.toString())
            startActivity(intent1)
        } // Truy cập create card activity
        image.setOnClickListener {
            val intent2 = Intent(this@DashboardActivity, ProfileActivity::class.java)
            intent2.putExtra("dataIdOfUser", dataIdOfUser)
            intent2.putExtra("dataDate", tvDate.text.toString())
            startActivity(intent2)
        }

        val myCalender = Calendar.getInstance() // Tạo lịch
        val datePicker = DatePickerDialog.OnDateSetListener { view, year, month, dayOfMonth ->
            myCalender.set(Calendar.YEAR, year)
            myCalender.set(Calendar.MONTH, month)
            myCalender.set(Calendar.DAY_OF_MONTH, dayOfMonth)
            // nhớ bỏ cmt out
            updateLabel(myCalender, dataIdOfUser)
//            setRecycler(emailAuth.toString(), )
        } // Tạo date Picker
        btnDatePicker.setOnClickListener {
            DatePickerDialog(this, datePicker, myCalender.get(Calendar.YEAR), myCalender.get(
                Calendar.MONTH)
                , myCalender.get(Calendar.DAY_OF_MONTH)).show()
        }
    }
    private fun updateLabel(myCalendar: Calendar, dataIdOfUser: String?) {
        val myFormat = "dd-MM-yyyy"
        val sdf = SimpleDateFormat(myFormat)
        // nhớ bỏ cmt out
//        setRecycler(emailAuth, sdf.format(myCalendar.time).toString())
        tvDate.text = sdf.format(myCalendar.time).toString()
        getTaskData(dataIdOfUser, sdf.format(myCalendar.time).toString())
    }
    private fun getTaskData(dataIdOfUser: String?, dataDate: String?) {
        database.child("tasks").child("user$dataIdOfUser").addListenerForSingleValueEvent(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                taskArrayList.clear()
                if (snapshot.exists()) {
                    var countDone = 0
                    var countNotDone = 0
                    // Tính giá trị Progress Bar
                    for(taskSnapshot in snapshot.children)
                    if (taskSnapshot.child("date").value.toString() == dataDate.toString())
                    {
                        if (taskSnapshot.child("done").value.toString() == "YES") countDone ++
                        else countNotDone ++
                    }
                    // Add task: HIGH, no DONE
                    for(taskSnapshot in snapshot.children)
                        if (taskSnapshot.child("date").value.toString() == dataDate.toString() && taskSnapshot.child("priority").value.toString() == "HIGH" && taskSnapshot.child("done").value.toString() == "NO")
                        {
                            val task = taskSnapshot.getValue(CardInfo::class.java)
                          taskArrayList.add(task!!)
                        }
                    // Add task: MEDIUM, no DONE
                    for(taskSnapshot in snapshot.children)
                        if (taskSnapshot.child("date").value.toString() == dataDate.toString() && taskSnapshot.child("priority").value.toString() == "MEDIUM" && taskSnapshot.child("done").value.toString() == "NO")
                        {
                            val task = taskSnapshot.getValue(CardInfo::class.java)
                            taskArrayList.add(task!!)
                        }
                    // Add task: LOW, no DONE
                    for(taskSnapshot in snapshot.children)
                        if (taskSnapshot.child("date").value.toString() == dataDate.toString() && taskSnapshot.child("priority").value.toString() == "LOW" && taskSnapshot.child("done").value.toString() == "NO")
                        {
                            val task = taskSnapshot.getValue(CardInfo::class.java)
                            taskArrayList.add(task!!)
                        }
                    // Add task: HIGH, DONE
                    for(taskSnapshot in snapshot.children)
                        if (taskSnapshot.child("date").value.toString() == dataDate.toString() && taskSnapshot.child("priority").value.toString() == "HIGH" && taskSnapshot.child("done").value.toString() == "YES")
                        {
                            val task = taskSnapshot.getValue(CardInfo::class.java)
                            taskArrayList.add(task!!)
                        }
                    // Add task: MEDIUM, DONE
                    for(taskSnapshot in snapshot.children)
                        if (taskSnapshot.child("date").value.toString() == dataDate.toString() && taskSnapshot.child("priority").value.toString() == "MEDIUM" && taskSnapshot.child("done").value.toString() == "YES")
                        {
                            val task = taskSnapshot.getValue(CardInfo::class.java)
                            taskArrayList.add(task!!)
                        }
                    // Add task: LOW, DONE
                    for(taskSnapshot in snapshot.children)
                        if (taskSnapshot.child("date").value.toString() == dataDate.toString() && taskSnapshot.child("priority").value.toString() == "LOW" && taskSnapshot.child("done").value.toString() == "YES")
                        {
                            val task = taskSnapshot.getValue(CardInfo::class.java)
                            taskArrayList.add(task!!)
                        }
                    progressbar!!.progress = (countDone*100)/(countDone+countNotDone)
                    taskRecyclerView.adapter = Adapter(taskArrayList)
                }
            }

            override fun onCancelled(error: DatabaseError) {

            }

        })
    }
}