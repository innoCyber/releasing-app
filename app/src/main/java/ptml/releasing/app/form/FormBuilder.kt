package ptml.releasing.app.form

import android.content.Context
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import ptml.releasing.R
import ptml.releasing.app.form.FormUtils.applyBottomParams
import ptml.releasing.app.form.FormUtils.applyParams
import ptml.releasing.app.form.FormUtils.applyTopParams
import ptml.releasing.app.form.FormUtils.getDataForMultiSpinner
import ptml.releasing.app.form.FormUtils.getImageResourceByType
import ptml.releasing.app.form.FormUtils.inflateView
import ptml.releasing.app.form.adapter.BaseSelectAdapter
import ptml.releasing.app.form.adapter.FormSelectAdapter
import ptml.releasing.app.form.adapter.MultiSelectAdapter
import ptml.releasing.app.form.adapter.SingleSelectAdapter
import ptml.releasing.app.utils.Constants
import ptml.releasing.app.utils.SizeUtils
import ptml.releasing.app.views.MultiSpinner
import ptml.releasing.app.views.MultiSpinnerListener
import ptml.releasing.cargo_search.model.FindCargoResponse
import ptml.releasing.cargo_search.model.Option
import ptml.releasing.cargo_search.model.Value
import ptml.releasing.configuration.models.ConfigureDeviceData
import ptml.releasing.configuration.models.Options
import timber.log.Timber
import java.util.*

class FormBuilder constructor(val context: Context) {
    private var values: List<Value>? = null
    private var options: List<Option>? = null
    private val rootLayout = LinearLayout(context)
    private var listener: FormListener? = null
    private var multiSpinnerListener: MultiSpinnerListener? = null

