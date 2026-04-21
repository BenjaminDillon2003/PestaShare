package com.example.firstprototype.data

enum class ItemStatus { AVAILABLE, REQUESTED, BORROWED }

data class SharedItem(
    val id: Int,
    val name: String,
    val owner: String,
    val category: String,
    val status: ItemStatus = ItemStatus.AVAILABLE
)