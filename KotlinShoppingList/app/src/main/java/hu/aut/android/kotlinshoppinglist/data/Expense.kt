package hu.aut.android.kotlinshoppinglist.data

import java.io.Serializable

import android.arch.persistence.room.Entity
import android.arch.persistence.room.ColumnInfo
import android.arch.persistence.room.PrimaryKey

/*
Adatbázis táblát készti el.
Táblanév:shoppingitem.
Oszlopok:itemId, name,  price, bought.
@PrimaryKey(autoGenerate = true): elsődleges kulcs, automatikusan generálva.
Ide szükséges a bővítés új adattal.
 */
@Entity(tableName = "expenses")
data class Expense(
    @PrimaryKey(autoGenerate = true) var expenseId: Long?,
    @ColumnInfo(name = "description") var description: String,
    @ColumnInfo(name = "amount") var amount: Int,
    @ColumnInfo(name = "category") var category: String
) : Serializable
