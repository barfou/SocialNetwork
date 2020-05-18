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
import fr.barfou.socialnetwork.ui.activity.MainActivity
import fr.barfou.socialnetwork.ui.listener.OnLocationResult
import fr.barfou.socialnetwork.ui.utils.hide
import fr.barfou.socialnetwork.ui.utils.show
import fr.barfou.socialnetwork.ui.viewmodel.LoginViewModel
import kotlinx.android.synthetic.main.fragment_login.*

class LoginFragment : Fragment(), OnLocationResult {

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
        return inflater.inflate(R.layout.fragment_login, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        auth = FirebaseAuth.getInstance()

        (activity as? LoginActivity)?.supportActionBar?.apply {
            this.setTitle(R.string.app_name)
            this.setDisplayHomeAsUpEnabled(false)
        }

        btnLogin.setOnClickListener {
            (activity as? LoginActivity)?.run {
                this.getLastLocation { result -> // Callback invoked if permissions not needed
                    signInUser(location = result)
                }
            }
        }

        btnRegister.setOnClickListener {
            findNavController().navigate(
                    R.id.action_login_fragment_to_register_fragment
            )
        }
    }

    private fun signInUser(location: Location) {
        progress_bar.show()
        if (!etPseudo.text.isNullOrBlank() && !etPassword.text.isNullOrBlank()) {
            auth.signInWithEmailAndPassword(etPseudo.text.toString(), etPassword.text.toString())
                    .addOnCompleteListener(this.requireActivity()) { task ->
                        if (task.isSuccessful) {
                            val user = auth.currentUser
                            user?.run {
                                loginViewModel.updateUserLocation(user.uid, location.latitude, location.longitude)
                                progress_bar.hide()
                                val intent = Intent(requireContext(), MainActivity::class.java)
                                intent.putExtra("userId", user.uid)
                                startActivity(intent)
                            }
                        } else {
                            progress_bar.hide()
                            Toast.makeText(requireContext(), "Authentication failed.", Toast.LENGTH_SHORT).show()
                        }
                    }
        } else {
            progress_bar.hide()
            Toast.makeText(requireContext(), "Saisie incorrecte.", Toast.LENGTH_SHORT).show()
        }
    }

    // On Location Result Listener
    override fun invoke(result: Location) {
        signInUser(location = result)
    }
}