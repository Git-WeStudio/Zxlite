package we.zxlite

import android.content.Context
import android.graphics.drawable.Drawable
import com.bumptech.glide.GlideBuilder
import com.bumptech.glide.annotation.GlideModule
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions.withCrossFade
import com.bumptech.glide.module.AppGlideModule

@GlideModule
class GlideModule : AppGlideModule() {
    override fun applyOptions(ctx: Context, builder: GlideBuilder) {
        super.applyOptions(ctx, builder)
        builder.setDefaultTransitionOptions(Drawable::class.java, withCrossFade())
    }
}