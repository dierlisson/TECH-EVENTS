package com.dierlisson.techevents.presentation.events.form

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.dierlisson.techevents.R
import com.dierlisson.techevents.TechEventsApplication
import com.dierlisson.techevents.databinding.FragmentEventFormBinding
import com.dierlisson.techevents.domain.model.Event
import com.dierlisson.techevents.presentation.state.UiState
import java.util.Calendar
import java.util.Locale

class EventFormFragment : Fragment() {

    private var _binding: FragmentEventFormBinding? = null
    private val binding get() = _binding!!

    private val args: EventFormFragmentArgs by navArgs()

    private val viewModel: EventFormViewModel by viewModels {
        val appContainer = (requireActivity().application as TechEventsApplication).appContainer
        EventFormViewModelFactory(appContainer.eventsRepository)
    }

    private val categories = arrayOf("Android", "Kotlin", "Backend", "Web", "IA", "Cloud", "DevOps")
    private val formats = arrayOf("PRESENCIAL", "ONLINE")

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentEventFormBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupAdapters()
        setupPickers()
        setupListeners()
        setupBackPressedHandler()

        if (args.eventId > 0) {
            binding.tvFormTitle.text = "Editar Evento"
            viewModel.loadEventForEdit(args.eventId)
        } else {
            binding.tvFormTitle.text = "Novo Evento"
        }

