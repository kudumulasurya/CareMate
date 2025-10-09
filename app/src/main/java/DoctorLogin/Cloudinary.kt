package DoctorLogin

import android.app.Application
import com.cloudinary.android.MediaManager

class Cloudinary:Application() {
    override fun onCreate() {
        super.onCreate()
        val config = HashMap<String, String>()
        config["cloud_name"] = "dakhblh3a"
        config["api_key"] = "535234577688126"
        config["api_secret"] = "vPFqI4FPHY3bIwPX1hGFhyCRvvo"
        MediaManager.init(this, config)

    }
}