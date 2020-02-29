package zero.network.reto2

import android.content.Intent
import android.view.LayoutInflater.from
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import kotlinx.android.synthetic.main.playlist_card.view.*

class PlayListAdapter : RecyclerView.Adapter<PlayListAdapter.PlayListViewHolder>() {

    val items= mutableListOf<PlayList>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = PlayListViewHolder(
        from(parent.context).inflate(R.layout.playlist_card, parent, false)
    )

    override fun getItemCount() = items.size

    override fun onBindViewHolder(holder: PlayListViewHolder, position: Int) {
        holder.bind(items[position])
    }

    class PlayListViewHolder(private val view: View): RecyclerView.ViewHolder(view) {

        private val image = view.playlistImage
        private val title = view.playlistNameField
        private val creator = view.playlistCreatorField
        private val count = view.playlistCountField

        fun bind(playList: PlayList) {
            val requestOptions = RequestOptions()
                .placeholder(R.drawable.ic_launcher_background)
                .error(R.drawable.ic_launcher_background)

            Glide.with(itemView.context)
                .applyDefaultRequestOptions(requestOptions)
                .load(playList.image)
                .into(image)

            title.text = playList.title
            creator.text = playList.creator
            count.text = "${playList.songCount}"

            view.setOnClickListener {
                val intent = Intent(it.context, PlayListActivity::class.java)
                intent.putExtra("playlist", playList)
                it.context.startActivity(intent)
            }

        }

    }
}