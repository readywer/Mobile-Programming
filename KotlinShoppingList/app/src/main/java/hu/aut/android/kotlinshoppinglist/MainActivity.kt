package hu.aut.android.kotlinshoppinglist

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import hu.aut.android.kotlinshoppinglist.adapter.ExpenseAdapter
import kotlinx.android.synthetic.main.activity_main.*
import android.preference.PreferenceManager
import android.support.v7.widget.helper.ItemTouchHelper
import android.view.View
import android.widget.TextView
import hu.aut.android.kotlinshoppinglist.data.AppDatabase
import hu.aut.android.kotlinshoppinglist.data.Expense
import hu.aut.android.kotlinshoppinglist.touch.ShoppingTouchHelperCallback
import uk.co.samuelwall.materialtaptargetprompt.MaterialTapTargetPrompt




/*
Ez a fő, main.
Itt nem szükséges módosítani a Shopping Item-hez ha új adattagot adunk.
 */
class MainActivity : AppCompatActivity(), ExpenseDialog.ShoppingItemHandler {
    companion object {
        val KEY_FIRST = "KEY_FIRST"
        val KEY_ITEM_TO_EDIT = "KEY_ITEM_TO_EDIT"
    }

    private lateinit var adapter: ExpenseAdapter
    /*
    Alkalmazás create-kor hívódik meg
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Activity-main.xml-t hozza be
        setContentView(R.layout.activity_main)

        setSupportActionBar(toolbar)
        //Új elem hozzáadásakor hívódik meg, a rózsaszín levél ikonos gomb eseménykezelője
        //A ShoppingTimeDialog-ot hívja meg (jeleníti meg)
        fab.setOnClickListener { view ->
            ExpenseDialog().show(supportFragmentManager, "TAG_ITEM")
        }

        initRecyclerView()
            /*Új elem felvitelének vizsgálata, akkor hívódik meg, a dialógus címét állítja*/
        if (isFirstRun()) {
            MaterialTapTargetPrompt.Builder(this@MainActivity)
                    .setTarget(findViewById<View>(R.id.fab))
                    .setPrimaryText("Új költekezés")
                    .setSecondaryText("Új költekezés felvétele")
                    .show()
        }


        saveThatItWasStarted()
        displayTotalExpense()
    }
    private fun displayTotalExpense() {
        val dbThread = Thread {
            val totalExpense = AppDatabase.getInstance(this).expenseDao().getTotalExpense()
            runOnUiThread {
                val totalExpenseTextView = findViewById<TextView>(R.id.tvTotalExpense)
                totalExpenseTextView.text = "Total Expense: $totalExpense"
            }
        }
        dbThread.start()
    }
    private fun isFirstRun(): Boolean {
        return PreferenceManager.getDefaultSharedPreferences(this).getBoolean(
                KEY_FIRST, true
        )
    }

    private fun saveThatItWasStarted() {
        val sp = PreferenceManager.getDefaultSharedPreferences(this)
        sp.edit()
                .putBoolean(KEY_FIRST, false)
                .apply()
    }




    private fun initRecyclerView() {
        val dbThread = Thread {
            //Lekéri az összes Shopping Item-et.
            val items = AppDatabase.getInstance(this).expenseDao().findAllExpenses()
            runOnUiThread{
                adapter = ExpenseAdapter(this, items)
                recyclerShopping.adapter = adapter

                val callback = ShoppingTouchHelperCallback(adapter)
                val touchHelper = ItemTouchHelper(callback)
                touchHelper.attachToRecyclerView(recyclerShopping)

            }
        }
        dbThread.start()
    }
    /*Edit dialógus megnyitása*/
    fun showEditItemDialog(itemToEdit: Expense) {
        val editDialog = ExpenseDialog()

        val bundle = Bundle()
        bundle.putSerializable(KEY_ITEM_TO_EDIT, itemToEdit)
        editDialog.arguments = bundle

        editDialog.show(supportFragmentManager, "TAG_ITEM_EDIT")
    }

/*Új Shopping Item-kor beszúrjuk a DB-be a DAO segítségével*/
    override fun shoppingItemCreated(item: Expense) {
        val dbThread = Thread {
            val id = AppDatabase.getInstance(this@MainActivity).expenseDao().insertExpense(item)

            item.expenseId = id

            runOnUiThread{
                adapter.addItem(item)
                displayTotalExpense()
            }
        }
        dbThread.start()
    }
/*Update-or módosítjuk a Shopping Item-et a DAO segítségével*/
    override fun shoppingItemUpdated(item: Expense) {
        adapter.updateItem(item)

        val dbThread = Thread {
            AppDatabase.getInstance(this@MainActivity).expenseDao().updateExpense(item)
            displayTotalExpense()
            runOnUiThread { adapter.updateItem(item) }
        }
        dbThread.start()
    }
}
