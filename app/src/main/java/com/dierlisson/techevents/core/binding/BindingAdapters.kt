package com.dierlisson.techevents.core.binding

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.databinding.BindingAdapter
import com.bumptech.glide.Glide
import com.dierlisson.techevents.R
import java.text.NumberFormat
import java.util.Locale

object BindingAdapters {

    @JvmStatic
    @BindingAdapter("isVisible")
    fun bindIsVisible(view: View, isVisible: Boolean) {
        view.visibility = if (isVisible) View.VISIBLE else View.GONE
    }

    @JvmStatic
    @BindingAdapter("imageUrl", "placeholderRes", requireAll = false)
    fun bindImageUrl(imageView: ImageView, url: String?, placeholderRes: Int?) {
        val defaultPlaceholder = placeholderRes ?: R.drawable.bg_card_overlay
        if (!url.isNull_or_empty()) {
            Glide.with(imageView.context)
                .load(url)
                .placeholder(defaultPlaceholder)
                .error(defaultPlaceholder)
                .into(imageView)
        } else {
            imageView.setImageResource(defaultPlaceholder)
        }
    }

    @JvmStatic
    @BindingAdapter("formattedPrice")
    fun bindFormattedPrice(textView: TextView, price: Double) {
        if (price == 0.0) {
            textView.text = "Grátis"
            textView.setTextColor(textView.context.getColor(R.color.accent_green))
        } else {
            val ptBrLocale = Locale("pt", "BR")
            val currencyFormat = NumberFormat.getCurrencyInstance(ptBrLocale)
            textView.text = currencyFormat.format(price)
            textView.setTextColor(textView.context.getColor(R.color.primary_blue))
        }
    }

    private fun String?.isNull_or_empty(): Boolean = this == null || this.isBlank()
}
