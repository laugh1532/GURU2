package com.example.googlelogin.MyPage


import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import com.bumptech.glide.Glide
import com.example.googlelogin.R
import com.example.googlelogin.UserDTO
import com.example.myapplication.MyPost
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.fragment_my_page.*

/**
 * A simple [Fragment] subclass.
 */
class MyPage : Fragment() {

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

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val root = inflater.inflate(R.layout.fragment_my_page, container, false)

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
                val tv_name: TextView = root.findViewById(R.id.tv_name)
                tv_name.text = name
                val email = profile.email
                val tv_email: TextView = root.findViewById(R.id.tv_email)
                tv_email.text = email
                val photoUrl = profile.photoUrl
                val iv_profile: ImageView = root.findViewById(R.id.iv_profile)
                Glide.with(this).load(photoUrl).into(iv_profile)
                val uid = profile.uid
            }
        }

        getData(root)

        val btn_univ_edit: ImageButton = root.findViewById(R.id.btn_univ_edit) //대학 저장 button
        val tv_univ_saved: TextView = root.findViewById(R.id.tv_univ_saved) //대학 편집 textView
        val et_university: EditText = root.findViewById(R.id.et_university) //대학 편집 editText

        //대학 저장 button 이벤트
        btn_univ_edit.setOnClickListener { view ->
            tv_univ_saved.visibility = View.INVISIBLE
            et_university.visibility = View.VISIBLE
        }

        val btn_major_edit: ImageButton = root.findViewById(R.id.btn_major_edit) //전공 저장 button
        val tv_major_saved: TextView = root.findViewById(R.id.tv_major_saved) //전공 편집 textView
        val et_major: EditText = root.findViewById(R.id.et_major) //전공 편집 editText

        //전공 저장 button 이벤트
        btn_major_edit.setOnClickListener { view ->
            tv_major_saved.visibility = View.INVISIBLE
            et_major.visibility = View.VISIBLE
        }

        //저장 버튼, 버튼 이벤트
        val btn_save: Button = root.findViewById(R.id.btn_save)
        btn_save.setOnClickListener { view ->

            ///정보 저장하기
            setData()
            ///정보 받아오기
            getData(root)

            tv_univ_saved.visibility = View.VISIBLE
            tv_major_saved.visibility = View.VISIBLE
            et_major.visibility = View.INVISIBLE
            et_university.visibility = View.INVISIBLE
        }

        var mypost = MyPost()
        val btn_mypost : Button = root.findViewById(R.id.btn_mypost)
        btn_mypost.setOnClickListener{
            fragmentManager!!.beginTransaction()
                .replace(R.id.nav_host_fragment, mypost)
                .addToBackStack(null)
                .commit()
        }

        return root

    }

    //학교, 전공 정보 받아오기
    private fun getData(root:View) {
        val tv_major_saved: TextView = root.findViewById(R.id.tv_major_saved)
        val tv_univ_saved: TextView = root.findViewById(R.id.tv_univ_saved)

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

    //학교, 전공 정보 저장하기
    private fun setData() {
        val useremail = user?.email.toString()
        val univ = et_university.text.toString()
        val major = et_major.text.toString()

        val userInfo = UserDTO(
            major,
            univ,
            useremail
        )

        db?.collection("userInfo")?.document(user?.email.toString())
            ?.set(userInfo)
            ?.addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    //Toast.makeText(this, "성공했다.", Toast.LENGTH_SHORT).show()
                } else {
                    //Toast.makeText(this, "실패했다.", Toast.LENGTH_SHORT).show()
                    Log.d(TAG, task.exception.toString())
                }
            }
    }


}
