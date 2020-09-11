package com.example.googlelogin.Study


import android.app.AlertDialog
import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.example.googlelogin.PostDTO
import com.example.googlelogin.R
import com.example.googlelogin.StudyPostDTO
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.fragment_write_post.*
import kotlinx.android.synthetic.main.fragment_write_study.*
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

/**
 * A simple [Fragment] subclass.
 */
class StudyWrite : Fragment() {

    val db = FirebaseFirestore.getInstance()
    val user = FirebaseAuth.getInstance().currentUser

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        var mContext: Context? = getActivity()

        //view 생성
        var view = inflater.inflate(R.layout.fragment_write_study, container, false)

        //button 생성
        var btn_writeStudySave : Button = view.findViewById(R.id.btn_writeStudySave) //저장하기
        var btn_writeStudyFail : Button = view.findViewById(R.id.btn_writeStudyFail) //취소하기

        //editText 생성
        val et_writestudyTitle : EditText = view.findViewById(R.id.et_writeStudyTitle) //제목 입력
        val et_writeStudyTheme : EditText = view.findViewById(R.id.et_writeStudyTheme) //주제 입력
        val et_writeStudyLocation : EditText = view.findViewById(R.id.et_writeStudyLocation) //위치 입력
        val et_writeStudyContents : EditText = view.findViewById(R.id.et_writeStudyContents) //내용 입력


        if (user != null) {
            //user is signed in
        } else {
            //no user is signed in
        }

        user?.let {
            for (profile in it.providerData) {
                //id of the provider(ex: google.com)
                val name = profile.displayName
                val email = profile.email
            }
        }

        // 글 저장 button 이벤트
        btn_writeStudySave.setOnClickListener { view ->
            var getEdit1 = et_writeStudyTitle.text.toString()
            var getEdit2 = et_writeStudyTheme.text.toString()
            var getEdit3 = et_writeStudyLocation.text.toString()
            var getEdit4 = et_writeStudyContents.text.toString()

            getEdit1 = getEdit1.trim()
            getEdit2 = getEdit2.trim()
            getEdit3 = getEdit3.trim()
            getEdit4 = getEdit4.trim()

            if(getEdit1.length <= 0 || getEdit2.length <= 0 || getEdit3.length <= 0 || getEdit4.length <= 0){
                Toast.makeText(this.activity, "빈칸 없이 입력하세요.", Toast.LENGTH_SHORT).show()
            }

            else{
                setStudyPost()
                fragmentManager!!.popBackStack() //이전으로 돌아가기
            }
        }

        // 글 작성 취소 button 이벤트
        btn_writeStudyFail.setOnClickListener { view ->
            //알림창
            val builder = AlertDialog.Builder(mContext)
            builder.setTitle("알림")
            builder.setMessage("정말 나가시겠습니까?\n글 내용은 저장되지 않습니다.")

            builder.setPositiveButton("확인") { _, _ ->
                fragmentManager!!.popBackStack() //이전으로 돌아가기
            }
            builder.setNegativeButton("취소") { _, _ -> }
            builder.show()
        }

        return view
    }

    private fun setStudyPost() {

        ///포스트 내용 db에 올리기
        val studyTitle = et_writeStudyTitle.text.toString()
        val studyTheme = et_writeStudyTheme.text.toString()
        val studyLocation = et_writeStudyLocation.text.toString()
        val studyContent = et_writeStudyContents.text.toString()
        val studyWriter = user?.email.toString()
        val dateAndtime = LocalDateTime.now()
        var studyDate = dateAndtime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:SS"))
        val studyDocumentName = "${studyWriter}+${studyDate}"
        //val postMajor = getArguments()?.getString("boardMajor")

        val studypostDTO = StudyPostDTO(
            studyTitle,
            studyWriter,
            studyDate,
            studyContent,
            studyDocumentName,
            studyTheme,
            studyLocation
        )

        db?.collection("Study")
            ?.document(studyDocumentName)
            ?.set(studypostDTO)
            ?.addOnCompleteListener { task ->
            }
    }
}
