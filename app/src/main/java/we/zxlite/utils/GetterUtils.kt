package we.zxlite.utils

import android.content.res.Resources
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Rect
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.text.Html
import android.widget.TextView
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import we.zxlite.GlideApp

object GetterUtils {

    class ImageGetter(val htmlView: TextView) :
        Html.ImageGetter {

        override fun getDrawable(source: String): Drawable {
            val urlDrawable = UrlDrawable()
            val target = BitmapTarget(urlDrawable)
            GlideApp.with(htmlView.context).asBitmap().load(source).into(target)
            return urlDrawable
        }

        private inner class BitmapTarget(private val urlDrawable: UrlDrawable) :
            CustomTarget<Bitmap>() {
            override fun onResourceReady(resource: Bitmap, transition: Transition<in Bitmap>?) {
                val drawable = BitmapDrawable(htmlView.context.resources, resource)
                htmlView.post {
                    val width: Int
                    val height: Int
                    if (drawable.intrinsicWidth * 2 > htmlView.width) {
                        width = htmlView.width
                        height = drawable.intrinsicHeight * htmlView.width / drawable.intrinsicWidth
                    } else {
                        width = drawable.intrinsicWidth * 2
                        height = drawable.intrinsicHeight * 2
                    }
                    val rect = Rect(0, 0, width, height)
                    drawable.bounds = rect
                    urlDrawable.bounds = rect
                    urlDrawable.drawable = drawable
                    htmlView.text = htmlView.text
                    htmlView.invalidate()
                }
            }

            override fun onLoadCleared(placeholder: Drawable?) {}
        }

        class UrlDrawable(resources: Resources? = null, bitmap: Bitmap? = null) :
            BitmapDrawable(resources, bitmap) {
            var drawable: Drawable? = null
            override fun draw(canvas: Canvas) {
                drawable?.draw(canvas)
            }
        }
    }
}