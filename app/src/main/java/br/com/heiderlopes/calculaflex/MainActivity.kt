package br.com.heiderlopes.calculaflex

import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.view.WindowManager
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.crashlytics.android.Crashlytics
import com.crashlytics.android.core.CrashlyticsCore
import com.orhanobut.hawk.Hawk
import io.fabric.sdk.android.Fabric

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        fullScreen()

        setContentView(R.layout.activity_main)

        val teste = HashMap<String, List<Any>>()

        teste["teste"] = listOf<String>("1", "2", "3")
        teste["teste2"] = listOf<Int>(1, 2, 3)

        Hawk.put("teste", teste)

        val data = HashMap<String, List<Any>>()
        val retorno = Hawk.get("teste", data)

        Log.i("TAG", "Teste")

        val crashlyticsCore = CrashlyticsCore.Builder()
            .disabled(BuildConfig.DEBUG)
            .build()
        Fabric.with(this, Crashlytics.Builder().core(crashlyticsCore).build())

        val crashButton = Button(this)
        crashButton.setText("Crash!")
        crashButton.setOnClickListener {
            Crashlytics.getInstance().crash() // Force a crash
        }
        addContentView(
            crashButton,
            ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
        )
    }

    private fun fullScreen() {
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        supportActionBar?.hide()
        this.window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        );
    }
}
