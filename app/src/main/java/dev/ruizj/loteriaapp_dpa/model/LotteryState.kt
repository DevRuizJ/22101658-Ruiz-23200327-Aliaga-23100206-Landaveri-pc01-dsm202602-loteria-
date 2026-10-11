package dev.ruizj.loteriaapp_dpa.model

data class LotteryState(
    val isLoading: Boolean = false,
    val currentDraw: Draw = Draw(),
    val userTicket: Ticket? = null,
    val selectedNumbers: Set<Int> = emptySet(),
    val errorMessage: String? = null,
    val successMessage: String? = null
) {
    val publishedBallsCount: Int
        get() = currentDraw.drawnNumbers.size.coerceAtMost(6)

    val matchedNumbers: List<Int>
        get() = userTicket?.numbers?.filter { currentDraw.drawnNumbers.contains(it) } ?: emptyList()

    val hitsCount: Int
        get() = matchedNumbers.size

    val isDrawFinishedAndComplete: Boolean
        get() = currentDraw.status == DrawStatus.FINISHED && currentDraw.drawnNumbers.size >= 6

    val finalResultText: String?
        get() {
            return if (isDrawFinishedAndComplete) {
                if (hitsCount == 6) "GANADOR" else "SIGUE INTENTANDO"
            } else null
        }
}
