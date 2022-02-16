package com.vritant.foodon.customerUtils

data class Customer(
    val city : String?,
    val FirstName : String?,
    val LastName : String?,
    val Password : String?,
    val ConfirmPassword : String?,
    val EmailId : String?,
    val MobileNo : String?,
    val State : String?,
    val Area : String?,
    val LocalAddress : String?
    ){
    constructor() : this(null,null,null,null,null,null,null,null,null,null)
}
