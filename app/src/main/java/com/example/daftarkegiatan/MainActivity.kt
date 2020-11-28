package com.example.daftarkegiatan

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.daftarkegiatan.room.Constant
import com.example.daftarkegiatan.room.Note
import com.example.daftarkegiatan.room.NoteDB
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.*

class MainActivity : AppCompatActivity() {

    private val db by lazy { NoteDB (this) }
    lateinit var noteAdapter: NoteAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setupView()
        setupListener()
        setupRecyclerView()
    }

    override fun onResume(){
        super.onResume()
        loadData()
    }

    private fun loadData(){
        CoroutineScope(Dispatchers.IO).launch {
            noteAdapter.setData(db.noteDao().getNotes())
            withContext(Dispatchers.Main) {
                noteAdapter.notifyDataSetChanged()
            }
        }
    }

    private fun setupView(){
        supportActionBar!!.apply{
            title="Catatan"
        }
    }
    private fun setupListener(){
        button_create.setOnClickListener {
            intentEdit(Constant.TYPE_CREATE,0)
        }
    }
    private fun setupRecyclerView(){
        noteAdapter= NoteAdapter(
                arrayListOf(),
                object:NoteAdapter.OnAdapterListener{
                    override fun onClick(note: Note) {
                        //membaca detail
                        intentEdit(Constant.TYPE_READ,note.id)
                    }

                    override fun onUpdate(note: Note) {
                        intentEdit(Constant.TYPE_UPDATE,note.id)
                    }

                    override fun onDelete(note: Note) {
                        deleteAlert(note)
                    }
                })
        list_note.apply{
            layoutManager=LinearLayoutManager(applicationContext)
            adapter=noteAdapter
        }
    }
    private fun intentEdit(intent_type:Int, note_id:Int){
        startActivity(
                Intent(this, EditActivity::class.java)
                .putExtra("intent_type",intent_type)
                .putExtra("note_id",note_id)
        )
    }



    private fun deleteAlert(note: Note){
        val dialog = AlertDialog.Builder(this)
        dialog.apply {
            setTitle("Konfirmasi")
            setMessage("Apakah anda yakin ingin menghapus ${note.title}?")
            setNegativeButton("Batal") { dialogInterface, i ->
                dialogInterface.dismiss()
            }
            setPositiveButton("Hapus") { dialogInterface, i ->
                CoroutineScope(Dispatchers.IO).launch{
                    db.noteDao().deleteNote(note)
                    dialogInterface.dismiss()
                    loadData()
                }
            }
        }
        dialog.show()
    }
}