package com.example.notesapp_room_w6_d4

import android.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.notesapp_room_w6_d4.data.NoteDataBase
import com.example.notesapp_room_w6_d4.data.Note
import com.example.notesapp_room_w6_d4.databinding.ActivityMainBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MainActivity : AppCompatActivity() {
    private val noteDao by lazy { NoteDataBase.getDatabase(this).noteDAO() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        initializeBinding()
        initializeRecycler()
    }

    fun submitButton(view: View) {
        Log.d("TAG MAIN", "Button Pressed")
        addNote()
        updateRV()
    }

    private fun addNote() {
        val text = binding.etNote.text.toString()
        CoroutineScope(IO).launch {
            Log.d("TAG MAIN", "note is: $text")
            noteDao.insertNote(Note(0, text))
        }
        binding.etNote.text.clear()
        Log.d("TAG MAIN", "new data added")
        Toast.makeText(this, "data saved!!", Toast.LENGTH_LONG).show()
    }

    private fun updateNote(note: Note, nNote: String) {
        Log.d("TAG MAIN", "INSIDE UPDATE")
        CoroutineScope(IO).launch {
            noteDao.updateNote(Note(note.id, nNote))
        }
        updateRV()
    }

    private fun deleteNote(note: Note){
        Log.d("TAG MAIN", "INSIDE delete")
        CoroutineScope(IO).launch {
            noteDao.deleteNote(note)
        }
        updateRV()
    }

    private lateinit var messages: ArrayList<Note>
    private fun updateRV() {
        CoroutineScope(IO).launch {
            val data = async {
                noteDao.getAllNotes()
            }.await()
            if(data.isNotEmpty()){
                messages = data as ArrayList<Note>
                Log.d("TAG MAIN", "messages retrived from database: \n$messages")
                withContext(Main){
                    adapter.update(messages)
                    Log.d("TAG MAIN", "RV updated")
                }
            }else{
                Log.e("TAG MAIN", "Unable to get data", )
            }
        }
    }

    private lateinit var binding: ActivityMainBinding
    private fun initializeBinding() {
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        Log.d("TAG MAIN", "Binding")
    }

    private lateinit var adapter: Recycler
    private fun initializeRecycler() {
        adapter = Recycler(this)
        binding.rvMain.adapter = adapter
        binding.rvMain.layoutManager = LinearLayoutManager(this)
        Log.d("TAG MAIN", "RV")
        updateRV()
    }

    fun alertDialog(isUpdate: Boolean, note: Note) {
        val dialogBuilder = AlertDialog.Builder(this)
        val newLayout = LinearLayout(this)
        newLayout.orientation = LinearLayout.VERTICAL
        if (isUpdate) {
            val newTask = EditText(this)
            newTask.hint = "update note: "
            newLayout.addView(newTask)

            Log.d("TAG ALERT", "INSIDE UPDATE")

            dialogBuilder
                .setPositiveButton("Update") { _, _ ->
                    Log.d("TAG ALERT", "INSIDE POS BUTTON")
                    Log.d("TAG ALERT", "NOTE IS: $note")
                    updateNote(note, newTask.text.toString())
                    //adapter.notifyDataSetChanged()
                }
                .setNegativeButton("Cancel") { dialog, _ -> dialog.cancel() }

        } // update note
        else {
            val text = TextView(this)
            text.text = "Are you sure you wish to delete note? "
            newLayout.addView(text)

            //pos > delete
            //neg > cancel
            dialogBuilder
                .setPositiveButton("Delete") { _, _ ->
                    Log.d("TAG ALERT", "INSIDE POS BUTTON")
                    Log.d("TAG ALERT", "NOTE IS: $note")
                    deleteNote(note)
                }
                .setNegativeButton("Cancel") { dialog, _ -> dialog.cancel() }

        } //delete note
        val alert = dialogBuilder.create()
        alert.setTitle("Note")
        alert.setView(newLayout)
        alert.show()
    }
}

