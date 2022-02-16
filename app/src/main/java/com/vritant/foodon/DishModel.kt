package com.vritant.foodon

data class DishModel(val Dishes : String?,
                     val RandomUID : String?,
                     val Description : String?,
                     val Quantity : String?,
                     val Price : String?,
                     val ImageURL : String?,
                     val ChefId : String?
                     ){
    constructor() : this(null,null,null,null,null,null,null)
}
