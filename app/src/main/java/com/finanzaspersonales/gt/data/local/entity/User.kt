package com.finanzaspersonales.gt.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "User")
data class User(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val nombreUsuario: String,
    val passwordHash: String,
    val fechaCreacion: Long
)
