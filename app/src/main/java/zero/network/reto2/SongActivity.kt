package zero.network.reto2

import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_song.*


class SongActivity : AppCompatActivity() {

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_song)

        backButton.setOnClickListener { finish() }

        val extra = intent.extras
        if (extra == null) {
            finish()
            return
        }
        val song = extra.getSerializable("song")

        if (song == null) {
            finish()
            return
        }


        song.apply {
            if (this is Song){
                songAlbumField.text = album
                songArtistField.text = artist
                songDurationField.text = "${duration/60}:${duration%60}"

                loadImage(image, songImage)

                songListenButton.setOnClickListener {
                    val i = Intent(Intent.ACTION_VIEW)
                    i.data = Uri.parse(link)
                    startActivity(i)
                }
            }
        }

    }

}
