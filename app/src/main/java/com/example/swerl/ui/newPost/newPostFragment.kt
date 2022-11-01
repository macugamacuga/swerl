package com.example.swerl.ui.newPost
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import androidx.camera.core.*
import com.example.swerl.R
import androidx.camera.view.PreviewView
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.video.Recorder
import androidx.camera.video.Recording
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ViewModelProviders
import com.example.swerl.Constants
import com.example.swerl.Constants.TAG
import com.example.swerl.ui.home.HomeViewModel
import com.google.common.util.concurrent.ListenableFuture
import kotlinx.android.synthetic.main.activity_login.*
import kotlinx.android.synthetic.main.fragment_new_post.*
import java.io.File
import java.text.SimpleDateFormat
import java.util.*


class newPostFragment : Fragment() {

    private lateinit var viewBinding: NewPostViewModel
    private lateinit var cameraProviderFuture : ListenableFuture<ProcessCameraProvider>
    private var imageCapture: ImageCapture? = null
private  lateinit var outputDirectory:File
    private var videoCapture: VideoCapture? = null
    private var recording: Recording? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        val root =  inflater.inflate(R.layout.fragment_new_post, container, false)
        val viewFinder=root.findViewById<PreviewView>(R.id.viewFinder)
val imageCaptureButton=root.findViewById<Button>(R.id.image_capture_button)

        outputDirectory=getOutputDirectory()
        if(allPermissionGranted()){
            Toast.makeText(context,"granted",Toast.LENGTH_SHORT).show()
            startCamera()
        }
        else{
            ActivityCompat.requestPermissions(
                requireActivity(),Constants.REQUIRED_PERMISSIONS,
            Constants.REQUEST_CODE_PERMISSIONS)
        }
imageCaptureButton.setOnClickListener() {
    takePhoto()
}
        return root
    }

        private  fun getOutputDirectory(): File {
var mediadir= ContextCompat.getExternalFilesDirs(requireContext(),Environment.DIRECTORY_PICTURES).firstOrNull()?.let{
    mFile->
    File(mFile,resources.getString(R.string.app_name)).apply {
        mkdir()
    }
}
            return if (mediadir!=null && mediadir.exists())
                mediadir else requireContext().filesDir
        }

    private fun takePhoto(){
        val imageCapture=imageCapture ?: return
            val photoFile=File(
                outputDirectory,
                SimpleDateFormat(Constants.FILE_NAME_FORMAT,
                Locale.getDefault()).format(System.
                                    currentTimeMillis())+".png"
            )
        val outputOptions=ImageCapture
            .OutputFileOptions
            .Builder(photoFile)
            .build()

        imageCapture.takePicture(
            outputOptions,ContextCompat.getMainExecutor(requireContext()),
            object :ImageCapture.OnImageSavedCallback{
                override fun onImageSaved(outputFileResults: ImageCapture.OutputFileResults) {
                    val savedUri=Uri.fromFile(photoFile)
                    val msg="photosaved"
                    Toast.makeText(context,
                        "$msg $savedUri",
                   Toast.LENGTH_SHORT ).show()
                }

                override fun onError(exception: ImageCaptureException) {
                    Log.e(TAG,"onError:${exception.message}", exception)
                }

            }

        )
    }


  private fun startCamera(){
val cameraProviderFuture=ProcessCameraProvider
    .getInstance(requireContext())
cameraProviderFuture.addListener({
    val cameraProvider:ProcessCameraProvider=cameraProviderFuture.get()
    val preview=Preview.Builder().build()
        .also {
            mPreview->

            mPreview.setSurfaceProvider(
                viewFinder.surfaceProvider
            )
        }
    imageCapture=ImageCapture.Builder().build()
    val cameraSelector=CameraSelector.DEFAULT_BACK_CAMERA

    try {
        cameraProvider.unbindAll()
        cameraProvider.bindToLifecycle(
            this,cameraSelector,preview,imageCapture)

    }catch (e:Exception){
Log.d(TAG,"startCamera Fail:",e)
    }

},ActivityCompat.getMainExecutor(requireContext()))
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray) {
        if(requestCode==Constants.REQUEST_CODE_PERMISSIONS){
            if(allPermissionGranted()){
                startCamera()
            }else{
                Toast.makeText(context,"Permissions not granted",Toast.LENGTH_SHORT).show()
                activity?.finish()
            }
        }
    }

    private fun allPermissionGranted() =
        Constants.REQUIRED_PERMISSIONS.all {
            ActivityCompat.checkSelfPermission(
                requireContext(),it
            )==PackageManager.PERMISSION_GRANTED
        }




}