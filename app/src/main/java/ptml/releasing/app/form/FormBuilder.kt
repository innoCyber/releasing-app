package ptml.releasing.app.form

import android.content.Context
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.*
import ptml.releasing.R
import ptml.releasing.app.form.FormUtils.getImageResourceByType
import ptml.releasing.app.form.FormUtils.inflateView
import ptml.releasing.configuration.models.ConfigureDeviceData
import timber.log.Timber
import java.util.*

class FormBuilder constructor(val context: Context) {
    private val rootLayout = LinearLayout(context)
    private var listener:FormListener? = null

    init {
        rootLayout.orientation = LinearLayout.VERTICAL
        rootLayout.layoutParams =
                LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT)
    }


    fun setListener(listener: FormListener): FormBuilder{
        this.listener = listener
        return this
    }

    /**
     * Creates a form view from the list of configuration options
     * @param configDataList the list of configuration options
     * @return a linear layout of the form
     * */
    fun build(configDataList: List<ConfigureDeviceData>): LinearLayout {
        //sort the list based on the position
        val positionComparator = Comparator<ConfigureDeviceData> { o1, o2 -> o1.position.compareTo(o2.position) }
        Collections.sort(configDataList, positionComparator)

        //iterate over the list to get each config
        for (configureDeviceData in configDataList) {
            //create and add the view gotten based on the config
            val formView = createViewFromConfig(configureDeviceData)
            listener?.onFormAdded(configureDeviceData)
            if (formView != null) {
                rootLayout.addView(
                        formView,
                        ViewGroup.LayoutParams(
                                ViewGroup.LayoutParams.MATCH_PARENT,
                                ViewGroup.LayoutParams.WRAP_CONTENT
                        )
                )

            }
        }

        if (rootLayout.childCount <= 0) { //show an error view if no view had been added
            rootLayout.addView(createErrorView())
        } else { //add the save and reset buttons
            rootLayout.addView(createBottomButtons())
        }


        return rootLayout
    }


    private fun createViewFromConfig(data: ConfigureDeviceData): View? {
        try {
            val formType = FormType.valueOf(data.type)
            when (formType) {
                FormType.LABEL -> {
                    return createLabel(data)
                }

                FormType.TEXTBOX -> {
                    return createTextBox(data)
                }

                FormType.MULTI_LINE_TEXTBOX -> {
                    return createMultilineTextBox(data)
                }
                FormType.IMAGES  -> {
                    return createButton(data)
                }

                FormType.PRINTER -> {
                    return createButton(data)
                }

                FormType.DAMAGES -> {
                    return createButton(data)
                }


                FormType.SINGLE_SELECT -> {
                    return createSingleSelect(data)
                }


                FormType.MULTI_SELECT -> {
                    return createMultiSelectSelect(data)
                }


                else -> {
                    Timber.e("Could not find form type for %s", formType.type)
                    return null
                }

            }
        } catch (e: Exception) { //return an error view
            Timber.e("Could not find create form for %s", data.type)
            return null
        }

    }


    /**
     * Creates a TextView that functions as a label
     *  Applies the necessary styles
     *  @param data the config
     * @return TextView
     * */
    private fun createLabel(data: ConfigureDeviceData): TextView {
        val textView = TextView(context)
        textView.tag = data.id
        //todo: Apply styles
        return textView

    }


    /**
     * Creates a regular edit text for the form
     *  Applies the necessary styles
     *  @param @param data the config
     * @return EditText
     * */
    private fun createTextBox(data: ConfigureDeviceData): EditText {
        val editText = EditText(context)
        editText.tag = data.id
        //todo: Apply styles
        return editText

    }


    /**
     * Creates a multiline edit text for the form
     *  Applies the necessary styles
     *  @param @param data the config
     * @return EditText
     * */
    private fun createMultilineTextBox(data: ConfigureDeviceData): EditText {
        val editText = EditText(context)
        editText.tag = data

        editText.setSingleLine(false)
        editText.imeOptions = EditorInfo.IME_FLAG_NO_ENTER_ACTION

        //todo: Apply styles
        return editText

    }


    /**
     * Creates a Button for the form
     *  Applies the necessary styles
     *  @param @param data the config
     * @return the button view
     * */
    private fun createButton(data: ConfigureDeviceData): View {
        val view = inflateView(context, R.layout.button_layout)
        val titleView = view.findViewById<TextView>(R.id.tv_title)
        val imageView = view.findViewById<ImageView>(R.id.img)
        titleView.text = data.title
        imageView.setImageResource(getImageResourceByType(data.type))
        view.tag = data

        return view

    }


    /**
     * Creates a Spinner for the form
     *  Applies the necessary styles
     *  @param @param data the config
     * @return Spinner
     * */
    private fun createSingleSelect(data: ConfigureDeviceData): Spinner {
        val spinner = Spinner(context)
        spinner.tag = data
        //add the items
        val adapter = FormSelectAdapter(context, data.options)
        spinner.adapter = adapter
        //todo: Apply styles
        return spinner

    }


    /**
     * Creates a Multiselect for the form
     *  Applies the necessary styles
     *  @param @param data the config
     * @return Spinner
     * */
    private fun createMultiSelectSelect(data: ConfigureDeviceData): Spinner {
        val spinner = Spinner(context)
        spinner.tag = data
        //add the items
        val adapter = FormSelectAdapter(context, data.options)
        spinner.adapter = adapter
        //todo: Apply styles
        return spinner

    }


    /**
     * Creates a TextView that functions as a label
     *  Applies the necessary styles
     *  @param data the config
     * @return TextView
     * */
    private fun createErrorView(): TextView {
        val textView = TextView(context)
        //todo: Apply styles
        return textView

    }


    private fun createBottomButtons(): LinearLayout {
        val bottomLayout = LinearLayout(context)
        bottomLayout.orientation = LinearLayout.HORIZONTAL

        val saveButton = Button(context)
        saveButton.layoutParams = LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f)

        val resetButton = Button(context)
        resetButton.layoutParams = LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f)

//        TODO: Add Styles

        bottomLayout.addView(saveButton)
        bottomLayout.addView(resetButton)

        return bottomLayout
    }



}