package com.niskender.newswithhmssearchkit.ui.splash

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.niskender.newswithhmssearchkit.R
import com.niskender.newswithhmssearchkit.data.TokenState
import com.niskender.newswithhmssearchkit.databinding.FragmentSplashBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

@AndroidEntryPoint
class SplashFragment : Fragment(R.layout.fragment_splash) {

    private var _binding: FragmentSplashBinding? = null
    private val binding get() = _binding!!

    private val viewModel: SplashViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentSplashBinding.bind(view)

        lifecycleScope.launch {
            viewModel.accessToken.collect {
                if (it is TokenState.Success) {
                    findNavController().navigate(R.id.action_splashFragment_to_homeFragment)
                }
                if (it is TokenState.Failure) {
                    binding.progressBar.visibility = View.GONE
                    binding.tv.text = "An error occurred, check your connection"
                }
            }
        }

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}