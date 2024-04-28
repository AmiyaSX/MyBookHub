package com.example.mybookhub.ui

import android.app.AlertDialog
import android.content.Context
import android.content.SharedPreferences
import android.graphics.Typeface
import android.os.Bundle
import android.text.Spannable
import android.text.SpannableString
import android.text.style.StyleSpan
import android.widget.Button
import android.widget.EditText
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.drawerlayout.widget.DrawerLayout
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.example.mybookhub.R
import com.example.mybookhub.bean.LibraryBook
import com.example.mybookhub.ui.library.LibraryFragmentDirections
import com.example.mybookhub.data.vm.LibraryViewModel
import com.google.android.material.navigation.NavigationView

class MainActivity : AppCompatActivity() {

    private lateinit var appBarConfig: AppBarConfiguration

    private lateinit var drawerLayout: DrawerLayout

    private lateinit var navController: NavController

    private lateinit var navView: NavigationView

    private val viewModel: LibraryViewModel by viewModels()

    // SharedPreferences instance
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var editor: SharedPreferences.Editor

    // SharedPreferences key for account name
    private val ACCOUNT_NAME_KEY = "account_name"

    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Initialize SharedPreferences
        sharedPreferences = getSharedPreferences("MyBookHubPrefs", Context.MODE_PRIVATE)
        editor = sharedPreferences.edit()


        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)

        val navHostFragment = supportFragmentManager.findFragmentById(
            R.id.nav_host_fragment
        ) as NavHostFragment
        navController = navHostFragment.navController

        drawerLayout = findViewById(R.id.drawer_layout)
        appBarConfig = AppBarConfiguration(setOf(
            R.id.library,
            R.id.browse_books,
            R.id.manage_notes
        ), drawerLayout)

        setupActionBarWithNavController(navController, appBarConfig)

        navView = findViewById<NavigationView>(R.id.nav_view)
        navView.setupWithNavController(navController)

        navView.setNavigationItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.login -> {
                    val dialogView = layoutInflater.inflate(R.layout.dialog_connect_open_library, null)
                    val dialog = AlertDialog.Builder(this)
                        .setView(dialogView)
                        .create()
                    val btnCancel = dialogView.findViewById<Button>(R.id.cancel_note)
                    val btnSave = dialogView.findViewById<Button>(R.id.save_note)
                    val account = sharedPreferences.getString(ACCOUNT_NAME_KEY, "")
                    val editText = dialogView.findViewById<EditText>(R.id.account_name)
                    editText.setText(account)
                    btnCancel.setOnClickListener { dialog.dismiss() }
                    btnSave.setOnClickListener {
                        editor.putString(ACCOUNT_NAME_KEY, editText.editableText.toString())
                        editor.apply()

                        dialog.dismiss()
                    }

                    dialog.show()
                    true
                }
                R.id.library -> {
                    // Navigate to LibraryFragment
                    val navController = findNavController(R.id.nav_host_fragment)
                    navController.navigate(R.id.library)
                    drawerLayout.closeDrawers()
                    true
                }
                R.id.browse_books -> {
                    // Navigate to BrowseBooksFragment
                    val navController = findNavController(R.id.nav_host_fragment)
                    navController.navigate(R.id.browse_books)
                    drawerLayout.closeDrawers()
                    true
                }
                R.id.manage_notes -> {
                    // Navigate to ManageNotesFragment
                    val navController = findNavController(R.id.nav_host_fragment)
                    navController.navigate(R.id.manage_notes)
                    drawerLayout.closeDrawers()
                    true
                }
                // Add more cases for other menu items if needed
                else -> false
            }
        }

        // observe books and update submenu in navdrawer on change
        viewModel.libraryBooks.observe(this) { books ->
            addEntriesToDrawer(books)
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment)
        return navController.navigateUp(appBarConfig) || super.onSupportNavigateUp()
    }

    private fun addEntriesToDrawer(books: List<LibraryBook>) {
        val navView: NavigationView = findViewById(R.id.nav_view)
        val entriesSubMenu = navView.menu.findItem(R.id.submenu_item).subMenu

        // make "Recently Viewed" title for submenu bold
        val title = SpannableString(getString(R.string.label_submenu_title))
        title.setSpan(StyleSpan(Typeface.BOLD), 0, title.length, Spannable.SPAN_INCLUSIVE_INCLUSIVE)
        navView.menu.findItem(R.id.submenu_item).title = title

        // add book entries to menu
        val subMenuBooks = books.take(5)

        entriesSubMenu?.clear()
        for (book in subMenuBooks) {
            entriesSubMenu?.add(book.title)?.setOnMenuItemClickListener {
                //close drawer
                drawerLayout.closeDrawers()

                //navigate to book detail page
                val action = LibraryFragmentDirections.navigateToBookDetails(book)
                navController.navigate(action)

                true
            }
        }
    }
}