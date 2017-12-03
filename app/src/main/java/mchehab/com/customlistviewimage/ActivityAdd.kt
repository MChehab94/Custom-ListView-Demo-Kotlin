package mchehab.com.customlistviewimage

import android.app.Activity
import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.app.AlertDialog
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import org.parceler.Parcels

class ActivityAdd : AppCompatActivity() {

    val editTextFirstName by lazy {
        findViewById<EditText>(R.id.editTextFirstName)
    }

    val editTextLastName by lazy {
        findViewById<EditText>(R.id.editTextLastName)
    }

    val editTextDescription by lazy {
        findViewById<EditText>(R.id.editTextDescription)
    }

    val person = Person("", "", "", "")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add)

        val spinnerImages = findViewById<Spinner>(R.id.spinnerImages)
        val button = findViewById<Button>(R.id.buttonAdd)
        button.setOnClickListener {
            if(isValid()){
                val intent = Intent()
                val bundle = Bundle()

                person.firstName = editTextFirstName.text.toString()
                person.lastName = editTextLastName.text.toString()
                person.description = editTextDescription.text.toString()
                person.imageName = spinnerImages.selectedItem as String

                bundle.putParcelable("person", Parcels.wrap(person))
                intent.putExtras(bundle)
                setResult(Activity.RESULT_OK, intent)
                finish()
            }else{
                displayAlertDialog()
            }
        }
    }

    private fun isValid(): Boolean{
        if(isEditTextEmpty(editTextFirstName))
            return false
        if(isEditTextEmpty(editTextLastName))
            return false
        if(isEditTextEmpty(editTextDescription))
            return false
        return true
    }

    private fun isEditTextEmpty(editText: EditText): Boolean{
        return editText.text.toString().isEmpty()
    }

    private fun displayAlertDialog(){
        AlertDialog.Builder(this)
                .setTitle("Error")
                .setMessage("Make sure all of the fields are filled")
                .setCancelable(false)
                .setPositiveButton("Ok", { dialog, which -> dialog.dismiss() })
                .create()
                .show()
    }
}
