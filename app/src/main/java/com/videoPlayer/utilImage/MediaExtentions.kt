package com.videoPlayer.utilImage

import android.content.ContentUris
import android.content.Context
import android.database.Cursor
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Environment
import android.provider.DocumentsContract
import android.provider.MediaStore
import android.text.TextUtils
import com.videoPlayer.constant.IntentConstant
import java.io.*
import kotlin.math.ceil
import kotlin.math.ln
import kotlin.math.pow

fun compressImageFile(context: Context?, pathUri: Uri): File {
    var b: Bitmap? = null
    val realPath: String? = getRealPathFromURI(context!!, pathUri)
    if (realPath != null && realPath != "") {
        val f = File(realPath)
        val o: BitmapFactory.Options = BitmapFactory.Options()
        o.inJustDecodeBounds = true

        var fis: FileInputStream
        try {
            fis = FileInputStream(f)
            BitmapFactory.decodeStream(fis, null, o)
            fis.close()
        } catch (e: FileNotFoundException) {
            e.printStackTrace()
        } catch (e: IOException) {
            e.printStackTrace()
        }

        var scale = 1
        if (o.outHeight > IntentConstant.IMAGE_MAX_SIZE || o.outWidth > IntentConstant.IMAGE_MAX_SIZE) {
            scale = 2.0.pow(
                ceil(
                    ln(
                        IntentConstant.IMAGE_MAX_SIZE / o.outHeight.coerceAtLeast(o.outWidth)
                            .toDouble()
                    ) / ln(0.5)
                )
            ).toInt()
        }

        //Decode with inSampleSize
        val o2: BitmapFactory.Options = BitmapFactory.Options()
        o2.inSampleSize = scale
        try {
            fis = FileInputStream(f)
            b = BitmapFactory.decodeStream(fis, null, o2)
            fis.close()
        } catch (e: FileNotFoundException) {
            e.printStackTrace()
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    val destFile = File(getImageFilePath())
    try {
        val out = FileOutputStream(destFile)
        b?.compress(Bitmap.CompressFormat.PNG, 90, out)
        out.flush()
        out.close()

    } catch (e: Exception) {
        e.printStackTrace()
    }
    return destFile
}

fun getImageFilePath(): String {
    val file =
        Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES + "/VideoPlayer")
    if (!file.exists()) {
        file.mkdirs()
    }
    return file.absolutePath + "/IMG_" + System.currentTimeMillis() + ".jpg"
}

fun getRealPathFromURI(context: Context, uri: Uri?): String? {
    var contentUri = uri
    var cursor: Cursor?
    var filePath: String? = ""
    when (contentUri) {
        null -> return filePath

        else -> {
            val file = File(contentUri.path!!)
            when {
                file.exists() -> filePath = file.path
            }

            when {
                !TextUtils.isEmpty(filePath) -> return filePath
                else -> {
                    val proj = arrayOf(MediaStore.Images.Media.DATA)
                    try {
                        val wholeID = DocumentsContract.getDocumentId(contentUri)
                        val id: String = when {
                            wholeID.contains(":") -> wholeID.split(":".toRegex())
                                .dropLastWhile { it.isEmpty() }
                                .toTypedArray()[1]
                            else -> wholeID
                        }

                        cursor = context.contentResolver.query(
                            MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                            proj,
                            MediaStore.Images.Media._ID + "='" + id + "'",
                            null,
                            null
                        )

                        when {
                            cursor != null -> {
                                val columnIndex = cursor.getColumnIndex(proj[0])
                                when {
                                    cursor.moveToFirst() -> filePath = cursor.getString(columnIndex)
                                }

                                when {
                                    !TextUtils.isEmpty(filePath) -> contentUri = Uri.parse(filePath)
                                }
                            }
                        }
                    } catch (e: IllegalArgumentException) {
                        e.printStackTrace()
                    }
                    when {
                        !TextUtils.isEmpty(filePath) -> return filePath
                        else -> {
                            try {
                                cursor = context.contentResolver.query(
                                    contentUri!!,
                                    proj,
                                    null,
                                    null,
                                    null
                                )

                                when {
                                    cursor == null -> return contentUri.path
                                    cursor.moveToFirst() -> filePath =
                                        cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA))
                                }

                                when {
                                    !cursor!!.isClosed -> cursor.close()
                                }
                            } catch (e: Exception) {
                                e.printStackTrace()
                                filePath = contentUri!!.path
                            }

                            when (filePath) {
                                null -> filePath = ""
                            }
                            return filePath
                        }
                    }
                }
            }
        }
    }
}

