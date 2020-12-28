package com.myapp.foodapp.fragment

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.RelativeLayout
import android.widget.Toast
import androidx.core.app.ActivityCompat
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.myapp.foodapp.R
import com.myapp.foodapp.util.ConnectionManager
import org.json.JSONException
import org.json.JSONObject

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [ForgotPasswordInputFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class ForgotPasswordInputFragment(val contextParam: Context) : Fragment() {

    lateinit var etMobileNumber: EditText
    lateinit var etEmail: EditText
    lateinit var btnNext: Button
    lateinit var forgotPasswordInputLayout: RelativeLayout

    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_forgot_password_input, container, false)

        etMobileNumber = view.findViewById(R.id.etMobileNumber)
        etEmail = view.findViewById(R.id.etEmail)
        btnNext = view.findViewById(R.id.btnNext)
        forgotPasswordInputLayout = view.findViewById(R.id.forgotPasswordInputLayout)

        btnNext.setOnClickListener{

            if (etMobileNumber.text.isBlank() || etMobileNumber.text.length != 10) {
                etMobileNumber.error = "Invalid Mobile Number"
            } else {
                if (etEmail.text.isBlank()) {
                    etEmail.error = "Email Missing"
                } else {
                    if (ConnectionManager().checkConnectivity(activity as Context)) {
                        try {

                            val loginUser = JSONObject()
                            loginUser.put("mobile_number", etMobileNumber.text)
                            loginUser.put("email", etEmail.text)

                            println(loginUser.getString("mobile_number"))
                            println(loginUser.getString("email"))

                            val queue = Volley.newRequestQueue(activity as Context)
                            val url = "http://" + getString(R.string.ip_address) + "/v2/forgot_password/fetch_result"

                            forgotPasswordInputLayout.visibility = View.VISIBLE

                            val jsonObjectRequest = object : JsonObjectRequest(
                                Method.POST,
                                url,
                                loginUser,
                                Response.Listener {

                                    val response = it.getJSONObject("data")
                                    val success = response.getBoolean("success")
                                    if (success) {

                                        val firstTry = response.getBoolean("first_try")

                                        if (firstTry) {
                                            Toast.makeText(
                                                contextParam,
                                                "OTP sent",
                                                Toast.LENGTH_SHORT
                                            ).show()

                                            ChangePasswordFragment()

                                        } else {
                                            Toast.makeText(
                                                contextParam,
                                                "OTP sent already",
                                                Toast.LENGTH_SHORT
                                            ).show()

                                            ChangePasswordFragment()
                                        }

                                    } else {
                                        val responseMessageServer =
                                            response.getString("errorMessage")
                                        Toast.makeText(
                                            contextParam,
                                            responseMessageServer.toString(),
                                            Toast.LENGTH_SHORT
                                        ).show()
                                    }
                                    forgotPasswordInputLayout.visibility = View.INVISIBLE
                                },
                                Response.ErrorListener {

                                    Toast.makeText(
                                        contextParam,
                                        "Some Error occurred!!!",
                                        Toast.LENGTH_SHORT
                                    ).show()

                                    forgotPasswordInputLayout.visibility = View.INVISIBLE
                                }) {
                                override fun getHeaders(): MutableMap<String, String> {
                                    val headers = HashMap<String, String>()
                                    headers["Content-type"] = "application/json"
                                    headers["token"] = getString(R.string.token)
                                    return headers
                                }
                            }
                            queue.add(jsonObjectRequest)

                        } catch (e: JSONException) {
                            Toast.makeText(
                                contextParam,
                                "Some unexpected error occurred!!!",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    } else {
                        val alterDialog = androidx.appcompat.app.AlertDialog.Builder(activity as Context)
                        alterDialog.setTitle("No Internet")
                        alterDialog.setMessage("Check Internet Connection!")
                        alterDialog.setPositiveButton("Open Settings") { _, _ ->
                            val settingsIntent = Intent(Settings.ACTION_SETTINGS)
                            startActivity(settingsIntent)
                        }
                        alterDialog.setNegativeButton("Exit") { _, _ ->
                            ActivityCompat.finishAffinity(activity as Activity)
                        }
                        alterDialog.create()
                        alterDialog.show()
                    }
                }
            }
        }

        return view
    }

    fun ChangePasswordFragment() {
        val transaction = fragmentManager?.beginTransaction()
        transaction?.replace(
            R.id.frameLayout,
            ForgotPasswordFragment(contextParam, etMobileNumber.text.toString())
        )
        transaction?.commit()
    }

    override fun onResume() {

        if (!ConnectionManager().checkConnectivity(activity as Context)) {

            val alterDialog = androidx.appcompat.app.AlertDialog.Builder(activity as Context)
            alterDialog.setTitle("No Internet")
            alterDialog.setMessage("Check Internet Connection!")
            alterDialog.setPositiveButton("Open Settings") { _, _ ->
                val settingsIntent = Intent(Settings.ACTION_SETTINGS)
                startActivity(settingsIntent)
            }
            alterDialog.setNegativeButton("Exit") { _, _ ->
                ActivityCompat.finishAffinity(activity as Activity)
            }
            alterDialog.setCancelable(false)
            alterDialog.create()
            alterDialog.show()
        }

        super.onResume()
    }

   /* companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment ForgotPasswordInputFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            ForgotPasswordInputFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }*/
}