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

        observeViewModel()
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
                else -> "Todos"
            }
            viewModel.onCategorySelected(category)
        }
    }

    private fun setupFormatChips() {
        binding.chipGroupFormat.setOnCheckedStateChangeListener { _, checkedIds ->
            if (checkedIds.isEmpty()) return@setOnCheckedStateChangeListener
            val format = when (checkedIds.first()) {
                R.id.chipFormatPresencial -> "Presencial"
                R.id.chipFormatOnline -> "Online"
                else -> "Todos"
            }
            viewModel.onFormatSelected(format)
        }
    }

    private fun setupFavoritesChip() {
        binding.chipFavoritesOnly.setOnCheckedChangeListener { _, isChecked ->
            viewModel.onFavoritesOnlyToggled(isChecked)
        }
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
            binding.chipFavoritesOnly.isChecked = false
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
