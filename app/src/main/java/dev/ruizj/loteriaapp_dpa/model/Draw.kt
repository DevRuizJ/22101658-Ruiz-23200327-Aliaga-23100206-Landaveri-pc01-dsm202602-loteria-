package dev.ruizj.loteriaapp_dpa.model

enum class DrawStatus {
    OPEN,
    IN_PROGRESS,
    FINISHED;

    companion object {
        fun fromString(value: String?): DrawStatus {
            return when (value?.uppercase()) {
                "IN_PROGRESS", "INICIADO" -> IN_PROGRESS
                "FINISHED", "FINALIZADO" -> FINISHED
                else -> OPEN
            }
        }
    }
}

data class Draw(
    val id: String = "test_draw",
    val name: String = "Sorteo de Prueba - PC01",
    val date: String = "2026-02-23",
    val status: DrawStatus = DrawStatus.OPEN,
    val drawnNumbers: List<Int> = emptyList()
)
