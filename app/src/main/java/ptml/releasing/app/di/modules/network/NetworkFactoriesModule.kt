package ptml.releasing.app.di.modules.network

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.jakewharton.retrofit2.adapter.kotlin.coroutines.CoroutineCallAdapterFactory
import ptml.releasing.app.di.scopes.ReleasingAppScope
import dagger.Module
import dagger.Provides
import ptml.releasing.admin_configuration.models.*
import retrofit2.converter.gson.GsonConverterFactory

@Module
class NetworkFactoriesModule {

    @Provides
    @ReleasingAppScope
    fun provideGsonConverterFactory(gson: Gson): GsonConverterFactory {
        return GsonConverterFactory.create(gson)
    }

    @Provides
    @ReleasingAppScope
    fun provideCoroutineAdapterFactory(): CoroutineCallAdapterFactory {
        return CoroutineCallAdapterFactory()
    }


    @Provides
    @ReleasingAppScope
    fun provideGson(): Gson{
        val builder = GsonBuilder()
        builder.registerTypeAdapter(
            CargoType::class.java,
            CargoTypeSerializer()
        )
        builder.registerTypeAdapter(
            CargoType::class.java,
            CargoTypeDeserializer()
        )
        builder.registerTypeAdapter(
            Terminal::class.java,
            TerminalSerializer()
        )
        builder.registerTypeAdapter(
            Terminal::class.java,
            TerminalDeserializer()
        )
        builder.registerTypeAdapter(
            OperationStep::class.java,
            OperationStepSerializer()
        )
        builder.registerTypeAdapter(
            OperationStep::class.java,
            OperationStepDeserializer()
        )
        return builder.create()
    }
}