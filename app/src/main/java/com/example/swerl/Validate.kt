package com.example.swerl

import android.content.Context
import android.content.pm.PackageManager

class Validate {


     fun checkCameraHardware(context: Context): Boolean {
        return context.packageManager.hasSystemFeature(PackageManager.FEATURE_CAMERA)
    }
    }

