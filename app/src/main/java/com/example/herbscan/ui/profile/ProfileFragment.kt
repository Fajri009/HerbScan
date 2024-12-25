package com.example.herbscan.ui.profile

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.credentials.ClearCredentialStateRequest
import androidx.credentials.CredentialManager
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.example.herbscan.ViewModelFactory
import com.example.herbscan.data.local.preference.user.User
import com.example.herbscan.data.local.preference.user.UserPreference
import com.example.herbscan.databinding.FragmentProfileBinding
import com.example.herbscan.ui.auth.login.LoginActivity
import com.example.herbscan.ui.profile.changePassword.ChangePasswordActivity
import com.example.herbscan.ui.profile.editProfile.EditProfileActivity
import com.example.herbscan.ui.profile.favorite.FavoriteActivity
import com.example.herbscan.ui.profile.history.HistoryActivity
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.launch
import com.example.herbscan.data.network.Result
import com.example.herbscan.data.network.firebase.UserAuth

class ProfileFragment : Fragment() {
    private lateinit var binding: FragmentProfileBinding
    private val viewModel by viewModels<ProfileViewModel> {
        ViewModelFactory.getInstance()
    }

    private var userModel = User()
    private lateinit var userPreference: UserPreference

    private lateinit var auth: FirebaseAuth
    private lateinit var userAuth: UserAuth

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentProfileBinding.inflate(layoutInflater)

        auth = FirebaseAuth.getInstance()

        userPreference = UserPreference(requireContext())
        userModel = userPreference.getUser()

        getCurrentUser()

        binding.apply {
            btnEditProfile.setOnClickListener {
                val intent = Intent(requireContext(), EditProfileActivity::class.java)
                intent.putExtra("USER_AUTH", userAuth)
                startActivity(intent)
            }
            btnChangePassword.setOnClickListener {
                val intent = Intent(requireContext(), ChangePasswordActivity::class.java)
                intent.putExtra("USER_AUTH", userAuth)
                startActivity(intent)
            }
            btnHistory.setOnClickListener {
                val intent = Intent(requireContext(), HistoryActivity::class.java)
                startActivity(intent)
            }
            btnFavorite.setOnClickListener {
                val intent = Intent(requireContext(), FavoriteActivity::class.java)
                startActivity(intent)
            }
            btnLanguage.setOnClickListener {

            }
            btnLogout.setOnClickListener {
                logout()
            }
        }

        return binding.root
    }

    private fun getCurrentUser() {
        viewModel.getCurrentUser().observe(requireActivity()) { result ->
            if (result != null) {
                when (result) {
                    is Result.Loading -> {
                        binding.progressBar.visibility = View.VISIBLE
                    }
                    is Result.Success -> {
                        binding.progressBar.visibility = View.GONE

                        userAuth = result.data

                        binding.apply {
                            Glide.with(requireContext())
                                .load(userAuth.profilePic)
                                .into(ivProfilePicture)
                            tvUserName.text = buildString {
                                append(userAuth.firstName)
                                append(" ")
                                append(userAuth.lastName)
                            }
                            tvUserEmail.text = userAuth.email
                        }
                    }
                    is Result.Error -> {
                        binding.progressBar.visibility = View.GONE

                        showToast(result.error)
                    }
                }
            }
        }
    }

    private fun logout() {
        lifecycleScope.launch {
            val credentialManager = CredentialManager.create(requireContext())

            auth.signOut()
            credentialManager.clearCredentialState(ClearCredentialStateRequest())

            userModel.uid = ""
            userModel.rememberMe = false
            userPreference.setUser(userModel)

            val intent = Intent(requireContext(), LoginActivity::class.java)
            startActivity(intent)
        }
    }

    private fun showToast(message: String) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
    }
}