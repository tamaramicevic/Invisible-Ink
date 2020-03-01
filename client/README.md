
## Network Debugging
Network requests can be viewed from AS's built in profiler.

Steps:  
    1. Within AS click `View > Tools > Profiler`  
    2. Launch the application on your device/emulator  
    3. From the profile tab, click the `+` to attach to the application process  
    4. Click on the `Network` row within the profiler  
    5. Subsequent network request will be displayed within the AS profiler

## Runtime API Response Mocking
Runtime API responses can be mocked by implementing a custom Interceptor and adding it to
the OkHttpClient.

Steps:
   1. Create a new Interceptor subclass inside package `com.invisibleink.utils.interceptors`--see the `FetchNotesInterceptor` for an example
   2. Add the interceptor to the `OkHttpClient` for desired builds; e.g. `DEBUG` builds by updating the `provideOkHttpClient` method in the `com.invisibleink.injection.modules.NetworkModule`
   3. Implement logic for parsing responses