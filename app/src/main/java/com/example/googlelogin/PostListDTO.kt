package com.example.googlelogin

data class PostListDTO(
    val title: String?=null,
    val date: String?=null,
    var content: String?=null,
    val documentname: String?=null
):Comparable<PostListDTO>, Comparator<PostListDTO> {
    override fun compareTo(other: PostListDTO): Int {
        return this.date!!.compareTo(other.date!!)
    }

    override fun compare(o1: PostListDTO?, o2: PostListDTO?): Int {
        return o2!!.date!!.compareTo(o1!!.date!!)
    }
}