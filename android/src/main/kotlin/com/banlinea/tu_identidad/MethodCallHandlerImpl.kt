package com.banlinea.tu_identidad

import android.app.Activity.RESULT_OK
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.provider.MediaStore
import androidx.annotation.NonNull
import androidx.annotation.Nullable
import com.tuid.idval.Models.method
import com.tuid.idval.TuID.init
import com.tuid.idval.TuID.INEValidation
import com.tuid.idval.TuID.AUTHID_ACTIVITY_RESULT
import io.flutter.embedding.engine.plugins.activity.ActivityPluginBinding
import io.flutter.plugin.common.MethodCall
import io.flutter.plugin.common.MethodChannel
import io.flutter.plugin.common.PluginRegistry.ActivityResultListener
import java.io.IOException
import java.util.*


class MethodCallHandlerImpl: MethodChannel.MethodCallHandler , ActivityResultListener{

    private lateinit var mActivityPluginBinding : ActivityPluginBinding
    var mResult : MethodChannel.Result? = null

    fun setActivityPluginBinding(@Nullable activityPluginBinding: ActivityPluginBinding) {
        this.mActivityPluginBinding = activityPluginBinding
    }


    override fun onMethodCall(@NonNull call: MethodCall, @NonNull result: MethodChannel.Result) {
        when (call.method) {
            "init" -> handleInit(call, result)
            else -> {
                result.notImplemented()
            }
        }
    }

    private fun handleInit(call: MethodCall, result: MethodChannel.Result) {
        mResult = result
        mActivityPluginBinding.addActivityResultListener(this)


        var showTutorial = false
        if (call.hasArgument("showTutorial")) {
            showTutorial = call.argument("showTutorial")?:false
        }
        var showResults = false
        if (call.hasArgument("showResults")) {
            showResults = call.argument("showResults")?:false
        }
        var apiKey = ""
        if (call.hasArgument("apiKey")) {
            apiKey = call.argument("apiKey")?:""
        }

        var INEMethod = method.INE
        if (call.hasArgument("method")) {
            INEMethod = when(call.argument("method")?:""){
                "INE" -> method.INE
                "IDVAL" -> method.IDVAL
                "ONLYOCR" -> method.ONLYOCR
                else -> method.INE
            }
        }

        var INEValidationInfo = true
        if (call.hasArgument("INEValidationInfo")) {
            INEValidationInfo = call.argument("INEValidationInfo")?:true
        }

        var INEValidationQuality = true
        if (call.hasArgument("INEValidationQuality")) {
            INEValidationQuality = call.argument("INEValidationQuality")?:true
        }

        var INEValidationPatterns = true
        if (call.hasArgument("INEValidationPatterns")) {
            INEValidationPatterns = call.argument("INEValidationPatterns")?:true
        }

        var INEValidationCurp = true
        if (call.hasArgument("INEValidationCurp")) {
            INEValidationCurp = call.argument("INEValidationCurp")?:true
        }

        init(mActivityPluginBinding.activity, showTutorial, showResults, apiKey, INEMethod, INEValidation(INEValidationInfo, INEValidationQuality, INEValidationPatterns, INEValidationCurp))
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?): Boolean {
        if (requestCode == AUTHID_ACTIVITY_RESULT) {
            return if (resultCode == RESULT_OK) {
                val result: MutableMap<String, Any?> = HashMap()
                val extras = data!!.extras!!
                result["status"] = extras.getBoolean("status")
                result["response"] = extras.getString("response")
                result["error"] = extras.getString("error")
                result["inefPath"] = (extras.getParcelable("inefPath") as Uri).toString()
                result["inebPath"] = (extras.getParcelable("inebPath") as Uri).toString()

                mResult!!.success(result)
                true
            }else{
                mResult!!.success(null)
                false
            }
        }
        return false
    }
}
