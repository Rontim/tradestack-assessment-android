package io.ibuqa.tradestack.collections.di

import android.content.Context
import androidx.room.Room
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import io.ibuqa.tradestack.collections.BuildConfig
import io.ibuqa.tradestack.collections.data.AppDatabase
import io.ibuqa.tradestack.collections.data.CollectionDao
import io.ibuqa.tradestack.collections.data.CollectionsApi
import java.util.concurrent.TimeUnit
import javax.inject.Singleton
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import java.util.UUID
import javax.inject.Named
import androidx.core.content.edit

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun database(@ApplicationContext ctx: Context): AppDatabase =
        Room.databaseBuilder(ctx, AppDatabase::class.java, "collections.db")
            .fallbackToDestructiveMigration()
            .build()

    @Provides
    fun dao(db: AppDatabase): CollectionDao = db.collections()

    @Provides
    @Singleton
    fun http(): OkHttpClient = OkHttpClient.Builder()
        .addInterceptor(HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BASIC
        })
        // The stub server is slow on purpose. These numbers are a starting
        // point, not a recommendation.
        .connectTimeout(10, TimeUnit.SECONDS)
        .readTimeout(10, TimeUnit.SECONDS)
        .build()

    @Provides
    @Singleton
    fun api(client: OkHttpClient): CollectionsApi {
        val moshi = Moshi.Builder().add(KotlinJsonAdapterFactory()).build()
        return Retrofit.Builder()
            .baseUrl(BuildConfig.API_BASE_URL)
            .client(client)
            .addConverterFactory(MoshiConverterFactory.create(moshi))
            .build()
            .create(CollectionsApi::class.java)
    }

    @Provides
    @Singleton
    @Named("deviceId")
    fun deviceId(@ApplicationContext ctx: Context): String {
        val prefs = ctx.getSharedPreferences("prefs", Context.MODE_PRIVATE)
        return prefs.getString("device_id", null) ?: UUID.randomUUID().toString()
            .also { prefs.edit { putString("device_id", it) } }
    }
}
