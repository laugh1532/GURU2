//package com.example.googlelogin.Study
//
//import android.content.Context
//import android.net.Uri
//import android.os.Bundle
//import androidx.fragment.app.Fragment
//import android.view.LayoutInflater
//import android.view.View
//import android.view.ViewGroup
//import android.widget.*
//import com.bumptech.glide.Glide
//import com.example.googlelogin.R
//import com.example.googlelogin.ReplyDTO
//import com.example.googlelogin.StudyRoomDTO
//import com.google.firebase.firestore.FirebaseFirestore
//import com.google.firebase.storage.FirebaseStorage
//import kotlinx.android.synthetic.main.studyroom_list_item.*
//
//
//class StudyRoom : Fragment() {
//    private var firestore = FirebaseFirestore.getInstance()
//
//    fun newInstance4(studyroomRecommendation: String): Fragment {
//        var fragment = StudyRoom()
//        var bundle = Bundle()
//
//        bundle.putString("StudyRoomReco", studyroomRecommendation)
//        fragment.setArguments(bundle)
//
//        return fragment
//    }
//
//    override fun onCreateView(
//        inflater: LayoutInflater, container: ViewGroup?,
//        savedInstanceState: Bundle?
//    ): View? {
//        //댓글 리스트 받을 arraylist
//        var studyroomDTOList: ArrayList<StudyRoomDTO> = ArrayList<StudyRoomDTO>()
//        //view 생성
//        var view = inflater.inflate(R.layout.fragment_studyroom, container, false)
//        //textView 생성, 글 내용
//        var location_V = view.findViewById<TextView>(R.id.tv_studyroomLocation)
//        //button 생성, 글 내용
//        var btn_back = view.findViewById<ImageButton>(R.id.btn_back)
//        //listView 생성
//        var studyroomListView = view.findViewById<ListView>(R.id.studyroom_ListView)
//
//        //뒤로가기 버튼
//        btn_back.setOnClickListener { view ->
//            fragmentManager!!.popBackStack()
//        }
//
//        //지역 받아오기
//        val StudyRoomRecomm = getArguments()?.getString("StudyRoomReco")
//
//
//
//        //db에서 정보 받아오기
//        firestore?.collection("studyRoom")
//            ?.whereEqualTo("studyRoomLocation", StudyRoomRecomm)
//            ?.get()
//            ?.addOnSuccessListener { result ->
//                var studyroomDTO: StudyRoomDTO
//
//                for (document in result) {
//                    studyroomDTO = document.toObject(StudyRoomDTO::class.java)
//                    studyroomDTOList.add(studyroomDTO)
//
//                    //listView에 adapter 연결
//                    var adapter = MyAdapter(this!!.activity!!, studyroomDTOList)
//                    studyroomListView.adapter = adapter
//                    //fileName = studyroomDTO?.fileName.toString()
//
////                    //사진 불러오기
////                    if (fileName != "" || fileName == null) {
////                        var storage = FirebaseStorage.getInstance()
////                        var storageRef = storage.reference
////                        var imageRef = storageRef.child(fileName)
////
////                        imageRef.getDownloadUrl().addOnCompleteListener { task ->
////                            if (task.isSuccessful()) {
////                                // Glide 이용하여 이미지뷰에 로딩
////                                Glide.with(this)
////                                    .load(task.getResult())
////                                    .override(1024, 980)
////                                    .into(studyroomImage)
////                            } else {
////                                // URL을 가져오지 못하면 토스트 메세지
////                                Toast.makeText(
////                                    this!!.activity!!,
////                                    task.getException().toString(),
////                                    Toast.LENGTH_SHORT
////                                ).show()
////                            }
//
//                }
//
//            }
//        return view
//    }
//
//    inner class MyAdapter(
//        context: Context,
//        studyroomDTOList: ArrayList<StudyRoomDTO>
//    ) : BaseAdapter() {
//        var context = context
//        var studyroomDTO = studyroomDTOList
//
//        override fun getView(
//            position: Int,
//            convertView: View?,
//            parent: ViewGroup?
//        ): View {
//            val view: View = LayoutInflater.from(context)
//                .inflate(R.layout.studyroom_list_item, null)
//
//            var studyroomImage = view.findViewById<ImageView>(R.id.studyroomImage)
//            var studyroomName = view.findViewById<TextView>(R.id.studyroomName)
//            var studyroomAddress =
//                view.findViewById<TextView>(R.id.studyroomAddress)
//
//            //사진 불러오기
//            if (studyroomDTO[position].fileName != "" || studyroomDTO[position].fileName == null) {
//                var storage = FirebaseStorage.getInstance()
//                var storageRef = storage.reference
//                var imageRef = storageRef.child(studyroomDTO[position].fileName.toString())
//
//                imageRef.getDownloadUrl().addOnCompleteListener { task ->
//                    if (task.isSuccessful()) {
//                        // Glide 이용하여 이미지뷰에 로딩
//                        Glide.with(view)
//                            .load(task.getResult())
//                            .override(1024, 980)
//                            .into(studyroomImage)
//                    }
//
//                    studyroomName.text = studyroomDTO[position].name
//                    studyroomAddress.text = studyroomDTO[position].address
//
//                    return view
//                }
//
//                override fun getItem(position: Int): Any {
//                    return studyroomDTO[position]
//                }
//
//                override fun getItemId(position: Int): Long {
//                    return 0
//                }
//
//                override fun getCount(): Int {
//                    return studyroomDTO.size
//                }
//            }
//        }
//    }
//}