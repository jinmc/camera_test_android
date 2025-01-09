package com.example.myapplication

import android.os.Parcel
import android.os.Parcelable
import android.graphics.RectF

data class Detection(
    val boundingBox: RectF,
    val label: String,
    val confidence: Float
) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readParcelable(RectF::class.java.classLoader)!!,
        parcel.readString()!!,
        parcel.readFloat()
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeParcelable(boundingBox, flags)
        parcel.writeString(label)
        parcel.writeFloat(confidence)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Detection> {
        override fun createFromParcel(parcel: Parcel): Detection {
            return Detection(parcel)
        }

        override fun newArray(size: Int): Array<Detection?> {
            return arrayOfNulls(size)
        }
    }
}