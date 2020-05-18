package fr.barfou.socialnetwork.ui.fragment

import android.content.Intent
import android.location.Location
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.get
import androidx.navigation.fragment.findNavController
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

class RegisterFragment : Fragment(), OnLocationResult {

    private lateinit var loginViewModel: LoginViewModel
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activity?.run {
            loginViewModel = ViewModelProvider(this, LoginViewModel).get()
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
                    (activity as? LoginActivity)?.run {
                        this.getLastLocation { result -> // Callback invoked if permissions not needed
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

    // On Location Result Listener
    override fun invoke(result: Location) { // Callback invoked if permissions needed
        registerUser(location = result)
    }
}