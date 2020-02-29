package zero.network.reto2

import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import kotlinx.android.synthetic.main.activity_song.*


class SongActivity : AppCompatActivity() {

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_song)

        backButton.setOnClickListener { finish() }

        intent.extras!!.getSerializable("song").apply {
            if (this is Song){
                songAlbumField.text = album
                songArtistField.text = artist
                songDurationField.text = "${duration/60}:${duration%60}"
                val requestOptions = RequestOptions()
                    .placeholder(R.drawable.ic_launcher_background)
                    .error(R.drawable.ic_launcher_background)
                Glide.with(this@SongActivity)
                    .applyDefaultRequestOptions(requestOptions)
                    .load(image)
                    .into(songImage)

                songListenButton.setOnClickListener {
                    val i = Intent(Intent.ACTION_VIEW)
                    i.data = Uri.parse(link)
                    startActivity(i)
                }
            }
        }

    }
}
