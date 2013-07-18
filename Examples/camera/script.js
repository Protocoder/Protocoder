var Camera = Packages.android.hardware.Camera;
var SurfaceHolder = Packages.android.view.SurfaceHolder;
var SurfaceView = Packages.android.view.SurfaceView;
var Window = Packages.android.view.Window;

function onCreate(bundle) 
{
    Activity.requestWindowFeature(Window.FEATURE_NO_TITLE);
    var preview = createPreviewSurface();
    Activity.setContentView(preview.getSurfaceView());
}

function createPreviewSurface()
{
    var camera = null;
    var surface = new SurfaceView(Activity);
    
    var object = {
        
        getSurfaceView : function() { 
            return surface; },
            
        surfaceCreated : function(holder) {
            camera = Camera.open();
            try {
                camera.setPreviewDisplay(holder); } 
            catch (exception) {
                camera.release();
                camera = null; } },
                
        surfaceDestroyed : function(holder) {
            camera.stopPreview();
            camera.release();
            camera = null; },
            
        surfaceChanged : function(holder, format, w, h) {
            // Crashes the camera on Nexus One:
            // var parameters = camera.getParameters();
            // parameters.setPreviewSize(w, h);
            // camera.setParameters(parameters);
            camera.startPreview(); }
    };
 
    var callback = createInstance(Packages.android.view.SurfaceHolder.Callback, object);
    surface.getHolder().addCallback(callback);
    surface.getHolder().setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);

    return object;
}

// Create an instance of a Java interface.
//   javaInterface - the interface type
//   object - JS object that will receive messages
//     sent to the instance
function createInstance(javaInterface, object)
{
    // Convert a Java array to a JavaScript array
    function javaArrayToJsArray(javaArray)
    {
        var jsArray = [];
        for (i = 0; i < javaArray.length; ++i) {
            jsArray[i] = javaArray[i];
        }
        return jsArray;
    }
    
    var lang = Packages.java.lang;
    var interfaces = lang.reflect.Array.newInstance(lang.Class, 1);
    interfaces[0] = javaInterface;
    var obj = lang.reflect.Proxy.newProxyInstance(
        lang.ClassLoader.getSystemClassLoader(),
        interfaces,
        // Note, args is a Java array
        function(proxy, method, args) {
            // Convert Java array to JavaScript array
            return object[method.getName()].apply(
                null,
                javaArrayToJsArray(args));
        });
    return obj;
}
