package mchehab.com.customlistviewimage

import org.parceler.Parcel
import org.parceler.ParcelConstructor
import org.parceler.ParcelProperty

/**
 * Created by muhammadchehab on 10/29/17.
 */
@Parcel
data class Person @ParcelConstructor
constructor(@ParcelProperty("firstName") var firstName: String,
        @ParcelProperty("lastName") var lastName:String,
        @ParcelProperty("description") var description: String,
        @ParcelProperty("imageName") var imageName: String)