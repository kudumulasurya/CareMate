package com.example.medicare

import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView

class DocumentAdapter(
    private val documentList: List<DocumentItem>,
    private val listener: OnItemClickListener
) : RecyclerView.Adapter<DocumentAdapter.DocumentViewHolder>() {

    interface OnItemClickListener {
        fun onItemClick(document: DocumentItem)
    }

    inner class DocumentViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val previewImage: ImageView = itemView.findViewById(R.id.documentImage)
        val titleText: TextView = itemView.findViewById(R.id.documentTitle)
        val timeText: TextView = itemView.findViewById(R.id.documentTime)

        init {
            itemView.setOnClickListener {
                val position = bindingAdapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    listener.onItemClick(documentList[position])
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DocumentViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.document_item, parent, false)
        return DocumentViewHolder(view)
    }

    override fun onBindViewHolder(holder: DocumentViewHolder, position: Int) {
        val document = documentList[position]
        holder.titleText.text = document.name
        holder.timeText.text = document.timeStamp

        if (document.type == "pdf") {
            holder.previewImage.setImageDrawable(
                ContextCompat.getDrawable(holder.itemView.context, R.drawable.doc)
            )
        } else {
            holder.previewImage.setImageURI(document.uri)
        }
    }

    override fun getItemCount(): Int = documentList.size
}
