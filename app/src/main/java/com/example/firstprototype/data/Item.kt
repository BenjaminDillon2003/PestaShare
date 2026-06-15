package com.example.firstprototype.data

import android.net.Uri
import java.time.LocalDate

// 1. Añadimos el estado para la alerta de economía circular
enum class ItemStatus { AVAILABLE, REQUESTED, BORROWED, RECYCLE_SUGGESTED }

data class SharedItem(
    val id: Int,
    val name: String,
    val owner: String,
    val category: String,
    val status: ItemStatus = ItemStatus.AVAILABLE,
    val imageUri: Uri? = null,

    // 2. Nuevas propiedades inteligentes para PestaShare
    val pointsValue: Int = 50, // Puntos que otorga compartir este artículo
    val createdAt: LocalDate = LocalDate.now() // Fecha de publicación para calcular la antigüedad
)