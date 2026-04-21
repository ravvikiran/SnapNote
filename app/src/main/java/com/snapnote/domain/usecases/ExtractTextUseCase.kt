package com.snapnote.domain.usecases

import android.content.Context
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import android.util.Log
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.latin.TextRecognizerOptions
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

class ExtractTextUseCase(private val context: Context) {

    suspend fun execute(imageUri: Uri): String = withContext(Dispatchers.IO) {
        var bitmap: Bitmap? = null
        var inputImage: InputImage? = null
        
        return@withContext try {
            bitmap = loadBitmap(imageUri)
            if (bitmap != null) {
                // Create InputImage and TextRecognizer only after bitmap is loaded to avoid resource leaks
                inputImage = InputImage.fromBitmap(bitmap, 0)
                val recognizer = TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS)
                
                try {
                    val result = recognizer.process(inputImage).await()
                    result.text
                } finally {
                    // Properly close the recognizer after use
                    recognizer.close()
                }
            } else {
                ""
            }
        } catch (e: Exception) {
            Log.e("ExtractTextUseCase", "Error extracting text: ${e.javaClass.simpleName}: ${e.message}")
            ""
        } finally {
            // Clean up bitmap - InputImage doesn't hold a reference, so no need to close it separately
            bitmap?.recycle()
        }
    }

    private fun loadBitmap(uri: Uri): Bitmap? {
        return try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                val source = ImageDecoder.createSource(context.contentResolver, uri)
                ImageDecoder.decodeBitmap(source) { decoder, _, _ ->
                    decoder.allocator = ImageDecoder.ALLOCATOR_SOFTWARE
                }
            } else {
                @Suppress("DEPRECATION")
                MediaStore.Images.Media.getBitmap(context.contentResolver, uri)
            }
        } catch (e: Exception) {
            Log.e("ExtractTextUseCase", "Error loading bitmap: ${e.javaClass.simpleName}")
            null
        }
    }
}
