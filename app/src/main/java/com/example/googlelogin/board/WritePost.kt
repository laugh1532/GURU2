package com.example.myapplication


import android.annotation.SuppressLint
import android.app.Activity
import android.app.AlertDialog
import android.app.ProgressDialog
import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.content.DialogInterface
import android.content.Intent
import android.net.Uri
import android.provider.MediaStore
import android.util.Log
import android.util.Log.d
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import com.bumptech.glide.Glide
import com.example.googlelogin.PostDTO
import com.example.googlelogin.R
import com.example.googlelogin.ReplyDTO
import com.example.googlelogin.UserDTO
import com.firebase.ui.auth.AuthUI
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.google.firestore.v1.Write
import kotlinx.android.synthetic.main.activity_result.*
import kotlinx.android.synthetic.main.fragment_write_post.*
import java.text.SimpleDateFormat
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*


/**
 * A simple [Fragment] subclass.
 */
@Suppress("DEPRECATION")
class WritePost : Fragment() {
    private val TAG = "WritePost"
    private val OPEN_GALLERY = 1
    private var mStorageRef: StorageReference? = null
    private var currentImageUrl: Uri? = null

    var fileName = String()

    val db = FirebaseFirestore.getInstance()

    val user = FirebaseAuth.getInstance().currentUser

    /////////////////////////////////gallery

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        //setContentView(R.layout.fragment_write_post)
        var button5: Button? = view?.findViewById(R.id.showImage_ImageView)   //사진첨부하기
        button5?.setOnClickListener { openGallery() }
        mStorageRef = FirebaseStorage.getInstance().getReference();
    }

    private fun openGallery() {
        val intent = Intent(Intent.ACTION_GET_CONTENT)
        intent.setType("image/*")
        startActivityForResult(intent, OPEN_GALLERY)

    }
//    private fun getContentResolver(fragmentWritePost: Int): ContentResolver? {
//
//        return getContentResolver(R.layout.fragment_write_post)
//
//    }

    @SuppressLint("RestrictedApi")
    @Override
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        val resolver = activity!!.contentResolver
        if(resultCode == Activity.RESULT_OK){
            if(requestCode==OPEN_GALLERY){
//                var currentImageUrl : Uri? = data?.data
                currentImageUrl = data!!.getData();
//                Log.d(AuthUI.TAG, "uri:" + String.format(currentImageUrl.toString()))
//                Log.d(TAG, "uri:" + toString(currentImageUrl))
                try{
                    var bitmap = MediaStore.Images.Media.getBitmap(resolver,currentImageUrl)
                    showImage_ImageView.setImageBitmap(bitmap)
                }catch (e: Exception){
                    e.printStackTrace()
                }
            }
            else{
                Log.d("ActivityResult","something wrong")
            }
        }
    }

    private fun toString(currentImageUrl: Uri?): String {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    @SuppressLint("RestrictedApi", "SimpleDateFormat")
    private fun uploadFile() { //업로드할 파일이 있으면 수행

        if (currentImageUrl != null) { //업로드 진행 Dialog 보이기
            val progressDialog = ProgressDialog(this.context)
            progressDialog.setTitle("업로드중...")
            progressDialog.show()
            //storage
            val storage = FirebaseStorage.getInstance()
            //Unique한 파일명을 만들자.
            val formatter = SimpleDateFormat("yyyyMMddHHmmSS")
            val now = Date()
            var time = formatter.format(now).toString()
            fileName = "images/" + user?.email.toString() + time + ".jpg"
            //storage 주소와 폴더 파일명을 지정해 준다.
            val storageRef =
                storage.getReferenceFromUrl("gs://loginproject-a92e2.appspot.com/")
                    .child(fileName)
            //올라가거라...
            storageRef.putFile(currentImageUrl!!) //성공시
                .addOnSuccessListener {
                    progressDialog.dismiss() //업로드 진행 Dialog 상자 닫기
                    Toast.makeText(AuthUI.getApplicationContext(), "업로드 완료!", Toast.LENGTH_SHORT)
                        .show()

                } //실패시
                .addOnFailureListener {
                    progressDialog.dismiss()
                    Toast.makeText(AuthUI.getApplicationContext(), "업로드 실패!", Toast.LENGTH_SHORT)
                        .show()
                } //진행중
                .addOnProgressListener { taskSnapshot ->
                    val progress =
                        100 * taskSnapshot.bytesTransferred / taskSnapshot.totalByteCount.toDouble()
                    //dialog에 진행률을 퍼센트로 출력해 준다
                    progressDialog.setMessage("Uploaded " + progress.toInt() + "% ...")
                }
        }
    }

    ///////////////////////////////////////

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        var mContext: Context? = getActivity() //DB에서 context 쓸 때 하는 것
        //view 생성
        var view = inflater.inflate(R.layout.fragment_write_post, container, false)

        //button 생성
        var button3 : Button = view.findViewById(R.id.button3) //저장하기
        var button4 : Button = view.findViewById(R.id.button4) //취소하기
        var button5 : Button = view.findViewById(R.id.button5) //사진첨부하기

        //editText 생성
        val et_writePostTitle : EditText = view.findViewById(R.id.et_writePostTitle) //제목 입력
        val et_writePostContents : EditText = view.findViewById(R.id.et_writePostContents) //내용 입력


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
        button3.setOnClickListener { view ->
            uploadFile()
            var getEdit1 = et_writePostTitle.text.toString()
            var getEdit2= et_writePostContents.text.toString()
            getEdit1 = getEdit1.trim()
            getEdit2 = getEdit2.trim()

            if(getEdit1.length <= 0 || getEdit2.length <= 0){
                Toast.makeText(this.activity, "빈칸 없이 입력하세요.", Toast.LENGTH_SHORT).show()
            }

            else{
                setPost()
                fragmentManager!!.popBackStack() //이전으로 돌아가기
            }
        }

        // 글 작성 취소 button 이벤트
        button4.setOnClickListener { view ->
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

        // 사진 첨부 button 이벤트
        button5.setOnClickListener { view ->
            openGallery()
            button5?.setOnClickListener { openGallery() }
        }

        return view
    }

    fun newInstance(boardMajor:String):Fragment{
        var fragment = WritePost()
        var bundle:Bundle = Bundle()

        bundle.putString("boardMajor", boardMajor)
        fragment.setArguments(bundle)

        return fragment
    }

    private fun setPost() {
        ///포스트 내용 db에 올리기
        val postTitle = et_writePostTitle.text.toString()
        val postContent = et_writePostContents.text.toString()
        val postWriter = user?.email.toString()
        val dateAndtime = LocalDateTime.now()
        var postDate = dateAndtime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:SS"))
        val postdocumentname = "${postWriter}+${postDate}"
        val postMajor = getArguments()?.getString("boardMajor")
        val postFileName = fileName

        val postDTO = PostDTO(
            postTitle,
            postWriter,
            postDate,
            postContent,
            postdocumentname,
            postMajor,
            postFileName
        )

        db?.collection("Board")
            ?.document(postdocumentname)
            ?.set(postDTO)
            ?.addOnCompleteListener { task ->
            }
    }

}