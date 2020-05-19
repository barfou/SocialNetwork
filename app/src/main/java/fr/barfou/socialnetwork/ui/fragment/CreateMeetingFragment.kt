package fr.barfou.socialnetwork.ui.fragment

import android.app.DatePickerDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.get
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import fr.barfou.socialnetwork.R
import fr.barfou.socialnetwork.data.model.Meeting
import fr.barfou.socialnetwork.data.model.Theme
import fr.barfou.socialnetwork.data.model.getTheme
import fr.barfou.socialnetwork.ui.activity.MainActivity
import fr.barfou.socialnetwork.ui.utils.IntToDateString
import fr.barfou.socialnetwork.ui.utils.convertLatLongToLocation
import fr.barfou.socialnetwork.ui.utils.getCurrentDate
import fr.barfou.socialnetwork.ui.viewmodel.MainViewModel
import kotlinx.android.synthetic.main.fragment_create_meeting.*


class CreateMeetingFragment: Fragment(), OnMapReadyCallback {

    private lateinit var mainViewModel: MainViewModel
    lateinit var googleMap: GoogleMap
    var location: LatLng? = null

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
        return inflater.inflate(R.layout.fragment_create_meeting, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        (activity as? MainActivity)?.supportActionBar?.apply {
            this.setTitle(R.string.title_fragment_create_meeting)
            this.setDisplayHomeAsUpEnabled(false)
        }

        (activity as? MainActivity)?.run {
            this.mode = MainActivity.Mode.PROFILE
        }

        //Affichage de la carte
        mapMeeting.onCreate(savedInstanceState)
        mapMeeting.onResume()

        mapMeeting.getMapAsync(this)
        //

        val listTheme = mutableListOf<String>()
        listTheme.add("SPORT")
        listTheme.add("CULTURE")
        val aaSpinnerTheme = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, listTheme)
        aaSpinnerTheme.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spTheme!!.adapter = aaSpinnerTheme

        val listType = mutableListOf<String>()

        val aaSpinnerType = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, listType)
        aaSpinnerType.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spType!!.adapter = aaSpinnerType

        spTheme?.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
            override fun onNothingSelected(parent: AdapterView<*>?) {}

            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                listType.clear()
                when (position) {
                    0 -> mainViewModel.getTypeWithTheme(Theme.SPORT).forEach {
                        listType.add(it.name)
                    }

                    1 -> mainViewModel.getTypeWithTheme(Theme.CULTURE).forEach {
                        listType.add(it.name)
                    }
                }
                aaSpinnerType.notifyDataSetChanged()
            }
        }

        var currentDate = getCurrentDate()
        val tblCurrentDate = currentDate.split("/")
        var day = tblCurrentDate[0].toInt()
        var month = tblCurrentDate[1].toInt() - 1
        var year = tblCurrentDate[2].toInt()

        etDateMeeting.setText(currentDate)

        dtpDateMeeting.setOnClickListener {
            if(etDateMeeting.text.toString() != "") {
                val tblDate : List<String> = etDateMeeting.text.toString().split("/")

                day = tblDate[0].toInt()
                month = tblDate[1].toInt() - 1
                year = tblDate[2].toInt()
            }

            val dpd = DatePickerDialog(
                this.requireContext(),
                DatePickerDialog.BUTTON_POSITIVE,
                DatePickerDialog.OnDateSetListener { _, year, monthOfYear, dayOfMonth ->
                    // Display Selected date in textbox
                    //currentDate = "$dayOfMonth/${monthOfYear + 1}/$year"
                    currentDate = IntToDateString(dayOfMonth, monthOfYear + 1, year)
                    etDateMeeting.setText(currentDate)
                }, year, month, day
            )
            dpd.show()
        }

        clLocalisation.setOnClickListener {
            (activity as? MainActivity)?.run {
                this.getLastLocation { result -> // Callback invoked if permissions not needed
                    val currentLocation = convertLatLongToLocation(requireContext(), result.latitude, result.longitude)
                    location = LatLng(currentLocation.lat, currentLocation.long)
                    googleMap.clear()
                    googleMap.addMarker(MarkerOptions()
                        .position(location!!)
                        .title("Your position")
                    )

                    val center = CameraUpdateFactory.newLatLng(
                        location
                    )
                    googleMap.moveCamera(center)
                }
            }
        }

        btnCancelCreateMeeting.setOnClickListener {
            requireActivity().onBackPressed()
        }

        btnConfirmCreateMeeting.setOnClickListener {
            if(location != null) {
                //Traitement des données changées
                val fireBaseId = ""
                val userId: String = mainViewModel.currentUser!!.firebaseId
                val type: String = spType.selectedItem.toString()
                val typeId: String = mainViewModel.getIdTypeWithName(type)
                val theme: String = spTheme.selectedItem.toString()
                val name: String = etNameMeeting.text.toString()
                val date_creation: String = getCurrentDate()
                val date_event: String = etDateMeeting.text.toString()
                val lat: String = location!!.latitude.toString()
                val long: String = location!!.longitude.toString()
                val imgUrl = ""
                val details = etMoreInformations.text.toString()

                if(name != "" && date_event != "" && details != "") {
                    val meeting = Meeting(fireBaseId, userId, typeId, type, getTheme(theme), name, date_creation, date_event, lat, long, imgUrl, details)
                    mainViewModel.addNewMeeting(meeting)
                    requireActivity().onBackPressed()
                } else {
                    Toast.makeText(requireContext(), "Veuillez verifier que tous les champs sont remplis", Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(requireContext(), "Veuillez selectionner un emplacement sur la carte", Toast.LENGTH_SHORT).show()
            }
        }

    }

    override fun onMapReady(map: GoogleMap?) {
        map?.let {
            googleMap = it
        }

        googleMap.setOnMapClickListener {
            location = it
            googleMap.clear()
            googleMap.addMarker(MarkerOptions()
                .position(location!!)
                .title("Event posotion")
            )

            val center = CameraUpdateFactory.newLatLng(
                location
            )
            googleMap.moveCamera(center)
        }
    }


}