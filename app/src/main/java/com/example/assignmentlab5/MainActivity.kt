package com.example.assignmentlab5

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SwipeToDismissBox
import androidx.compose.material3.SwipeToDismissBoxValue
import androidx.compose.material3.Text
import androidx.compose.material3.rememberSwipeToDismissBoxState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import com.example.assignmentlab5.data.Note
import com.example.assignmentlab5.ui.theme.AssignmentLab5Theme

class MainActivity : ComponentActivity() {

    // by viewModels() gets us a NoteViewModel scoped to this Activity's lifecycle
    private val viewModel: NoteViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            AssignmentLab5Theme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    NoteScreen(
                        viewModel = viewModel,
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }
    }
}

@Composable
fun NoteScreen(viewModel: NoteViewModel, modifier: Modifier = Modifier) {
    // collectAsState subscribes to the StateFlow; this recomposes automatically
    // every time the underlying table changes — no manual refresh needed.
    val notes by viewModel.notes.collectAsState()
    var text by remember { mutableStateOf("") }

    Column(modifier = modifier.fillMaxSize().padding(16.dp)) {
        Row(modifier = Modifier.fillMaxWidth()) {
            OutlinedTextField(
                value = text,
                onValueChange = { text = it },
                label = { Text("New note") },
                modifier = Modifier.weight(1f)
            )
            Button(
                onClick = {
                    viewModel.addNote(text)
                    text = ""
                },
                modifier = Modifier.padding(start = 8.dp)
            ) {
                Text("Add")
            }
        }

        LazyColumn(
            modifier = Modifier.padding(top = 16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // key = note.id keeps each row's animation/state tied to the right
            // item even as the list is reordered or items are removed.
            items(notes, key = { it.id }) { note: Note ->
                SwipeableNoteCard(
                    note = note,
                    onDelete = { viewModel.removeNote(note) },
                    onToggleDone = { viewModel.toggleNote(note) },
                    modifier = Modifier.animateItem()
                )
            }
        }
    }
}

/**
 * A single todo card that can be swiped:
 *  - swipe RIGHT (start -> end) marks the note done/undone
 *  - swipe LEFT  (end -> start) deletes the note
 *
 * The card fades/shrinks out with [AnimatedVisibility] right before the
 * delete callback actually removes it from the list, so it doesn't just
 * pop out of existence.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SwipeableNoteCard(
    note: Note,
    onDelete: () -> Unit,
    onToggleDone: () -> Unit,
    modifier: Modifier = Modifier
) {
    // Controls the exit animation; we flip this to false first, let the
    // shrink/fade animation play, then actually delete the underlying item.
    var visible by remember { mutableStateOf(true) }
    var pendingDelete by remember { mutableStateOf(false) }

    val dismissState = rememberSwipeToDismissBoxState(
        confirmValueChange = { value ->
            when (value) {
                SwipeToDismissBoxValue.EndToStart -> {
                    // Swiped left -> delete
                    pendingDelete = true
                    visible = false
                    true
                }
                SwipeToDismissBoxValue.StartToEnd -> {
                    // Swiped right -> toggle done, then snap back to resting position
                    onToggleDone()
                    false
                }
                SwipeToDismissBoxValue.Settled -> false
            }
        }
    )

    // Once the exit animation finishes playing, actually remove the item.
    LaunchedEffect(pendingDelete) {
        if (pendingDelete) {
            kotlinx.coroutines.delay(250)
            onDelete()
        }
    }

    AnimatedVisibility(
        visible = visible,
        exit = shrinkVertically(animationSpec = tween(250)) + fadeOut(animationSpec = tween(200)),
        enter = fadeIn(animationSpec = tween(200))
    ) {
        SwipeToDismissBox(
            state = dismissState,
            modifier = modifier,
            backgroundContent = {
                SwipeBackground(dismissState.dismissDirection)
            }
        ) {
            NoteCard(note = note, onToggleDone = onToggleDone)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SwipeBackground(direction: SwipeToDismissBoxValue) {
    val (color, icon, alignment) = when (direction) {
        SwipeToDismissBoxValue.StartToEnd ->
            Triple(Color(0xFF2E7D32), Icons.Default.CheckCircle, Alignment.CenterStart)
        SwipeToDismissBoxValue.EndToStart ->
            Triple(Color(0xFFC62828), Icons.Default.Delete, Alignment.CenterEnd)
        SwipeToDismissBoxValue.Settled ->
            Triple(Color.Transparent, null, Alignment.Center)
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(color, RoundedCornerShape(12.dp))
            .padding(horizontal = 20.dp),
        contentAlignment = alignment
    ) {
        icon?.let {
            Icon(imageVector = it, contentDescription = null, tint = Color.White, modifier = Modifier.size(28.dp))
        }
    }
}

@Composable
private fun NoteCard(note: Note, onToggleDone: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (note.isDone)
                MaterialTheme.colorScheme.surfaceVariant
            else
                MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 14.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = note.title,
                textDecoration = if (note.isDone) TextDecoration.LineThrough else TextDecoration.None,
                color = if (note.isDone)
                    MaterialTheme.colorScheme.onSurfaceVariant
                else
                    MaterialTheme.colorScheme.onSurface
            )
        }
    }
}