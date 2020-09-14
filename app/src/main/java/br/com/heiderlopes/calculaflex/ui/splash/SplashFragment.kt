package br.com.heiderlopes.calculaflex.ui.splash

import android.os.Bundle
import android.os.Handler
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.widget.ImageView
import android.widget.TextView
import androidx.navigation.fragment.FragmentNavigatorExtras
import androidx.navigation.fragment.NavHostFragment
import br.com.heiderlopes.calculaflex.R
import br.com.heiderlopes.calculaflex.utils.firebase.RemoteConfigUtils

class SplashFragment : Fragment() {

    private lateinit var ivLogoApp: ImageView
    private lateinit var tvAppName: TextView

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_splash, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setUpView(view)
        startAnimation()

        updateRemoteConfig()
    }

    private fun updateRemoteConfig() {
        Handler().postDelayed({
            RemoteConfigUtils.fetchAndActivate()
                .addOnCompleteListener {
                    nextScreen()
                }
        }, 2000)
    }

    private fun nextScreen() {
        val extras = FragmentNavigatorExtras(
            ivLogoApp to "logoApp",
            tvAppName to "textApp"
        )
        NavHostFragment.findNavController(this)
            .navigate(
                R.id.action_splashFragment_to_login_nav_graph,
                null, null, extras
            )
    }

    private fun setUpView(view: View) {
        ivLogoApp = view.findViewById(R.id.ivLogoApp)
        tvAppName = view.findViewById(R.id.tvAppName)

    }

    private fun startAnimation() {
        val anim = AnimationUtils.loadAnimation(context, R.anim.anim_splash)
        ivLogoApp.startAnimation(anim)
        tvAppName.startAnimation(anim)
    }
}
