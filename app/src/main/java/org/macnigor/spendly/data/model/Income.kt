package org.macnigor.spendly.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "income")
class Income (
    @PrimaryKey(autoGenerate = true)
    val id:Int=0,
    val name: String,
    val amount: Double,
    val date: String)