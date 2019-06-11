package ptml.releasing.app.form

import android.content.Context
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.textfield.TextInputLayout
import ptml.releasing.R
import ptml.releasing.app.form.FormUtils.applyBottomParams
import ptml.releasing.app.form.FormUtils.applyParams
import ptml.releasing.app.form.FormUtils.applyLabelParams
import ptml.releasing.app.form.FormUtils.applyTopParams
import ptml.releasing.app.form.FormUtils.changeBgColor
import ptml.releasing.app.form.FormUtils.changeBgDrawable
import ptml.releasing.app.form.FormUtils.getButtonValidationErrorMessage
import ptml.releasing.app.form.FormUtils.getDataForMultiSpinner
import ptml.releasing.app.form.FormUtils.getImageResourceByType
import ptml.releasing.app.form.FormUtils.getQuickRemarksDataForMultiSpinner
import ptml.releasing.app.form.FormUtils.inflateView
import ptml.releasing.app.form.adapter.*
import ptml.releasing.app.form.base.BuilderPresenter
import ptml.releasing.app.form.base.BuilderView
import ptml.releasing.app.utils.Constants
import ptml.releasing.app.utils.setValidation
import ptml.releasing.app.views.MultiSpinner
import ptml.releasing.app.views.MultiSpinnerListener
import ptml.releasing.cargo_info.model.FormSelection
import ptml.releasing.cargo_search.model.FindCargoResponse
import ptml.releasing.cargo_search.model.Option
import ptml.releasing.cargo_search.model.Value
import ptml.releasing.configuration.models.ConfigureDeviceData
import ptml.releasing.configuration.models.Options
import ptml.releasing.quick_remarks.model.QuickRemark
import timber.log.Timber
import java.util.*

class FormBuilder constructor(val context: Context) : BuilderView {
    private var quickRemarks: Map<Int, QuickRemark>? = null
    internal var data: List<ConfigureDeviceData>? = null
    private val rootLayout = LinearLayout(context)
    private var listener: FormListener? = null
    private var multiSpinnerListener: MultiSpinnerListener? = null
    private var singleSelectListener: SingleSelectListener<Options>? = null
    var error = false

    private val presenter: BuilderPresenter

    init {
        rootLayout.orientation = LinearLayout.VERTICAL
        rootLayout.layoutParams =
            LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )

