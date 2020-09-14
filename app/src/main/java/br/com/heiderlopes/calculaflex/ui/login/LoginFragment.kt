package br.com.heiderlopes.calculaflex.ui.login

import android.os.Bundle
import android.view.View
import android.view.animation.AnimationUtils
import android.widget.*
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import androidx.transition.TransitionInflater
import br.com.heiderlopes.calculaflex.R
import br.com.heiderlopes.calculaflex.exceptions.EmailInvalidException
import br.com.heiderlopes.calculaflex.exceptions.PasswordInvalidException
import br.com.heiderlopes.calculaflex.extensions.hideKeyboard
import br.com.heiderlopes.calculaflex.models.RequestState
import br.com.heiderlopes.calculaflex.ui.base.BaseFragment
import br.com.heiderlopes.calculaflex.ui.base.auth.NAVIGATION_KEY

class LoginFragment : BaseFragment() {

    override val layout = R.layout.fragment_login

    override val screeName = "Login"

    private val loginViewModel: LoginViewModel by viewModels()

    private lateinit var containerLogin: LinearLayout
    private lateinit var tvSubTitleLogin: TextView
    private lateinit var tvNewAccount: TextView
    private lateinit var tvResetPassword: TextView
    private lateinit var btLogin: Button
    private lateinit var etEmailLogin: EditText
    private lateinit var etPasswordLogin: EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        sharedElementEnterTransition = TransitionInflater
            .from(context)
            .inflateTransition(android.R.transition.move)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setUpView(view)
        startLoginAnimation()
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

    private fun registerObserver() {
        this.loginViewModel.loginState.observe(viewLifecycleOwner, Observer {
            when (it) {
                is RequestState.Success -> showSuccess()
                is RequestState.Error -> showError(it.throwable)
                is RequestState.Loading -> showLoading("Realizando a autenticação")
            }
        })

        this.loginViewModel.resetPasswordState.observe(viewLifecycleOwner, Observer {
            when (it) {
                is RequestState.Success -> {
                    hideLoading()
                    showMessage(it.data)
                }
                is RequestState.Error -> showError(it.throwable)
                is RequestState.Loading -> showLoading("Reenviando o e-mail para alteração")
            }
        })
    }

    private fun showSuccess() {
        hideLoading()
        val navIdFromArguments = arguments?.getInt(NAVIGATION_KEY)
        if (navIdFromArguments == null) {
            findNavController().navigate(R.id.main_nav_graph)
        } else {
            findNavController().popBackStack(navIdFromArguments, false)
        }
    }

    private fun showError(t: Throwable) {
        hideLoading()
        etEmailLogin.error = null
        etPasswordLogin.error = null

        showMessage(t.message)
        when (t) {
            is EmailInvalidException -> {
                etEmailLogin.error = t.message
                etEmailLogin.requestFocus()
            }
            is PasswordInvalidException -> {
                etPasswordLogin.error = t.message
                etPasswordLogin.requestFocus()
            }
        }
    }

    private fun setUpView(view: View) {
        containerLogin = view.findViewById(R.id.containerLogin)
        tvSubTitleLogin = view.findViewById(R.id.tvSubTitleLogin)
        tvNewAccount = view.findViewById(R.id.tvNewAccount)
        tvResetPassword = view.findViewById(R.id.tvResetPassword)
        btLogin = view.findViewById(R.id.btLogin)
        etEmailLogin = view.findViewById(R.id.etEmailLogin)
        etPasswordLogin = view.findViewById(R.id.etPasswordLogin)

        btLogin.setOnClickListener {
            hideKeyboard()
            loginViewModel.signIn(
                etEmailLogin.text.toString(),
                etPasswordLogin.text.toString()
            )
        }

        tvResetPassword.setOnClickListener {
            loginViewModel.resetPassword(etEmailLogin.text.toString())
        }

        tvNewAccount.setOnClickListener {
            NavHostFragment.findNavController(this)
                .navigate(R.id.action_loginFragment_to_signUpFragment)
        }
    }

    private fun startLoginAnimation() {
        val anim = AnimationUtils.loadAnimation(context, R.anim.anim_form_login)
        containerLogin.startAnimation(anim)
        tvSubTitleLogin.startAnimation(anim)
        tvNewAccount.startAnimation(anim)
        tvResetPassword.startAnimation(anim)
    }
}