    init {
        rootLayout.orientation = LinearLayout.VERTICAL
        rootLayout.layoutParams =
            LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT)
    }


    fun findView(any: Any):View{
        return rootLayout.findViewWithTag<View>(any)
    }




    fun setListener(listener: FormListener): FormBuilder {
        this.listener = listener
        return this
    }

    fun setMultiSelectListener(listener: MultiSpinnerListener): FormBuilder {
        this.multiSpinnerListener = listener
        return this
    }


    /**
     * Initializes the form with pre-filled data
     * @param findCargoResponse The object with the pre-filled data, referenced by the form id
     * */
    fun init(findCargoResponse: FindCargoResponse?): FormBuilder {
        this.values = findCargoResponse?.values
        this.options = findCargoResponse?.options


        return this
    }


    /**
     * Creates a form view from the list of configuration options
     * @param configDataList the list of configuration options
     * @return a linear layout of the form
     * */
    fun build(configDataList: List<ConfigureDeviceData>?): LinearLayout {
        //sort the list based on the position
        Timber.d("Config list: %s", configDataList)
        val positionComparator = Comparator<ConfigureDeviceData> { o1, o2 -> o1.position.compareTo(o2.position) }
        Collections.sort(configDataList ?: mutableListOf(), positionComparator)
        val params = LinearLayout.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )


        //iterate over the list to get each config

        for (i in 0 until (configDataList?.size ?: mutableListOf<ConfigureDeviceData>().size)) {
            //create and add the view gotten based on the config
            val configureDeviceData = configDataList?.get(i)

            val formView = createViewFromConfig(configureDeviceData)
            listener?.onFormAdded(configureDeviceData)
            if (formView != null) {
                if (i == 0) {
                    params.setMargins(0, SizeUtils.dp2px(context, 32f), 0, 0)
                } else {
                    params.setMargins(0, 0, 0, 0)
                }
                rootLayout.addView(formView)

            }
        }

        if (rootLayout.childCount <= 0) { //show an error view if no view had been added
            rootLayout.addView(createErrorView())
        } else { //add the save and reset buttons
            val bottom = createBottomButtons()
            applyBottomParams(bottom)
            rootLayout.addView(bottom)
        }

        initializeData()

        return rootLayout
    }


    private fun createViewFromConfig(data: ConfigureDeviceData?): View? {
        try {
            val formType = FormType.fromType(data!!.type)
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
                FormType.IMAGES -> {
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
                    Timber.e("Could not find form type for %s", data.type)
                    return null
                }

            }
        } catch (e: Exception) { //return an error view
            Timber.e(e, "Could not create form for %s", data?.type)
            return null
        }

    }


    /**
     * Creates a TextView that functions as a label
     *  @param data the config
     * @return TextView
     * */
    private fun createLabel(data: ConfigureDeviceData): TextView {
        val textView = inflateView(context, R.layout.form_label) as TextView
        textView.tag = data.id
        textView.text = data.title
        applyTopParams(textView)
        return textView

    }


    /**
     * Creates a regular edit text for the form
     *  @param @param data the config
     * @return EditText
     * */
    private fun createTextBox(data: ConfigureDeviceData): EditText {
        val editText = inflateView(context, R.layout.form_textbox) as EditText
        editText.tag = data.id
        editText.hint = data.title


        applyParams(editText)
        return editText

    }


    /**
     * Creates a multiline edit text for the form
     *  Applies the necessary styles
     *  @param @param data the config
     * @return EditText
     * */
    private fun createMultilineTextBox(data: ConfigureDeviceData): EditText {
        val editText = inflateView(context, R.layout.form_textbox_multiline) as EditText
        editText.tag = data.id
        editText.hint = data.title
        applyParams(editText)
        return editText

    }


    /**
     * Creates a Button for the form
     *  Applies the necessary styles. Form Types expected are
     *  @see FormType.DAMAGES
     *  @see FormType.IMAGES
     *  @see FormType.PRINTER
     *  @param @param data the config
     * @return the button view
     * */
    private fun createButton(data: ConfigureDeviceData): View {
        val view = inflateView(context, R.layout.button_layout)
        val titleView = view.findViewById<TextView>(R.id.tv_title)
        val numberView = view.findViewById<TextView>(R.id.tv_number)
        val imageView = view.findViewById<ImageView>(R.id.img)
        numberView.visibility = if(data.type == FormType.PRINTER.type) View.INVISIBLE else View.VISIBLE
        titleView.text = data.title
        imageView.setImageResource(getImageResourceByType(data.type))
        view.tag = data.id
        view.setOnClickListener {
            listener?.onClickFormButton(FormType.fromType(data.type), it)
        }
        applyTopParams(view)
        return view

    }




    /**
     * Creates a Spinner for the form
     *  Applies the necessary styles
     *  @param @param data the config
     * @return Spinner
     * */
    private fun createSingleSelect(data: ConfigureDeviceData): View {
        if (data.options.size > Constants.ITEM_TO_EXPAND) {
            val spinner = inflateView(context, R.layout.form_single_select) as Spinner
            spinner.tag = data.id
            //add the items
            val adapter = FormSelectAdapter(context, data.options)
            spinner.adapter = adapter



            applyParams(spinner)
            return spinner
        } else {
            val adapter = SingleSelectAdapter<Options>()
            adapter.setItems(data.options)
            val recyclerView = RecyclerView(context)
            recyclerView.adapter = adapter
            recyclerView.layoutManager = GridLayoutManager(context, 2)
            applyParams(recyclerView)
            recyclerView.tag = data.id
            return recyclerView
        }

    }


    /**
     * Creates a Multiselect for the form
     *  Applies the necessary styles
     *  @param @param data the config
     * @return Spinner
     * */
    private fun createMultiSelectSelect(data: ConfigureDeviceData): View {
        if (data.options.size > Constants.ITEM_TO_EXPAND) {
            val spinner = inflateView(context, R.layout.form_multi_select) as MultiSpinner
            spinner.tag = data.id
            spinner.defaultHintText = data.title
            spinner.setItems(getDataForMultiSpinner(data.options), multiSpinnerListener)
            applyParams(spinner)
            return spinner
        } else {
            val adapter = MultiSelectAdapter<Options>()
            adapter.setItems(data.options)
            val recyclerView = RecyclerView(context)
            recyclerView.adapter = adapter
            recyclerView.layoutManager = GridLayoutManager(context, 2)
            applyParams(recyclerView)
            recyclerView.tag = data.id
            return recyclerView

        }


    }


    /**
     * Creates a TextView that functions as a label
     *  Applies the necessary styles
     * @return TextView
     * */
    private fun createErrorView(): TextView {
        val textView = TextView(context)
        return textView

    }


    private fun createBottomButtons(): View {
        return inflateView(context, R.layout.form_bottom)
    }

    private fun initializeData() {
        initializeValues()
        initializeOptions()
    }

    private fun initializeValues() {
        for (value in values ?: mutableListOf()) {
            bindValuesDataToView(rootLayout.findViewWithTag<View>(value.id), value.value)
        }
    }

    private fun initializeOptions() {
        for (option in options ?: mutableListOf()) {
            bindOptionsDataToView(rootLayout.findViewWithTag<View>(option.id), option.selected)
        }
    }

    private fun bindValuesDataToView(view: View, data: String?) {
        when (view) {
            is EditText -> view.setText(data)
            is TextView -> view.text = data
            else -> {
                Timber.d("Unknown view %s", view::class.java.name)
            }
        }
    }


    private fun bindOptionsDataToView(view: View, data: List<Int>?) {
        when (view) {
            is MultiSpinner -> { //check this first, for multi select
                view.setSelection(data)
            }

            is Spinner -> { //for single select
                if (data?.isEmpty() == false) { //there is data
                    val position = data[0]
                    view.setSelection(position)
                }
            }
            is RecyclerView -> {
                if(data?.isEmpty() == false && view.adapter  is BaseSelectAdapter<*, *>){
                    ((view.adapter) as BaseSelectAdapter<*,*>).initSelectedItems(data)
                }else{
                    Timber.d("Unknown adapter %s", view.adapter)
                }
            }
            else ->{
                Timber.d("Unknown view %s", view::class.java.name)
            }
        }
    }




}