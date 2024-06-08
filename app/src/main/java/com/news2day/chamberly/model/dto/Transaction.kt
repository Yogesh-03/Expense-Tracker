package com.news2day.chamberly.model.dto

data class Transaction(
    val id:String = "",
    val userId:String = "",
    val date:String = "",
    val time:String = "",
    val amount:String = "",
    val category:String = "",
    val paymentMethod:String? = "",
    val refNumber:String? = "",
    val description:String? = " "
)