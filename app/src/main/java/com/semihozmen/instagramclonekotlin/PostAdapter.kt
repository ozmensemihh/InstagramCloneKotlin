package com.semihozmen.instagramclonekotlin

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.semihozmen.instagramclonekotlin.databinding.RecRowBinding
import com.squareup.picasso.Picasso

class PostAdapter (val list: List<Post>) :RecyclerView.Adapter<PostAdapter.PostHolder>() {

    class PostHolder (val binding:RecRowBinding) : RecyclerView.ViewHolder(binding.root){

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PostHolder {
        val binding = RecRowBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return PostHolder(binding)
    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun onBindViewHolder(holder: PostHolder, position: Int) {

        holder.binding.txtPostEmail.text = list.get(position).email
        holder.binding.txtPostComment.text = list.get(position).comment
        Picasso.get().load(list.get(position).downloadUrl).into(holder.binding.imgPostImg)

    }
}