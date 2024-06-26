package com.example.mybookhub.ui.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.mybookhub.R
import com.example.mybookhub.data.Book
import com.google.android.material.button.MaterialButton

class BrowseBooksAdapter(
    private var books: List<Book>,
    private val onBookClick: (Book) -> Unit,
    private val setButtonText: (Book, MaterialButton) -> Unit,
    private val onCartClick: (Book) -> Unit
) : RecyclerView.Adapter<BrowseBooksAdapter.ViewHolder>() {

    fun updateBookList(newBookList: List<Book>?){
        notifyItemRangeRemoved(0, books.size)
        books = newBookList ?: listOf()
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
        return ViewHolder(view, onBookClick, onCartClick)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(books[position], setButtonText)
    }

    class ViewHolder(
        itemView: View,
        onClick: (Book) -> Unit,
        onCartClick: (Book) -> Unit
    ) : RecyclerView.ViewHolder(itemView) {
        private val coverIV : ImageView = itemView.findViewById(R.id.book_cover)
        private val titleTV = itemView.findViewById<TextView>(R.id.book_title)
        private val authorTV = itemView.findViewById<TextView>(R.id.book_author)
        private val ratingTV = itemView.findViewById<TextView>(R.id.book_rating)
        private val button = itemView.findViewById<MaterialButton>(R.id.book_button)
        private val cartButton = itemView.findViewById<MaterialButton>(R.id.buy_book_button)

        private lateinit var currentBook: Book

        init {
            button.setOnClickListener{
                onClick(currentBook)
            }

            cartButton.setOnClickListener {
                onCartClick(currentBook)
            }
        }

        fun bind(book: Book, setButtonText:(Book, MaterialButton) -> Unit) {
            currentBook = book

            val ctx = itemView.context

            // TODO Load the image, properly handle rating
            titleTV.text = currentBook.title
            authorTV.text = currentBook.author
            ratingTV.text = String.format("%.2f", currentBook.rating)

            Glide.with(ctx)
                .load(currentBook.coverURL)
                .fitCenter()
                .into(coverIV)

            setButtonText(currentBook, button)

            //hide buy book button if there is no amazon link
            if (currentBook.amazonLink.isNullOrEmpty()) {
                cartButton.visibility = View.GONE
            } else {
                cartButton.visibility = View.VISIBLE
            }
    }
}


}