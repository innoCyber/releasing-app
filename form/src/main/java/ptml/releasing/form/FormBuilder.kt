package ptml.releasing.form


import android.content.Context
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.*
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.textfield.TextInputLayout
import ptml.releasing.form.FormUtils.applyBottomParams
import ptml.releasing.form.FormUtils.applyLabelParams
import ptml.releasing.form.FormUtils.applyParams
import ptml.releasing.form.FormUtils.applySimpleTextParams
import ptml.releasing.form.FormUtils.applyTintToBackground
import ptml.releasing.form.FormUtils.applyTopParams
import ptml.releasing.form.FormUtils.changeBgColor
import ptml.releasing.form.FormUtils.changeBgDrawable
import ptml.releasing.form.FormUtils.getButtonValidationErrorMessage
import ptml.releasing.form.FormUtils.getDataForMultiSpinner
import ptml.releasing.form.FormUtils.getImageResourceByType
import ptml.releasing.form.FormUtils.getQuickRemarksDataForMultiSpinner
import ptml.releasing.form.FormUtils.inflateView
import ptml.releasing.form.adapter.*
import ptml.releasing.form.base.BuilderPresenter
import ptml.releasing.form.base.BuilderView
import ptml.releasing.form.models.*
import ptml.releasing.form.utils.Constants
import ptml.releasing.form.utils.setValidation
import ptml.releasing.form.views.MultiSpinner
import ptml.releasing.form.views.MultiSpinnerListener
import timber.log.Timber
import java.util.*
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.text.InputType


