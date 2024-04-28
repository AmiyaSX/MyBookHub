package com.example.mybookhub.ui

import android.graphics.Typeface
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Spannable
import android.text.SpannableString
import android.text.style.StyleSpan
import androidx.appcompat.widget.Toolbar
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.drawerlayout.widget.DrawerLayout
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupWithNavController
import com.example.mybookhub.R
import com.example.mybookhub.bean.LibraryBook
import com.example.mybookhub.ui.library.LibraryFragmentDirections
import com.example.mybookhub.ui.vm.LibraryViewModel
import com.google.android.material.navigation.NavigationView

class MainActivity : AppCompatActivity() {
    private lateinit var appBarConfig: AppBarConfiguration
    private lateinit var drawerLayout: DrawerLayout
    private lateinit var navController: NavController
    private val viewModel: LibraryViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

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

        findViewById<NavigationView>(R.id.nav_view)?.setupWithNavController(navController)

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