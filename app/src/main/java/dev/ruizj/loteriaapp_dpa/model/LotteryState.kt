package dev.ruizj.loteriaapp_dpa.model

data class LotteryState(
    val isLoading: Boolean = false,
    val currentDraw: Draw = Draw(),
    val userTicket: Ticket? = null,
    val selectedNumbers: Set<Int> = emptySet(),
    val errorMessage: String? = null,
    val successMessage: String? = null
) {
    val matchedNumbers: List<Int>
        get() = userTicket?.numbers?.filter { currentDraw.drawnNumbers.contains(it) } ?: emptyList()

    val hitsCount: Int
        get() = matchedNumbers.size

    val isWinner: Boolean
        get() = hitsCount == 6

    val hitsPercentage: Float
        get() = (hitsCount / 6.0f) * 100f
}
