package ivan.pacheco.cristinalozanobeauty.shared.remote

import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

object Firestore {

    private const val APPOINTMENTS = "appointments"
    private const val CLIENTS = "clients"
    private const val EVENTS_INDEX = "eventsIndex"
    private const val CLIENT_ID = "clientId"
    private const val APPOINTMENT_ID = "appointmentId"
    private const val EVENT = "event"
    private const val ID = "id"

    // Firestore singleton instance
    val db: FirebaseFirestore by lazy { FirebaseFirestore.getInstance() }

    suspend fun migrateEventsIndex() {

        // Get all appointments from clients
        val appointmentsSnapshot = db.collectionGroup(APPOINTMENTS).get().await()

        for (doc in appointmentsSnapshot.documents) {
            val data = doc.data ?: continue
            val event = data[EVENT] as? Map<*, *> ?: continue
            val eventId = event[ID] as? String ?: continue

            // Extract client id from parent path
            val clientId = doc.reference.parent.parent?.id ?: continue
            val appointmentId = doc.id

            // Create eventsIndex document if not exists
            val indexRef = db.collection(EVENTS_INDEX).document(eventId)
            val indexSnap = indexRef.get().await()

            if (!indexSnap.exists()) {
                indexRef.set(
                    mapOf(
                        CLIENT_ID to clientId,
                        APPOINTMENT_ID to appointmentId
                    )
                ).await()
                println("Index creado para eventId=$eventId")
            } else {
                println("Index ya existía para eventId=$eventId")
            }
        }
        println("Migración completada ✅")
    }

    fun migrateFieldClients(oldField: String, newField: String) {
        val clientsRef = Firestore.db.collection(CLIENTS)

        clientsRef.get().addOnSuccessListener { querySnapshot ->
            for (document in querySnapshot.documents) {
                val value = document.get(oldField)
                if (value != null) {
                    val updates = mapOf(
                        newField to value,
                        oldField to FieldValue.delete()
                    )
                    document.reference.update(updates)
                }
            }
        }
    }

    fun migrateFieldClientsToArray(oldField: String, newField: String) {
        val clientsRef = db.collection(CLIENTS)

        clientsRef.get().addOnSuccessListener { querySnapshot ->
            for (document in querySnapshot.documents) {
                val value = document.get(oldField)
                if (value != null && value.toString().isNotEmpty()) {
                    val updates = mapOf(
                        newField to listOf(value),
                        oldField to FieldValue.delete()
                    )
                    document.reference.update(updates)
                }
            }
        }
    }

}