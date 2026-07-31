package com.dierlisson.techevents.presentation.events.detail

import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.bumptech.glide.Glide
import com.dierlisson.techevents.R
import com.dierlisson.techevents.TechEventsApplication
import com.dierlisson.techevents.databinding.FragmentEventDetailBinding
import com.dierlisson.techevents.domain.model.Event
import com.dierlisson.techevents.presentation.state.UiState
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.Locale

class EventDetailFragment : Fragment() {

    private var _binding: FragmentEventDetailBinding? = null
    private val binding get() = _binding!!

    private val args: EventDetailFragmentArgs by navArgs()

    private val viewModel: EventDetailViewModel by viewModels {
        val appContainer = (requireActivity().application as TechEventsApplication).appContainer
        EventDetailViewModelFactory(appContainer.eventsRepository)
    }

    private var currentEvent: Event? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentEventDetailBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.btnBack.setOnClickListener {
            findNavController().navigateUp()
        }

        viewModel.loadEventDetail(args.eventId)
        observeViewModel()
    }

    private fun observeViewModel() {
        viewModel.eventDetailState.observe(viewLifecycleOwner) { state ->
            when (state) {
                is UiState.Loading -> {
                    binding.pbDetailLoading.visibility = View.VISIBLE
                }
                is UiState.Success -> {
                    binding.pbDetailLoading.visibility = View.GONE
                    val event = state.data
                    currentEvent = event
                    bindEventDetails(event)
                }
                is UiState.Error -> {
                    binding.pbDetailLoading.visibility = View.GONE
                    Toast.makeText(requireContext(), state.message, Toast.LENGTH_LONG).show()
                }
                else -> {}
            }
        }

        viewModel.deletionState.observe(viewLifecycleOwner) { state ->
            if (state == null) return@observe
            when (state) {
                is UiState.Loading -> {
                    binding.pbDetailLoading.visibility = View.VISIBLE
                }
                is UiState.Success -> {
                    binding.pbDetailLoading.visibility = View.GONE
                    viewModel.onDeletionStateHandled()
                    Toast.makeText(requireContext(), "Evento excluído com sucesso!", Toast.LENGTH_SHORT).show()
                    findNavController().navigateUp()
                }
                is UiState.Error -> {
                    binding.pbDetailLoading.visibility = View.GONE
                    viewModel.onDeletionStateHandled()
                    Toast.makeText(requireContext(), "Erro ao excluir: ${state.message}", Toast.LENGTH_LONG).show()
                }
                else -> {}
            }
        }
    }

    private fun bindEventDetails(event: Event) {
        binding.tvDetailTitle.text = event.title
        binding.tvDetailCategoryBadge.text = event.category
        binding.tvDetailCategoryBadge.setBackgroundResource(
            com.dierlisson.techevents.core.util.CategoryUtils.getCategoryBackgroundRes(event.category, event.title, event.date)
        )
        binding.tvDetailFormatBadge.text = event.format
        binding.tvDetailDescription.text = event.description
        binding.tvDetailOrganizer.text = "Organizador: ${event.organizer}"

        // Image loading
        if (!event.imageUrl.isNull_or_empty()) {
            Glide.with(this)
                .load(event.imageUrl)
                .placeholder(R.drawable.bg_card_overlay)
                .error(R.drawable.bg_card_overlay)
                .into(binding.ivDetailBanner)
        }

        // Date Main & Subtext
        binding.tvDetailDateMain.text = formatDateMain(event.date)
        val dayOfWeek = formatDayOfWeek(event.date)
        binding.tvDetailDateSub.text = "$dayOfWeek • ${event.startTime} - ${event.endTime}"

        // Location & Address
        if (event.format.equals("ONLINE", ignoreCase = true)) {
            binding.tvDetailVenueName.text = "Evento Online"
            binding.tvDetailAddressSub.text = event.eventUrl ?: "Link será disponibilizado na data do evento"
        } else {
            binding.tvDetailVenueName.text = event.venueName ?: "Local Presencial"
            binding.tvDetailAddressSub.text = listOfNotNull(event.address, event.city, event.state).joinToString(" - ")
        }

        // Location Click for Google Maps
        binding.containerLocation.setOnClickListener {
            openGoogleMaps(event)
        }

        // Price
        if (event.price == 0.0) {
            binding.tvDetailPrice.text = "Grátis"
            binding.tvDetailPrice.setTextColor(requireContext().getColor(R.color.accent_green))
        } else {
            val ptBrLocale = Locale("pt", "BR")
            val currencyFormat = NumberFormat.getCurrencyInstance(ptBrLocale)
            binding.tvDetailPrice.text = currencyFormat.format(event.price)
            binding.tvDetailPrice.setTextColor(requireContext().getColor(R.color.primary_blue))
        }

        // Favorite Icon Status
        binding.btnDetailFavorite.setImageResource(
            if (event.isFavorite) R.drawable.ic_favorite else R.drawable.ic_favorite_border
        )
        binding.btnDetailFavorite.setOnClickListener {
            viewModel.toggleFavorite()
        }

        // Share Action
        binding.btnShare.setOnClickListener {
            shareEvent(event)
        }

        // Edit Action
        binding.btnEditEvent.setOnClickListener {
            val action = EventDetailFragmentDirections.actionEventDetailFragmentToEventFormFragment(eventId = event.id)
            findNavController().navigate(action)
        }

        // Delete Action
        binding.btnDeleteEvent.setOnClickListener {
            showDeleteConfirmationDialog(event)
        }
    }

    private fun openGoogleMaps(event: Event) {
        if (event.format.equals("ONLINE", ignoreCase = true)) {
            if (!event.eventUrl.isNull_or_empty()) {
                try {
                    val intent = Intent(Intent.ACTION_VIEW, Uri.parse(event.eventUrl))
                    startActivity(intent)
                } catch (e: Exception) {
                    Toast.makeText(requireContext(), "Não foi possível abrir o link do evento", Toast.LENGTH_SHORT).show()
                }
            }
            return
        }

        val uri = if (event.latitude != null && event.longitude != null) {
            Uri.parse("geo:${event.latitude},${event.longitude}?q=${event.latitude},${event.longitude}(${Uri.encode(event.title)})")
        } else {
            val addressQuery = listOfNotNull(event.venueName, event.address, event.city, event.state).joinToString(", ")
            Uri.parse("geo:0,0?q=${Uri.encode(addressQuery)}")
        }

        val mapIntent = Intent(Intent.ACTION_VIEW, uri)
        mapIntent.setPackage("com.google.android.apps.maps")

        try {
            startActivity(mapIntent)
        } catch (e: ActivityNotFoundException) {
            // Fallback to any maps/browser app
            try {
                val genericIntent = Intent(Intent.ACTION_VIEW, uri)
                startActivity(genericIntent)
            } catch (ex: Exception) {
                Toast.makeText(requireContext(), "Nenhum aplicativo de mapas disponível no dispositivo.", Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun shareEvent(event: Event) {
        val shareText = """
            📌 ${event.title}
            📅 Data: ${formatDateMain(event.date)} às ${event.startTime}
            💻 Formato: ${event.format}
            🔗 ${event.eventUrl ?: "Confira no app Tech Events!"}
        """.trimIndent()

        val sendIntent = Intent().apply {
            action = Intent.ACTION_SEND
            putExtra(Intent.EXTRA_TEXT, shareText)
            type = "text/plain"
        }

        val shareIntent = Intent.createChooser(sendIntent, "Compartilhar Evento")
        startActivity(shareIntent)
    }

    private fun showDeleteConfirmationDialog(event: Event) {
        AlertDialog.Builder(requireContext())
            .setTitle("Excluir Evento")
            .setMessage("Deseja realmente excluir o evento '${event.title}'? Esta ação não pode ser desfeita.")
            .setPositiveButton("Excluir") { _, _ ->
                viewModel.deleteEvent()
            }
            .setNegativeButton("Cancelar", null)
            .show()
    }

    private fun formatDateMain(dateIso: String): String {
        return try {
            val inputFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            val outputFormat = SimpleDateFormat("dd 'de' MMMM, yyyy", Locale("pt", "BR"))
            val parsed = inputFormat.parse(dateIso)
            if (parsed != null) outputFormat.format(parsed) else dateIso
        } catch (e: Exception) {
            dateIso
        }
    }

    private fun formatDayOfWeek(dateIso: String): String {
        return try {
            val inputFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            val dayFormat = SimpleDateFormat("EEEE", Locale("pt", "BR"))
            val parsed = inputFormat.parse(dateIso)
            if (parsed != null) dayFormat.format(parsed).replaceFirstChar { it.uppercase() } else ""
        } catch (e: Exception) {
            ""
        }
    }

    private fun String?.isNull_or_empty(): Boolean = this == null || this.isBlank()

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
