package com.example.statelag

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class CardViewModel : ViewModel() {

    private val reducer = CardReducer()
    private var data: CardModel? = null

    val state = reducer.state

    fun init(data: CardModel) {
        this.data = data
        val copy = data.copy(text = data.text, duration = (Math.random() * 200).toLong())
        viewModelScope.launch {
            reducer.update(copy)
        }
    }

    class CardReducer {

        val state = MutableStateFlow<CardState>(CardState.None)

        suspend fun update(cardModel: CardModel) = withContext(Dispatchers.IO) {
            state.value = CardState.Content(cardModel)
        }
    }

    data class CardModel(val text: String, val duration: Long)

    sealed class CardState {
        object None : CardState()
        data class Content(val data: CardModel) : CardState()

    }
}