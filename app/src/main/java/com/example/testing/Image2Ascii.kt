package com.example.testing
/*
        This is the class which manages the conversion of image to text
        It will read image from the ImageView uploaded and converts it to display it
        in a TextView and hides the Image
 */
import android.app.Activity
import android.content.Context
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.Matrix
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.graphics.drawable.toBitmap
import kotlin.math.max

// !!! Important : We have to inherit context from Mainactivity
// Otherwise we couldn't use findViewbyId, so, take it from Main and
// then use it as activity, to get views
class Image2Ascii(context: Context) : AppCompatActivity() {
    var act: Activity = context as Activity  // using passed context as activity
    fun img2ascii() {
        Log.d("", "Working here")
        val img: ImageView = act.findViewById(R.id.img1)
        var bitmap: Bitmap = img.drawable.toBitmap() // Converting image to Bitmap to use
        val orig_height: Int = bitmap.height
        val orig_width: Int = bitmap.width
        // get dimensions to resize
        var nh = orig_height
        var nw = orig_width
        val max_dim = max(orig_height, orig_width)
        val factor: Double = max_dim / 200.0
        nh = (orig_height / factor).toInt()
        nw = (orig_width / factor).toInt()
        Log.d("0", "final height are " + nh + ", " + nw)
        bitmap = getResizedBitmap(bitmap, nh, nw) // resize to new dimensions
        var str = ""
        for (i in 0 until nh) {
            for (j in 0 until nw) {
                try {
                    // for each pixel, add the required character
                    val color = bitmap.getPixel(j, i)
                    val red = Color.red(color)
                    val green = Color.green(color)
                    val blue = Color.blue(color)
                    val pixval = red * 0.3 + blue * 0.59 + green * 0.11
                    str = str + getval(pixval)
                } catch (e: Exception) {
                    Log.d(1.toString(), "hdf")
                    break
                }
            }
            str = str + "\n"
        }
        val textView: TextView = act.findViewById(R.id.txt)
        textView.text = str
        textView.visibility=View.VISIBLE
        img.visibility = View.GONE

    }

    fun getResizedBitmap(bm: Bitmap, newHeight: Int, newWidth: Int): Bitmap {
        val width = bm.width
        val height = bm.height
        val scaleWidth = newWidth.toFloat() / width
        val scaleHeight = newHeight.toFloat() / height
        val matrix = Matrix()
        // RESIZE THE BIT MAP
        matrix.postScale(scaleWidth, scaleHeight)
        // RECREATE THE NEW BITMAP
        val nbitmap = Bitmap.createBitmap(
            bm, 0, 0, width, height,
            matrix, false
        )
        return nbitmap
    }

    fun getval(g: Double): String {
        val str: String
        str = if (g >= 240) {
            " "
        } else if (g >= 210) {
            "."
        } else if (g >= 190) {
            "*"
        } else if (g >= 170) {
            "+"
        } else if (g >= 120) {
            "^"
        } else if (g >= 110) {
            "&"
        } else if (g >= 80) {
            "8"
        } else if (g >= 60) {
            "#"
        } else {
            "@"
        }
        return str
    }


}