package com.automotive.tpms.helper.contacts

import android.content.ContentResolver
import android.provider.ContactsContract

fun readConacts(
    contentResolver: ContentResolver,
    stringFormatterfn: (name: String, phoneNumber: String) -> String
): List<String> {
   val contactsList: MutableList<String> = mutableListOf()

    val cursor = contentResolver.query(
        ContactsContract.Contacts.CONTENT_URI,
        arrayOf(ContactsContract.Contacts._ID, ContactsContract.Contacts.DISPLAY_NAME),
        null, // Selection (WHERE clause)
        null, // Selection arguments
        null  // Sort order
    )

    cursor?.use {
        while (it.moveToNext()) {
            val id = it.getString(it.getColumnIndexOrThrow(ContactsContract.Contacts._ID))
            val name =
                it.getString(it.getColumnIndexOrThrow(ContactsContract.Contacts.DISPLAY_NAME))
            var phoneNumber: String = ""

            val hasPhone =
                it.getInt(it.getColumnIndexOrThrow(ContactsContract.Contacts.HAS_PHONE_NUMBER))
            if (hasPhone > 0) {
                val phoneCursor = contentResolver.query(
                    ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                    null,
                    ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = ?",
                    arrayOf(id),
                    null
                )
                phoneCursor?.use { pc ->
                    while (pc.moveToNext()) {
                        phoneNumber =
                            pc.getString(pc.getColumnIndexOrThrow(ContactsContract.CommonDataKinds.Phone.NUMBER))
                        if (phoneNumber.isEmpty()) {
                            break
                        }
                    }
                }
            }

            val resultedString = stringFormatterfn(name, phoneNumber)
            if (resultedString.isNotEmpty()) {
                contactsList.add(resultedString)
            }
        }
    }

    return contactsList
}