package com.sunnyweather.android.ui.place

import android.Manifest
import android.content.ContentUris
import android.content.Context
import android.content.Intent
import android.database.Cursor
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import com.sunnyweather.android.MainActivity
import com.sunnyweather.android.R
import com.sunnyweather.android.ui.weather.WeatherActivity
import kotlinx.android.synthetic.main.fragment_place.*
import android.app.Activity.RESULT_CANCELED
import android.content.pm.PackageManager
import android.os.Build
import android.os.Environment
import android.provider.DocumentsContract
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import java.io.File
import android.annotation.TargetApi as TargetApi1

class PlaceFragment : Fragment() {

    val viewModel by lazy { ViewModelProviders.of(this).get(PlaceViewModel::class.java) }

    private lateinit var adapter: PlaceAdapter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_place, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        if (activity is MainActivity && viewModel.isPlaceSaved()) {
            val place = viewModel.getSavedPlace()
            val intent = Intent(context, WeatherActivity::class.java).apply {
                putExtra("id", place.id)
                putExtra("place_name", place.name)
            }
            startActivity(intent)
            activity?.finish()
            return
        }
        val layoutManager = LinearLayoutManager(activity)
        recyclerView.layoutManager = layoutManager
        adapter = PlaceAdapter(this, viewModel.placeList)
        recyclerView.adapter = adapter
        searchPlaceEdit.addTextChangedListener { editable ->
            val content = editable.toString()
            if (content.isNotEmpty()) {
                viewModel.searchPlaces(content)
            } else {
                recyclerView.visibility = View.GONE
                bgImageView.visibility = View.VISIBLE
                viewModel.placeList.clear()
                adapter.notifyDataSetChanged()
            }
        }
        viewModel.placeLiveData.observe(this, Observer { result ->
            val places = result.getOrNull()
            if (places != null) {
                recyclerView.visibility = View.VISIBLE
                bgImageView.visibility = View.GONE
                viewModel.placeList.clear()
                viewModel.placeList.addAll(places)
                adapter.notifyDataSetChanged()
            } else {
                Toast.makeText(activity, "未能查询到任何地点", Toast.LENGTH_SHORT).show()
                result.exceptionOrNull()?.printStackTrace()
            }
        })
        navBtn2.setOnClickListener {
            applyOpenAlbumPermission()
        }
    }


    private val REQUEST_EXTERNAL_STORAGE = 1;
    private val PERMISSIONS_STORAGE = arrayOf(
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE)


    var realPathFromUri:String?=null//图片真实路径
    var photo: Bitmap?=null//图片的bitmap
    var imageUri: Uri? =null//图片的路径
    //下列三个是拍照和从相册获取的标志
    companion object {
        const val WRITE_EXTERNAL_STORAGE = 1
        const val OPEN_ALBUM = 3
//        const val CROP_IMAGE = 4
    }
    fun applyOpenAlbumPermission() {
        applyWritePermission(OPEN_ALBUM){
            val permission = ActivityCompat.checkSelfPermission(activity!!,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE);
            if (permission != PackageManager.PERMISSION_GRANTED) {
                // We don't have permission so prompt the user
                ActivityCompat.requestPermissions(activity!!, PERMISSIONS_STORAGE, REQUEST_EXTERNAL_STORAGE);
            }
            allPhoto()
        }


    }
    private fun allPhoto() {
        val intent = Intent()
        intent.action = Intent.ACTION_GET_CONTENT
        intent.type = "image/*"
        startActivityForResult(intent, OPEN_ALBUM)
    }
    override  fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
            OPEN_ALBUM -> {
                if (resultCode == RESULT_CANCELED) {
                    Toast.makeText(
                            context,
                            "点击取消从相册选择",
                            Toast.LENGTH_LONG
                    ).show()
                    return
                }
                try {
                    imageUri = data?.data
                    imagePath = RealPathFromUriUtils.getRealPathFromUri(context, data!!.data)
                    val permission = ActivityCompat.checkSelfPermission(activity!!,
                            Manifest.permission.WRITE_EXTERNAL_STORAGE);
                    if (permission != PackageManager.PERMISSION_GRANTED) {
                        // We don't have permission so prompt the user
                        ActivityCompat.requestPermissions(activity!!, PERMISSIONS_STORAGE, REQUEST_EXTERNAL_STORAGE);
                    }
                    val ind = getBitmapByAlbum(data)
                    bgImageView.setImageBitmap(ind)
//                    upImage(1)
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
            else -> {
            }
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int,
                                            permissions: Array<out String>,
                                            grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        //权限请求结果
        when (requestCode) {
            WRITE_EXTERNAL_STORAGE -> {
                permissionHint(grantResults, "没有读写权限") {}
            }
            OPEN_ALBUM -> {
                permissionHint(grantResults, "没有读写权限") {
                    allPhoto()
                }
            }
            else -> {
                Toast.makeText(context, "没有权限", Toast.LENGTH_SHORT).show()
            }
        }
    }

    /**
     * 权限结果处理lambda函数
     * @param grantResults 请求结果
     * @param msg toast内容
     * @param target 权限拿到要做什么
     */
    private fun permissionHint(grantResults: IntArray, msg: String, target: () -> Unit) {
        if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            target()
        } else {
            Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
        }
    }

    /**
     * 请求读写权限
     * @param requestCode 请求码
     * @param target 要做什么
     */
    private fun applyWritePermission(requestCode: Int, target: () -> Unit) {
        val permissions = listOf(Manifest.permission.WRITE_EXTERNAL_STORAGE)
        //android6.0之后，需要动态申请读写权限
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            //读写是否已经授权
            val check = ContextCompat.checkSelfPermission(context!!, permissions[0])
            if (check == PackageManager.PERMISSION_GRANTED) {
                target()
            } else {
                //如果未发现授权，则请求权限
                requestPermissions(arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE),
                        requestCode)
            }
        } else {
            target()
        }
    }

    private lateinit var photoUri: Uri
    private lateinit var imagePath: String
    //供裁剪使用
    private lateinit var oriUri: Uri
    fun getBitmapByAlbum(data: Intent): Bitmap {
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
//            handleImageAfterKitKat(data)
//        } else {
//            handleImageBeforeKitKat(data)
//        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            //7.0适配
            oriUri = FileProvider.getUriForFile(context!!, "com.wayeal.wateraffair.user.provider", File(imagePath))
        }
        val permission = ActivityCompat.checkSelfPermission(activity!!,
                Manifest.permission.WRITE_EXTERNAL_STORAGE);
        if (permission != PackageManager.PERMISSION_GRANTED) {
            // We don't have permission so prompt the user
            ActivityCompat.requestPermissions(activity!!, PERMISSIONS_STORAGE, REQUEST_EXTERNAL_STORAGE);
        }
        return MediaStore.Images.Media.getBitmap(context!!.contentResolver, oriUri)
    }
    @TargetApi1(Build.VERSION_CODES.KITKAT)
    private fun handleImageAfterKitKat(data: Intent) {
        val uri = data.data
        //document类型的Uri
        when {
            DocumentsContract.isDocumentUri(context, uri) -> {
                //通过documentId处理
                val docId = DocumentsContract.getDocumentId(uri)
                when (uri?.authority) {
                    "com.android.externalstorage.documents" -> {
                        val type = docId.split(":")[0]
                        if ("primary".equals(type, ignoreCase = true)) {
                            imagePath = Environment.getExternalStorageDirectory()
                                    .toString() + "/" + docId.split(":")[1]
                        }
                    }
                    //media类型解析
                    "com.android.providers.media.documents" -> {
                        val id = docId.split(":")[1]
                        val type = docId.split(":")[0]
                        val contentUri: Uri? = when (type) {
                            "image" -> MediaStore.Images.Media.EXTERNAL_CONTENT_URI
                            "video" -> MediaStore.Video.Media.EXTERNAL_CONTENT_URI
                            "audio" -> MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
                            else -> null
                        }
                        val selection = "_id=?"
                        val selectionArgs: Array<String> = arrayOf(id)
                        imagePath = getImagePath(contentUri!!, selection, selectionArgs)!!
                    }
                    //downloads文件解析
                    "com.android.providers.downloads.documents" -> {
                        ContentUris.withAppendedId(
                                Uri.parse("content://downloads/public_downloads"), docId.toLong()
                        ).apply {
                            imagePath = getImagePath(this, null, null)!!
                        }
                    }
                    else -> {
                    }
                }
            }
            "content".equals(uri?.scheme, ignoreCase = true) ->
                //content类型数据不需要解析，直接传入生成即可
                imagePath = getImagePath(uri!!, null, null)!!
            "file".equals(uri?.scheme, ignoreCase = true) ->
                //file类型的uri直接获取图片路径即可
                imagePath = uri!!.path!!
        }
    }
    private fun handleImageBeforeKitKat(data: Intent) {
        val uri = data.data
        imagePath = getImagePath(uri!!, null, null)!!
    }
    private fun getImagePath(uri: Uri, selection: String?, selectionArgs: Array<String>?): String? {
        var cursor: Cursor? = null
        try {
            cursor = context!!.contentResolver.query(uri, null, selection, selectionArgs, null)
            if (cursor?.moveToFirst()!!) {
                return cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA))
            }
        } finally {
            cursor?.close()
        }
        return null
    }

    open fun getOptions(path: String?): BitmapFactory.Options? {
        val options =
                BitmapFactory.Options()
        options.inJustDecodeBounds = true
        BitmapFactory.decodeFile(path, options)
        options.inPreferredConfig = Bitmap.Config.RGB_565
        options.inSampleSize = 4 //此项参数可以根据需求进行计算
        options.inJustDecodeBounds = false
        return options
    }
    //删除图片url
    open fun delteImageUri(context: Context, uri: Uri?) {
        context.contentResolver.delete(uri!!, null, null)
    }
}
