package com.example.mybookhub.ui.adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.mybookhub.ui.library.CurrentlyReadingFragment
import com.example.mybookhub.ui.library.LocalLibraryFragment
import com.example.mybookhub.ui.library.WantToReadFragment

class LibraryPagerAdapter(fragmentActivity: FragmentActivity) : FragmentStateAdapter(fragmentActivity) {

    override fun getItemCount(): Int {
        // Return the number of tabs
        return 3
    }

    override fun createFragment(position: Int): Fragment {
        // Return the Fragment associated with the specified position
        return when (position) {
            0 -> LocalLibraryFragment() // Fragment for Local Library tab
            1 -> CurrentlyReadingFragment() // Fragment for Currently Reading tab
            2 -> WantToReadFragment() // Fragment for Want to Read tab
            else -> throw IllegalArgumentException("Invalid tab position")
        }
    }

    override fun getItemId(position: Int): Long {
        return super.getItemId(position)
    }

//    override fun getPageTitle(position: Int): CharSequence? {
//        // Set the title for each tab
//        return when (position) {
//            0 -> "Local Library"
//            1 -> "Currently Reading"
//            2 -> "Want to Read"
//            else -> null
//        }
//    }
}