fun getRealPath(context: Context, uri: Uri): String? {
    if (DocumentsContract.isDocumentUri(context, uri)) {
        if (isExternalStorageDocument(uri)) {
            val docId = DocumentsContract.getDocumentId(uri)
            val split = docId.split(":".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
            val type = split[0]

            if ("primary".equals(type, ignoreCase = true)) {
                return Environment.getExternalStorageDirectory().toString() + "/" + split[1]
            } else {
                val external = context.externalMediaDirs
                if (external.size > 1) {
                    var filePath = external[1].absolutePath
                    filePath = filePath.substring(0, filePath.indexOf("Android")) + split[1]
                    return filePath
                }
                return null
            }
        } else if (isDownloadsDocument(uri)) {
            var cursor: Cursor? = null
            try {
                cursor = context.contentResolver.query(
                    uri,
                    arrayOf(MediaStore.MediaColumns.DISPLAY_NAME),
                    null,
                    null,
                    null
                )
                if (cursor != null && cursor.moveToFirst()) {
                    val fileName = cursor.getString(0)
                    val path = Environment.getExternalStorageDirectory()
                        .toString() + "/Download/" + fileName
                    if (!TextUtils.isEmpty(path)) {
                        return path
                    }
                }
            } finally {
                cursor?.close()
            }
            val id = DocumentsContract.getDocumentId(uri)
            try {
                val contentUri = ContentUris.withAppendedId(
                    Uri.parse("content://downloads/public_downloads"),
                    id.toLong()
                )
                return getDataColumn(context, contentUri, null, null)
            } catch (e: NumberFormatException) {
                //In Android 8 and Android P the id is not a number
                e.printStackTrace()
                return if (uri.path != null) {
                    uri.path!!.replaceFirst("^/document/raw:", "").replaceFirst("^raw:", "")
                } else {
                    null
                }
            }
        } else if (isMediaDocument(uri)) {
            val docId = DocumentsContract.getDocumentId(uri)
            val split = docId.split(":".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
            val type = split[0]

            var contentUri: Uri? = null
            when (type) {
                "image" -> contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI
                "video" -> contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI
                "audio" -> contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
            }
            val selection = "_id=?"
            val selectionArgs = arrayOf(split[1])

            return getDataColumn(context, contentUri, selection, selectionArgs)
        }
    } else if ("content".equals(uri.scheme!!, ignoreCase = true)) {
        return if (isGooglePhotosUri(uri)) uri.lastPathSegment else getDataColumn(
            context,
            uri,
            null,
            null
        )
    } else if ("file".equals(uri.scheme!!, ignoreCase = true)) {
        return uri.path
    }
    return null
}

fun isExternalStorageDocument(uri: Uri): Boolean {
    return "com.android.externalstorage.documents" == uri.authority
}

fun isDownloadsDocument(uri: Uri): Boolean {
    return "com.android.providers.downloads.documents" == uri.authority
}

fun getDataColumn(
    context: Context, uri: Uri?, selection: String?,
    selectionArgs: Array<String>?
): String? {

    var cursor: Cursor? = null
    val column = "_data"
    val projection = arrayOf(column)

    try {
        cursor =
            context.contentResolver.query(uri!!, projection, selection, selectionArgs, null)
        if (cursor != null && cursor.moveToFirst()) {
            val index = cursor.getColumnIndexOrThrow(column)
            return cursor.getString(index)
        }
    } finally {
        cursor?.close()
    }
    return null
}

fun isGooglePhotosUri(uri: Uri): Boolean {
    return "com.google.android.apps.photos.content" == uri.authority
}

fun isMediaDocument(uri: Uri): Boolean {
    return "com.android.providers.media.documents" == uri.authority
}