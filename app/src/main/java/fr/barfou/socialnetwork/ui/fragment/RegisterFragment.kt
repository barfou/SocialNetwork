package fr.barfou.socialnetwork.ui.fragment

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Address
import android.location.Location
import android.location.LocationManager
import android.os.Bundle
import android.os.Looper
import android.provider.Settings
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.get
import androidx.navigation.fragment.findNavController
import com.google.android.gms.location.*
import com.google.firebase.auth.FirebaseAuth
import fr.barfou.socialnetwork.ui.activity.LoginActivity
import fr.barfou.socialnetwork.R
import fr.barfou.socialnetwork.data.model.User
import fr.barfou.socialnetwork.ui.activity.MainActivity
import fr.barfou.socialnetwork.ui.listener.OnLocationResult
import fr.barfou.socialnetwork.ui.utils.getCurrentDate
import fr.barfou.socialnetwork.ui.utils.hide
import fr.barfou.socialnetwork.ui.utils.show
import fr.barfou.socialnetwork.ui.viewmodel.LoginViewModel
import kotlinx.android.synthetic.main.fragment_login.btnLogin
import kotlinx.android.synthetic.main.fragment_login.btnRegister
import kotlinx.android.synthetic.main.fragment_login.etPassword
import kotlinx.android.synthetic.main.fragment_register.*

class RegisterFragment : Fragment() {

    val PERMISSION_ID = 42
    lateinit var mFusedLocationClient: FusedLocationProviderClient
    lateinit var addresses: List<Address>
    var currentLocation: Location? = null

    private lateinit var loginViewModel: LoginViewModel
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activity?.run {
            loginViewModel = ViewModelProvider(this, LoginViewModel).get()
            mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        } ?: throw IllegalStateException("Invalid Activity")
    }


    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_register, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        auth = FirebaseAuth.getInstance()

        (activity as? LoginActivity)?.supportActionBar?.apply {
            this.setTitle(R.string.app_name)
            this.setDisplayHomeAsUpEnabled(false)
        }

        btnLogin.setOnClickListener {
            findNavController().navigate(
                    R.id.action_register_fragment_to_login_fragment
            )
        }

        btnRegister.setOnClickListener {
            if (!etPseudoRegister.text.isNullOrBlank() && !etMail.text.isNullOrBlank() && !etPassword.text.isNullOrBlank()) {
                if (chkCGU.isChecked) {
                    getLastLocation { result ->
                        result?.run {
                            registerUser(location = result)
                        }
                    }
                } else {
                    progress_bar.hide()
                    Toast.makeText(requireContext(), "Veuillez acceptez les CGU.", Toast.LENGTH_LONG).show()
                }
            } else {
                progress_bar.hide()
                Toast.makeText(requireContext(), "Saisie incorrecte.", Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun registerUser(location: Location) {
        progress_bar.show()
        auth.createUserWithEmailAndPassword(etMail.text.toString(), etPassword.text.toString())
                .addOnCompleteListener(this.requireActivity()) { task ->
                    if (task.isSuccessful) {
                        val user = auth.currentUser
                        user?.run {
                            try {
                                progress_bar.hide()
                                var tempLat = location.latitude
                                var tempLong = location.longitude
                                loginViewModel.insertUserDetails(User(user.uid, etMail.text.toString(), etPseudoRegister.text.toString(), "", getCurrentDate(), "", location.latitude.toString(), location.longitude.toString()))
                                val intent = Intent(requireContext(), MainActivity::class.java)
                                intent.putExtra("userId", user.uid)
                                startActivity(intent)
                            } catch (e: Exception) {
                                e.printStackTrace()
                                Toast.makeText(requireContext(), "Creation failed." + task.exception, Toast.LENGTH_LONG).show()
                            }
                        }

                    } else {
                        progress_bar.hide()
                        Toast.makeText(requireContext(), "Creation failed." + task.exception, Toast.LENGTH_LONG).show()
                    }
                }
    }

    @SuppressLint("MissingPermission")
    private fun getLastLocation(onLocationResult: OnLocationResult) {
        if (checkPermissions()) {
            if (isLocationEnabled()) {

                mFusedLocationClient.lastLocation.addOnCompleteListener(this.requireActivity()) { task ->
                    val location: Location? = task.result
                    if (location == null) {
                        requestNewLocationData()
                    } else {
                        try {
                            onLocationResult(location)
                        } catch (e: Exception) {
                            println(e.toString())
                        }
                    }
                }
            } else {
                Toast.makeText(this.requireActivity(), "Turn on location", Toast.LENGTH_LONG).show()
                val intent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
                startActivity(intent)
            }
        } else {
            requestPermissions()
        }
    }

    @SuppressLint("MissingPermission")
    private fun requestNewLocationData() {
        val mLocationRequest = LocationRequest()
        mLocationRequest.priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        mLocationRequest.interval = 0
        mLocationRequest.fastestInterval = 0
        mLocationRequest.numUpdates = 1

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this.requireActivity())
        mFusedLocationClient!!.requestLocationUpdates(
                mLocationRequest, mLocationCallback,
                Looper.myLooper()
        )
    }

    private val mLocationCallback = object : LocationCallback() {
        override fun onLocationResult(locationResult: LocationResult) {
            val mLastLocation: Location = locationResult.lastLocation
        }
    }

    private fun isLocationEnabled(): Boolean {
        val locationManager: LocationManager = requireActivity().getSystemService(Context.LOCATION_SERVICE) as LocationManager
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(
                LocationManager.NETWORK_PROVIDER
        )
    }

    private fun checkPermissions(): Boolean {
        if (ActivityCompat.checkSelfPermission(
                        this.requireActivity(),
                        Manifest.permission.ACCESS_COARSE_LOCATION
                ) == PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(
                        this.requireActivity(),
                        Manifest.permission.ACCESS_FINE_LOCATION
                ) == PackageManager.PERMISSION_GRANTED
        ) {
            return true
        }
        return false
    }

    private fun requestPermissions() {
        ActivityCompat.requestPermissions(
                this.requireActivity(),
                arrayOf(Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION),
                PERMISSION_ID
        )
    }


    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == PERMISSION_ID) {
            if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                getLastLocation { result ->
                    result?.run {
                        registerUser(location = result)
                    }
                }
            }
        }
    }
}