class FormBuilder constructor(val context: Context) : BuilderView {
    private var quickRemarks: Map<Int, QuickRemark>? = null
    private var voyages: Map<Int, Voyage>? = null
    var data: List<FormConfiguration>? = null
    var damageData: List<FormDamage>? = null
    private val rootLayout = LinearLayout(context)
    private var formListener: FormListener? = null
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
        this.formListener = listener
        return this
    }


    /**
     * Initializes the form with pre-filled data
     * @param findCargoResponse
     * the object with the pre-filled data, referenced by the form id
     * */
    override fun init(findCargoResponse: FormPreFillResponse?): BuilderView {
        return presenter.init(findCargoResponse)
    }

    override fun reset() {
        initializeData()
    }


    override fun getBottomButtons(): View? {
        if (rootLayout.childCount > 0) {
            val bottom = createBottomButtons()
            applyBottomParams(bottom)
            return bottom
        }
        return null
    }


    /*override fun buildDamage(
        damageDataList:List<FormDamage>


    ): LinearLayout{
        this.damageData = damageDataList

        for (i in 0 until (data?.size ?: mutableListOf<FormConfiguration>().size)) {
            //create and add the view gotten based on the config
            val configureDeviceData = data?.get(i)

            val formView = createViewFromConfig(configureDeviceData, i)
         //   formListener?.onFormAdded(configureDeviceData)
            if (formView != null) {
                rootLayout.addView(formView)
            } else {
                Timber.e("Form view is null")
            }

        }
*/
    //return rootLayout
    //}

    override fun build(

        configDataList: List<FormConfiguration>?,
        remarks: Map<Int, QuickRemark>?,
        voyages: Map<Int, Voyage>?
    ): LinearLayout {
        //sort the list based on the position
        formListener?.onStartLoad()
        Timber.d("Config list: %s", configDataList)
        this.data = configDataList
        quickRemarks = remarks
        this.voyages = voyages
        presenter.initializeQuickRemarks(quickRemarks)
        presenter.initializeVoyages(voyages)

        val positionComparator =
            Comparator<FormConfiguration> { o1, o2 -> o1.position.compareTo(o2.position) }
        Collections.sort(data ?: mutableListOf(), positionComparator)
        data = (data ?: mutableListOf()).categorizeView()

        //iterate over the list to get each config
        for (i in 0 until (data?.size ?: mutableListOf<FormConfiguration>().size)) {
            //create and add the view gotten based on the config

            val configureDeviceData = data?.get(i)

            val formView = createViewFromConfig(configureDeviceData, i)
            formListener?.onFormAdded(configureDeviceData)
            if (formView != null) {
                rootLayout.addView(formView)

            } else {
                Timber.e("Form view is null")
            }
        }

        if (rootLayout.childCount <= 0) { //show an url view if no view had been added
            error = true
            rootLayout.addView(createErrorView())
        }

//        initializeData()
        formListener?.onEndLoad()
        return rootLayout
    }


    fun List<FormConfiguration>.categorizeView(): List<FormConfiguration> {
        val labels = mutableListOf<FormConfiguration>()
        val textboxes = mutableListOf<FormConfiguration>()
        val multilineTextboxs = mutableListOf<FormConfiguration>()
        val images = mutableListOf<FormConfiguration>()
        val printers = mutableListOf<FormConfiguration>()
        val printerDamages = mutableListOf<FormConfiguration>()
        val damages = mutableListOf<FormConfiguration>()
        val singleSelect = mutableListOf<FormConfiguration>()
        val multiSelects = mutableListOf<FormConfiguration>()
        val quickRemarks = mutableListOf<FormConfiguration>()
        val checkboxes = mutableListOf<FormConfiguration>()
        val simpleTexts = mutableListOf<FormConfiguration>()
        val voyages = mutableListOf<FormConfiguration>()
        val unknowns = mutableListOf<FormConfiguration>()
        forEach {
            when (FormType.fromType(it?.type)) {
                FormType.LABEL -> {
                    labels.add(it)
                }

                FormType.TEXTBOX -> {
                    textboxes.add(it)
                }

                FormType.MULTI_LINE_TEXTBOX -> {
                    multilineTextboxs.add(it)
                }
                FormType.IMAGES -> {
                    images.add(it)
                }

                FormType.PRINTER -> {
                    printers.add(it)
                }

                FormType.PRINTER_DAMAGES -> {
                    printerDamages.add(it)
                }


                FormType.DAMAGES -> {
                    damages.add(it)
                }


                FormType.SINGLE_SELECT -> {
                    singleSelect.add(it)
                }


                FormType.MULTI_SELECT -> {
                    multiSelects.add(it)
                }

                FormType.QUICK_REMARK -> {
                    quickRemarks.add(it)
                }


                FormType.CHECK_BOX -> {
                    checkboxes.add(it)
                }

                //create simple text
                FormType.SIMPLE_TEXT -> {
                    simpleTexts.add(it)
                }

                FormType.VOYAGE -> {
                    voyages.add(it)
                }

                else -> {
                    unknowns.add(it)
                }
            }
        }
        return simpleTexts + labels + voyages + checkboxes + singleSelect + textboxes +
                multilineTextboxs + damages + printerDamages + printers + multiSelects + quickRemarks +
                images + unknowns
    }


    override fun createViewFromConfig(data: FormConfiguration?, i: Int): View? {
        try {
            when (FormType.fromType(data?.type)) {
                FormType.LABEL -> {
                    presenter.initializeDefaultValue(data)
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
                    return createButton(data, i)
                }

                FormType.PRINTER -> {
                    return createButton(data, i)
                }

                FormType.PRINTER_DAMAGES -> {
                    return createButton(data, i)
                }


                FormType.DAMAGES -> {
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

                //create simple text
                FormType.SIMPLE_TEXT -> {
                    presenter.initializeDefaultValue(data)
                    return createSimpleText(data, i)
                }

                FormType.VOYAGE -> {
                    presenter.initializeDefaultOption(data)
                    return createVoyageForm(data, i)
                }

                else -> {
                    Timber.e("Could not find form type for %s", data?.type)
                    return null
                }
            }
        } catch (e: Exception) { //return an url view
            Timber.e(e, "Could not create form for %s", data?.type)
            return null
        }
    }

    /**
     * Creates a TextView that functions as a label
     *  @param data the config
     * @return TextView
     * */
    private fun createLabel(data: FormConfiguration?, i: Int): LinearLayout {
        val linearLayout = inflateView(context, R.layout.form_label) as LinearLayout
        val label = linearLayout.findViewById<TextView>(R.id.label)
        val title = linearLayout.findViewById<TextView>(R.id.title)
        title.tag = data?.id
        label.text = data?.title
//        if (i == 0) {
//            applyTopParams(linearLayout)
//        } else {
//            applyLabelParams(linearLayout)
//        }
        return linearLayout
    }

    private fun createSimpleText(data: FormConfiguration?, position: Int): TextView {
        val textView = inflateView(context, R.layout.form_simple_text) as TextView
        textView.tag = data?.id
        textView.text = data?.title
        if (position == 0) {
            applyTopParams(textView)
        } else {
            applySimpleTextParams(textView)
        }
        return textView
    }


    /**
     * Creates a regular edit text for the form
     *  @param @param data the config
     * @return EditText
     * */
    private fun createTextBox(data: FormConfiguration?, i: Int): TextInputLayout {
        val inputLayout = inflateView(context, R.layout.form_textbox) as TextInputLayout
        inputLayout.tag = data?.id
        val editText = inputLayout.findViewById<EditText>(R.id.edit)
        editText.hint = data?.title
        editText.setValidation(data?.dataValidation)
        editText.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(text: Editable?) {
                formListener?.onDataChange(data, text?.toString())
                inputLayout.error = ""
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(text: CharSequence?, start: Int, before: Int, count: Int) {
            }
        })
        if (i == 0) {
            applyTopParams(inputLayout)
        } else {
            applyParams(inputLayout)
        }
        return inputLayout
    }

    override fun validateTextBox(data: FormConfiguration?): Boolean {
        return presenter.validateTextBox(data)
    }


    override fun showTextBoxError(data: FormConfiguration?) {
        val inputLayout = rootLayout.findViewWithTag<TextInputLayout>(data?.id)
        inputLayout.error = context.getString(R.string.required_field_msg)
    }

    override fun getTextBoxText(data: FormConfiguration?): String {
        val inputLayout = rootLayout.findViewWithTag<TextInputLayout>(data?.id)
        val editText = inputLayout.findViewById<EditText>(R.id.edit)
        return editText.text.trim().toString()
    }

    override fun getTextBoxValue(data: FormConfiguration?): Value? {
        return presenter.getTextBoxValue(data)
    }


    /**
     * Creates a multiline edit text for the form
     *  Applies the necessary styles
     *  @param @param data the config
     * @return EditText
     * */
    private fun createMultilineTextBox(data: FormConfiguration?, i: Int): TextInputLayout {
        val inputLayout = inflateView(context, R.layout.form_textbox_multiline) as TextInputLayout
        inputLayout.tag = data?.id
        val editText = inputLayout.findViewById<EditText>(R.id.edit)
        editText.hint = data?.title
        //editText.setValidation(data?.dataValidation)
        editText.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(text: Editable?) {
                formListener?.onDataChange(data, text?.toString())
                inputLayout.error = ""
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(text: CharSequence?, start: Int, before: Int, count: Int) {

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
    private fun createButton(data: FormConfiguration?, i: Int): View {
        val view = inflateView(context, R.layout.form_button_layout)
        val rootView = view.findViewById<View>(R.id.button_root);
        val titleView = view.findViewById<TextView>(R.id.tv_title)
        val numberView = view.findViewById<TextView>(R.id.tv_number)
        val imageView = view.findViewById<ImageView>(R.id.img)
        numberView.visibility =
            if (data?.type == FormType.PRINTER.type || data?.type == FormType.PRINTER_DAMAGES.type) View.INVISIBLE else View.VISIBLE
        //   if(data?.type == FormType.PRINTER_DAMAGES.type) View.INVISIBLE else View.VISIBLE

        titleView.text = data?.title
        imageView.setImageResource(getImageResourceByType(data?.type))
        view.tag = data?.id
        rootView.setOnClickListener {
            formListener?.onClickFormButton(FormType.fromType(data?.type), it)
        }
        view.findViewById<TextView>(R.id.tv_error).visibility = View.INVISIBLE
        if (i == 0) {
            applyTopParams(view)
        } else {
            applyParams(view)
        }
        return view
    }


    override fun validateButton(data: FormConfiguration?): Boolean? {
        return presenter.validateButton(data)
    }

    override fun getButtonNumber(data: FormConfiguration?): String {
        if (data?.type != FormType.PRINTER.type) {

            val view = rootLayout.findViewWithTag<ViewGroup>(data?.id)
            Timber.d("Getting url text view for buttons ")
            val numberView = view.findViewById<TextView>(R.id.tv_number)
            return numberView.text.toString()
        }

        return "0"
    }

    override fun showButtonError(data: FormConfiguration?) {
        val view = rootLayout.findViewWithTag<ViewGroup>(data?.id)
        val message = context.getString(getButtonValidationErrorMessage(data?.type))
        formListener?.onError(message)
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
    private fun createSingleSelect(formConfiguration: FormConfiguration?, i: Int): View? {
        if (formConfiguration?.options?.size ?: 0 > 0) {
            if (Constants.ITEM_TO_EXPAND < formConfiguration?.options?.size ?: 0) {
                val view = inflateView(context, R.layout.form_single_select)
                view.findViewById<TextView>(R.id.tv_error).visibility = View.INVISIBLE
                val spinner = view.findViewById<Spinner>(R.id.select)
                val titleTextView = view.findViewById<TextView>(R.id.tv_title)
                titleTextView.text = formConfiguration?.title
                view.tag = formConfiguration?.id
                //add the items
                val adapter = FormSelectAdapter(context, formConfiguration?.options)
                spinner.adapter = adapter
                spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                    override fun onNothingSelected(p0: AdapterView<*>?) {}

                    override fun onItemSelected(
                        parent: AdapterView<*>?,
                        view: View?,
                        position: Int,
                        id: Long
                    ) {
                        formListener?.onDataChange(
                            formConfiguration,
                            listOf((parent?.getItemAtPosition(position) as Options).id)
                        )
                    }
                }
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
                titleTextView.text = formConfiguration?.title
                val adapter = SingleSelectAdapter<Options>()
                adapter.setItems(formConfiguration?.options)
                adapter.listener = object : SingleSelectListener<Options> {
                    override fun onItemSelected(item: Options?) {
                        formListener?.onDataChange(formConfiguration, listOf(item?.id))
                        errorTextView.text = ""
                        changeBgColor(recyclerView, false)
                    }
                }
                errorTextView.visibility = View.INVISIBLE
                recyclerView.adapter = adapter
                recyclerView.layoutManager =
                    LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
                view.tag = formConfiguration?.id
                if (i == 0) {
                    applyTopParams(view)
                } else {
                    applyParams(view)
                }
                return view
            }
        } else {
            Timber.e(
                "No options for %s form type with data: %s",
                formConfiguration?.type,
                formConfiguration
            )
            return null
        }
    }

    override fun validateSingleSelect(data: FormConfiguration?): Boolean? {
        return presenter.validateSingleSelect(data)
    }

    @Suppress("UNCHECKED_CAST")
    override fun getSingleSelectRVItem(data: FormConfiguration?): Options? {
        val view = rootLayout.findViewWithTag<View>(data?.id)
        val recyclerView = view.findViewById<RecyclerView>(R.id.select)
        val adapter = recyclerView.adapter as SingleSelectAdapter<Options>
        return adapter.selectedItem
    }

    override fun getSingleSelectSpinnerItem(data: FormConfiguration?): Options? {
        val view = rootLayout.findViewWithTag<View>(data?.id)
        val spinner = view.findViewById<Spinner>(R.id.select)
        val adapter = spinner.adapter as FormSelectAdapter<*>
        val selectedPosition = spinner.selectedItemPosition
        return if (adapter.count > selectedPosition) {
            spinner.getItemAtPosition(selectedPosition) as Options?
        } else {
            null
        }
    }

    override fun showSingleSelectError(data: FormConfiguration?) {
        if (Constants.ITEM_TO_EXPAND >= data?.options?.size ?: 0) {
            val view = rootLayout.findViewWithTag<View>(data?.id)
            val recyclerView = view.findViewById<RecyclerView>(R.id.select)
            val message = context.getString(R.string.select_one_item)
            formListener?.onError(message)
            val textView = view.findViewById<TextView>(R.id.tv_error)
            textView.text = message
            textView.visibility = View.VISIBLE
            changeBgColor(recyclerView, true)
        } else {
            // NO need to show for spinner
            Timber.w("No need to validate a spinner")
        }
    }

    override fun showVoyageError(data: FormConfiguration?) {
        if (Constants.ITEM_TO_EXPAND >= voyages?.size ?: 0) {
            val view = rootLayout.findViewWithTag<View>(data?.id)
            val recyclerView = view.findViewById<RecyclerView>(R.id.select)
            val message = context.getString(R.string.select_one_item)
            formListener?.onError(message)
            val textView = view.findViewById<TextView>(R.id.tv_error)
            textView.text = message
            textView.visibility = View.VISIBLE
            changeBgColor(recyclerView, true)
        } else {
            // NO need to show for spinner
            Timber.w("No need to validate a spinner voyage")
        }
    }


    override fun getSingleSelect(data: FormConfiguration?): FormSelection? {
        return presenter.getSingleSelect(data)
    }

    private fun createQuickRemarkSelect(data: FormConfiguration?, i: Int): View? {

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
                            formListener?.onDataChange(data, spinner.selectedItems.keys.toList())
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
                        formListener?.onDataChange(data, item.values.map {
                            it.id
                        })
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

    private fun createVoyageForm(formConfiguration: FormConfiguration?, i: Int): View? {
        Timber.d("VOYAGES:  $voyages --- VOYAGE_SIZE = ${voyages?.size}")
        if (voyages?.isNotEmpty() == true) {
            Timber.d("Not empty")
            if (voyages?.size ?: 0 > Constants.ITEM_TO_EXPAND) {
                Timber.d("Spinner voyage")
                val view = inflateView(context, R.layout.form_single_select)
                view.findViewById<TextView>(R.id.tv_error).visibility = View.INVISIBLE
                val spinner = view.findViewById<Spinner>(R.id.select)
                val titleTextView = view.findViewById<TextView>(R.id.tv_title)
                titleTextView.text = formConfiguration?.title
                view.tag = formConfiguration?.id
                //add the items
                val adapter = FormSelectAdapter(context, voyages?.values?.toList())
                spinner.adapter = adapter
                spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                    override fun onNothingSelected(p0: AdapterView<*>?) {

                    }

                    override fun onItemSelected(
                        parent: AdapterView<*>?,
                        view: View?,
                        position: Int,
                        id: Long
                    ) {
                        formListener?.onDataChange(
                            formConfiguration,
                            parent?.getItemAtPosition(position) as Voyage
                        )
                    }
                }
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
                titleTextView.text = formConfiguration?.title
                val adapter = SingleSelectAdapter<Voyage>()
                adapter.setItems(voyages?.values?.toList())
                adapter.listener = object : SingleSelectListener<Voyage> {
                    override fun onItemSelected(item: Voyage?) {
                        formListener?.onDataChange(formConfiguration, item)
                        errorTextView.text = ""
                        changeBgColor(recyclerView, false)
                    }
                }
                errorTextView.visibility = View.INVISIBLE
                recyclerView.adapter = adapter
                recyclerView.layoutManager = GridLayoutManager(context, 2)
                view.tag = formConfiguration?.id
                if (i == 0) {
                    applyTopParams(view)
                } else {
                    applyParams(view)
                }
                return view
            }
        } else {
            Timber.e(
                "No options for %s form type with data: %s",
                formConfiguration?.type,
                formConfiguration
            )
            return null
        }
    }


    override fun showMultiSelectError(data: FormConfiguration?) {
        val view = rootLayout.findViewWithTag<View>(data?.id)
        val selectView = view.findViewById<View>(R.id.select)
        multiSelectError(view)
        changeBgDrawable(selectView, true)
    }

    override fun validateQuickRemarkSelect(data: FormConfiguration?): Boolean? {
        return presenter.validateQuickRemarkSelect(data)
    }


    override fun getQuickRemarkSelect(data: FormConfiguration?): FormSelection? {
        return presenter.getQuickRemarkSelect(data)
    }

    override fun getVoyagesSelect(data: FormConfiguration?): Voyage? {
        return presenter.getVoyagesSelect(data)
    }

    override fun getVoyageSelectRVItem(data: FormConfiguration?): Voyage? {
        val view = rootLayout.findViewWithTag<View>(data?.id)
        val recyclerView = view.findViewById<RecyclerView>(R.id.select)
        val adapter = recyclerView.adapter as SingleSelectAdapter<Voyage>
        return adapter.selectedItem
    }

    override fun getVoyageSelectSpinnerItem(data: FormConfiguration?): Voyage? {
        val view = rootLayout.findViewWithTag<View>(data?.id)
        val spinner = view.findViewById<Spinner>(R.id.select)
        val adapter = spinner.adapter as FormSelectAdapter<Voyage>
        val selectedPosition = spinner.selectedItemPosition
        return if (adapter.count > selectedPosition) {
            spinner.getItemAtPosition(selectedPosition) as Voyage
        } else {
            null
        }
    }

    override fun validateVoyagesSelect(data: FormConfiguration?): Boolean? {
        return presenter.validateVoyagesSelect(data)
    }

    override fun getMultiSelectRVItems(data: FormConfiguration?): List<Int> {
        val view = rootLayout.findViewWithTag<View>(data?.id)
        val recyclerView = view.findViewById<RecyclerView>(R.id.select)
        val adapter = recyclerView.adapter as MultiSelectAdapter<*>
        return getMultiSelectIdList(adapter.selectedItems)
    }


    override fun getMultiSelectSpinnerItems(data: FormConfiguration?): List<Int> {
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
    private fun createMultiSelectSelect(data: FormConfiguration?, i: Int): View? {
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
                            formListener?.onDataChange(data, spinner.selectedItems.keys.toList())
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
                        formListener?.onDataChange(data, item.values.map {
                            it.id
                        })
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

    override fun validateMultiSelect(data: FormConfiguration?): Boolean? {
        return presenter.validateMultiSelect(data)
    }

    override fun getMultiSelect(data: FormConfiguration?): FormSelection? {
        return presenter.getMultiSelect(data)
    }

    private fun getMultiSelectIdList(items: MutableMap<Int, out SelectModel>): List<Int> {
        return items.keys.toList()

    }


    private fun multiSelectError(view: View) {
        val message = context.getString(R.string.select_at_least_one_item)
        formListener?.onError(message)
        view.findViewById<TextView>(R.id.tv_error).text = message
        view.findViewById<TextView>(R.id.tv_error).visibility = View.VISIBLE
    }


    private fun createCheckBox(data: FormConfiguration?, i: Int): View {
        val view = inflateView(context, R.layout.form_check_box)
        val textView = view.findViewById<TextView>(R.id.tv_error)
        textView.visibility = View.INVISIBLE
        val checkBox = view.findViewById<CheckBox>(R.id.check_box)
        view.tag = data?.id
        checkBox.text = data?.title
        checkBox.setOnCheckedChangeListener { buttonView, checked ->
            formListener?.onDataChange(data, checked.toString())
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

    override fun validateCheckBox(data: FormConfiguration?): Boolean? {
        return presenter.validateCheckBox(data)
    }

    override fun getCheckBoxCheckedState(data: FormConfiguration?): Boolean {
        val view = rootLayout.findViewWithTag<View>(data?.id)
        val checkBox = view.findViewById<CheckBox>(R.id.check_box)
        return checkBox.isChecked
    }

    override fun showCheckBoxError(data: FormConfiguration?) {
        val view = rootLayout.findViewWithTag<View>(data?.id)
        val checkBox = view.findViewById<CheckBox>(R.id.check_box)
        val textView = view.findViewById<TextView>(R.id.tv_error)
        textView.visibility = View.VISIBLE
        val message = context.getString(R.string.field_required)
        textView.text = message
        changeBgColor(checkBox, true)
    }


    override fun getCheckBoxValue(data: FormConfiguration?): Value {
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
            applyTintToBackground(saveButton, ContextCompat.getColor(context, R.color.colorOther))
            formListener?.onClickSave()
        }
        val resetButton = view.findViewById<Button>(R.id.btn_reset)

        resetButton.setOnClickListener {
            formListener?.onClickReset()
        }
        return view
    }

    fun addInfoView(view: View) {
        val root = rootLayout.findViewById<ViewGroup>(R.id.extra_info)
        if (root == null) {
            Timber.e("ROOT is null")

        }
        root?.addView(view)
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
            is TextView -> data?.apply { if (isNotEmpty()) view.text = this }

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
        val adapter = view.adapter as FormSelectAdapter<*>
        for (position in 0 until adapter.count) {
            val option = adapter.getItem(position)
            if (option?.id() == id) {
                return position
            }
        }

        return 0
    }
}



