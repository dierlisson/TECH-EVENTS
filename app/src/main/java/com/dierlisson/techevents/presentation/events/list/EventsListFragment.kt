package com.dierlisson.techevents.presentation.events.list

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.dierlisson.techevents.R
import com.dierlisson.techevents.TechEventsApplication
import com.dierlisson.techevents.databinding.FragmentEventsListBinding
import com.dierlisson.techevents.presentation.adapter.EventsAdapter

class EventsListFragment : Fragment() {

    private var _binding: FragmentEventsListBinding? = null
    private val binding get() = _binding!!

    private val viewModel: EventsListViewModel by viewModels {
        val appContainer = (requireActivity().application as TechEventsApplication).appContainer
        EventsListViewModelFactory(this, appContainer.eventsRepository)
    }

    private lateinit var adapter: EventsAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentEventsListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupRecyclerView()
        setupSearchInput()
        setupCategoryChips()
        setupFormatChips()
        setupFavoritesChip()
        setupSortButton()
        setupFab()
        setupErrorRetryButtons()

        checkNetworkAndShowBanner()
        observeViewModel()
    }

    private fun checkNetworkAndShowBanner() {
        val isConnected = com.dierlisson.techevents.core.util.NetworkUtils.isNetworkAvailable(requireContext())
        binding.tvOfflineBanner.visibility = if (!isConnected) View.VISIBLE else View.GONE
    }

    private fun setupRecyclerView() {
        adapter = EventsAdapter(
            onEventClick = { event ->
                val action = EventsListFragmentDirections.actionEventsListFragmentToEventDetailFragment(event.id)
                findNavController().navigate(action)
            },
            onFavoriteClick = { event ->
                viewModel.toggleFavorite(event)
            },
            onRetryPaginationClick = {
                viewModel.loadNextPage()
            }
        )

        val layoutManager = LinearLayoutManager(requireContext())
        binding.rvEventsList.layoutManager = layoutManager
        binding.rvEventsList.adapter = adapter

        // Infinite Scroll Listener
        binding.rvEventsList.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                if (dy > 0) { // Scrolling down
                    val visibleItemCount = layoutManager.childCount
                    val totalItemCount = layoutManager.itemCount
                    val firstVisibleItemPosition = layoutManager.findFirstVisibleItemPosition()

                    if ((visibleItemCount + firstVisibleItemPosition) >= totalItemCount - 2
                        && firstVisibleItemPosition >= 0
                    ) {
                        viewModel.loadNextPage()
                    }
                }
            }
        })
    }

    private fun setupSearchInput() {
        binding.etSearchQuery.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                val query = s?.toString() ?: ""
                binding.btnClearSearch.visibility = if (query.isNotEmpty()) View.VISIBLE else View.GONE
                viewModel.onSearchQueryChanged(query)
            }

            override fun afterTextChanged(s: Editable?) {}
        })

        binding.btnClearSearch.setOnClickListener {
            binding.etSearchQuery.setText("")
        }
    }

    private fun setupCategoryChips() {
        applyCategoryChipStyles()

        binding.chipGroupCategory.setOnCheckedStateChangeListener { _, checkedIds ->
            if (checkedIds.isEmpty()) return@setOnCheckedStateChangeListener
            val category = when (checkedIds.first()) {
                R.id.chipCategoryAndroid -> "Android"
                R.id.chipCategoryKotlin -> "Kotlin"
                R.id.chipCategoryBackend -> "Backend"
                R.id.chipCategoryWeb -> "Web"
                R.id.chipCategoryIA -> "IA"
                R.id.chipCategoryCloud -> "Cloud"
                R.id.chipCategoryDevOps -> "DevOps"
                R.id.chipCategoryEncerrados -> "Encerrados"
                else -> "Todos"
            }
            viewModel.onCategorySelected(category)
        }
    }

    private fun applyCategoryChipStyles() {
        val categoryChipColors = listOf(
            binding.chipCategoryAll to Pair(android.graphics.Color.parseColor("#37474F"), android.graphics.Color.parseColor("#ECEFF1")),
            binding.chipCategoryAndroid to Pair(android.graphics.Color.parseColor("#1B5E20"), android.graphics.Color.parseColor("#E8F5E9")),
            binding.chipCategoryKotlin to Pair(android.graphics.Color.parseColor("#7F52FF"), android.graphics.Color.parseColor("#F3E5F5")),
            binding.chipCategoryBackend to Pair(android.graphics.Color.parseColor("#1A237E"), android.graphics.Color.parseColor("#E8EAF6")),
            binding.chipCategoryWeb to Pair(android.graphics.Color.parseColor("#006064"), android.graphics.Color.parseColor("#E0F7FA")),
            binding.chipCategoryIA to Pair(android.graphics.Color.parseColor("#4A148C"), android.graphics.Color.parseColor("#F3E5F5")),
            binding.chipCategoryCloud to Pair(android.graphics.Color.parseColor("#01579B"), android.graphics.Color.parseColor("#E1F5FE")),
            binding.chipCategoryDevOps to Pair(android.graphics.Color.parseColor("#E65100"), android.graphics.Color.parseColor("#FFF3E0")),
            binding.chipCategoryEncerrados to Pair(android.graphics.Color.parseColor("#37474F"), android.graphics.Color.parseColor("#ECEFF1"))
        )

        categoryChipColors.forEach { (chip, colorPair) ->
            val mainColor = colorPair.first
            val lightBgColor = colorPair.second

            val states = arrayOf(
                intArrayOf(android.R.attr.state_checked),
                intArrayOf(-android.R.attr.state_checked)
            )
            val bgColors = intArrayOf(mainColor, lightBgColor)
            val textColors = intArrayOf(android.graphics.Color.WHITE, mainColor)
            val tintColors = intArrayOf(android.graphics.Color.WHITE, mainColor)

            chip.chipBackgroundColor = android.content.res.ColorStateList(states, bgColors)
            chip.setTextColor(android.content.res.ColorStateList(states, textColors))
            chip.chipIconTint = android.content.res.ColorStateList(states, tintColors)
            chip.checkedIconTint = android.content.res.ColorStateList(states, tintColors)
            chip.chipStrokeWidth = 0f
        }
    }

    private fun setupFormatChips() {
        binding.chipGroupFormat.setOnCheckedStateChangeListener { _, checkedIds ->
            if (checkedIds.isEmpty()) return@setOnCheckedStateChangeListener
            val format = when (checkedIds.first()) {
                R.id.chipFormatPresencial -> "Presencial"
                R.id.chipFormatOnline -> "Online"
                R.id.chipFormatFavorites -> "Favoritos"
                R.id.chipFormatFinished -> "Finalizados"
                else -> "Todos"
            }
            if (format == "Favoritos") {
                viewModel.onFavoritesOnlyToggled(true)
                viewModel.onFormatSelected("Todos")
            } else {
                viewModel.onFavoritesOnlyToggled(false)
                viewModel.onFormatSelected(format)
            }
        }
    }

    private fun setupFavoritesChip() {
        // Handled via chipFormatFavorites in chipGroupFormat
    }

    private fun setupSortButton() {
        val showSortDialog = {
            val sortOptions = arrayOf("Data mais próxima", "Nome", "Mais inscritos")
            AlertDialog.Builder(requireContext())
                .setTitle("Ordenar por")
                .setItems(sortOptions) { _, which ->
                    val selected = sortOptions[which]
                    viewModel.onSortSelected(selected)
                }
                .setNegativeButton("Cancelar", null)
                .show()
        }

        binding.btnFilterOptions.setOnClickListener { showSortDialog() }
        binding.tvCurrentSort.setOnClickListener { showSortDialog() }
    }

    private fun setupFab() {
        binding.fabCreateEvent.setOnClickListener {
            val action = EventsListFragmentDirections.actionEventsListFragmentToEventFormFragment(eventId = -1L)
            findNavController().navigate(action)
        }
    }

    private fun setupErrorRetryButtons() {
        binding.errorStateView.btnErrorRetry.setOnClickListener {
            viewModel.loadInitialEvents()
        }
        binding.emptyStateView.btnEmptyClearFilters.setOnClickListener {
            binding.etSearchQuery.setText("")
            binding.chipCategoryAll.isChecked = true
            binding.chipFormatAll.isChecked = true
            viewModel.clearFilters()
        }
    }

    private fun observeViewModel() {
        viewModel.uiState.observe(viewLifecycleOwner) { state ->
            // Search Text Sync
            if (binding.etSearchQuery.text.toString() != state.searchQuery) {
                binding.etSearchQuery.setText(state.searchQuery)
            }

            // Current Sort Label
            binding.tvCurrentSort.text = state.selectedSort

            // Loading / Error / Empty / List Visibility logic
            binding.pbInitialLoading.visibility = if (state.isLoading) View.VISIBLE else View.GONE

            if (!state.isLoading && state.errorMessage != null && state.events.isEmpty()) {
                binding.errorStateView.containerErrorState.visibility = View.VISIBLE
                binding.errorStateView.tvErrorMessage.text = state.errorMessage
                binding.rvEventsList.visibility = View.GONE
                binding.emptyStateView.containerEmptyState.visibility = View.GONE
            } else if (!state.isLoading && state.filteredEvents.isEmpty()) {
                binding.emptyStateView.containerEmptyState.visibility = View.VISIBLE
                binding.errorStateView.containerErrorState.visibility = View.GONE
                binding.rvEventsList.visibility = View.GONE
            } else {
                binding.rvEventsList.visibility = View.VISIBLE
                binding.emptyStateView.containerEmptyState.visibility = View.GONE
                binding.errorStateView.containerErrorState.visibility = View.GONE

                adapter.submitList(state.filteredEvents)
                adapter.updatePaginationState(state.paginationState)
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
