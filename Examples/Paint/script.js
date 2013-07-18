//
// JavaScript paint sample program.
//
// @author Mikael Kindborg
// Email: mikael.kindborg@gmail.com
// Blog: divineprogrammer@blogspot.com
// Twitter: @divineprog
// Copyright (c) Mikael Kindborg 2010
// Source code license: MIT
//

// Java classes

var MotionEvent = Packages.android.view.MotionEvent;
var Paint = Packages.android.graphics.Paint;
var Color = Packages.android.graphics.Color;
var RectF = Packages.android.graphics.RectF;
var Point = Packages.android.graphics.Point;
var Bitmap = Packages.android.graphics.Bitmap;
var Canvas = Packages.android.graphics.Canvas;
var Menu = Packages.android.view.Menu;
var Toast = Packages.android.widget.Toast;
var MediaStore = Packages.android.provider.MediaStore;
var Morph = Packages.com.makewithmoto.apprunner.Morph;
//var DroidScriptFileHandler = Packages.com.motorola.gopi.rhinorunner.DroidScriptFileHandler;
    
// Global variables

var Sketch;
var OptionsMenuItems;

// Application entry point

function onCreate(bundle)
{
    Sketch = SketchMorph();
    Activity.setContentView(Sketch.theMorph);
}

// SketchMorph "constructor"

function SketchMorph()
{
    // Local variables ("private instance variables")
    
    var brushRadius = 10;
    var brushColor = Color.BLACK;
    var morph = new Morph(Activity);
    var bitmap = null;
    var canvas = null;
    var lastPoint = null;
    var artisticMode = false;
    
    // Event handlers
    
    morph.setOnDrawListener(function(canvas)
    { 
        canvas.drawBitmap(bitmap, 0, 0, null);
    });
    
    morph.setOnSizeChangedListener(function(w, h, oldw, oldh)
    {
        // Create bitmap filled with white color.
        bitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
        canvas = new Canvas(bitmap);
        paint = new Paint();
        paint.setARGB(255, 255, 255, 255);
        paint.setStyle(Paint.Style.FILL);
        canvas.drawPaint(paint);
        morph.invalidate();
    });
    
    morph.setOnTouchListener(function(view, event)
    {
        var action = event.getAction();
        var x = event.getX();
        var y = event.getY();
        if (action == MotionEvent.ACTION_DOWN)
        {
            paintDot(x, y);
            lastPoint = new Point(x, y);
            view.invalidate();
        }
        
        if (action == MotionEvent.ACTION_MOVE)
        {
            paintStroke(lastPoint.x, lastPoint.y, x, y);
            if (!artisticMode) { lastPoint = new Point(x, y); }
            view.invalidate();
        }
        
        return true;
    });
    
    // Local functions ("private methods")

    function paintDot(x, y)
    {
        // Random colour in artistic mode
        if (artisticMode) {
            function random255() { return Math.random() * 255; }
            brushColor = Color.rgb(random255(), random255(), random255()); }
        var paint = new Paint();
        paint.setColor(brushColor);
        paint.setStyle(Paint.Style.FILL);
        paint.setAntiAlias(true);
        canvas.drawOval(
            new RectF(
                x - brushRadius, 
                y - brushRadius, 
                x + brushRadius, 
                y + brushRadius), 
            paint);
    }

    function paintStroke(x1, y1, x2, y2)
    {
        var paint = new Paint();
        paint.setColor(brushColor);
        paint.setStyle(Paint.Style.FILL);
        paint.setAntiAlias(true);
        paint.setStrokeWidth(brushRadius * 2);
        paint.setStrokeCap(Paint.Cap.ROUND);
        canvas.drawLine(x1, y1, x2, y2, paint);
    }
    
    function savePainting()
    {
        function pad(n) { return n < 10 ? "0" + n : "" + n; }
        
        function paintingName() {
            var date = new Date();
            return "DroidScriptPainting" 
                + date.getFullYear() 
                + pad(date.getMonth() + 1)
                + pad(date.getDate())
                + pad(date.getHours())
                + pad(date.getMinutes())
                + pad(date.getSeconds()); }
        
        // Image name does not seem to be used in gallery?
        result = MediaStore.Images.Media.insertImage(
            Activity.getContentResolver(),
            bitmap,
            paintingName(),
            paintingName());
        
        if (!result) {
            showToast("Could not save painting"); }
        else {
            showToast("Painting saved in media gallery"); }
        
//        try {
//            out = DroidScriptFileHandler.create().openExternalStorageFileOutputStream(filename()));
//            bitmap.compress(Bitmap.CompressFormat.PNG, 90, out); } 
//        catch (error) {
//            showToast("Could not save image"); }
    }

    function showToast(message)
    {
        Toast.makeText(
            Activity,
            message,
            Toast.LENGTH_SHORT).show();
    }

    // Return object with "public methods"
    
    return {
        theMorph: morph,
        save: savePainting,
        setBrushSize: function(size) { brushRadius = size / 2; },
        setColor: function(color) { brushColor = color; },
        toggleArtisticMode: function() { artisticMode = !artisticMode; }
    };
}

function onCreateOptionsMenu(menu)
{
    // We create the menu dynamically instead!
    return true;
}

function onPrepareOptionsMenu(menu)
{
    OptionsMenuItems = 
        [["Small Brush", function() { Sketch.setBrushSize(20); }],
         ["Large Brush", function() { Sketch.setBrushSize(50); }],
         ["Black Color", function() { Sketch.setColor(Color.BLACK); }],
         ["White Color", function() { Sketch.setColor(Color.WHITE); }],
         ["Toggle Artistic Mode", function() { Sketch.toggleArtisticMode(); }],
         ["Save Painting", function() { Sketch.save(); }]];
    menu.clear();
    menuAddItems(menu, OptionsMenuItems);
    
    return true;
}

function onOptionsItemSelected(item)
{
    menuDispatch(item, OptionsMenuItems);
    return true;
}

function menuAddItems(menu, items)
{
    for (var i = 0; i < items.length; ++i)
    {
        menu.add(Menu.NONE, Menu.FIRST + i, Menu.NONE, items[i][0]);
    }
}

function menuDispatch(item, items)
{
    var i = item.getItemId() - Menu.FIRST;
    items[i][1]();;
}
