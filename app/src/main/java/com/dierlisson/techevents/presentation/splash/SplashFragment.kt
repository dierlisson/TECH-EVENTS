package com.dierlisson.techevents.presentation.splash

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.dierlisson.techevents.R
import com.dierlisson.techevents.databinding.FragmentSplashBinding

class SplashFragment : Fragment() {

    private var _binding: FragmentSplashBinding? = null
    private val binding get() = _binding!!

    private val viewModel: SplashViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSplashBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Apply smooth entrance animation
        val fadeInScale = AnimationUtils.loadAnimation(requireContext(), android.R.anim.fade_in)
        binding.logoContainer.startAnimation(fadeInScale)

        viewModel.navigateToEventsList.observe(viewLifecycleOwner) { shouldNavigate ->
            if (shouldNavigate) {
                viewModel.onNavigationHandled()
                findNavController().navigate(R.id.action_splashFragment_to_eventsListFragment)
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
