package com.example.myapplication

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
import com.google.firebase.firestore.FirebaseFirestore
import java.util.*
import kotlin.collections.ArrayList

class PostList : Fragment() {
    //fragment
    var showPost = ShowPost()
    var writePost = WritePost()

    //firestore 연결 준비
    val firestore = FirebaseFirestore.getInstance()

    //실제로 글 목록 보여줄 arrayList
    var postList = ArrayList<PostListDTO>()

    //boardName 받아오는 함수 정의
    fun newInstance(boardName:String):Fragment{
        var fragment = PostList()
        var bundle = Bundle()

        bundle.putString("boardName", boardName)
        fragment.setArguments(bundle)

        return fragment
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        //db에서 글 받을 객체를 담은 리스트 초기화, 원본 데이터
        var PostListDTOList:ArrayList<PostListDTO> = ArrayList<PostListDTO>()

        // view 생성
        var view = inflater.inflate(R.layout.fragment_post_list, container, false)

        //listView 생성
        var postListView: ListView = view.findViewById(R.id.postListView)

        //button 생성
        var button2 : Button = view.findViewById<Button>(R.id.button2)
        var btn_back : ImageButton = view.findViewById(R.id.btn_back)

        //editText 생성
        var editText:EditText = view.findViewById(R.id.editText)

        //textView 가져오기
        var textView:TextView = view.findViewById(R.id.textView)

        //게시판 이름 가져오기
        textView.text = getArguments()?.getString("boardName")


        //db에서 글 데이터 가져오기
        firestore.collection("Board")
            .whereEqualTo("boardMajor", textView.text.toString())
            .get()
            .addOnSuccessListener { result ->
                // 글 하나 받아올 postlistDTO
                var postlistDTO:PostListDTO

                for (document in result) {
                    postlistDTO = document.toObject(PostListDTO::class.java)
                    PostListDTOList.add(postlistDTO) //리스트에 글 저장
                }

                //내림차순 정렬
                Collections.sort(PostListDTOList, PostListDTO())

                //원본 데이터를 실제 사용할 리스트에 담기
                postList = PostListDTOList

                //listView에 adapter 연결
                var adapter = MyAdapter(this!!.activity!!, PostListDTOList)
                postListView.adapter = adapter

            }

        //listView 클릭시 해당 post로 이동
        postListView.onItemClickListener = AdapterView.OnItemClickListener { parent, view, position, id ->
            //editText.setText("")
            fragmentManager!!.beginTransaction()
                .replace(R.id.nav_host_fragment, showPost.newInstance2(postList[position].title!!))
                .addToBackStack(null)
                .commit()
        }

        //글쓰기 button 이벤트
        button2.setOnClickListener {
            //editText.setText("")
            fragmentManager!!.beginTransaction()
                .replace(R.id.nav_host_fragment, writePost.newInstance(textView.text.toString()))
                .addToBackStack(null)
                .commit()
        }

        //뒤로가기 button 이벤트
        btn_back.setOnClickListener { view ->
            //editText.setText("")
            fragmentManager!!.popBackStack()
        }

        //검색기능
        editText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
            }

            override fun afterTextChanged(s: Editable) {
                val search = s.toString()
                (postListView.getAdapter() as PostList.MyAdapter?)?.getFilter()?.filter(search)
            }
        })

        return view
    }

    inner class MyAdapter(context: Context, postListDTOList: ArrayList<PostListDTO>) : BaseAdapter(), Filterable{
        var context = context
        var postlistDTO = postListDTOList //원본 arraylist
        var filteredItemList:ArrayList<PostListDTO> = postlistDTO //필터링된 arraylist

        //리스트 필터
        var listFilter: Filter? = null

        override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
            val view: View = LayoutInflater.from(context).inflate(R.layout.post_list_item, null)

            //textView 받아오기
            var title = view.findViewById<TextView>(R.id.title)
            var content = view.findViewById<TextView>(R.id.content)
            var date = view.findViewById<TextView>(R.id.date)

            var tmp : PostListDTO = filteredItemList[position]

            //받아온 textView에 정보 저장
            title.text = tmp.title
            content.text = tmp.content
            date.text = tmp.date

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
                    result.values = postlistDTO
                    result.count = postlistDTO.size
                }
                else{
                    var itemList : ArrayList<PostListDTO> = ArrayList<PostListDTO>()

                    //글자 비교
                    for(item:PostListDTO in postlistDTO){

                        //검색어와 글 제목 비교
                        if(item.title!!.toUpperCase().contains(constraint.toString().toUpperCase())){
                            itemList.add(item)
                        }

                        //검색어와 글 내용 비교
                        else if(item.content!!.toUpperCase().contains(constraint.toString().toUpperCase())){
                            itemList.add(item)
                        }
                    }

                    //필터링 된 리스트를 실제 사용할 리스트에 담기
                    postList = itemList

                    result.values = itemList
                    result.count = itemList.size
                }
                return result
            }

            override fun publishResults(constraint: CharSequence?, results: FilterResults?) {
                filteredItemList = results?.values as ArrayList<PostListDTO>

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