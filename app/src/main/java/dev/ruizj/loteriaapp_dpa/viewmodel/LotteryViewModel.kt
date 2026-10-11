package dev.ruizj.loteriaapp_dpa.viewmodel

import androidx.lifecycle.ViewModel
import dev.ruizj.loteriaapp_dpa.model.DrawStatus
import dev.ruizj.loteriaapp_dpa.model.LotteryState
import dev.ruizj.loteriaapp_dpa.model.Ticket
import dev.ruizj.loteriaapp_dpa.repository.LotteryRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class LotteryViewModel(
    private val lotteryRepository: LotteryRepository = LotteryRepository()
) : ViewModel() {

    private val _lotteryState = MutableStateFlow(LotteryState())
    val lotteryState: StateFlow<LotteryState> = _lotteryState.asStateFlow()

    fun initLottery(userId: String, drawId: String = "test_draw") {
        if (userId.isBlank()) return

        _lotteryState.value = _lotteryState.value.copy(isLoading = true)

        // Listen to Draw updates
        lotteryRepository.listenToDraw(
            drawId = drawId,
            onUpdate = { draw ->
                _lotteryState.value = _lotteryState.value.copy(
                    currentDraw = draw,
                    isLoading = false
                )
            },
            onError = { error ->
                _lotteryState.value = _lotteryState.value.copy(
                    errorMessage = error,
                    isLoading = false
                )
            }
        )

        // Listen to User Ticket
        lotteryRepository.listenToUserTicket(
            userId = userId,
            drawId = drawId,
            onUpdate = { ticket ->
                val selected = ticket?.numbers?.toSet() ?: _lotteryState.value.selectedNumbers
                _lotteryState.value = _lotteryState.value.copy(
                    userTicket = ticket,
                    selectedNumbers = selected,
                    isLoading = false
                )
            },
            onError = { error ->
                _lotteryState.value = _lotteryState.value.copy(
                    errorMessage = error,
                    isLoading = false
                )
            }
        )
    }

    fun toggleNumberSelection(number: Int) {
        if (number !in 1..45) return

        // Do not allow toggling if ticket is already registered or draw is not OPEN
        if (_lotteryState.value.currentDraw.status != DrawStatus.OPEN) {
            _lotteryState.value = _lotteryState.value.copy(
                errorMessage = "El sorteo ya no está abierto. No se pueden modificar las jugadas."
            )
            return
        }

        val currentSet = _lotteryState.value.selectedNumbers.toMutableSet()
        if (currentSet.contains(number)) {
            currentSet.remove(number)
        } else {
            if (currentSet.size >= 6) {
                _lotteryState.value = _lotteryState.value.copy(
                    errorMessage = "Solo puedes seleccionar exactamente 6 números."
                )
                return
            }
            currentSet.add(number)
        }

        _lotteryState.value = _lotteryState.value.copy(
            selectedNumbers = currentSet,
            errorMessage = null
        )
    }

    fun quickPick() {
        if (_lotteryState.value.currentDraw.status != DrawStatus.OPEN) {
            _lotteryState.value = _lotteryState.value.copy(
                errorMessage = "El sorteo no está abierto para selección."
            )
            return
        }

        val randomNumbers = (1..45).shuffled().take(6).sorted().toSet()
        _lotteryState.value = _lotteryState.value.copy(
            selectedNumbers = randomNumbers,
            errorMessage = null
        )
    }

    fun clearSelectedNumbers() {
        if (_lotteryState.value.currentDraw.status != DrawStatus.OPEN) return
        _lotteryState.value = _lotteryState.value.copy(
            selectedNumbers = emptySet()
        )
    }

    fun registerTicket(
        userId: String,
        userEmail: String,
        drawId: String = "test_draw"
    ) {
        val state = _lotteryState.value

        if (state.currentDraw.status != DrawStatus.OPEN) {
            _lotteryState.value = state.copy(
                errorMessage = "Las jugadas solo se pueden registrar mientras el sorteo está abierto."
            )
            return
        }

        val selectedList = state.selectedNumbers.toList().sorted()
        if (selectedList.size != 6) {
            _lotteryState.value = state.copy(
                errorMessage = "Debes seleccionar exactamente 6 números distintos entre 1 y 45."
            )
            return
        }

        if (selectedList.any { it !in 1..45 }) {
            _lotteryState.value = state.copy(
                errorMessage = "Los números deben estar comprendidos entre 1 y 45 inclusive."
            )
            return
        }

        _lotteryState.value = state.copy(isLoading = true)

        val newTicket = Ticket(
            id = "${userId}_$drawId",
            userId = userId,
            userEmail = userEmail,
            drawId = drawId,
            numbers = selectedList,
            createdAt = System.currentTimeMillis()
        )

        lotteryRepository.saveUserTicket(
            ticket = newTicket,
            onSuccess = {
                _lotteryState.value = _lotteryState.value.copy(
                    isLoading = false,
                    successMessage = "¡Jugada registrada con éxito!",
                    errorMessage = null
                )
            },
            onError = { error ->
                _lotteryState.value = _lotteryState.value.copy(
                    isLoading = false,
                    errorMessage = error
                )
            }
        )
    }

    fun clearMessages() {
        _lotteryState.value = _lotteryState.value.copy(
            errorMessage = null,
            successMessage = null
        )
    }

    override fun onCleared() {
        super.onCleared()
        lotteryRepository.stopListeners()
    }
}
