package com.vritant.foodon.chefUtils

data class Chef(
    val Area : String?,
    val City : String?,
    val EmailId : String?,
    val FirstName : String?,
    val LastName : String?,
    val House : String?,
    val Mobile : String?,
    val Password : String?,
    val PostCode : String?,
    val State : String?
){
    constructor() : this(null,null,null,null,null,null,null,null,null,null)
}
