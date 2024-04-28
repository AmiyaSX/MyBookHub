package com.example.mybookhub.ui.library

import android.os.Bundle
import android.view.View
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.navigation.fragment.findNavController
import androidx.viewpager2.widget.ViewPager2
import com.example.mybookhub.R
import com.example.mybookhub.bean.LibraryBook
import com.example.mybookhub.bean.Note
import com.example.mybookhub.data.vm.LibraryViewModel
import com.example.mybookhub.data.vm.NotesViewModel
import com.example.mybookhub.ui.adapter.LibraryAdapter
import com.example.mybookhub.ui.adapter.LibraryPagerAdapter
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator


class LibraryFragment : Fragment(R.layout.fragment_library) {
    private val tag: String = "LibraryFragment"

    private lateinit var viewPager: ViewPager2
    private lateinit var tabLayout: TabLayout


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        tabLayout = view.findViewById(R.id.tab_layout)
        viewPager = view.findViewById<ViewPager2>(R.id.view_page)

        viewPager.adapter = LibraryPagerAdapter(requireActivity())
        TabLayoutMediator(tabLayout, viewPager) { tab, position ->
            tab.text = when (position) {
                0 -> "Local Library"
                1 -> "Currently Reading"
                else -> "Want to Read"
            }
        }.attach()
    }
}