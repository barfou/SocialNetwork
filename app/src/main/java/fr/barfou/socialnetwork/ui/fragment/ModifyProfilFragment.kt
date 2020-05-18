package fr.barfou.socialnetwork.ui.fragment

import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.get
import androidx.navigation.fragment.findNavController
import fr.barfou.socialnetwork.ui.activity.LoginActivity
import fr.barfou.socialnetwork.R
import fr.barfou.socialnetwork.data.model.User
import fr.barfou.socialnetwork.ui.activity.MainActivity
import fr.barfou.socialnetwork.ui.adapter.TrophyAdapter
import fr.barfou.socialnetwork.ui.utils.convertLatLongToLocation
import fr.barfou.socialnetwork.ui.viewmodel.MainViewModel
import kotlinx.android.synthetic.main.fragment_modify_profil.*
import kotlinx.android.synthetic.main.fragment_profil.*
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.*
import java.util.Calendar.*


class ModifyProfilFragment: Fragment() {

    val c = getInstance()
    var year : Int? = null
    var month : Int? = null
    var day : Int? = null

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

        val user = mainViewModel.currentUser

        if (user != null) {
            tvNameProfil.text = user.getInitials()

            etLoginUser.setText(user.pseudo)

            showLocation(user.latitude, user.longitude)

            etBio.setText(user.about)
            //etPassword.setText(user.)

            clLocation.setOnClickListener {
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

            /*dtpBirthday.setOnClickListener {
                if(etBirthday.text.toString() == "") {
                    day = c.get(DAY_OF_MONTH)
                    month = c.get(MONTH)
                    year = c.get(YEAR)
                } else {
                    val tblDate : List<String> = etBirthday.text.toString().split("/")

                    day = tblDate[0].toInt()
                    month = tblDate[1].toInt()
                    year = tblDate[2].toInt()
                }

                val dpd = DatePickerDialog(
                    this.requireContext(),
                    DatePickerDialog.OnDateSetListener { _, year, monthOfYear, dayOfMonth ->

                        // Display Selected date in textbox
                        etBirthday.setText("$dayOfMonth/$monthOfYear/$year")
                    }, year!!, month!!, day!!)

                dpd.show()
            }*/

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