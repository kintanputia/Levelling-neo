package com.example.daftarkegiatan.room

import android.widget.EditText
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.sql.Date

@Entity
data class Note(
        @PrimaryKey(autoGenerate = true)
        val id: Int=0,
        val title: String,
        val note: String,
        val jurusan: String
)