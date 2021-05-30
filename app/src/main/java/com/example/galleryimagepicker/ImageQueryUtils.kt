package com.example.galleryimagepicker

import android.annotation.SuppressLint
import android.content.Context
import android.database.Cursor
import android.net.Uri
import android.provider.MediaStore

@SuppressLint("InlinedApi")
class ImageQueryUtils(private val context: Context) {

    fun loadFolderNameList(): ArrayList<AlbumItem> {
        val result = arrayListOf<AlbumItem>()
        val albumCursor = context.contentResolver.query(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                arrayOf(MediaStore.Images.Media.BUCKET_DISPLAY_NAME, MediaStore.Images.ImageColumns.BUCKET_ID),
                null,
                null,
                MediaStore.Images.Media.DATE_TAKEN + " DESC"
        )

        try {
            result.add(AlbumItem.getAllAlbumItem())
            if (albumCursor == null) return result

            albumCursor.doWhile {
                val bucketId = albumCursor.getString(albumCursor.getColumnIndex(MediaStore.Images.ImageColumns.BUCKET_ID))
                val name = albumCursor.getString(albumCursor.getColumnIndex(MediaStore.Images.Media.BUCKET_DISPLAY_NAME)) ?: bucketId
                val albumItem = AlbumItem(name, false, bucketId)
                if (!result.contains(albumItem)) {
                    result.add(albumItem)
                }
            }
        } finally {
            if (albumCursor != null && !albumCursor.isClosed) {
                albumCursor.close()
            }
        }

        return result
    }

    fun loadAlbumImages(
            albumItem: AlbumItem
    ): ArrayList<GalleryImageItem> {
        val result = arrayListOf<GalleryImageItem>()
        var photoCursor: Cursor? = null

        try {
            if (albumItem.needQueryAllAlbum) {
                photoCursor = context.contentResolver.query(
                        MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                        arrayOf(
                                MediaStore.Images.Media._ID,
                                MediaStore.Images.Media.DATA
                        ),
                        null,
                        null,
                        MediaStore.Images.Media.DATE_TAKEN + " DESC"
                )
            } else {
                photoCursor = context.contentResolver.query(
                        MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                        arrayOf(
                                MediaStore.Images.Media._ID,
                                MediaStore.Images.Media.DATA // Use only when you need absolute path of image, ex.1
                        ),
                        "${MediaStore.Images.ImageColumns.BUCKET_ID} =?",
                        arrayOf(albumItem.bucketId),
                        MediaStore.Images.Media.DATE_TAKEN + " DESC"
                )
            }
            photoCursor?.isAfterLast ?: return result
            photoCursor.doWhile {
                val columnIndexId = photoCursor.getColumnIndexOrThrow(MediaStore.Images.Media._ID)
                val imageId = photoCursor.getString(columnIndexId)
                val path = Uri.withAppendedPath(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "" + imageId).toString()
                result.add(GalleryImageItem(path))
                // ex.1: get image absolute path (less secure)
                // val path = photoCursor.getString((photoCursor.getColumnIndex(MediaStore.Images.Media.DATA)))
                // result.add(GalleryImageItem(path))
            }
        } finally {
            if (photoCursor != null && !photoCursor.isClosed) {
                photoCursor.close()
            }
        }

        return result
    }
}