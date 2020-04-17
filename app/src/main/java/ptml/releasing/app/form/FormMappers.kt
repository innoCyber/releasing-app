package ptml.releasing.app.form

import ptml.releasing.app.data.domain.model.voyage.ReleasingVoyage
import ptml.releasing.cargo_info.model.ReleasingFormSelection
import ptml.releasing.cargo_search.model.FindCargoResponse
import ptml.releasing.cargo_search.model.FormOption
import ptml.releasing.cargo_search.model.FormValue
import ptml.releasing.configuration.models.*
import ptml.releasing.form.FormModelMapper
import ptml.releasing.form.models.*
import ptml.releasing.quick_remarks.model.ReleasingQuickRemark
import ptml.releasing.form.models.OperationStep as FormOperationStep
import ptml.releasing.form.models.Options as FormOptions
import ptml.releasing.form.models.Terminal as FormTerminal

/**
 * Created by kryptkode on 11/12/2019.
 */

data class FormMappers(
    val configureDeviceMapper: ConfigureDeviceMapper,
    val formPrefillMapper: FormPreFillModelMapper,
    val formDataMapper: FormDataMapper,
    val operationTypeMapper: OperationTypeMapper,
    val terminalMapper: TerminalMapper,
    val formSelectionMapper: FormSelectionMapper,
    val formValueMapper: FormValueMapper,
    val quickRemarkMapper: QuickRemarkMapper,
    val voyagesMapper: VoyagesMapper
)

//TODO: Clean up the data classes for form
class ConfigureDeviceMapper(private val formDataMapper: FormDataMapper) :
    FormModelMapper<ConfigureDeviceResponse, FormConfigureDeviceResponse> {

    override fun mapFromModel(model: ConfigureDeviceResponse): FormConfigureDeviceResponse {
        val response =
            FormConfigureDeviceResponse(model.data.map { formDataMapper.mapFromModel(it) })
        response.isSuccess = model.isSuccess
        response.message = model.message
        return response
    }

    override fun mapToModel(model: FormConfigureDeviceResponse): ConfigureDeviceResponse {
        val response =
            ConfigureDeviceResponse(model.data.map { formDataMapper.mapToModel(it) })
        response.isSuccess = model.isSuccess
        response.message = model.message
        return response
    }
}

class FormDataMapper(private val optionMapper: FormOptionMapper) :
    FormModelMapper<ReleasingConfigureDeviceData, FormConfiguration> {
    override fun mapFromModel(model: ReleasingConfigureDeviceData): FormConfiguration {
        val response = FormConfiguration(
            model.position,
            model.type,
            model.title,
            model.required,
            model.editable,
            model.options.map { optionMapper.mapFromModel(it) },
            model.dataValidation
        )
        response.id = model.id
        return response
    }

    override fun mapToModel(model: FormConfiguration): ReleasingConfigureDeviceData {
        val response = ReleasingConfigureDeviceData(
            model.position,
            model.type,
            model.title,
            model.required,
            model.editable,
            model.options?.map { optionMapper.mapToModel(it) } ?: listOf(),
            model.dataValidation
        )
        response.id = model.id

        return response
    }
}

class FormOptionMapper : FormModelMapper<ReleasingOptions, FormOptions> {
    override fun mapFromModel(model: ReleasingOptions): FormOptions {
        val formOption = FormOptions(model.name, model.isSelected)
        formOption.id = model.id
        return formOption
    }

    override fun mapToModel(model: FormOptions): ReleasingOptions {
        val formOption = ReleasingOptions(model.name, model.isSelected)
        formOption.id = model.id
        return formOption
    }
}

class TerminalMapper : FormModelMapper<ReleasingTerminal, FormTerminal> {

    override fun mapFromModel(model: ReleasingTerminal): FormTerminal {
        val terminal = FormTerminal(0)
        terminal.id = model.id
        terminal.value = model.value
        return terminal
    }

