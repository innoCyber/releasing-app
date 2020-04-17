package ptml.releasing.app.di.modules.form

import dagger.Module
import dagger.Provides
import ptml.releasing.app.form.*

/**
 * Created by kryptkode on 4/7/2020.
 */
@Module
class FormMapperModule {

    @Provides
    fun provideFormValueMapper(): FormValueMapper {
        return FormValueMapper()
    }

    @Provides
    fun provideSelectionMapper(): FormSelectionMapper {
        return FormSelectionMapper()
    }

    @Provides
    fun provideFormPreFillOptionModelMapper(): FormPreFillOptionModelMapper {
        return FormPreFillOptionModelMapper()
    }

    @Provides
    fun provideFormPreFillValueModelMapper(): FormPreFillValueModelMapper {
        return FormPreFillValueModelMapper()
    }

    @Provides
    fun provideFormPreFillModelMapper(
        eMapper: FormPreFillValueModelMapper,
        optionMapper: FormPreFillOptionModelMapper
    ): FormPreFillModelMapper {
        return FormPreFillModelMapper(eMapper, optionMapper)
    }

    @Provides
    fun provideOperationTypeMapper(): OperationTypeMapper {
        return OperationTypeMapper()
    }

    @Provides
    fun provideTerminalMapper(): TerminalMapper {
        return TerminalMapper()
    }

    @Provides
    fun provideFormOptionMapper(): FormOptionMapper {
        return FormOptionMapper()
    }

    @Provides
    fun provideFormDataMapper(formOptionMapper: FormOptionMapper): FormDataMapper {
        return FormDataMapper(formOptionMapper)
    }

    @Provides
    fun provideConfigureDeviceMapper(formDataMapper: FormDataMapper): ConfigureDeviceMapper {
        return ConfigureDeviceMapper(formDataMapper)
    }

    @Provides
    fun provideQuickRemarkMapper(): QuickRemarkMapper {
        return QuickRemarkMapper()
    }

    @Provides
    fun provideFormMappers(
        configureDeviceMapper: ConfigureDeviceMapper,
        formPrefillMapper: FormPreFillModelMapper,
        formDataMapper: FormDataMapper,
        operationTypeMapper: OperationTypeMapper,
        terminalMapper: TerminalMapper,
        formSelectionMapper: FormSelectionMapper,
        formValueMapper: FormValueMapper,
        quickRemarkMapper: QuickRemarkMapper
    ): FormMappers {
        return FormMappers(
            configureDeviceMapper,
            formPrefillMapper,
            formDataMapper,
            operationTypeMapper,
            terminalMapper,
            formSelectionMapper,
            formValueMapper,
            quickRemarkMapper
        )
    }
}