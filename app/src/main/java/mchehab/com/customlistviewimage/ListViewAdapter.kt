package mchehab.com.customlistviewimage

import android.content.Context
import android.graphics.drawable.Drawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ImageView
import android.widget.TextView

/**
 * Created by muhammadchehab on 10/29/17.
 */
class ListViewAdapter(var context: Context, var listPerson: List<Person>) : BaseAdapter() {

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        var view = convertView
        var viewHolder = ViewHolder()
        if (view == null){
            view = LayoutInflater.from(context).inflate(R.layout.row_listview, null)

            viewHolder.imageViewProfilePic = view!!.findViewById(R.id.imageViewProfilePic)
            viewHolder.textViewName = view.findViewById(R.id.textViewName)
            viewHolder.textViewDescription = view.findViewById(R.id.textViewDescription)

            view.tag = viewHolder
        }else{
            viewHolder = view.tag as ViewHolder
        }

        val person = listPerson[position]

        viewHolder.textViewName.text = "${person.firstName} ${person.lastName}"
        viewHolder.textViewDescription.text = person.description
        viewHolder.imageViewProfilePic.setImageDrawable(getImageDrawable(person.imageName))

        return view
    }

    private fun getImageDrawable(imageName: String): Drawable {
        val id = context.resources.getIdentifier(imageName, "drawable",
                context.packageName)
        return context.resources.getDrawable(id)
    }

    override fun getItem(position: Int): Any {
        return listPerson[position]
    }

    override fun getItemId(position: Int): Long {
        return 0
    }

    override fun getCount(): Int {
        return listPerson.size
    }

    class ViewHolder{
        lateinit var imageViewProfilePic: ImageView
        lateinit var textViewName: TextView
        lateinit var textViewDescription: TextView
    }
}