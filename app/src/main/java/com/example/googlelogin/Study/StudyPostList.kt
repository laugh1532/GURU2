package com.example.googlelogin.Study


import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import com.example.googlelogin.PostListDTO
import com.example.googlelogin.R
import com.example.googlelogin.StudyListDTO
import com.example.googlelogin2.MapView
import com.example.myapplication.PostList
import com.example.myapplication.ShowPost
import com.example.myapplication.WritePost
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.android.synthetic.main.fragment_map_view.*
import kotlinx.android.synthetic.main.fragment_study_post_list.*

/**
 * A simple [Fragment] subclass.
 */
class StudyPostList : Fragment() {

    //fragment
    var mapView = MapView()
    var StudyShow = StudyShow()
    var StudyWrite = StudyWrite()

    //firestore 연결 준비
    val firestore = FirebaseFirestore.getInstance()

    //실제로 글 목록 보여줄 arrayList
    var studyPostList = ArrayList<StudyListDTO>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        //db에서 글 받을 객체를 담은 리스트 초기화, 원본 데이터
        var studyListDTOList:ArrayList<StudyListDTO> = ArrayList<StudyListDTO>()

        // view 생성
        var view = inflater.inflate(R.layout.fragment_study_post_list, container, false)

        //listView 생성
        var studypostListView: ListView = view.findViewById(R.id.studypostListView)

        //button 생성
        var btn_write :Button = view.findViewById(R.id.btn_write)
        var btn_map : Button = view.findViewById(R.id.btn_map)

        //editText 생성
        var editText: EditText = view.findViewById(R.id.editText)


        //db에서 데이터 받아오기
        firestore?.collection("Study")
            ?.orderBy("studyDate", Query.Direction.DESCENDING)
            ?.get()
            ?.addOnSuccessListener { result ->
                var studylistDTO:StudyListDTO
                for (document in result) {
                    studylistDTO = document.toObject(StudyListDTO::class.java)
                    studyListDTOList.add(studylistDTO)
                }
                studyPostList = studyListDTOList

                //listView에 adapter 연결
                studypostListView.adapter = MyAdapter(this!!.activity!!, studyListDTOList)
            }

        //listView 클릭시 해당 post로 이동
        studypostListView.onItemClickListener = AdapterView.OnItemClickListener { parent, view, position, id ->
            /***** ShowPost()로 데이터 넘기기 *****/
            fragmentManager!!.beginTransaction()
                .replace(R.id.nav_host_fragment, StudyShow.newInstance3(studyPostList[position].studyTitle!!))
                .addToBackStack(null)
                .commit()
        }

        //글쓰기 button 이벤트
        btn_write.setOnClickListener {
            fragmentManager!!.beginTransaction()
                .replace(R.id.nav_host_fragment, StudyWrite)
                .addToBackStack(null)
                .commit()
        }
        //map 버튼
        btn_map.setOnClickListener {
            fragmentManager!!.beginTransaction()
                .replace(R.id.nav_host_fragment,mapView)
                .addToBackStack(null)
                .commit()
        }

        //검색 기능
        editText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
            }

            override fun afterTextChanged(s: Editable) {
                val search = s.toString()
                (studypostListView.getAdapter() as StudyPostList.MyAdapter?)?.getFilter()?.filter(search)
            }
        })

        return view
    }

    inner class MyAdapter(context: Context, studyListDTOList:ArrayList<StudyListDTO>) : BaseAdapter(),
        Filterable {
        var context = context

        var studyListDTO = studyListDTOList //원본 arrayList
        var filteredItemList: ArrayList<StudyListDTO> = studyListDTO //필터링 된 arrayList

        var listFilter: Filter? = null //리스트 필터

        override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
            val view: View = LayoutInflater.from(context).inflate(R.layout.study_list_item, null)

            //textView 불러오기
            var title = view.findViewById<TextView>(R.id.title)
            var location = view.findViewById<TextView>(R.id.location)
            var theme = view.findViewById<TextView>(R.id.theme)

            //필터링 된 postList 하나씩 빼내기
            var tmp = filteredItemList[position]

            //필터링 된 postList에 담긴 정보 textView에 적용시키기
            title.text = tmp.studyTitle
            location.text = tmp.studyLocation
            theme.text = tmp.studyTheme

            return view
        }

        //검색기능 구현, filterable 오버라이딩
        override fun getFilter(): Filter? {
            if(listFilter == null){
                listFilter = ListFilter()
            }
            return listFilter
        }

        //검색 기능 구현
        inner class ListFilter() : Filter() {
            override fun performFiltering(constraint: CharSequence?): FilterResults {
                var result : FilterResults = FilterResults()

                if(constraint == null || constraint.length == 0){
                    result.values = studyListDTO
                    result.count = studyListDTO.size
                }
                else{
                    var itemList : ArrayList<StudyListDTO> = ArrayList<StudyListDTO>()

                    //글자 비교
                    for(item:StudyListDTO in studyListDTO){

                        //검색어와 제목 비교
                        if(item.studyTitle!!.toUpperCase().contains(constraint.toString().toUpperCase())){
                            itemList.add(item)
                        }

                        //검색어와 주제 비교
                        else if(item.studyTheme!!.toUpperCase().contains(constraint.toString().toUpperCase())){
                            itemList.add(item)
                        }

                        //검색어와 장소 비교
                        else if(item.studyLocation!!.toUpperCase().contains(constraint.toString().toUpperCase())){
                            itemList.add(item)
                        }
                    }

                    //필터링 된 리스트를 실제 사용할 리스트에 담기
                    studyPostList = itemList

                    result.values = itemList
                    result.count = itemList.size
                }
                return result
            }

            override fun publishResults(constraint: CharSequence?, results: FilterResults?) {
                filteredItemList = results?.values as ArrayList<StudyListDTO>

                if(results.count > 0) {
                    notifyDataSetChanged()
                }
                else{
                    notifyDataSetInvalidated()
                }
            }
        }

        override fun getItem(position: Int): Any {
            return filteredItemList[position]
        }

        override fun getItemId(position: Int): Long {
            return 0
        }

        override fun getCount(): Int {
            return filteredItemList.size
        }
    }
}