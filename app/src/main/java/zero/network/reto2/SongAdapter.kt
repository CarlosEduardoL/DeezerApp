package zero.network.reto2

import android.content.Intent
import android.view.LayoutInflater.from
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.song_card.view.*
import zero.network.reto2.utils.loadImage

class SongAdapter: RecyclerView.Adapter<SongAdapter.SongViewHolder>() {

    val songs = mutableListOf<Song>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = SongViewHolder(
        from(parent.context).inflate(R.layout.song_card, parent, false)
    )

    override fun getItemCount(): Int = songs.size

    override fun onBindViewHolder(holder: SongViewHolder, position: Int) {
        holder.bind(songs[position])
    }

    class SongViewHolder(private val view: View): RecyclerView.ViewHolder(view) {

        private val title = view.songNameField
        private val artist = view.songArtistField
        private val date = view.songDateField
        private val image = view.songImage

        fun bind(song: Song) {

            view.context.loadImage(song.image, image)

            title.text = song.name
            artist.text = song.artist
            date.text = song.releaseDate

            view.setOnClickListener {
                val intent = Intent(view.context, SongActivity::class.java)
                intent.putExtra("song", song)
                it.context.startActivity(intent)
            }
        }

    }
}