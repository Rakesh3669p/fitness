package com.gym.gymapp.ui.address.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.gym.gymapp.databinding.ItemActivePacksBinding
import com.gym.gymapp.databinding.ItemAdressBinding
import com.gym.gymapp.databinding.ItemFaqBinding
import com.gym.gymapp.ui.address.model.AddressData
import javax.inject.Inject

class AddressListAdapter @Inject constructor() :
    RecyclerView.Adapter<AddressListAdapter.ViewHolder>() {

    lateinit var context: Context
    var from: String = ""

    inner class ViewHolder(val binding: ItemAdressBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(data: AddressData) {
            with(binding) {
                address.text = data.address
                addressState.text = "${data.state_name}, ${data.city_name}, ${data.pincode}"
                mobile.text = "mobile: ${data.alternate_phone}"

                deleteAddress.isVisible = from != "checkOut"

                deleteAddress.setOnClickListener {
                    addressClickListener?.let {
                        it(data.id)
                    }
                }

                editAddress.setOnClickListener {
                    editAddressClickListener?.let {
                        it(data.id, data)
                    }
                }

                root.setOnClickListener {
                    addressClick?.let {
                        it(adapterPosition)
                    }
                }
            }
        }
    }

    private val differCallBack = object : DiffUtil.ItemCallback<AddressData>() {
        override fun areItemsTheSame(
            oldItem: AddressData,
            newItem: AddressData
        ): Boolean {
            return oldItem == newItem
        }

        override fun areContentsTheSame(
            oldItem: AddressData,
            newItem: AddressData
        ): Boolean {
            return oldItem.hashCode() == newItem.hashCode()
        }
    }


    val differ = AsyncListDiffer(this, differCallBack)

    private var addressClickListener: ((position: Int) -> Unit)? = null

    private var addressClick: ((position: Int) -> Unit)? = null

    private var editAddressClickListener: ((position: Int, addressData: AddressData) -> Unit)? =
        null

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): AddressListAdapter.ViewHolder {
        context = parent.context
        return ViewHolder(
            ItemAdressBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        )
    }

    override fun onBindViewHolder(holder: AddressListAdapter.ViewHolder, position: Int) {
        holder.bind(differ.currentList[position])
    }

    override fun getItemCount(): Int = differ.currentList.size

    fun setOnEditAddressClickListener(listener: (position: Int, addressData: AddressData) -> Unit) {
        editAddressClickListener = listener
    }

    fun setOnDeleteAddressClickListener(listener: (position: Int) -> Unit) {
        addressClickListener = listener
    }

    fun setOnAddressClick(listener: (position: Int) -> Unit) {
        addressClick = listener
    }

    fun setAddressfrom(from: String) {
        this.from = from
    }
}