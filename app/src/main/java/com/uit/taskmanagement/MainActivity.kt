package com.uit.taskmanagement

import android.app.AlertDialog
import android.app.DatePickerDialog
import android.content.ContentValues
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.activity_main.*
import org.bouncycastle.jce.provider.BouncyCastleProvider
import java.io.UnsupportedEncodingException
import java.security.InvalidKeyException
import java.security.NoSuchAlgorithmException
import java.security.Security
import java.text.SimpleDateFormat
import java.util.*
import javax.crypto.*
import javax.crypto.spec.SecretKeySpec

class MainActivity : AppCompatActivity() {
    var database: DatabaseReference = FirebaseDatabase.getInstance().reference
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        btnLogin.setOnClickListener {
            checkAccount(txtEmailLogin.text.toString(), txtPasswordLogin.text.toString())
        }
        tvSignUp.setOnClickListener {
            registerIntent()
        }
        tvSignUpSecond.setOnClickListener {
            registerIntent()
        }
    }
    private fun registerIntent() {
        val intent1 = Intent(this@MainActivity, RegisterActivity::class.java)
        startActivity(intent1)
    } // Truy cập register
    private fun loginIntent(idTemp: String) {
        val intent = Intent(this@MainActivity, OTP_verify::class.java)
        intent.putExtra("dataIdOfUser", idTemp)
        intent.putExtra("dataDate", getDate())
        startActivity(intent)
    } // Truy cập dashboard
    private fun checkAccount(emailTemp: String, passTemp: String) {
        database.child("accounts").addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                var idOfUser = ""
                var check = false
                var numOfAccount = 1
                for (postSnapshot in dataSnapshot.children) {
                    numOfAccount++
                    if (postSnapshot.child("email").value.toString() == emailTemp && passTemp == decryptWithAES("662ede816988e58fb6d057d9d85605e0",postSnapshot.child("password").value.toString()).toString())
                    {
                        idOfUser = postSnapshot.child("id").value.toString()
                        check = true
                        break
                    }
                }
                if (check) {
                    showDefaultDialog("Đăng nhập thành công")
                    loginIntent(idOfUser)
                }
                else {
                    showDefaultDialog("Đăng nhập thất bại")
                    clearText()
                }
            }
            override fun onCancelled(databaseError: DatabaseError) {
                // Getting Post failed, log a message
                Log.w(ContentValues.TAG, "loadPost:onCancelled", databaseError.toException())
                // ...
            }
        })
    }
    private fun showDefaultDialog(noiDung: String) {
        val alertDialog = AlertDialog.Builder(this)
        alertDialog.apply {
            setTitle(noiDung)
        }.create().show()
    } // Hiện dialog
    private fun clearText() {
        txtEmailLogin.setText("")
        txtPasswordLogin.setText("")
    } // Xóa Text
    private fun getDate(): String {
        val myCalenderCurrent = Calendar.getInstance()
        val datePicker = DatePickerDialog.OnDateSetListener { view, year, month, dayOfMonth ->
            myCalenderCurrent.set(Calendar.YEAR, year)
            myCalenderCurrent.set(Calendar.MONTH, month)
            myCalenderCurrent.set(Calendar.DAY_OF_MONTH, dayOfMonth)
        }
        return updateLabel(myCalenderCurrent)
    }
    private fun updateLabel(myCalendar: Calendar) : String {
        val myFormat = "dd-MM-yyyy"
        val sdf = SimpleDateFormat(myFormat)
        return sdf.format(myCalendar.time).toString()
    }
    private fun decryptWithAES(key: String, strToDecrypt: String?): String? {
        Security.addProvider(BouncyCastleProvider())
        var keyBytes: ByteArray

        try {
            keyBytes = key.toByteArray(charset("UTF8"))
            val skey = SecretKeySpec(keyBytes, "AES")
            val input = org.bouncycastle.util.encoders.Base64
                .decode(strToDecrypt?.trim { it <= ' ' }?.toByteArray(charset("UTF8")))

            synchronized(Cipher::class.java) {
                val cipher = Cipher.getInstance("AES/ECB/PKCS7Padding")
                cipher.init(Cipher.DECRYPT_MODE, skey)

                val plainText = ByteArray(cipher.getOutputSize(input.size))
                var ptLength = cipher.update(input, 0, input.size, plainText, 0)
                ptLength += cipher.doFinal(plainText, ptLength)
                val decryptedString = String(plainText)
                return decryptedString.trim { it <= ' ' }
            }
        } catch (uee: UnsupportedEncodingException) {
            uee.printStackTrace()
        } catch (ibse: IllegalBlockSizeException) {
            ibse.printStackTrace()
        } catch (bpe: BadPaddingException) {
            bpe.printStackTrace()
        } catch (ike: InvalidKeyException) {
            ike.printStackTrace()
        } catch (nspe: NoSuchPaddingException) {
            nspe.printStackTrace()
        } catch (nsae: NoSuchAlgorithmException) {
            nsae.printStackTrace()
        } catch (e: ShortBufferException) {
            e.printStackTrace()
        }

        return null
    }
}