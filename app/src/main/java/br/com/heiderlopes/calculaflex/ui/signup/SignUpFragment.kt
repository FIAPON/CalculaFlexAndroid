package br.com.heiderlopes.calculaflex.ui.signup

import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.NavHostFragment
import br.com.concrete.canarinho.watcher.TelefoneTextWatcher
import br.com.concrete.canarinho.watcher.evento.EventoDeValidacao

import br.com.heiderlopes.calculaflex.R
import br.com.heiderlopes.calculaflex.extensions.hideKeyboard
import br.com.heiderlopes.calculaflex.models.NewUser
import br.com.heiderlopes.calculaflex.models.RequestState
import br.com.heiderlopes.calculaflex.ui.base.BaseFragment
import com.airbnb.lottie.LottieAnimationView

class SignUpFragment : BaseFragment() {

    override val layout = R.layout.fragment_sign_up
    override val screeName = "SignUp"

    private val signUpViewModel: SignUpViewModel by viewModels()

    private lateinit var etUserNameSignUp: EditText
    private lateinit var etEmailSignUp: EditText
    private lateinit var etPhoneSignUp: EditText
    private lateinit var etPasswordSignUp: EditText
    private lateinit var cbTermsSignUp: LottieAnimationView
    private lateinit var tvTerms: TextView
    private lateinit var btCreateAccount: Button
    private lateinit var btLoginSignUp: TextView

    private var checkBoxDone = false

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setUpView(view)

        registerObserver()
    }

    private fun setUpView(view: View) {

        etUserNameSignUp = view.findViewById(R.id.etUserNameSignUp)
        etEmailSignUp = view.findViewById(R.id.etEmailSignUp)
        etPhoneSignUp = view.findViewById(R.id.etPhoneSignUp)
        etPasswordSignUp = view.findViewById(R.id.etPasswordSignUp)
        cbTermsSignUp = view.findViewById(R.id.cbTermsSignUp)
        tvTerms = view.findViewById(R.id.tvTerms)
        btCreateAccount = view.findViewById(R.id.btCreateAccount)
        btLoginSignUp = view.findViewById(R.id.btLoginSignUp)
        setUpListener()
    }

    private fun setUpListener() {

        etPhoneSignUp.addTextChangedListener(TelefoneTextWatcher(object : EventoDeValidacao {
            override fun totalmenteValido(valorAtual: String?) {

            }

            override fun invalido(valorAtual: String?, mensagem: String?) {

            }

            override fun parcialmenteValido(valorAtual: String?) {

            }
        }))

        tvTerms.setOnClickListener {
            NavHostFragment.findNavController(this)
                .navigate(R.id.action_signUpFragment_to_termsFragment)
        }

        btCreateAccount.setOnClickListener {
            hideKeyboard()
            if(checkBoxDone) {
                val newUser = NewUser(
                    etUserNameSignUp.text.toString(),
                    etEmailSignUp.text.toString(),
                    etPhoneSignUp.text.toString(),
                    etPasswordSignUp.text.toString()
                )
                signUpViewModel.signUp(newUser)
            } else {
                showMessage("Você precisa aceitar os termos de uso.")
            }
        }

        setUpCheckboxListener()
    }

    private fun setUpCheckboxListener() {
        cbTermsSignUp.setOnClickListener {
            if (checkBoxDone) {
                cbTermsSignUp.speed = -1f
                cbTermsSignUp.playAnimation()
                checkBoxDone = false
            } else {
                cbTermsSignUp.speed = 1f
                cbTermsSignUp.playAnimation()
                checkBoxDone = true
            }
        }
    }

    private fun registerObserver() {
        this.signUpViewModel.signUpState.observe(viewLifecycleOwner, Observer {
            when (it) {
                is RequestState.Success -> {
                    hideLoading()
                    NavHostFragment.findNavController(this)
                        .navigate(R.id.main_nav_graph)
                }
                is RequestState.Error -> {
                    hideLoading()
                    showMessage(it.throwable.message)
                }
                is RequestState.Loading -> showLoading("Realizando a autenticação")
            }
        })
    }
}
