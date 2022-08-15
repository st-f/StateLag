package com.example.statelag

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class CardViewModel : ViewModel() {

    private val reducer = CardReducer()
    val state = reducer.state

    fun init(data: CardModel) {
        viewModelScope.launch {
            reducer.update(data)
        }
    }

    class CardReducer {

        val state = MutableStateFlow<CardState>(CardState.None)

        suspend fun update(cardModel: CardModel) = withContext(Dispatchers.IO) {
            state.value = map(cardModel)
        }

        private fun map(cardModel: CardModel): CardState.Content {
            return CardState.Content(cardModel.copy(number = (Math.random() * 100).toLong()))
        }
    }

    data class CardModel(val number: Long)

    sealed class CardState {
        object None : CardState()
        data class Content(val data: CardModel) : CardState()
    }
}