        presenter = FormBuilderPresenter(this)
    }


    fun findView(any: Any): View {
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
     * @param findCargoResponse
     * the object with the pre-filled data, referenced by the form id
     * */
    override fun init(findCargoResponse: FindCargoResponse?): BuilderView {
        return presenter.init(findCargoResponse)
    }

    override fun reset() {
        initializeData()
    }

    override fun getBottomButtons(): View? {
        if (rootLayout.childCount > 0) {
            val bottom = createBottomButtons()
            applyBottomParams(bottom)
            return bottom;
        }
        return null
    }

    /**
     * Creates a form view from the list of configuration options
     * @param configDataList the list of configuration options
     * @return a linear layout of the form
     * */
    override fun build(
        configDataList: List<ConfigureDeviceData>?,
        remarks: Map<Int, QuickRemark>?
    ): LinearLayout {
        //sort the list based on the position
        listener?.onStartLoad()
        Timber.d("Config list: %s", configDataList)
        this.data = configDataList
        quickRemarks = remarks
        presenter.provideQuickRemarks(quickRemarks)

        val positionComparator =
            Comparator<ConfigureDeviceData> { o1, o2 -> o1.position.compareTo(o2.position) }
        Collections.sort(data ?: mutableListOf(), positionComparator)

        //iterate over the list to get each config
        for (i in 0 until (data?.size ?: mutableListOf<ConfigureDeviceData>().size)) {
            //create and add the view gotten based on the config
            val configureDeviceData = data?.get(i)

            val formView = createViewFromConfig(configureDeviceData, i)
            listener?.onFormAdded(configureDeviceData)
            if (formView != null) {
                rootLayout.addView(formView)
            } else {
                Timber.e("Form view is null")
            }
        }

        if (rootLayout.childCount <= 0) { //show an error view if no view had been added
            error = true
            rootLayout.addView(createErrorView())
        }

//        initializeData()
        listener?.onEndLoad()
        return rootLayout
    }


    override fun createViewFromConfig(data: ConfigureDeviceData?, i: Int): View? {
        try {
            when (FormType.fromType(data?.type)) {
                FormType.LABEL -> {

                    return createLabel(data, i)
                }

                FormType.TEXTBOX -> {
                    presenter.initializeDefaultValue(data)
                    return createTextBox(data, i)
                }

                FormType.MULTI_LINE_TEXTBOX -> {
                    presenter.initializeDefaultValue(data)
                    return createMultilineTextBox(data, i)
                }
                FormType.IMAGES -> {
                    //todo: Handle the initialization
                    return createButton(data, i)
                }

                FormType.PRINTER -> {
                    return createButton(data, i)
                }

                FormType.DAMAGES -> {
                    //todo: Handle the initialization
                    return createButton(data, i)
                }


                FormType.SINGLE_SELECT -> {
                    presenter.initializeDefaultOption(data)
                    return createSingleSelect(data, i)
                }


                FormType.MULTI_SELECT -> {
                    presenter.initializeDefaultOption(data)
                    return createMultiSelectSelect(data, i)
                }

                FormType.QUICK_REMARK -> {
                    presenter.initializeDefaultOption(data)
                    return createQuickRemarkSelect(data, i)
                }


                FormType.CHECK_BOX -> {
                    Timber.d("Initializing checkbox: %s", data)
                    presenter.initializeDefaultValue(data)
                    return createCheckBox(data, i)
                }

                else -> {
                    Timber.e("Could not find form type for %s", data?.type)
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
    private fun createLabel(data: ConfigureDeviceData?, i: Int): TextView {
        val textView = inflateView(context, R.layout.form_label) as TextView
        textView.tag = data?.id
        textView.text = data?.title
        if (i == 0) {
            applyTopParams(textView)
        } else {
            applyLabelParams(textView)
        }
        return textView
    }


    /**
     * Creates a regular edit text for the form
     *  @param @param data the config
     * @return EditText
     * */
    private fun createTextBox(data: ConfigureDeviceData?, i: Int): TextInputLayout {
        val inputLayout = inflateView(context, R.layout.form_textbox) as TextInputLayout
        inputLayout.tag = data?.id
        val editText = inputLayout.findViewById<EditText>(R.id.edit)
        inputLayout.hint = data?.title
        editText.setValidation(data?.dataValidation)
        editText.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {

            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                inputLayout.error = ""
            }
        })
        if (i == 0) {
            applyTopParams(inputLayout)
        } else {
            applyParams(inputLayout)
        }
        return inputLayout
    }

    override fun validateTextBox(data: ConfigureDeviceData?): Boolean {
        return presenter.validateTextBox(data)
    }


    override fun showTextBoxError(data: ConfigureDeviceData?) {
        val inputLayout = rootLayout.findViewWithTag<TextInputLayout>(data?.id)
        inputLayout.error = context.getString(R.string.required_field_msg)
    }

    override fun getTextBoxText(data: ConfigureDeviceData?): String {
        val inputLayout = rootLayout.findViewWithTag<TextInputLayout>(data?.id)
        val editText = inputLayout.findViewById<EditText>(R.id.edit)
        return editText.text.toString()
    }

    override fun getTextBoxValue(data: ConfigureDeviceData?): Value? {
        return presenter.getTextBoxValue(data)
    }


    /**
     * Creates a multiline edit text for the form
     *  Applies the necessary styles
     *  @param @param data the config
     * @return EditText
     * */
    private fun createMultilineTextBox(data: ConfigureDeviceData?, i: Int): TextInputLayout {
        val inputLayout = inflateView(context, R.layout.form_textbox_multiline) as TextInputLayout
        inputLayout.tag = data?.id
        val editText = inputLayout.findViewById<EditText>(R.id.edit)
        inputLayout.hint = data?.title
        editText.setValidation(data?.dataValidation)
        editText.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {

            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                inputLayout.error = ""
            }
        })

        if (i == 0) {
            applyTopParams(inputLayout)
        } else {
            applyParams(inputLayout)
        }
        return inputLayout

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
    private fun createButton(data: ConfigureDeviceData?, i: Int): View {
        val view = inflateView(context, R.layout.button_layout)
        val rootView = view.findViewById<View>(R.id.button_root);
        val titleView = view.findViewById<TextView>(R.id.tv_title)
        val numberView = view.findViewById<TextView>(R.id.tv_number)
        val imageView = view.findViewById<ImageView>(R.id.img)
        numberView.visibility =
            if (data?.type == FormType.PRINTER.type) View.INVISIBLE else View.VISIBLE
        titleView.text = data?.title
        imageView.setImageResource(getImageResourceByType(data?.type))
        view.tag = data?.id
        rootView.setOnClickListener {
            listener?.onClickFormButton(FormType.fromType(data?.type), it)
        }
        view.findViewById<TextView>(R.id.tv_error).visibility = View.INVISIBLE
        if (i == 0) {
            applyTopParams(view)
        } else {
            applyParams(view)
        }
        return view
    }


    override fun validateButton(data: ConfigureDeviceData?): Boolean? {
        return presenter.validateButton(data)
    }

    override fun getButtonNumber(data: ConfigureDeviceData?): String {
        if (data?.type != FormType.PRINTER.type) {
            val view = rootLayout.findViewWithTag<ViewGroup>(data?.id)
            Timber.d("Getting error text view for buttons ")
            val numberView = view.findViewById<TextView>(R.id.tv_number)
            return numberView.text.toString()
        }

        return "0"
    }

    override fun showButtonError(data: ConfigureDeviceData?) {
        val view = rootLayout.findViewWithTag<ViewGroup>(data?.id)
        val message = context.getString(getButtonValidationErrorMessage(data?.type))
        listener?.onError(message)
        Timber.d("Error message: %s", message)
        view.findViewById<TextView>(R.id.tv_error).text = message
        view.findViewById<TextView>(R.id.tv_error).visibility = View.VISIBLE
    }


    /**
     * Creates a Spinner for the form
     *  Applies the necessary styles
     *  @param @param data the config
     * @return Spinner
     * */
    private fun createSingleSelect(data: ConfigureDeviceData?, i: Int): View? {
        if (data?.options?.size ?: 0 > 0) {
            if (Constants.ITEM_TO_EXPAND < data?.options?.size ?: 0) {
                val view = inflateView(context, R.layout.form_single_select)
                view.findViewById<TextView>(R.id.tv_error).visibility = View.INVISIBLE
                val spinner = view.findViewById<Spinner>(R.id.select)
                val titleTextView = view.findViewById<TextView>(R.id.tv_title)
                titleTextView.text = data?.title
                view.tag = data?.id
                //add the items
                val adapter = FormSelectAdapter(context, data?.options)
                spinner.adapter = adapter
                if (i == 0) {
                    applyTopParams(view)
                } else {
                    applyParams(view)
                }
                return view
            } else {
                val view = inflateView(context, R.layout.form_rv)
                val errorTextView = view.findViewById<TextView>(R.id.tv_error)
                val recyclerView = view.findViewById<RecyclerView>(R.id.select)
                val titleTextView = view.findViewById<TextView>(R.id.tv_title)
                titleTextView.text = data?.title
                val adapter = SingleSelectAdapter<Options>()
                adapter.setItems(data?.options)
                adapter.listener = object : SingleSelectListener<Options> {
                    override fun onItemSelected(item: Options?) {
                        singleSelectListener?.onItemSelected(item)
                        errorTextView.text = ""
                        changeBgColor(recyclerView, false)
                    }
                }
                errorTextView.visibility = View.INVISIBLE
                recyclerView.adapter = adapter
                recyclerView.layoutManager = GridLayoutManager(context, 2)
                view.tag = data?.id
                if (i == 0) {
                    applyTopParams(view)
                } else {
                    applyParams(view)
                }
                return view
            }
        } else {
            Timber.e("No options for %s form type with data: %s", data?.type, data)
            return null
        }
    }

    override fun validateSingleSelect(data: ConfigureDeviceData?): Boolean? {
        return presenter.validateSingleSelect(data)
    }

    override fun getSingleSelectRVItem(data: ConfigureDeviceData?): Options? {
        val view = rootLayout.findViewWithTag<View>(data?.id)
        val recyclerView = view.findViewById<RecyclerView>(R.id.select)
        val adapter = recyclerView.adapter as SingleSelectAdapter<*>
        return adapter.selectedItem as Options
    }

    override fun getSingleSelectSpinnerItem(data: ConfigureDeviceData?): Options? {
        val view = rootLayout.findViewWithTag<View>(data?.id)
        val spinner = view.findViewById<Spinner>(R.id.select)
        val adapter = spinner.adapter as FormSelectAdapter
        val selectedPosition = spinner.selectedItemPosition
        return if (adapter.count > selectedPosition) {
            spinner.getItemAtPosition(selectedPosition) as Options?
        } else {
            null
        }
    }

    override fun showSingleSelectError(data: ConfigureDeviceData?) {
        if (Constants.ITEM_TO_EXPAND > data?.options?.size ?: 0) {
            val view = rootLayout.findViewWithTag<View>(data?.id)
            val recyclerView = view.findViewById<RecyclerView>(R.id.select)
            val message = context.getString(R.string.select_one_item)
            listener?.onError(message)
            val textView = view.findViewById<TextView>(R.id.tv_error)
            textView.text = message
            textView.visibility = View.VISIBLE
            changeBgColor(recyclerView, true)
        } // NO need to show for spinner
    }


    override fun getSingleSelect(data: ConfigureDeviceData?): FormSelection? {
        return presenter.getSingleSelect(data)
    }

    private fun createQuickRemarkSelect(data: ConfigureDeviceData?, i: Int): View? {

        if (quickRemarks?.isNotEmpty() == true) {

            if (Constants.ITEM_TO_EXPAND < quickRemarks?.size ?: 0) {
                val view = inflateView(context, R.layout.form_multi_select)
                val textView = view.findViewById<TextView>(R.id.tv_error)
                textView.visibility = View.INVISIBLE
                val titleTextView = view.findViewById<TextView>(R.id.tv_title)
                titleTextView.text = data?.title
                val spinner = view.findViewById<MultiSpinner<QuickRemark>>(R.id.select)
                view.tag = data?.id
                spinner.defaultHintText = data?.title
                spinner.setItems(
                    getQuickRemarksDataForMultiSpinner(quickRemarks?.values?.toList()),
                    object : MultiSpinnerListener {
                        override fun onItemsSelected(selected: List<Boolean>) {
                            multiSpinnerListener?.onItemsSelected(selected)
                            textView.text = ""
                            changeBgDrawable(spinner, false)
                        }
                    })
                if (i == 0) {
                    applyTopParams(view)
                } else {
                    applyParams(view)
                }
                return view
            } else {
                val view = inflateView(context, R.layout.form_rv)
                val recyclerView = view.findViewById<RecyclerView>(R.id.select)
                val textView = view.findViewById<TextView>(R.id.tv_error)
                val titleTextView = view.findViewById<TextView>(R.id.tv_title)
                titleTextView.text = data?.title
                val adapter = MultiSelectAdapter<QuickRemark>()
                adapter.listener = object : MultiSelectListener<QuickRemark> {
                    override fun onItemsSelected(item: Map<Int, QuickRemark>) {
                        textView.text = ""
                        changeBgColor(recyclerView, false)
                    }
                }
                adapter.setItems(quickRemarks?.values?.toList())
                recyclerView.adapter = adapter
                recyclerView.layoutManager = GridLayoutManager(context, 2)
                if (i == 0) {
                    applyTopParams(view)
                } else {
                    applyParams(view)
                }
                view.tag = data?.id
                return view

            }

        } else {
            Timber.e("No data for %s form type with data: %s", data?.type, data)
            return null
        }
    }


    override fun showMultiSelectError(data: ConfigureDeviceData?) {
        val view = rootLayout.findViewWithTag<View>(data?.id)
        val selectView = view.findViewById<View>(R.id.select)
        multiSelectError(view)
        changeBgDrawable(selectView, true)
    }

    override fun validateQuickRemarkSelect(data: ConfigureDeviceData?): Boolean? {
       return presenter.validateQuickRemarkSelect(data)
    }


    override fun getQuickRemarkSelect(data: ConfigureDeviceData?): FormSelection? {
       return presenter.getQuickRemarkSelect(data)
    }

    override fun getMultiSelectRVItems(data: ConfigureDeviceData?): List<Int> {
        val view = rootLayout.findViewWithTag<View>(data?.id)
        val recyclerView = view.findViewById<RecyclerView>(R.id.select)
        val adapter = recyclerView.adapter as MultiSelectAdapter<*>
        return getMultiSelectIdList(adapter.selectedItems)
    }


    override fun getMultiSelectSpinnerItems(data: ConfigureDeviceData?): List<Int> {
        val view = rootLayout.findViewWithTag<View>(data?.id)
        val spinner = view.findViewById<MultiSpinner<Options>>(R.id.select)
        return spinner.selectedItems.keys.toList()
    }

    /**
     * Creates a Multiselect for the form
     *  Applies the necessary styles
     *  @param @param data the config
     * @return Spinner
     * */
    private fun createMultiSelectSelect(data: ConfigureDeviceData?, i: Int): View? {
        if (data?.options?.size ?: 0 > 0) {
            if (Constants.ITEM_TO_EXPAND < data?.options?.size ?: 0) {
                val view = inflateView(context, R.layout.form_multi_select)
                val textView = view.findViewById<TextView>(R.id.tv_error)
                textView.visibility = View.INVISIBLE
                val titleTextView = view.findViewById<TextView>(R.id.tv_title)
                titleTextView.text = data?.title
                val spinner = view.findViewById<MultiSpinner<Options>>(R.id.select)
                view.tag = data?.id
                spinner.defaultHintText = data?.title
                spinner.setItems(
                    getDataForMultiSpinner(data?.options),
                    object : MultiSpinnerListener {
                        override fun onItemsSelected(selected: List<Boolean>) {
                            multiSpinnerListener?.onItemsSelected(selected)
                            textView.text = ""
                            changeBgDrawable(spinner, false)
                        }
                    })
                if (i == 0) {
                    applyTopParams(view)
                } else {
                    applyParams(view)
                }
                return view
            } else {
                val view = inflateView(context, R.layout.form_rv)
                val recyclerView = view.findViewById<RecyclerView>(R.id.select)
                val textView = view.findViewById<TextView>(R.id.tv_error)
                val titleTextView = view.findViewById<TextView>(R.id.tv_title)
                titleTextView.text = data?.title
                val adapter = MultiSelectAdapter<Options>()
                adapter.listener = object : MultiSelectListener<Options> {
                    override fun onItemsSelected(item: Map<Int, Options>) {
                        textView.text = ""
                        changeBgColor(recyclerView, false)
                    }
                }
                adapter.setItems(data?.options)
                recyclerView.adapter = adapter
                recyclerView.layoutManager = GridLayoutManager(context, 2)
                if (i == 0) {
                    applyTopParams(view)
                } else {
                    applyParams(view)
                }
                view.tag = data?.id
                return view

            }
        } else {
            Timber.e("No options for %s form type with data: %s", data?.type, data)
            return null
        }
    }

    override fun validateMultiSelect(data: ConfigureDeviceData?): Boolean? {
        return presenter.validateMultiSelect(data)
    }

    override fun getMultiSelect(data: ConfigureDeviceData?): FormSelection? {
       return presenter.getMultiSelect(data)
    }

    private fun getMultiSelectIdList(items: MutableMap<Int, out SelectModel>): List<Int> {
        return items.keys.toList()

    }


    private fun multiSelectError(view: View) {
        val message = context.getString(R.string.select_at_least_one_item)
        listener?.onError(message)
        view.findViewById<TextView>(R.id.tv_error).text = message
        view.findViewById<TextView>(R.id.tv_error).visibility = View.VISIBLE
    }


    private fun createCheckBox(data: ConfigureDeviceData?, i: Int): View {
        val view = inflateView(context, R.layout.form_check_box)
        val textView = view.findViewById<TextView>(R.id.tv_error)
        textView.visibility = View.INVISIBLE
        val checkBox = view.findViewById<CheckBox>(R.id.check_box)
        view.tag = data?.id
        checkBox.text = data?.title
        checkBox.setOnCheckedChangeListener { buttonView, _ ->
            changeBgColor(buttonView, false)
            textView.text = ""
        }
        if (i == 0) {
            applyTopParams(view)
        } else {
            applyParams(view)
        }
        return view
    }

    override fun validateCheckBox(data: ConfigureDeviceData?): Boolean? {
       return presenter.validateCheckBox(data)
    }

    override fun getCheckBoxCheckedState(data: ConfigureDeviceData?): Boolean {
        val view = rootLayout.findViewWithTag<View>(data?.id)
        val checkBox = view.findViewById<CheckBox>(R.id.check_box)
        return checkBox.isChecked
    }

    override fun showCheckBoxError(data: ConfigureDeviceData?) {
        val view = rootLayout.findViewWithTag<View>(data?.id)
        val checkBox = view.findViewById<CheckBox>(R.id.check_box)
        val textView = view.findViewById<TextView>(R.id.tv_error)
        textView.visibility = View.VISIBLE
        val message = context.getString(R.string.field_required)
        textView.text = message
        changeBgColor(checkBox, true)
    }


    override fun getCheckBoxValue(data: ConfigureDeviceData?): Value {
       return presenter.getCheckBoxValue(data)
    }


    /**
     * Creates a TextView that functions as a label
     *  Applies the necessary styles
     * @return TextView
     * */
    private fun createErrorView(): View {
        val view = inflateView(context, R.layout.form_error)
        val textView = view.findViewById<TextView>(R.id.tv_config_message)
        textView.text = context.getString(R.string.form_error_msg)
        return view
    }


    private fun createBottomButtons(): View {
        val view = inflateView(context, R.layout.form_bottom)
        //set listeners on the buttons
        val saveButton = view.findViewById<Button>(R.id.btn_save)
        saveButton.setOnClickListener {
            it.setBackgroundResource(R.drawable.btn_bg_other)
            listener?.onClickSave()
        }
        val resetButton = view.findViewById<Button>(R.id.btn_reset)

        resetButton.setOnClickListener {
            listener?.onClickReset()
        }
        return view
    }

    override fun initializeData() {
        presenter.initializeValues()
        presenter.initializeOptions()
    }


    override fun bindOptionsDataToView(option: Option) {
        Timber.d("Options: %s", option)
        val view = rootLayout.findViewWithTag<View>(option.id)
        val select = view?.findViewById<View>(R.id.select)
        bindOptionsDataToView(select, option.selected)
    }


    override fun bindValuesDataToView(value: Value) {
        val root = rootLayout.findViewWithTag<View>(value.id)
        bindValuesDataToView(root, value.value)
    }


    private fun bindValuesDataToView(view: View?, data: String?) {
        when (view) {
            is TextInputLayout -> {
                view.findViewById<EditText>(R.id.edit).setText(data)
                view.findViewById<EditText>(R.id.edit).setSelection(data?.length ?: 0)
            }
            is TextView -> view.text = data

            is LinearLayout -> {
                val checkbox = view.findViewById<CheckBox>(R.id.check_box)
                val checked = try {
                    data?.toBoolean() ?: false
                } catch (e: Exception) {
                    Timber.e(e)
                    false
                }
                Timber.d("Checkbox initialization: %s", checked)
                checkbox?.isChecked = checked
            }
            else -> {
                Timber.d("Unknown view %s", view?.javaClass?.name)
            }
        }
    }


    private fun bindOptionsDataToView(view: View?, data: List<Int>?) {
        when (view) {
            is MultiSpinner<*> -> { //check this first, for multi select
                Timber.d("MultiSpinner initialization: %s", data?.size)
                view.setSelection(data)
            }

            is Spinner -> { //for single select
                Timber.d("Spinner initialization")
                if (data?.isEmpty() == false) { //there is data
                    Timber.d("Single select: data exists: %s", data.size)
                    val id = data[0]
                    val position = getSingleSelectPositionFromOptionsId(view, id)
                    view.setSelection(position)
                } else {
                    Timber.d("Data does not exist, selecting 0")
                    view.setSelection(0)
                }
            }
            is RecyclerView -> {
                Timber.d("RV initialization")
                if (view.adapter is BaseSelectAdapter<*, *>) {
                    ((view.adapter) as BaseSelectAdapter<*, *>).initSelectedItems(data)
                } else {
                    Timber.d("Unknown adapter %s", view.adapter)
                }
            }
            else -> {
                Timber.d("Unknown view %s", view?.javaClass?.name)
            }
        }
    }

    private fun getSingleSelectPositionFromOptionsId(view: Spinner, id: Int): Int {
        val adapter = view.adapter as FormSelectAdapter
        for (position in 0 until adapter.count) {
            val option = adapter.getItem(position)
            if (option.id == id) {
                return position
            }
        }

        return 0
    }
}