        observeViewModel()
    }

    private fun setupAdapters() {
        val categoryAdapter = ArrayAdapter(requireContext(), android.R.layout.simple_dropdown_item_1line, categories)
        binding.actvCategory.setAdapter(categoryAdapter)

        val formatAdapter = ArrayAdapter(requireContext(), android.R.layout.simple_dropdown_item_1line, formats)
        binding.actvFormat.setAdapter(formatAdapter)

        binding.actvFormat.setOnItemClickListener { _, _, position, _ ->
            val selectedFormat = formats[position]
            updatePresencialFieldsVisibility(selectedFormat)
        }
    }

    private fun updatePresencialFieldsVisibility(format: String) {
        binding.containerPresencialFields.visibility = if (format.equals("PRESENCIAL", ignoreCase = true)) {
            View.VISIBLE
        } else {
            View.GONE
        }
    }

    private fun setupPickers() {
        binding.etDate.setOnClickListener {
            val calendar = Calendar.getInstance()
            val datePicker = DatePickerDialog(
                requireContext(),
                { _, year, month, dayOfMonth ->
                    val formattedDate = String.format(Locale.getDefault(), "%04d-%02d-%02d", year, month + 1, dayOfMonth)
                    binding.etDate.setText(formattedDate)
                },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
            )
            datePicker.show()
        }

        binding.etStartTime.setOnClickListener {
            showTimePicker { time -> binding.etStartTime.setText(time) }
        }

        binding.etEndTime.setOnClickListener {
            showTimePicker { time -> binding.etEndTime.setText(time) }
        }
    }

    private fun showTimePicker(onTimeSelected: (String) -> Unit) {
        val calendar = Calendar.getInstance()
        val timePicker = TimePickerDialog(
            requireContext(),
            { _, hourOfDay, minute ->
                val formattedTime = String.format(Locale.getDefault(), "%02d:%02d", hourOfDay, minute)
                onTimeSelected(formattedTime)
            },
            calendar.get(Calendar.HOUR_OF_DAY),
            calendar.get(Calendar.MINUTE),
            true
        )
        timePicker.show()
    }

    private fun setupListeners() {
        binding.btnFormBack.setOnClickListener {
            confirmDiscardAndNavigateUp()
        }

        binding.btnSubmitForm.setOnClickListener {
            submitForm()
        }
    }

    private fun setupBackPressedHandler() {
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                confirmDiscardAndNavigateUp()
            }
        })
    }

    private fun confirmDiscardAndNavigateUp() {
        if (hasUnsavedChanges()) {
            AlertDialog.Builder(requireContext())
                .setTitle("Descartar alterações?")
                .setMessage("Você possui alterações não salvas no formulário. Deseja sair assim mesmo?")
                .setPositiveButton("Descartar") { _, _ ->
                    findNavController().navigateUp()
                }
                .setNegativeButton("Continuar editando", null)
                .show()
        } else {
            findNavController().navigateUp()
        }
    }

    private fun hasUnsavedChanges(): Boolean {
        return binding.etTitle.text.toString().isNotBlank() ||
                binding.etDescription.text.toString().isNotBlank() ||
                binding.etOrganizer.text.toString().isNotBlank()
    }

    private fun submitForm() {
        val eventToSubmit = Event(
            id = if (args.eventId > 0) args.eventId else 0L,
            title = binding.etTitle.text.toString().trim(),
            description = binding.etDescription.text.toString().trim(),
            category = binding.actvCategory.text.toString().trim(),
            format = binding.actvFormat.text.toString().trim(),
            date = binding.etDate.text.toString().trim(),
            startTime = binding.etStartTime.text.toString().trim(),
            endTime = binding.etEndTime.text.toString().trim(),
            venueName = binding.etVenueName.text.toString().trim().ifEmpty { null },
            address = binding.etAddress.text.toString().trim().ifEmpty { null },
            city = binding.etCity.text.toString().trim().ifEmpty { null },
            state = binding.etState.text.toString().trim().ifEmpty { null },
            organizer = binding.etOrganizer.text.toString().trim(),
            imageUrl = binding.etImageUrl.text.toString().trim().ifEmpty { null },
            price = binding.etPrice.text.toString().toDoubleOrNull() ?: 0.0,
            totalSeats = binding.etTotalSeats.text.toString().toIntOrNull() ?: 100,
            registeredParticipants = binding.etRegisteredParticipants.text.toString().toIntOrNull() ?: 0,
            eventUrl = binding.etEventUrl.text.toString().trim().ifEmpty { null },
            latitude = binding.etLatitude.text.toString().toDoubleOrNull(),
            longitude = binding.etLongitude.text.toString().toDoubleOrNull()
        )

        val error = viewModel.submitForm(eventToSubmit)
        if (error != null) {
            Toast.makeText(requireContext(), error, Toast.LENGTH_LONG).show()
        }
    }

    private fun observeViewModel() {
        viewModel.formState.observe(viewLifecycleOwner) { state ->
            when (state) {
                is UiState.Loading -> {
                    binding.pbFormLoading.visibility = View.VISIBLE
                }
                is UiState.Success -> {
                    binding.pbFormLoading.visibility = View.GONE
                    prefillForm(state.data)
                }
                is UiState.Error -> {
                    binding.pbFormLoading.visibility = View.GONE
                    Toast.makeText(requireContext(), state.message, Toast.LENGTH_LONG).show()
                }
                else -> {}
            }
        }

        viewModel.submitState.observe(viewLifecycleOwner) { state ->
            if (state == null) return@observe
            when (state) {
                is UiState.Loading -> {
                    binding.pbFormLoading.visibility = View.VISIBLE
                    binding.btnSubmitForm.isEnabled = false
                }
                is UiState.Success -> {
                    binding.pbFormLoading.visibility = View.GONE
                    binding.btnSubmitForm.isEnabled = true
                    viewModel.onSubmitStateHandled()
                    val msg = if (args.eventId > 0) "Evento atualizado com sucesso!" else "Evento criado com sucesso!"
                    Toast.makeText(requireContext(), msg, Toast.LENGTH_SHORT).show()
                    findNavController().navigateUp()
                }
                is UiState.Error -> {
                    binding.pbFormLoading.visibility = View.GONE
                    binding.btnSubmitForm.isEnabled = true
                    viewModel.onSubmitStateHandled()
                    Toast.makeText(requireContext(), "Erro ao salvar: ${state.message}", Toast.LENGTH_LONG).show()
                }
                else -> {}
            }
        }
    }

    private fun prefillForm(event: Event) {
        binding.etTitle.setText(event.title)
        binding.etDescription.setText(event.description)
        binding.actvCategory.setText(event.category, false)
        binding.actvFormat.setText(event.format, false)
        binding.etDate.setText(event.date)
        binding.etStartTime.setText(event.startTime)
        binding.etEndTime.setText(event.endTime)
        binding.etVenueName.setText(event.venueName ?: "")
        binding.etAddress.setText(event.address ?: "")
        binding.etCity.setText(event.city ?: "")
        binding.etState.setText(event.state ?: "")
        binding.etOrganizer.setText(event.organizer)
        binding.etPrice.setText(event.price.toString())
        binding.etTotalSeats.setText(event.totalSeats.toString())
        binding.etRegisteredParticipants.setText(event.registeredParticipants.toString())
        binding.etImageUrl.setText(event.imageUrl ?: "")
        binding.etEventUrl.setText(event.eventUrl ?: "")
        binding.etLatitude.setText(event.latitude?.toString() ?: "")
        binding.etLongitude.setText(event.longitude?.toString() ?: "")

        updatePresencialFieldsVisibility(event.format)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
