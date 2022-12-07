package com.gym.gymapp.ui.homeScreens

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.content.IntentSender
import android.location.Address
import android.location.Geocoder
import android.location.LocationManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Looper
import android.provider.Settings
import android.view.View
import android.widget.Button
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.location.LocationManagerCompat.isLocationEnabled
import androidx.core.view.GravityCompat
import androidx.core.view.isVisible
import androidx.drawerlayout.widget.DrawerLayout
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.ui.NavigationUI
import com.bumptech.glide.Glide
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.location.*
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.gym.gymapp.OrderPlaced.OrderPlacedActivity
import com.gym.gymapp.R
import com.gym.gymapp.databinding.ActivityMainBinding
import com.gym.gymapp.ui.checkOut.CheckOutViewModel
import com.gym.gymapp.ui.loginSignUp.LoginSignUp
import com.gym.gymapp.utils.*
import com.onesignal.OneSignal
import com.razorpay.PaymentData
import com.razorpay.PaymentResultWithDataListener
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import pub.devrel.easypermissions.AppSettingsDialog
import pub.devrel.easypermissions.EasyPermissions
import java.util.*
import javax.inject.Inject


@AndroidEntryPoint
class MainActivity : AppCompatActivity(), View.OnClickListener,
    EasyPermissions.PermissionCallbacks, PaymentResultWithDataListener {

    private val checkOutViewModel: CheckOutViewModel by viewModels()

    private lateinit var googleSignInClient: GoogleSignInClient
    private lateinit var binding: ActivityMainBinding
    private lateinit var navController: NavController
    var isDrawerOpen: Boolean = false
    private var doubleBackToExitPressedOnce = false

    @Inject
    lateinit var session: SessionManager

    lateinit var locationRequest: LocationRequest
    var permissionToRequest = mutableListOf<String>()
    lateinit var fusedLocationProviderClient: FusedLocationProviderClient


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setTheme(R.style.mainScreenTheme)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        init()
        setOnClickListeners()
        setDrawerHeader()
    }


    private fun init() {
        println("token ${session.token}")
        val playerId = OneSignal.getDeviceState()!!.userId
        val modelName = Build.MODEL
        if (playerId != null) {
            session.playerId = playerId
        }

        var deviceId = Settings.Secure.getString(contentResolver, Settings.Secure.ANDROID_ID)
        session.deviceId = deviceId
        val gso =
            GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).requestEmail().build()
        googleSignInClient = GoogleSignIn.getClient(this, gso)

        initializeLocationSettings()
        navController = findNavController(R.id.navHostFragment)
        binding.bottomNavigation.menu.findItem(R.id.center).isEnabled = false
        NavigationUI.setupWithNavController(binding.bottomNavigation, navController)

        navController.addOnDestinationChangedListener { _, destination, _ ->
            when (destination.id) {
                R.id.homeFragment -> showBottomNav()
                R.id.menuBlog -> showBottomNav()
                R.id.menuFAQ -> showBottomNav()
                R.id.menuOffers -> showBottomNav()
                R.id.menuSocial -> showBottomNav()
                else -> hideBottomNav()
            }
        }

        if (intent.hasExtra("from")) {
            if (intent.getStringExtra("from") == "checkOut") {
                navController.navigate(R.id.addressFormFragment)
            }
        }

        updatedProfile.observe(this) {
            binding.navMenu.userName.text = it.name
            binding.navMenu.userEmail.text = it.email
            Glide.with(this).load(it.image).into(binding.navMenu.userImage)
        }
    }

    private fun initializeLocationSettings() {
        fusedLocationProviderClient =
            LocationServices.getFusedLocationProviderClient(applicationContext)
        locationRequest = LocationRequest.create().apply {
            priority = LocationRequest.PRIORITY_HIGH_ACCURACY
            interval = 500
            fastestInterval = 100
            numUpdates = 1
        }
        checkLocationEnabled()

    }

    private fun checkLocationEnabled() {
        var locationManager =
            applicationContext.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        if (isPermissionsGiven(applicationContext)) {
            if (!isLocationEnabled(locationManager)) {
                enableLocation()
            } else {
                getLocation()
            }
        } else {
            requestPermission()
        }
    }

    private fun requestPermission() {
        permissionToRequest.add(Manifest.permission.ACCESS_FINE_LOCATION)
        permissionToRequest.add(Manifest.permission.ACCESS_COARSE_LOCATION)
        EasyPermissions.requestPermissions(
            this,
            "Allow this Permission to serve you better",
            REQUEST_CODE_LOCATION,
            *permissionToRequest.toTypedArray()
        )
    }

    @SuppressLint("MissingPermission")
    private fun getLocation() {
        val locationCallback = object : LocationCallback() {
            override fun onLocationResult(p0: LocationResult?) {
                super.onLocationResult(p0)
                val locations = p0?.locations
                if (locations != null) {
                    session.userCurrentLat = locations[0].latitude.toString()
                    session.userCurrentLong = locations[0].longitude.toString()


                }
            }
        }

        fusedLocationProviderClient.lastLocation.addOnCompleteListener { task ->
            if (task.isSuccessful && task.result != null) {
                val location = task.result
                session.userCurrentLat = location!!.latitude.toString()
                session.userCurrentLong = location.longitude.toString()
                val geocoder = Geocoder(this@MainActivity, Locale.getDefault())
                val addresses: List<Address> = geocoder.getFromLocation(
                    session.userCurrentLat!!.toDouble(),
                    session.userCurrentLong!!.toDouble(),
                    1
                )
                val address: Address = addresses[0]
                session.pinCode = address.postalCode
                session.city = address.locality
                session.state = address.adminArea
                session.country = address.countryName
            } else {
                fusedLocationProviderClient.requestLocationUpdates(
                    locationRequest,
                    locationCallback,
                    Looper.getMainLooper()
                )
            }
        }
    }

    private fun enableLocation() {
        val builder = LocationSettingsRequest.Builder().addLocationRequest(locationRequest)
        builder.setAlwaysShow(true)
        val result = LocationServices.getSettingsClient(applicationContext)
            .checkLocationSettings(builder.build())

        result.addOnCompleteListener { task ->
            try {
                task.getResult(ApiException::class.java)
            } catch (e: ApiException) {
                when (e.statusCode) {
                    LocationSettingsStatusCodes.RESOLUTION_REQUIRED -> {
                        try {
                            e.status.startResolutionForResult(this, REQUEST_CODE_LOCATION)

                        } catch (ex: IntentSender.SendIntentException) {
                            ex.printStackTrace()
                        }
                    }
                    LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE -> {
                    }
                }
            }
        }
    }


    override fun onRequestPermissionsResult(
        requestCode: Int, permissions: Array<out String>, grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (!isPermissionsGiven(applicationContext)) {
            EasyPermissions.onRequestPermissionsResult(
                requestCode,
                permissions,
                grantResults,
                this
            )
        } else {
            checkLocationEnabled()
        }

    }

    override fun onPermissionsGranted(requestCode: Int, perms: MutableList<String>) {

    }

    override fun onPermissionsDenied(requestCode: Int, perms: MutableList<String>) {
        if (EasyPermissions.somePermissionPermanentlyDenied(this, perms)) {
            AppSettingsDialog.Builder(this).build().show()
        } else {
            checkLocationEnabled()
        }
    }

    fun setDrawerHeader() {

        with(binding.navMenu) {
            if (session.isLogin!!) {
                userEmail.isVisible = true
                userName.isVisible = true
                loginSignUpLbl.isVisible = false
                menuLogout.isVisible = true
                Glide.with(this@MainActivity).load(session.loginImage)
                    .error(R.drawable.user_icon).into(userImage)
                userName.text = session.loginName
                userEmail.text = session.loginEmail

            } else {
                userEmail.isVisible = false
                userName.isVisible = false
                menuLogout.isVisible = false
                loginSignUpLbl.isVisible = true
                loginSignUpLbl.setOnClickListener(this@MainActivity)
            }
        }

    }

    private fun hideBottomNav() {
        binding.bottomNavigation.isVisible = false
        binding.homeFragment.isVisible = false
    }

    private fun showBottomNav() {
        binding.bottomNavigation.isVisible = true
        binding.homeFragment.isVisible = true
    }

    private fun setOnClickListeners() {
        with(binding) {
            homeFragment.setOnClickListener(this@MainActivity)
            drawerLayout.addDrawerListener(drawerListener)
            navMenu.menuOrders.setOnClickListener(this@MainActivity)
            navMenu.menuActivePacks.setOnClickListener(this@MainActivity)
            navMenu.menuAddress.setOnClickListener(this@MainActivity)
            navMenu.menuOnGoingCourse.setOnClickListener(this@MainActivity)
            navMenu.menuSessions.setOnClickListener(this@MainActivity)
            navMenu.menuAchievements.setOnClickListener(this@MainActivity)
            navMenu.menuServices.setOnClickListener(this@MainActivity)
            navMenu.menuTransactions.setOnClickListener(this@MainActivity)
            navMenu.menuInvite.setOnClickListener(this@MainActivity)
            navMenu.menuCommunity.setOnClickListener(this@MainActivity)
            navMenu.menuFAQ.setOnClickListener(this@MainActivity)
            navMenu.menuTermsConditions.setOnClickListener(this@MainActivity)
            navMenu.menuLogout.setOnClickListener(this@MainActivity)
            navMenu.userImage.setOnClickListener(this@MainActivity)
            navMenu.userName.setOnClickListener(this@MainActivity)
            navMenu.userEmail.setOnClickListener(this@MainActivity)
        }
    }

    private val drawerListener = object : DrawerLayout.DrawerListener {
        override fun onDrawerSlide(drawerView: View, slideOffset: Float) {
        }

        override fun onDrawerOpened(drawerView: View) {
            isDrawerOpen = true
        }

        override fun onDrawerClosed(drawerView: View) {
            isDrawerOpen = false
        }

        override fun onDrawerStateChanged(newState: Int) {}
    }


    fun openCloseDrawer() {
        if (isDrawerOpen) {
            binding.drawerLayout.closeDrawer(GravityCompat.START)
        } else {
            binding.drawerLayout.openDrawer(GravityCompat.START)
        }
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.homeFragment -> {
                navController.navigate(R.id.homeFragment)
            }

            R.id.menuOrders -> {
                checkLoginAndNavigate(R.id.ordersFragment)
                openCloseDrawer()
            }

            R.id.menuAddress -> {
                checkLoginAndNavigate(R.id.addressListFragment)
                openCloseDrawer()
            }

            R.id.menuActivePacks -> {
                checkLoginAndNavigate(R.id.activePacksFragment)
                openCloseDrawer()
            }

            R.id.menuOnGoingCourse -> {
                val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://Zolistic.in"))
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                intent.setPackage("com.android.chrome")
                try {
                    startActivity(intent)
                } catch (ex: ActivityNotFoundException) {
                    intent.setPackage("com.amazon.cloud9")
                    startActivity(intent)
                }
                openCloseDrawer()
            }

            R.id.menuSessions -> {
                checkLoginAndNavigate(R.id.homeFragment)
                openCloseDrawer()
            }

            R.id.menuAchievements -> {
                checkLoginAndNavigate(R.id.achievementsFragment)
                openCloseDrawer()
            }

            R.id.menuServices -> {
                checkLoginAndNavigate(R.id.homeFragment)
                openCloseDrawer()
            }

            R.id.menuTransactions -> {
                checkLoginAndNavigate(R.id.homeFragment)
                openCloseDrawer()
            }

            R.id.menuInvite -> {
                checkLoginAndNavigate(R.id.menuSocial)
                openCloseDrawer()
            }
            R.id.menuCommunity -> {
                val bundle = Bundle().apply { putString("id", "1") }
                navController.navigate(R.id.slugsFragment, bundle)
                openCloseDrawer()
            }
            R.id.menuFAQ -> {
                checkLoginAndNavigate(R.id.menuFAQ)
                openCloseDrawer()
            }
            R.id.menuTermsConditions -> {
                val bundle = Bundle().apply { putString("id", "2") }
                navController.navigate(R.id.slugsFragment, bundle)
                openCloseDrawer()
            }
            R.id.loginSignUpLbl -> {
                openCloseDrawer()
                startActivity(Intent(this@MainActivity, LoginSignUp::class.java))

            }
            R.id.menuLogout -> {
                logOut()
            }

            R.id.userImage -> {
                checkLoginAndNavigate(R.id.accountFragment)
                openCloseDrawer()
            }
            R.id.userName -> {
                checkLoginAndNavigate(R.id.accountFragment)
                openCloseDrawer()

            }
            R.id.userEmail -> {
                checkLoginAndNavigate(R.id.accountFragment)
                openCloseDrawer()
            }
        }
    }

    private fun checkLoginAndNavigate(id: Int) {
        if (session.isLogin!!) {
            navController.navigate(id)
        } else {
            showBottomSheetForLogin()
        }
    }

    private fun showBottomSheetForLogin() {
        val bottomSheetDialog = BottomSheetDialog(this)
        bottomSheetDialog.setContentView(R.layout.bottom_sheet_login_signup)
        val loginBtn = bottomSheetDialog.findViewById<Button>(R.id.loginBtn)
        val signUpBtn = bottomSheetDialog.findViewById<Button>(R.id.signUpBtn)
        loginBtn?.setOnClickListener {
            startActivity(
                Intent(this@MainActivity, LoginSignUp::class.java).putExtra(
                    "from",
                    "Login"
                )
            )
        }
        signUpBtn?.setOnClickListener {
            startActivity(
                Intent(this@MainActivity, LoginSignUp::class.java).putExtra(
                    "from",
                    "SignUp"
                )
            )
        }
        bottomSheetDialog.show()

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (resultCode) {

            Activity.RESULT_OK -> {
                if (REQUEST_CODE_LOCATION == requestCode) {
                    session.isLocationEnabled = true
                    checkLocationEnabled()
                }
            }

            Activity.RESULT_CANCELED -> {
                if (REQUEST_CODE_LOCATION == requestCode) {
                    session.isLocationEnabled = false
                    checkLocationEnabled()
                }
            }
        }
    }


    override fun onPaymentSuccess(p0: String?, paymentDetails: PaymentData?) {
        paymentDetails?.orderId
        paymentDetails?.signature
        paymentDetails?.paymentId

        println("paymentDetails ${paymentDetails?.data}")

        val params: MutableMap<String, String> = HashMap()
        params["cart_id"] = session.cartId.toString()
        params["net_amount"] = session.checkOutAmount.toString()
        params["address_id"] = session.checkOutAddressId.toString()
        params["razorpay_payment_id"] = paymentDetails?.paymentId.toString()
        params["razorpay_signature"] = paymentDetails?.signature.toString()
        println("checkOutParams $params")

        checkOutViewModel.checkOutApi(session.token!!, params)
        checkOutViewModel.checkOut.observe(this) { response ->
            when (response) {
                is Resource.Success -> {
                    session.cartId = ""
                    session.checkOutAmount = ""
                    session.checkOutAddressId = ""
                    session.cartCount = 0
                    startActivity(Intent(this, OrderPlacedActivity::class.java))
                    finish()
                }
                is Resource.Loading -> {
                }
                is Resource.Error -> {
                    showToast(response.message.toString())
                }
            }
        }
    }

    override fun onPaymentError(p0: Int, p1: String?, p2: PaymentData?) {
        showToast("$p1")
    }

    fun logOut() {
        googleSignInClient.signOut()
        openCloseDrawer()
        session.apply {
            isLogin = false
            loginName = ""
            loginEmail = ""
            loginImage = ""
            userCurrentLat = ""
            userCurrentLong = ""
            address = ""
            pinCode = ""
            state = ""
            city = ""
        }
        startActivity(Intent(this@MainActivity, LoginSignUp::class.java))
        finish()
    }

    override fun onBackPressed() {
        if (navController.currentDestination?.id == R.id.homeFragment) {
            if (doubleBackToExitPressedOnce) {
                super.onBackPressed()
                return
            }
            this.doubleBackToExitPressedOnce = true
            showToast("Please click BACK again to exit")
            lifecycleScope.launch {
                delay(2000)
                doubleBackToExitPressedOnce = false
            }
        } else {
            super.onBackPressed()
        }

    }

    override fun onResume() {
        super.onResume()
    }

}