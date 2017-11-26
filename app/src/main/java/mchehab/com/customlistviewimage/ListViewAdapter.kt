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
class ListViewAdapter(var context: Context,
                      var listPerson: MutableList<Person>,
                      var listPersonFilter: MutableList<Person> = ArrayList(listPerson),
                      var listPersonsSelected: MutableList<Person> = ArrayList(),
                      var listSelectedRows: MutableList<View> = ArrayList()) : BaseAdapter() {

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

        val person = listPersonFilter[position]

        viewHolder.textViewName.text = "${person.firstName} ${person.lastName}"
        viewHolder.textViewDescription.text = person.description
        viewHolder.imageViewProfilePic.setImageDrawable(getImageDrawable(person.imageName))

        val resource = if (listPersonsSelected.contains(person)) R.color.colorDarkGray else R.color.colorWhite
        view.setBackgroundResource(resource)

        return view
    }

    fun addItem(person: Person){
        listPerson.add(person)
        listPersonFilter.add(person)
    }

    fun handleLongPress(position: Int, view: View) {
        if (listSelectedRows.contains(view)) {
            listSelectedRows.remove(view)
            listPersonsSelected.remove(listPerson.get(position))
            view.setBackgroundResource(R.color.colorWhite)
        } else {
            listPersonsSelected.add(listPerson.get(position))
            listSelectedRows.add(view)
            view.setBackgroundResource(R.color.colorDarkGray)
        }
    }

    fun removeSelectedPersons() {
        listPerson.removeAll(listPersonsSelected)
        listPersonFilter.removeAll(listPersonsSelected)
        listPersonsSelected.clear()
        for (view in listSelectedRows)
            view.setBackgroundResource(R.color.colorWhite)
        listSelectedRows.clear()
    }

    fun filter(text: String){
        listPersonFilter.clear()
        if (text.length == 0)
            listPersonFilter.addAll(listPerson)
        else{
            listPersonFilter =  listPerson.filter { person ->  "${person.firstName} ${person
                    .lastName}".toLowerCase().contains(text)}.toMutableList()
        }

        notifyDataSetChanged()
    }

    private fun getImageDrawable(imageName: String): Drawable {
        val id = context.resources.getIdentifier(imageName, "drawable",
                context.packageName)
        return context.resources.getDrawable(id)
    }

    override fun getItem(position: Int): Any {
        return listPersonFilter[position]
    }

    override fun getItemId(position: Int): Long {
        return 0
    }

    override fun getCount(): Int {
        return listPersonFilter.size
    }

    class ViewHolder{
        lateinit var imageViewProfilePic: ImageView
        lateinit var textViewName: TextView
        lateinit var textViewDescription: TextView
    }
}