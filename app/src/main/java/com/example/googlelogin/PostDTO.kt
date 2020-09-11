package com.example.googlelogin

data class PostDTO (
    val title: String?=null,
    val writer: String?=null,
    val date: String?=null,
    val content: String?=null,
    val documentName: String?=null,
    val boardMajor: String?=null,
    val fileName:String?=null
)
    :Comparable<PostDTO>, Comparator<PostDTO> {
    override fun compareTo(other: PostDTO): Int {
        return this.date!!.compareTo(other.date!!)
    }

    override fun compare(o1: PostDTO?, o2: PostDTO?): Int {
        return o2!!.date!!.compareTo(o1!!.date!!)
    }
}
