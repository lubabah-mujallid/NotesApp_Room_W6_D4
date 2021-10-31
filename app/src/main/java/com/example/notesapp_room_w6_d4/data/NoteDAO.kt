package com.example.notesapp_room_w6_d4.data

import androidx.room.*

@Dao
interface NoteDAO {
    @Query("SELECT * FROM NOTES ORDER BY id ASC")
    fun getAllNotes(): List<Note>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insertNote(note: Note)

    @Update
    fun updateNote(note: Note)

    @Delete
    fun deleteNote(note: Note)

}