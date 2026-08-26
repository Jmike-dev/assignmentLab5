package com.example.assignmentlab5

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.assignmentlab5.data.Note
import com.example.assignmentlab5.data.NoteDb
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

// AndroidViewModel (not plain ViewModel) so we can safely hold the
// Application context — it's guaranteed not to leak an Activity.
class NoteViewModel(application: Application) : AndroidViewModel(application) {

    private val dao = NoteDb.getInstance(application).noteDao()

    // stateIn turns the Flow into a StateFlow Compose can read with collectAsState(),
    // starting empty and staying subscribed for 5s after the last collector leaves.
    val notes: StateFlow<List<Note>> = dao.observeAll()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun addNote(title: String) = viewModelScope.launch {
        if (title.isNotBlank()) {
            dao.add(Note(title = title, createdAt = System.currentTimeMillis()))
        }
    }

    fun removeNote(note: Note) = viewModelScope.launch {
        dao.remove(note)
    }
}