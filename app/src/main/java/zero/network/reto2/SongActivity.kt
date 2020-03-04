package zero.network.reto2

import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_song.*
import zero.network.reto2.utils.getSerializableOr
import zero.network.reto2.utils.loadImage


class SongActivity : AppCompatActivity() {

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_song)

        backButton.setOnClickListener { finish() }

        val song = intent.getSerializableOr<Song>("song") {
            finish()
            return
        }

        // Load Song information on the view
        song.apply {
            songAlbumField.text = album
            songArtistField.text = artist
            val minutes = duration % 60
            songDurationField.text = "${duration / 60}:${if (minutes > 10) "" else "0"}$minutes" // format the duration in minutes:seconds

            loadImage(image, songImage)

            songListenButton.setOnClickListener {
                val i = Intent(Intent.ACTION_VIEW).apply { data = Uri.parse(link) }
                startActivity(i)
            }
        }

    }

}
