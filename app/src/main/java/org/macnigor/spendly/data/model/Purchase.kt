package org.macnigor.spendly.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "purchases")
data class Purchase(
    @PrimaryKey(autoGenerate = true)
    val id:Int=0,
    val category: String,
    val name: String,
    val amount: Double,
    val date: String
)