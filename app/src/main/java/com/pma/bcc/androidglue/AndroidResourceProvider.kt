package com.pma.bcc.androidglue

import android.content.Context
import com.pma.bcc.viewmodels.ResourceProvider

class AndroidResourceProvider(val context: Context) : ResourceProvider {

    override fun getString(id: Int): String = context.getString(id)
}
