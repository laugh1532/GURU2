package com.example.googlelogin

data class StudyListDTO (
    val studyTitle : String?= null,
    val studyLocation : String?=null,
    val studyTheme : String?=null,
    val studyDocumentName : String?=null,
    val studyDate : String?=null,
    val boardname : String?="스터디게시판"
)
    :Comparable<StudyListDTO>, Comparator<StudyListDTO> {
    override fun compareTo(other: StudyListDTO): Int {
        return this.studyDate!!.compareTo(other.studyDate!!)
    }

    override fun compare(o1: StudyListDTO?, o2: StudyListDTO?): Int {
        return o2!!.studyDate!!.compareTo(o1!!.studyDate!!)
    }
}
