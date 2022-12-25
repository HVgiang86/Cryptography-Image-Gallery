package com.vcs.svtt.cryptographygallery.fragments

import android.graphics.BitmapFactory
import android.os.Bundle
import android.util.Base64
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.AdapterView
import android.widget.AdapterView.OnItemSelectedListener
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.widget.LinearLayoutCompat
import androidx.fragment.app.Fragment
import com.vcs.svtt.cryptographygallery.R
import com.vcs.svtt.cryptographygallery.model.Image
import com.vcs.svtt.cryptographygallery.model.ImageManager
import kotlinx.android.synthetic.main.activity_main.view.*
import kotlinx.android.synthetic.main.fragment_view_image.*
import java.io.BufferedInputStream
import java.io.File
import java.io.FileInputStream


// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val INDEX_PARAM = "index"

/**
 * A simple [Fragment] subclass.
 * Use the [ViewImageFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
@Suppress("DEPRECATION")
class ViewImageFragment : Fragment() {
    private lateinit var showEncryptedContentBtn:Button
    private lateinit var showImageButton: Button
    private lateinit var encryptedContentTV: TextView
    private lateinit var algorithmPicker:Spinner
    private lateinit var algorithmPickerContainer: LinearLayoutCompat
    private lateinit var encryptedContentFileBtn:Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            index = it.getInt(INDEX_PARAM,0)
            image = ImageManager.getInstance().getList()[index]
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        activity?.actionBar?.hide()
        requireActivity().window.addFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS)
        requireActivity().window.addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN)

        val v = inflater.inflate(R.layout.fragment_view_image, container, false)

        val items: MutableList<String> = arrayListOf()
        if (image.aes) items.add("AES")
        if (image.des) items.add("DES")
        if (image.rsa) items.add("RSA")
        if (image.md5) items.add("MD5")
        if (image.sha1) items.add("SHA1")
        if (image.sha512) items.add("SHA512")

        algorithmPicker = v.findViewById(R.id.algorithm_picker)
        showEncryptedContentBtn = v.findViewById(R.id.show_encrypted_content_btn)
        showImageButton = v.findViewById(R.id.show_image_btn)
        encryptedContentTV = v.findViewById(R.id.encrypted_content)
        algorithmPickerContainer = v.findViewById(R.id.algorithm_picker_container)
        encryptedContentFileBtn = v.findViewById(R.id.show_encrypted_file_content_btn)

        val adapter: ArrayAdapter<String> = ArrayAdapter(
            requireContext(),
            android.R.layout.simple_spinner_dropdown_item,
            items
        )
        algorithmPicker.adapter = adapter

        algorithmPicker.onItemSelectedListener = object:OnItemSelectedListener{
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                if ("AES" == items[position]) algorithmSelected = AES
                if ("DES" == items[position]) algorithmSelected = DES
                if ("RSA" == items[position]) algorithmSelected = RSA
                if ("MD5" == items[position]) algorithmSelected = MD5
                if ("SHA1" == items[position]) algorithmSelected = SHA1
                if ("SHA512" == items[position]) algorithmSelected = SHA512
                showEncryptedContent(algorithmSelected)
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                Toast.makeText(context,"Select an algorithm!",Toast.LENGTH_SHORT).show()
                algorithmSelected = 0
            }
        }

        showEncryptedContentBtn.setOnClickListener {
            showEncryptedContent(algorithmSelected)
        }

        showImageButton.setOnClickListener {
            showImage()
        }

        return v
    }

    override fun onResume() {
        super.onResume()
        showImage()
    }

    private fun showEncryptedContent(a: Int) {
        encryptedContentTV.visibility = View.VISIBLE
        showImageButton.visibility = View.VISIBLE
        algorithmPickerContainer.visibility = View.VISIBLE
        encryptedContentFileBtn.visibility = View.VISIBLE
        iv.visibility = View.GONE
        showEncryptedContentBtn.visibility = View.GONE

        encryptedContentTV.text = getEncryptedContent(a)
    }

    private fun getEncryptedContent(a: Int):String {
        var file = File(image.rawFilePath)
        when(a) {
            AES -> file = File(image.aesFilePath)
            DES -> file = File(image.desFilePath)
            RSA -> file = File(image.rsaFilePath)
            MD5 -> file = File(image.md5FilePath)
            SHA1 -> file = File(image.sha1FilePath)
            SHA512 -> file = File(image.sha512FilePath)
        }

        val fis = FileInputStream(file)
        val bis = BufferedInputStream(fis)
        val data = ByteArray(bis.available())

        return Base64.encodeToString(data,Base64.DEFAULT)
    }

    private fun hideEncryptedContent() {
        encryptedContentTV.visibility = View.GONE
        showImageButton.visibility = View.GONE
        algorithmPickerContainer.visibility = View.GONE
        encryptedContentFileBtn.visibility = View.GONE

    }

    private fun showImage() {
        hideEncryptedContent()
        iv.visibility = View.VISIBLE
        showEncryptedContentBtn.visibility = View.VISIBLE
        val imgFile = File(image.rawFilePath)
        iv.setImageBitmap(BitmapFactory.decodeFile(imgFile.absolutePath))
    }

    override fun onDetach() {
        activity?.actionBar?.show()
        requireActivity().window.clearFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS)
        requireActivity().window.clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN)

        super.onDetach()
    }

    companion object {
        private var index = 0
        private lateinit var image: Image
        private var algorithmSelected = 0
        private const val AES = 1
        private const val DES = 2
        private const val RSA = 3
        private const val MD5 = 4
        private const val SHA1 = 5
        private const val SHA512 = 6

        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @return A new instance of fragment ViewImageFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(index: Int) = ViewImageFragment().apply {
            arguments = Bundle().apply {
                this.putInt(INDEX_PARAM,index)
            }
        }
    }
}