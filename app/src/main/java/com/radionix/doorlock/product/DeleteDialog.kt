package com.radionix.doorlock.product

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.Toast
import androidx.appcompat.widget.AppCompatButton
import androidx.fragment.app.DialogFragment
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.radionix.doorlock.R
import com.radionix.doorlock.helper.AppController
import com.radionix.doorlock.home.MainActivity

class DeleteDialog(var device : String) : DialogFragment()
{

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        dialog!!.window?.setBackgroundDrawableResource(R.drawable.round_corner)

        val root = inflater.inflate(R.layout.alert_delete_device, container, false)

        val checkBox : CheckBox = root.findViewById(R.id.tank_name)
        val delete : AppCompatButton = root.findViewById(R.id.save)

        val databaseReference : DatabaseReference = FirebaseDatabase.getInstance().getReference("")
        val appController = AppController(requireContext())

        delete.setOnClickListener {

            if(checkBox.isChecked){

                databaseReference.child("userData").child(appController.getUid()).child(appController.getUsername()).child("deviceList")
                    .child(device).removeValue()
                startActivity(Intent(context, MainActivity::class.java))
            }
            else{
                Toast.makeText(requireContext(),"Accept above message", Toast.LENGTH_SHORT).show()
            }

        }


        return root
    }

    override fun onStart() {
        super.onStart()
        val width = (resources.displayMetrics.widthPixels * 0.85).toInt()
        val height = (resources.displayMetrics.heightPixels * 0.40).toInt()
        dialog!!.window?.setLayout(width, ViewGroup.LayoutParams.WRAP_CONTENT)
    }


}