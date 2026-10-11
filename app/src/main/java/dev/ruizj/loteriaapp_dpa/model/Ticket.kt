package dev.ruizj.loteriaapp_dpa.model

data class Ticket(
    val id: String = "",
    val userId: String = "",
    val userEmail: String = "",
    val drawId: String = "test_draw",
    val numbers: List<Int> = emptyList(),
    val createdAt: Long = System.currentTimeMillis()
)
