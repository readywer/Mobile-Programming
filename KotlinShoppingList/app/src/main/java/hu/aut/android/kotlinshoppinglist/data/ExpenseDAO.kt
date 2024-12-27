package hu.aut.android.kotlinshoppinglist.data

import android.arch.persistence.room.Dao
import android.arch.persistence.room.Delete
import android.arch.persistence.room.Insert
import android.arch.persistence.room.Query
import android.arch.persistence.room.Update
/*
Itt az adatbázis műveletek találhatóak.
Új adattagkor (új ShippingItem adattag), nem szükséges módosítani itt.
 */
@Dao
interface ExpenseDAO {

    // Az összes költség listázása
    @Query("SELECT * FROM expenses")
    fun findAllExpenses(): List<Expense>

    // Egy költség beszúrása
    @Insert
    fun insertExpense(expense: Expense): Long

    // Egy költség törlése
    @Delete
    fun deleteExpense(expense: Expense)

    // Egy költség módosítása
    @Update
    fun updateExpense(expense: Expense)

    @Query("SELECT SUM(amount) FROM expenses")
    fun getTotalExpense(): Int
}
