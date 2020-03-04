package zero.network.reto2.utils

import android.content.Context
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import zero.network.reto2.R

/**
 * Assign a image from an url to an image holder
 */
fun Context.loadImage(url: String, imageHolder: ImageView){
    val requestOptions = RequestOptions()
        .placeholder(R.drawable.ic_launcher_background)
        .error(R.drawable.ic_launcher_background)
    Glide.with(this)
        .applyDefaultRequestOptions(requestOptions)
        .load(url)
        .into(imageHolder)
}