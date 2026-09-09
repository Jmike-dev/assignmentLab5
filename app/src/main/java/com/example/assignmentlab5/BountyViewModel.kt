package com.example.assignmentlab5

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.assignmentlab5.data.Bounty
import com.example.assignmentlab5.data.BountyDb
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class BountyViewModel(application: Application) : AndroidViewModel(application) {

    private val dao = BountyDb.getInstance(application).bountyDao()

    private val _isLoading = MutableStateFlow(true)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    val bounties: StateFlow<List<Bounty>> = dao.observeAll()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    init {
        viewModelScope.launch {
            delay(1500)
            _isLoading.value = false
        }
    }

    fun acceptBounty(bounty: Bounty) = viewModelScope.launch {
        dao.update(bounty.copy(isAccepted = true))
    }

    fun abandonBounty(bounty: Bounty) = viewModelScope.launch {
        dao.delete(bounty)
    }

    fun advanceProgress(bounty: Bounty) = viewModelScope.launch {
        val nextProgress = if (bounty.progress >= 1f) 0f else (bounty.progress + 0.25f).coerceAtMost(1f)
        dao.update(bounty.copy(progress = nextProgress))
    }
}