    override fun mapToModel(model: FormTerminal): ReleasingTerminal {
        val terminal = ReleasingTerminal(0)
        terminal.id = model.id
        terminal.value = model.value
        return terminal
    }
}

class OperationTypeMapper : FormModelMapper<ReleasingOperationStep, FormOperationStep> {

    override fun mapFromModel(model: ReleasingOperationStep): FormOperationStep {
        val operationStep = FormOperationStep(0)
        operationStep.id = model.id
        operationStep.value = model.value
        return operationStep
    }

    override fun mapToModel(model: FormOperationStep): ReleasingOperationStep {
        val operationStep = ReleasingOperationStep(0)
        operationStep.id = model.id
        operationStep.value = model.value
        return operationStep
    }
}

class FormPreFillModelMapper(
    private val formPrefillValueMapper: FormPreFillValueModelMapper,
    private val formPrefillOptionMapper: FormPreFillOptionModelMapper
) : FormModelMapper<FindCargoResponse, FormPreFillResponse> {

    override fun mapFromModel(model: FindCargoResponse): FormPreFillResponse {
        return FormPreFillResponse(
            model.cargoId,
            0,
            model.barcode,
            model.values?.map { formPrefillValueMapper.mapFromModel(it) },
            model.options?.map { formPrefillOptionMapper.mapFromModel(it) })
    }


    override fun mapToModel(model: FormPreFillResponse): FindCargoResponse {
        return FindCargoResponse(
            model.cargoId,
            0,
            model.barcode,
            model.values?.map { formPrefillValueMapper.mapToModel(it) },
            model.options?.map { formPrefillOptionMapper.mapToModel(it) })
    }
}

class FormPreFillValueModelMapper : FormModelMapper<FormValue, Value> {
    override fun mapFromModel(model: FormValue): Value {
        val value = Value(model.value)
        value.id = model.id
        return value
    }

    override fun mapToModel(model: Value): FormValue {
        val value = FormValue(model.value)
        value.id = model.id
        return value
    }
}

class FormPreFillOptionModelMapper : FormModelMapper<FormOption, Option> {
    override fun mapFromModel(model: FormOption): Option {
        val option = Option(model.selected)
        option.id = model.id
        return option
    }

    override fun mapToModel(model: Option): FormOption {
        val option = FormOption(model.selected)
        option.id = model.id
        return option
    }
}

class FormSelectionMapper :
    FormModelMapper<ReleasingFormSelection, FormSelection> {

    override fun mapFromModel(model: ReleasingFormSelection): FormSelection {
        val formSelection = FormSelection(model.selectedOptions)
        formSelection.id = model.id
        return formSelection
    }

    override fun mapToModel(model: FormSelection): ReleasingFormSelection {
        val formSelection = ReleasingFormSelection(model.selectedOptions)
        formSelection.id = model.id
        return formSelection
    }
}


class FormValueMapper :
    FormModelMapper<FormValue, Value> {

    override fun mapFromModel(model: FormValue): Value {
        val value = Value(model.value)
        value.id = model.id
        return value
    }

    override fun mapToModel(model: Value): FormValue {
        val value = FormValue(model.value)
        value.id = model.id
        return value
    }
}

class QuickRemarkMapper : FormModelMapper<ReleasingQuickRemark, QuickRemark> {
    override fun mapFromModel(model: ReleasingQuickRemark): QuickRemark {
        val quickRemark = QuickRemark(model.name)
        quickRemark.id = model.id
        return quickRemark
    }

    override fun mapToModel(model: QuickRemark): ReleasingQuickRemark {
        val quickRemark = ReleasingQuickRemark(model.name)
        quickRemark.id = model.id
        return quickRemark
    }
}

class VoyagesMapper : FormModelMapper<ReleasingVoyage, Voyage> {
    override fun mapFromModel(model: ReleasingVoyage): Voyage {
        return Voyage(model.voyageNumber)
    }

    override fun mapToModel(model: Voyage): ReleasingVoyage {
        return ReleasingVoyage(model.voyageNumber)
    }
}
