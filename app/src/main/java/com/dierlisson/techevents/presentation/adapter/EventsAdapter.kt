package com.dierlisson.techevents.presentation.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.dierlisson.techevents.R
import com.dierlisson.techevents.databinding.ItemEventBinding
import com.dierlisson.techevents.databinding.ItemPaginationFooterBinding
import com.dierlisson.techevents.domain.model.Event
import com.dierlisson.techevents.presentation.state.PaginationState
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.Locale

class EventsAdapter(
    private val onEventClick: (Event) -> Unit,
    private val onFavoriteClick: (Event) -> Unit,
    private val onRetryPaginationClick: () -> Unit
) : ListAdapter<Event, RecyclerView.ViewHolder>(EventDiffCallback()) {

    private var paginationState: PaginationState = PaginationState.Idle

    companion object {
        private const val TYPE_EVENT = 0
        private const val TYPE_FOOTER = 1
    }

    fun updatePaginationState(newState: PaginationState) {
        val previousState = this.paginationState
        this.paginationState = newState
        if (previousState != newState) {
            notifyItemChanged(itemCount - 1)
        }
    }

    override fun getItemViewType(position: Int): Int {
        return if (position == itemCount - 1 && paginationState != PaginationState.Idle) {
            TYPE_FOOTER
        } else {
            TYPE_EVENT
        }
    }

    override fun getItemCount(): Int {
        val baseCount = super.getItemCount()
        return if (paginationState != PaginationState.Idle) baseCount + 1 else baseCount
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return if (viewType == TYPE_FOOTER) {
            val binding = ItemPaginationFooterBinding.inflate(inflater, parent, false)
            FooterViewHolder(binding, onRetryPaginationClick)
        } else {
            val binding = ItemEventBinding.inflate(inflater, parent, false)
            EventViewHolder(binding, onEventClick, onFavoriteClick)
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is EventViewHolder) {
            holder.bind(getItem(position))
        } else if (holder is FooterViewHolder) {
            holder.bind(paginationState)
        }
    }

    class EventViewHolder(
        private val binding: ItemEventBinding,
        private val onEventClick: (Event) -> Unit,
        private val onFavoriteClick: (Event) -> Unit
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(event: Event) {
            binding.tvEventTitle.text = event.title
            binding.tvCategoryChip.text = event.category
            binding.tvCategoryChip.setBackgroundResource(
                com.dierlisson.techevents.core.util.CategoryUtils.getCategoryBackgroundRes(event.category, event.title, event.date)
            )
            binding.tvEventOrganizer.text = event.organizer

            // Format Location
            val locationText = if (event.format.equals("ONLINE", ignoreCase = true)) {
                "Online"
            } else {
                listOfNotNull(event.venueName, event.city).joinToString(" - ").ifEmpty { event.address ?: "Presencial" }
            }
            binding.tvEventLocation.text = locationText

            // Format Date & Time
            val formattedDate = formatDateString(event.date)
            binding.tvEventDate.text = "$formattedDate • ${event.startTime}"

            // Format Price
            if (event.price == 0.0) {
                binding.tvEventPrice.text = "Grátis"
                binding.tvEventPrice.setTextColor(binding.root.context.getColor(R.color.accent_green))
            } else {
                val ptBrLocale = Locale("pt", "BR")
                val currencyFormat = NumberFormat.getCurrencyInstance(ptBrLocale)
                binding.tvEventPrice.text = currencyFormat.format(event.price)
                binding.tvEventPrice.setTextColor(binding.root.context.getColor(R.color.primary_blue))
            }

            // Format Seats
            binding.tvEventSeats.text = "${event.registeredParticipants}/${event.totalSeats} inscritos"

            // Favorite Icon
            binding.btnFavorite.setImageResource(
                if (event.isFavorite) R.drawable.ic_favorite else R.drawable.ic_favorite_border
            )

            // Click Listeners
            binding.cardEvent.setOnClickListener { onEventClick(event) }
            binding.btnFavorite.setOnClickListener { onFavoriteClick(event) }
        }

        private fun formatDateString(dateIso: String): String {
            return try {
                val inputFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                val outputFormat = SimpleDateFormat("dd 'de' MMMM, yyyy", Locale("pt", "BR"))
                val parsed = inputFormat.parse(dateIso)
                if (parsed != null) outputFormat.format(parsed) else dateIso
            } catch (e: Exception) {
                dateIso
            }
        }
    }

    class FooterViewHolder(
        private val binding: ItemPaginationFooterBinding,
        private val onRetryClick: () -> Unit
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(state: PaginationState) {
            binding.pbFooterLoading.visibility = View.GONE
            binding.containerFooterError.visibility = View.GONE
            binding.tvFooterEndOfList.visibility = View.GONE

            when (state) {
                is PaginationState.LoadingMore -> {
                    binding.pbFooterLoading.visibility = View.VISIBLE
                }
                is PaginationState.Error -> {
                    binding.containerFooterError.visibility = View.VISIBLE
                    binding.tvFooterError.text = state.message
                    binding.btnFooterRetry.setOnClickListener { onRetryClick() }
                }
                is PaginationState.EndOfList -> {
                    binding.tvFooterEndOfList.visibility = View.VISIBLE
                }
                else -> {}
            }
        }
    }

    class EventDiffCallback : DiffUtil.ItemCallback<Event>() {
        override fun areItemsTheSame(oldItem: Event, newItem: Event): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Event, newItem: Event): Boolean {
            return oldItem == newItem
        }
    }
}
