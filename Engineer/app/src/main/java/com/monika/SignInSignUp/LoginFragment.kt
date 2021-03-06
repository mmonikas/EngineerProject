package com.monika.SignInSignUp

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.EditText
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.navigation.Navigation
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.FirebaseAuth
import com.monika.Enums.FirebaseRequestResult
import com.monika.MainActivity.MainActivity
import com.monika.R
import com.monika.Services.Utils
import kotlinx.android.synthetic.main.fragment_login.*


class LoginFragment : Fragment() {

    //Google Login Request Code
    private val presenter = LoginFragmentPresenter()
    private val RC_SIGN_IN = 7

    //Google Sign In Client
    private lateinit var mGoogleSignInClient: GoogleSignInClient

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()
        mGoogleSignInClient = GoogleSignIn.getClient(activity!!, gso)
        return inflater.inflate(R.layout.fragment_login, container, false)
    }

    override fun onStart() {
        super.onStart()
        if (FirebaseAuth.getInstance().currentUser != null) {
            Navigation.findNavController(view!!).navigate(R.id.homeFragment)
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setLayout()
        setLoginButtonListener()
        setSignInWithGoogleListener()
        setSignUpButtonListener()
        setTextInputLayoutsListeners()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            presenter.signInUserWithGoogle(data) {
                result ->
                if (result == FirebaseRequestResult.SUCCESS) {
                    Navigation.findNavController(view!!).navigate(R.id.homeFragment)
                }
                else if (result == FirebaseRequestResult.FAILURE) {
                    (activity as MainActivity).showToast(R.string.errorGoogleSignIn)
                }
            }
        }
    }

    private fun setLayout() {
        (activity as MainActivity).supportActionBar?.hide()
        val text = loginFragment_signInWithGoogle.getChildAt(0)
        if (text != null && text is TextView) {
            text.text = getString(R.string.registerWithGoogle)
        }
    }

    private fun setTextInputLayoutsListeners() {
        (loginFragment_loginEditText as EditText).setOnEditorActionListener {
                _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE || actionId == EditorInfo.IME_ACTION_SEARCH) {
                if (context != null && view != null) {
                    Utils.hideSoftKeyBoard(context!!, view!!)
                }
            }
            false
        }
    }

    private fun setLoginButtonListener() {
        loginFragment_nextButton.setOnClickListener {
            val username = loginFragment_loginEditText.text.toString()
            val password = loginFragment_passwordEditText.text.toString()
            (activity as MainActivity).showProgressView()
            presenter.signInUserWith(username, password, activity as MainActivity) {
                result ->
                if (result == FirebaseRequestResult.SUCCESS) {
//                    presenter.fetchUserExercises { workoutList ->
//                        val bundle = bundleOf("workouts" to workoutList)
//                        val navOptions = NavOptions.Builder()
//                            .setEnterAnim(R.anim.nav_default_enter_anim)
//                            .build()
                        Navigation.findNavController(view!!).navigate(R.id.homeFragment, null)

                }
                else if (result == FirebaseRequestResult.FAILURE) {
                    (activity as MainActivity).hideProgressView()
                    (activity as MainActivity).showToast(R.string.errorSignIn)
                }
            }
        }

    }

    private fun setSignUpButtonListener() {
        loginFragment_registerButton.setOnClickListener {
            Navigation.findNavController(it).navigate(R.id.registerFragment)
        }
    }

    private fun setSignInWithGoogleListener() {
        loginFragment_signInWithGoogle.setOnClickListener {
            val signInIntent = mGoogleSignInClient.signInIntent
            startActivityForResult(signInIntent, RC_SIGN_IN)
        }
    }
}