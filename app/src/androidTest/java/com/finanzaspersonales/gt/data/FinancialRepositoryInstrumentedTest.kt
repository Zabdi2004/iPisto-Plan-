package com.finanzaspersonales.gt.data

import android.content.Context
import androidx.room.Room
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.finanzaspersonales.gt.data.local.AppDatabase
import com.finanzaspersonales.gt.data.local.entity.Ingreso
import com.finanzaspersonales.gt.data.repository.FinanzasRepository
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import java.util.UUID

@RunWith(AndroidJUnit4::class)
class FinancialRepositoryInstrumentedTest {
    private lateinit var context: Context
    private lateinit var databaseName: String
    private lateinit var database: AppDatabase

    @Before fun createDatabase() {
        context = InstrumentationRegistry.getInstrumentation().targetContext
        databaseName = "ipisto-test-${UUID.randomUUID()}"
        database = newDatabase()
    }

    @After fun closeAndDeleteDatabase() {
        if (::database.isInitialized) database.close()
        if (::databaseName.isInitialized) context.deleteDatabase(databaseName)
    }

    @Test fun crudPersistenceAndOwnerIsolation() = runBlocking {
        val ownerId = 10L
        val otherUserId = 20L
        val dao = database.ingresoDao()
        val otherUserRecordId = dao.insert(Ingreso(userId = otherUserId, nombre = "Privado", cantidad = 800.0, periodicidad = "Mensual"))
        val ownerRepository = repository(ownerId)

        val ownId = ownerRepository.insertIngreso(Ingreso(userId = otherUserId, nombre = "Salario", cantidad = 3500.0, periodicidad = "Mensual")).getOrThrow()
        assertEquals(1, dao.getAllByUserOnce(ownerId).size)
        assertEquals("Salario", dao.findById(ownId, ownerId)?.nombre)
        assertFalse(ownerRepository.updateIngreso(Ingreso(otherUserRecordId, ownerId, "Intrusión", 1.0, "Mensual")).isSuccess)
        assertFalse(ownerRepository.deleteIngreso(Ingreso(otherUserRecordId, ownerId, "Privado", 800.0, "Mensual")).isSuccess)

        assertTrue(ownerRepository.updateIngreso(Ingreso(ownId, ownerId, "Salario actualizado", 1250.50, "Mensual")).isSuccess)
        database.close()
        database = newDatabase()

        assertEquals(1250.50, database.ingresoDao().findById(ownId, ownerId)?.cantidad ?: 0.0, 0.0)
        assertEquals("Privado", database.ingresoDao().findById(otherUserRecordId, otherUserId)?.nombre)
        assertTrue(repository(ownerId).deleteIngreso(Ingreso(ownId, ownerId, "", 0.0, "Mensual")).isSuccess)
        assertTrue(database.ingresoDao().getAllByUserOnce(ownerId).isEmpty())
        assertEquals(1, database.ingresoDao().getAllByUserOnce(otherUserId).size)
    }

    private fun newDatabase() = Room.databaseBuilder(context, AppDatabase::class.java, databaseName).build()

    private fun repository(ownerId: Long) = FinanzasRepository(
        database.ingresoDao(), database.gastoFijoDao(), database.gastoVariableDao(), database.deudaDao(), ownerId
    )
}
