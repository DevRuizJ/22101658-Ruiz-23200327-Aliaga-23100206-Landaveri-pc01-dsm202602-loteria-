package dev.ruizj.loteriaapp_dpa.repository

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import dev.ruizj.loteriaapp_dpa.model.Draw
import dev.ruizj.loteriaapp_dpa.model.DrawStatus
import dev.ruizj.loteriaapp_dpa.model.Ticket

class LotteryRepository(
    private val db: FirebaseFirestore = FirebaseFirestore.getInstance()
) {
    private var drawListener: ListenerRegistration? = null
    private var ticketListener: ListenerRegistration? = null

    fun listenToDraw(
        drawId: String = "test_draw",
        onUpdate: (Draw) -> Unit,
        onError: (String) -> Unit
    ) {
        drawListener?.remove()
        val docRef = db.collection("draws").document(drawId)

        drawListener = docRef.addSnapshotListener { snapshot, error ->
            if (error != null) {
                onError(error.localizedMessage ?: "Error listening to draw updates.")
                return@addSnapshotListener
            }

            if (snapshot != null && snapshot.exists()) {
                val name = snapshot.getString("name") ?: "Sorteo de Prueba - PC01"
                val date = snapshot.getString("date") ?: "2026-02-23"
                val statusStr = snapshot.getString("status") ?: "OPEN"
                val rawNumbers = snapshot.get("drawnNumbers") as? List<*>
                val drawnNumbers = rawNumbers?.mapNotNull { (it as? Number)?.toInt() } ?: emptyList()

                val draw = Draw(
                    id = drawId,
                    name = name,
                    date = date,
                    status = DrawStatus.fromString(statusStr),
                    drawnNumbers = drawnNumbers
                )
                onUpdate(draw)
            } else {
                // Initial creation if the document does not exist in Firestore
                val initialDraw = Draw(
                    id = drawId,
                    name = "Sorteo de Prueba - PC01",
                    date = "2026-02-23",
                    status = DrawStatus.OPEN,
                    drawnNumbers = emptyList()
                )
                val initialData = hashMapOf(
                    "name" to initialDraw.name,
                    "date" to initialDraw.date,
                    "status" to "OPEN",
                    "drawnNumbers" to emptyList<Int>()
                )
                docRef.set(initialData)
                    .addOnSuccessListener { onUpdate(initialDraw) }
                    .addOnFailureListener { e -> onError(e.localizedMessage ?: "Error initializing draw.") }
            }
        }
    }

    fun listenToUserTicket(
        userId: String,
        drawId: String = "test_draw",
        onUpdate: (Ticket?) -> Unit,
        onError: (String) -> Unit
    ) {
        ticketListener?.remove()
        if (userId.isBlank()) {
            onUpdate(null)
            return
        }

        val ticketDocId = "${userId}_$drawId"
        ticketListener = db.collection("tickets").document(ticketDocId)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    onError(error.localizedMessage ?: "Error listening to ticket updates.")
                    return@addSnapshotListener
                }

                if (snapshot != null && snapshot.exists()) {
                    val uId = snapshot.getString("userId") ?: userId
                    val uEmail = snapshot.getString("userEmail") ?: ""
                    val dId = snapshot.getString("drawId") ?: drawId
                    val rawNumbers = snapshot.get("numbers") as? List<*>
                    val numbers = rawNumbers?.mapNotNull { (it as? Number)?.toInt() } ?: emptyList()
                    val createdAt = snapshot.getLong("createdAt") ?: System.currentTimeMillis()

                    val ticket = Ticket(
                        id = ticketDocId,
                        userId = uId,
                        userEmail = uEmail,
                        drawId = dId,
                        numbers = numbers,
                        createdAt = createdAt
                    )
                    onUpdate(ticket)
                } else {
                    onUpdate(null)
                }
            }
    }

    fun saveUserTicket(
        ticket: Ticket,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        val ticketDocId = "${ticket.userId}_${ticket.drawId}"
        val ticketData = hashMapOf(
            "id" to ticketDocId,
            "userId" to ticket.userId,
            "userEmail" to ticket.userEmail,
            "drawId" to ticket.drawId,
            "numbers" to ticket.numbers,
            "createdAt" to ticket.createdAt
        )

        db.collection("tickets").document(ticketDocId)
            .set(ticketData)
            .addOnSuccessListener { onSuccess() }
            .addOnFailureListener { e ->
                onError(e.localizedMessage ?: "Failed to save ticket.")
            }
    }

    fun stopListeners() {
        drawListener?.remove()
        ticketListener?.remove()
        drawListener = null
        ticketListener = null
    }
}
