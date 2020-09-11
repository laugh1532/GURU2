package com.example.googlelogin

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewOutlineProvider
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.core.view.isInvisible
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.UserInfo
import com.google.firebase.firestore.FirebaseFirestore
import io.opencensus.tags.Tag
import kotlinx.android.synthetic.main.activity_result.*
import kotlinx.android.synthetic.main.activity_result.view.*

class ResultActivity : AppCompatActivity() {

    private val TAG = "DocSnippets"

    //firebase Auth
    private lateinit var firebaseAuth: FirebaseAuth
    //google client
    private lateinit var googleSignInClient: GoogleSignInClient
    //private const val TAG = "GoogleActivity"
    private val RC_SIGN_IN = 99

    //cloud firestore 인스턴스 초기화
    val db = FirebaseFirestore.getInstance()

    val user = FirebaseAuth.getInstance().currentUser


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_result)

        if (user != null) {
            //user is signed in
        } else {
            //no user is signed in
        }

        user?.let {
            for (profile in it.providerData) {
                //id of the provider(ex: google.com)
                val providerId = profile.providerId
                //name, email address, and profile photo Url
                val name = profile.displayName
                tv_name.text = name
                val email = profile.email
                tv_email.text = email
                val photoUrl = profile.photoUrl
                Glide.with(this).load(photoUrl).into(iv_profile)
                val uid = profile.uid
            }
        }


        getData()

        btn_univ_edit.setOnClickListener { view ->
            tv_univ_saved.visibility = View.INVISIBLE
            et_university.visibility = View.VISIBLE
        }

        btn_major_edit.setOnClickListener { view ->
            tv_major_saved.visibility = View.INVISIBLE
            et_major.visibility = View.VISIBLE
        }


        btn_save.setOnClickListener { view ->
            ///정보 저장하기
            setData()
            ///정보 받아오기
            getData()

            tv_univ_saved.visibility = View.VISIBLE
            tv_major_saved.visibility = View.VISIBLE
            et_major.visibility = View.INVISIBLE
            et_university.visibility = View.INVISIBLE
        }


        btn_back.setOnClickListener { view ->
            var myIntent = Intent(this, MainActivity::class.java)
            startActivity(myIntent)
        }

        btn_toThird.setOnClickListener { view ->
            var myIntent2 = Intent(this, ThirdActivity::class.java)
            startActivity(myIntent2)
        }

//        btn_logout.setOnClickListener { view ->
//            signOut()
//        }
//
//        btn_revokeAccess.setOnClickListener { view ->
//            revokeAccess()
//        }

    }

    private fun setData() {

        val univ = et_university.text.toString()
        val major = et_major.text.toString()

        val userInfo = UserDTO(
            major,
            univ
        )

        db?.collection("userInfo")?.document(user?.email.toString())
            ?.set(userInfo)
            ?.addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Toast.makeText(this, "성공했다.", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(this, "실패했다.", Toast.LENGTH_SHORT).show()
                    Log.d(TAG, task.exception.toString())
                }
            }
    }

    private fun getData() {
        db?.collection("userInfo")?.document(user?.email.toString())
            ?.get()
            ?.addOnSuccessListener { document ->
                if (document != null) {
                    var userDTO = document.toObject(UserDTO::class.java)
                    tv_major_saved.text = userDTO?.major.toString()
                    tv_univ_saved.text = userDTO?.university.toString()
                }
            }
    }
}