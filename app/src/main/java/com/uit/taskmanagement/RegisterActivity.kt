package com.uit.taskmanagement

import android.app.AlertDialog
import android.content.ContentValues.TAG
import android.content.Intent
import android.os.Bundle
import android.util.Log
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.activity_register.*
import org.bouncycastle.jce.provider.BouncyCastleProvider
import org.bouncycastle.util.encoders.Base64
import java.io.UnsupportedEncodingException
import java.security.InvalidKeyException
import java.security.NoSuchAlgorithmException
import java.security.Security
import javax.crypto.*
import javax.crypto.spec.SecretKeySpec

class RegisterActivity : androidx.appcompat.app.AppCompatActivity() {
    var database: DatabaseReference = FirebaseDatabase.getInstance().reference // Tham chiếu database
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)
        // Đăng ký
        btnSignUp.setOnClickListener {
            dangKy()
        }
        // Đăng nhập
        tvLogin.setOnClickListener {
            val intent = Intent(this@RegisterActivity, MainActivity::class.java)
            startActivity(intent)
        }
    }
    private fun checkSpace(): Boolean {
        if (txtName.text.toString().trim{it<=' '}.isNotEmpty()
            && txtPassword.text.toString().trim{it<=' '}.isNotEmpty()
            && txtConfirmPassword.text.toString().trim{it<=' '}.isNotEmpty()
            && txtEmail.text.toString().trim{it<=' '}.isNotEmpty())
                return true
        return false
    } // Kiểm tra khoảng trắng
    private fun checkName(): Boolean { if (txtName.text.toString().length > 7) return false; return true} // Kiểm tra name
    private fun checkEmailSyntax(): Boolean { return txtEmail.text.toString().contains("@") } // Kiểm tra cú pháp email
    private fun checkEmailNotExists(emailTemp: String, nameTemp: String, passTemp: String){
        database.child("accounts").addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                var check: Boolean = true
                var numOfAccount: Int = 1
                for (postSnapshot in dataSnapshot.children) {
                    numOfAccount++
                    if (postSnapshot.child("email").value.toString() == emailTemp)
                    {
                        check = false
                        break
                    }
                }
                if (check) {
                    val intent = Intent(this@RegisterActivity, MainActivity::class.java)
                    database.child("accounts").child("user$numOfAccount").child("email").setValue(emailTemp)
                    database.child("accounts").child("user$numOfAccount").child("id").setValue(numOfAccount.toString())
                    database.child("accounts").child("user$numOfAccount").child("name").setValue(nameTemp)
                    database.child("accounts").child("user$numOfAccount").child("password").setValue(encrypt(passTemp,"662ede816988e58fb6d057d9d85605e0").toString())
                    showDefaultDialog("Đăng ký thành công")
                    startActivity(intent)
                }

            }

            override fun onCancelled(databaseError: DatabaseError) {
                // Getting Post failed, log a message
                Log.w(TAG, "loadPost:onCancelled", databaseError.toException())
                // ...
            }
        })

    } // Kiểm tra chưa tồn tại email
    private fun checkPassword(): Boolean {
        var soNguyen: Int = 0
        var chuHoa: Int = 0
        var chuThuong: Int = 0
        var kyTuDacBiet: Int = 0

        var strTemp: String = txtPassword.text.toString()
        for(i in strTemp.indices)    {
            if (strTemp[i] in '0'..'9')
                soNguyen++
            else if (strTemp[i] in 'a'..'z')
                chuThuong++
            else if (strTemp[i] in 'A'..'Z')
                chuHoa++
            else kyTuDacBiet++
        }
        if (strTemp.length >= 8 && soNguyen > 0 && chuThuong > 0 && chuHoa > 0 && kyTuDacBiet > 0)
            return true
        return false
    } // Kiểm tra mật khẩu (>= 8 ký tự, có ít nhất 1 chữ số, 1 chữ cái, 1 chữ in thường, 1 chữ in hoa, 1 ký tự đặc biệt
    private fun checkEqualPassword(): Boolean {
        if (txtConfirmPassword.text.toString() ==  txtPassword.text.toString())
            return true
        return false
    } // Kiểm tra password và confirm password
    private fun dangKy(){
        if (!checkName()) showDefaultDialog("Tên phải ít hơn 8 ký tự")
        else if (!checkSpace()) showDefaultDialog("Vui lòng điền đầy đủ các thông tin")
        else if (!checkEmailSyntax()) showDefaultDialog("Vui lòng điền đúng cú pháp email")
        else if (!checkPassword()) showDefaultDialog("Mật khẩu phải có nhiều hơn 7 ký tự và có ít nhất 1 chữ số, 1 chữ cái, 1 chữ in thường, 1 chữ in hoa, 1 ký tự đặc biệt")
        else if (!checkEqualPassword()) showDefaultDialog("Mật khẩu và xác nhận mật khẩu phải giống nhau")
        else checkEmailNotExists(txtEmail.text.toString(),txtName.text.toString(),txtPassword.text.toString())
        //showDefaultDialog("Email đã tồn tại")
        clearText()
    } // Tổng hợp hàm kiểm tra đăng ký
    private fun showDefaultDialog(noiDung: String) {
        val alertDialog = AlertDialog.Builder(this)
        alertDialog.apply {
            setTitle(noiDung)
        }.create().show()
    } // Hiện dialog
    private fun clearText() {
        txtName.setText("")
        txtEmail.setText("")
        txtPassword.setText("")
        txtConfirmPassword.setText("")
    } // Xóa Text
    private fun encrypt(strToEncrypt: String, secret_key: String): String? {
        Security.addProvider(BouncyCastleProvider())
        var keyBytes: ByteArray

        try {
            keyBytes = secret_key.toByteArray(charset("UTF8"))
            val skey = SecretKeySpec(keyBytes, "AES")
            val input = strToEncrypt.toByteArray(charset("UTF8"))

            synchronized(Cipher::class.java) {
                val cipher = Cipher.getInstance("AES/ECB/PKCS7Padding")
                cipher.init(Cipher.ENCRYPT_MODE, skey)

                val cipherText = ByteArray(cipher.getOutputSize(input.size))
                var ctLength = cipher.update(
                    input, 0, input.size,
                    cipherText, 0
                )
                ctLength += cipher.doFinal(cipherText, ctLength)
                return String(
                    Base64.encode(cipherText)
                )
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
