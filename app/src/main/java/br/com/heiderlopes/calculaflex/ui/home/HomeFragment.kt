package br.com.heiderlopes.calculaflex.ui.home

import android.os.Build
import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.activity.OnBackPressedCallback
import androidx.annotation.RequiresApi
import androidx.core.os.bundleOf
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.RecyclerView
import br.com.heiderlopes.calculaflex.R
import br.com.heiderlopes.calculaflex.extensions.startDeeplink
import br.com.heiderlopes.calculaflex.models.RequestState
import br.com.heiderlopes.calculaflex.models.dashboardmenu.DashboardItem
import br.com.heiderlopes.calculaflex.ui.base.auth.BaseAuthFragment
import br.com.heiderlopes.calculaflex.utils.CalculaFlexTracker

class HomeFragment : BaseAuthFragment() {

    override val layout = R.layout.fragment_home
    override val screeName = "Home"

    private val homeViewModel: HomeViewModel by viewModels()

    private lateinit var rvHomeDashboard: RecyclerView
    private lateinit var tvHomeHelloUser: TextView
    private lateinit var tvSubTitleSignUp: TextView

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setUpView(view)
        homeViewModel.createMenu()
        registerObserver()
        registerBackPressedAction()
    }

    private fun registerBackPressedAction() {
        val callback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                activity?.finish()
            }
        }
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, callback)
    }

    private fun setUpView(view: View) {
        rvHomeDashboard = view.findViewById(R.id.rvHomeDashboard)
        tvHomeHelloUser = view.findViewById(R.id.tvHomeHelloUser)
        tvSubTitleSignUp = view.findViewById(R.id.tvSubTitleSignUp)
    }

    private fun registerObserver() {
        homeViewModel.menuState.observe(viewLifecycleOwner, Observer {
            when (it) {
                is RequestState.Loading -> {
                    showLoading()
                }
                is RequestState.Success -> {
                    hideLoading()
                    setUpMenu(it.data)
                }
                is RequestState.Error -> {
                    hideLoading()
                }
            }
        })

        homeViewModel.userNameState.observe(viewLifecycleOwner, Observer {
            when (it) {
                is RequestState.Loading -> {
                    tvHomeHelloUser.text = "Carregando ..."
                }
                is RequestState.Success -> {
                    tvHomeHelloUser.text =
                        String.format(homeViewModel.dashboardMenu?.title ?: "", it.data)
                    tvSubTitleSignUp.text = homeViewModel.dashboardMenu?.subTitle
                }
                is RequestState.Error -> {
                    tvHomeHelloUser.text = "Bem-vindo"
                    tvSubTitleSignUp.text = homeViewModel.dashboardMenu?.subTitle
                }
            }
        })

        homeViewModel.logoutState.observe(viewLifecycleOwner, Observer {
            when (it) {
                is RequestState.Loading -> {
                    showLoading()
                }
                is RequestState.Success -> {
                    hideLoading()
                    findNavController().navigate(R.id.login_nav_graph)
                }
                is RequestState.Error -> {
                    hideLoading()
                    showMessage(it.throwable.message)
                }
            }
        })
    }

    private fun setUpMenu(items: List<DashboardItem>) {
        rvHomeDashboard.adapter = HomeListAdapter(items, this::clickItem)
    }

    private fun clickItem(item: DashboardItem) {
        item.onDisabledListener.let {
            it?.invoke(requireContext())
        }

        CalculaFlexTracker.trackEvent(requireActivity(),
            bundleOf("feature" to item.feature)
        )

        if (item.onDisabledListener == null) {
            when (item.feature) {
                "SIGN_OUT" -> {
                    homeViewModel.signOut()
                }
                else -> {
                    startDeeplink(item.action.deeplink)
                }
            }
        }
    }
}
