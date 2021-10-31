package com.example.notesapp_room_w6_d4.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [Note::class], version = 1, exportSchema = false)
abstract class NoteDataBase:RoomDatabase() {

    companion object{
        @Volatile
        private var INSTANCE: NoteDataBase? = null

        fun getDatabase(context: Context): NoteDataBase {
            val tempInstance = INSTANCE
            if(tempInstance != null) return tempInstance
            synchronized(this){
                val instance = Room.databaseBuilder(
                    context.applicationContext, NoteDataBase::class.java, "NOTES")
                    .fallbackToDestructiveMigration().build()
                INSTANCE = instance
                return instance
            }
        }
    }
    abstract fun noteDAO(): NoteDAO
}