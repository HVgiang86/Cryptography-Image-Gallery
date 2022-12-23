package com.vcs.svtt.cryptographygallery.model

data class Image(val fileName: String, val rawFilePath: String,val isEncrypted:Boolean,val encryptedFilePath: String)