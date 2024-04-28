package com.example.mybookhub.ui.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.mybookhub.R
import com.example.mybookhub.bean.BooksFromProfile
import com.example.mybookhub.data.BookProfile
import com.example.mybookhub.data.Work

class OnlineLibraryAdapter(
    private var books: List<BookProfile>
) : RecyclerView.Adapter<OnlineLibraryAdapter.ViewHolder>(){

    fun updateBookList(newBookList: BooksFromProfile?){
        notifyItemRangeRemoved(0, books.size)
        books = newBookList?.books ?: listOf()
        notifyItemRangeInserted(0, books.size)
    }

    fun clearBookList() {
        books = emptyList()
        notifyDataSetChanged()
    }

    override fun getItemCount() = books.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.browse_book_list_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(books[position].work)
    }

    class ViewHolder(
        itemView: View
    ) : RecyclerView.ViewHolder(itemView) {
        private val coverIV : ImageView = itemView.findViewById(R.id.book_cover)
        private val titleTV = itemView.findViewById<TextView>(R.id.book_title)
        private val authorTV = itemView.findViewById<TextView>(R.id.book_author)

        private lateinit var currentBook: Work

        fun bind(book: Work) {
            currentBook = book

            val ctx = itemView.context

            titleTV.text = currentBook.title
            authorTV.text = currentBook.author

            Glide.with(ctx)
                .load(currentBook.coverURL)
                .fitCenter()
                .into(coverIV)

        }
    }

}