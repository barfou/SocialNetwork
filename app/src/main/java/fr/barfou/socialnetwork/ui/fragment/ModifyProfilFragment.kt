package fr.barfou.socialnetwork.ui.fragment

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.get
import fr.barfou.socialnetwork.R
import fr.barfou.socialnetwork.ui.activity.MainActivity
import fr.barfou.socialnetwork.ui.utils.EditTextLinesLimiter
import fr.barfou.socialnetwork.ui.utils.convertLatLongToLocation
import fr.barfou.socialnetwork.ui.viewmodel.MainViewModel
import kotlinx.android.synthetic.main.fragment_modify_profil.*

class ModifyProfilFragment: Fragment() {

    private lateinit var mainViewModel: MainViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activity?.run {
            mainViewModel = ViewModelProvider(this, MainViewModel).get()
        } ?: throw IllegalStateException("Invalid Activity")
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_modify_profil, container, false)
    }

    @SuppressLint("SetTextI18n")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        (activity as? MainActivity)?.supportActionBar?.apply {
            this.setTitle(R.string.title_fragment_modify_profil)
            this.setDisplayHomeAsUpEnabled(true)
        }

        (activity as? MainActivity)?.run {
            this.mode = MainActivity.Mode.MODIFY_PROFILE
        }

        restrictTvBio()

        val user = mainViewModel.currentUser

        if (user != null) {
            tvNameProfil.text = user.getInitials()

            etLoginUser.setText(user.pseudo)

            showLocation(user.latitude, user.longitude)

            etBio.setText(user.about)
            //etPassword.setText(user.)

            btnLocalisation.setOnClickListener {
                //Traitement mise à jour localisation
                (activity as? MainActivity)?.run {
                    this.getLastLocation { result -> // Callback invoked if permissions not needed
                        val location = convertLatLongToLocation(requireContext(), result.latitude, result.longitude)
                        etTownUser.setText(location.town)
                        etCountryUser.setText(location.country)
                        user.latitude = result.latitude.toString()
                        user.longitude = result.longitude.toString()
                    }
                }
            }

            btnCancelEditProfil.setOnClickListener {
                requireActivity().onBackPressed()
            }

            btnConfirmEditProfil.setOnClickListener {
                //Traitement des données changées
                user.pseudo = etLoginUser.text.toString()
                user.about = etBio.text.toString()
                mainViewModel.updateUser(user)
                requireActivity().onBackPressed()
            }
        }
    }

    private fun restrictTvBio() {
        etBio.addTextChangedListener(EditTextLinesLimiter(etBio, 5));
    }

    @SuppressLint("SetTextI18n")
    private fun showLocation(latitude: String, longitude: String) {
        try {
            val location = convertLatLongToLocation(this.requireContext(), latitude.toDouble(), longitude.toDouble())
            val town = location.town
            val country = location.country
            etTownUser.setText(town)
            etCountryUser.setText(country)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}