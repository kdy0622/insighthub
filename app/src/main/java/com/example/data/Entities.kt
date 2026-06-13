package com.example.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "folders")
data class Folder(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val name: String,
    val colorHex: String
)

@Entity(tableName = "lectures")
data class Lecture(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val folderId: Int? = null,
    val title: String,
    val sourceLink: String,
    val speaker: String,
    val transcript: String,
    val summary: String,
    val strategy: String,       // Usana strategic analysis report (mindset, recruit, sales)
    val processedData: String,  // Usana-specific customized products sheets details and templates
    val language: String,       // ko, en, ja, zh
    val shareToken: String,     // simulated share token used to create sharing link
    val googleDocsLink: String, // Google Docs link generated on export
    val createdAt: Long = System.currentTimeMillis()
)
