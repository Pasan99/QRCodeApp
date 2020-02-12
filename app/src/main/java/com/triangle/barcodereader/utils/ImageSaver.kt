package com.triangle.barcodereader.utils

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Environment
import android.util.Log
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream

class ImageSaver(
    private var context : Context
) {

    companion object {
        fun isExternalStorageWritable() : Boolean{
            val state = Environment.getExternalStorageState()
            return Environment.MEDIA_MOUNTED == state
        }

        fun isExternalStorageReadable() : Boolean{
            val state = Environment.getExternalStorageState()
            return Environment.MEDIA_MOUNTED == state || Environment.MEDIA_MOUNTED_READ_ONLY == state
        }
    }

    private lateinit var fileName : String
    private lateinit var directoryName : String

    fun setFileName(name : String){
        this.fileName = name
    }

    fun setDirectoryName(name : String){
        this.directoryName = name
    }

    fun save(bitmap : Bitmap) : Boolean{
        var fileOutputStream : FileOutputStream? = null
        try{
            fileOutputStream = FileOutputStream(createFile())
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, fileOutputStream)
            return true
        }
        catch (i : Exception){
            Log.e("Image Saver", i.message.toString())
        }
        finally {
            try {
                fileOutputStream?.close()
            }catch (i : Exception){
                Log.e("Image Saver", i.message.toString())
            }
        }
        return false
    }

    private fun createFile() : File{
        val directory : File = getAlbumStorageDir(directoryName)

        if (!directory.exists() && !directory.mkdir()){
            directory.mkdir()
            val file = File(directory, fileName)
            FileOutputStream(file)
            Log.e("Image Saver", "Error Creating Directory")
        }
        return File(directory, fileName)
    }

    private fun getAlbumStorageDir(albumName : String) : File{
        return File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM).toString() + File.separator + albumName)
    }

    fun getAvailableFileNames() : ArrayList<String>{
        val names = arrayListOf<String>()
        val files = File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES).toString() + File.separator + directoryName).listFiles()
        if (files != null) {
            for (i in files) {
                names.add(i.name)
            }
        }
        return names
    }

    fun load() : Bitmap? {
        var fileInputStream : FileInputStream? = null
        try{
            fileInputStream = FileInputStream(createFile())
            return BitmapFactory.decodeStream(fileInputStream)
        }catch (i : Exception){
            Log.e("Image Saver", i.message.toString())
        }
        finally {
            try {
                fileInputStream?.close()
            }catch (i : Exception){
                Log.e("Image Saver", i.message.toString())
            }
        }
        return null
    }

}