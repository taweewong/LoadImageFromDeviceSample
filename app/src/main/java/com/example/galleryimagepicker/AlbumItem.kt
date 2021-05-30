package com.example.galleryimagepicker

data class AlbumItem(
        val name: String,
        val needQueryAllAlbum: Boolean,
        val bucketId: String
) {
    companion object {
        fun getAllAlbumItem(): AlbumItem = AlbumItem("All", true,"0")
    }
}
