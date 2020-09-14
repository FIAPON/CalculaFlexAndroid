package br.com.heiderlopes.calculaflex.ui.home

import android.content.Context
import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import br.com.heiderlopes.calculaflex.extensions.fromRemoteConfig
import br.com.heiderlopes.calculaflex.models.RequestState
import br.com.heiderlopes.calculaflex.models.dashboardmenu.DashboardItem
import br.com.heiderlopes.calculaflex.models.dashboardmenu.DashboardMenu
import br.com.heiderlopes.calculaflex.utils.featuretoggle.FeatureToggleHelper
import br.com.heiderlopes.calculaflex.utils.featuretoggle.FeatureToggleListener
import br.com.heiderlopes.calculaflex.utils.firebase.RemoteConfigKeys
import br.com.heiderlopes.calculaflex.utils.firebase.RemoteConfigUtils
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.iid.FirebaseInstanceId
import com.google.gson.Gson

class HomeViewModel : ViewModel() {

    val menuState = MutableLiveData<RequestState<List<DashboardItem>>>()

    private val db = FirebaseFirestore.getInstance()
    var dashboardMenu: DashboardMenu? = null
    val userNameState = MutableLiveData<RequestState<String>>()
    val logoutState = MutableLiveData<RequestState<String>>()

    fun signOut() {
        logoutState.value = RequestState.Loading
        FirebaseAuth.getInstance().signOut()
        logoutState.value = RequestState.Success("")
    }

    private fun saveToken() {
        val user = FirebaseAuth.getInstance().uid
        if (user != null)
            FirebaseInstanceId.getInstance().instanceId.addOnSuccessListener {
                db.collection("users")
                    .document(FirebaseAuth.getInstance().uid ?: "")
                    .update("token", it.token)
                    .addOnSuccessListener {}
                    .addOnFailureListener {}
            }
    }

    private fun getUser() {
        userNameState.value = RequestState.Loading

        val user = FirebaseAuth.getInstance().uid
        if (user == null) {
            userNameState.value = RequestState.Error(Throwable("Usuário deslogado"))
        } else {
            db.collection("users")
                .document(FirebaseAuth.getInstance().uid ?: "")
                .get()
                .addOnSuccessListener { documentSnapshot ->
                    saveToken()
                    val userName = documentSnapshot.data?.get("username") as String
                    userNameState.value = RequestState.Success(userName)
                }
                .addOnFailureListener {
                    userNameState.value = RequestState.Error(it)
                }
        }
    }

    fun createMenu() {
        menuState.value = RequestState.Loading

        RemoteConfigUtils.fetchAndActivate()
            .addOnCompleteListener {
                dashboardMenu =
                    Gson().fromRemoteConfig(
                        RemoteConfigKeys.MENU_DASHBOARD,
                        DashboardMenu::class.java
                    )

                getUser()

                val dashBoardItems = arrayListOf<DashboardItem>()

                val itemsMenu = dashboardMenu?.items ?: listOf()

                for (itemMenu in itemsMenu) {
                    FeatureToggleHelper().configureFeature(
                        itemMenu.feature,
                        object : FeatureToggleListener {
                            override fun onEnabled() {
                                dashBoardItems.add(itemMenu)
                            }

                            override fun onInvisible() {}

                            override fun onDisabled(clickListener: (Context) -> Unit) {
                                itemMenu.onDisabledListener = clickListener
                                dashBoardItems.add(itemMenu)
                            }
                        })
                }
                menuState.value = RequestState.Success(dashBoardItems)
            }
    }
}