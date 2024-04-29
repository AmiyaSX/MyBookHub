package com.example.mybookhub.ui.note

import android.app.AlertDialog
import android.os.Bundle
import android.view.LayoutInflater
import androidx.fragment.app.Fragment
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import androidx.fragment.app.viewModels
import androidx.lifecycle.asLiveData
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.mybookhub.R
import com.example.mybookhub.bean.NoteCategory
import com.example.mybookhub.data.vm.NotesViewModel
import com.example.mybookhub.databinding.FragmentNotesManageBinding
import com.example.mybookhub.ui.adapter.NoteCategoryAdapter
import com.google.android.material.progressindicator.CircularProgressIndicator
import kotlinx.coroutines.launch


class NotesManageFragment : Fragment() {
    private val tag: String = "NotesManageFragment"
    private var _binding: FragmentNotesManageBinding? = null
    private val binding get() = _binding!!
    private val viewModel: NotesViewModel by viewModels()
    private val categoryAdapter = NoteCategoryAdapter(emptyList(), ::onCategoryClick, ::onCategoryLongClick)
    private lateinit var categoryRecyclerView: RecyclerView
    private lateinit var categoryList: List<NoteCategory>
    private lateinit var loadingIndicator: CircularProgressIndicator

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentNotesManageBinding.inflate(inflater, container, false)
        return binding.root
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        categoryRecyclerView = view.findViewById(R.id.category_recycler_view)
        categoryRecyclerView.adapter = categoryAdapter
        categoryRecyclerView.layoutManager = GridLayoutManager(context, 2, RecyclerView.VERTICAL, false)

        viewModel.noteCategoryList.observe(viewLifecycleOwner) { categoryList ->
            if(categoryList.isEmpty()) {
                lifecycleScope.launch{
                    viewModel.addCategory(
                        NoteCategory(
                            id = 0,
                            "Default",
                            ""
                        )
                    )
                }
            }
            categoryAdapter.updateCategoryList(categoryList)
        }

        binding.addCategoryButton.setOnClickListener {
            showCategoryDialog(null)
        }
    }

    private fun onCategoryClick(category: NoteCategory) {
        val action = CategoryNoteFragmentDirections.navigateToNoteCategory(category)
        findNavController().navigate(action)
    }

    private fun onCategoryLongClick(category: NoteCategory) {
        showCategoryDialog(category)
    }


    private fun showCategoryDialog(noteCategory: NoteCategory?) {
        val dialogView = layoutInflater.inflate(R.layout.dialog_create_note_category, null)
        val dialog = AlertDialog.Builder(requireContext())
            .setView(dialogView)
            .create()

        val categoryTitleET = dialogView.findViewById<EditText>(R.id.note_category_title)
        val categoryDescriptionET = dialogView.findViewById<EditText>(R.id.note_category_description)
        val btnDelete = dialogView.findViewById<Button>(R.id.delete)
        val btnCancel = dialogView.findViewById<Button>(R.id.cancel)
        val btnSave = dialogView.findViewById<Button>(R.id.save)
        // if a category is clicked on, populate title and content
        if (noteCategory != null) {
            categoryTitleET.setText(noteCategory.title)
            categoryDescriptionET.setText(noteCategory.description)
            btnDelete.setOnClickListener {
                val notes = viewModel.getNotesByCategory(noteCategory.title).value
                if (notes != null) {
                    for (note in notes) {
                        viewModel.updateNoteCategory(note, "Default")
                    }
                }
                viewModel.deleteCategory(noteCategory)
                dialog.dismiss()
            }

            btnSave.setOnClickListener {
                viewModel.getCategoryByTitle(noteCategory.title).value
                viewModel.updateCategory(noteCategory, categoryTitleET.text.toString(), categoryDescriptionET.text.toString() )

                dialog.dismiss()
            }
        } else {
            btnSave.setOnClickListener {
                val categoryTitle = categoryTitleET.text.toString()
                val categoryDescription = categoryDescriptionET.text.toString()
                lifecycleScope.launch{
                    viewModel.addCategory(
                        NoteCategory(
                            id = 0,
                            categoryTitle,
                            categoryDescription
                        )
                    )
                }
                dialog.dismiss()
            }
            btnDelete.visibility = View.INVISIBLE
        }

        btnCancel.setOnClickListener { dialog.dismiss() }

        dialog.show()
    }
}