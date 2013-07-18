//
// JavaScript morph view sample program. A "Morph" is a view in 
// which JavaScript code can draw and handle events. (The name
// "Morph" is taken from Self/Squeak, but our morph is not yet
// by far as capable as a Self/Squeak morph.)
//
// @author Mikael Kindborg
// Email: mikael.kindborg@gmail.com
// Blog: divineprogrammer@blogspot.com
// Twitter: @divineprog
// Copyright (c) Mikael Kindborg 2010
// Source code license: MIT
//

//
// Called when activity starts.
//
function onCreate(bundle)
{
    var morph = ColorMorph();
    Activity.setContentView(morph);
}

//
// Returns a morph set up with drawing and event functions.
// Note that the returned object is not a JavaScript object,
// and that we do not call the function as "new ColorMorph()"
// but as "ColorMorph()". The event functions are closures
// that contains references to "instance variables" defined
// in the outer function scope. This gives an interesting
// perspective on what object-oriented programming is and
// how objects can be defined.
//
function ColorMorph()
{
    // Java classes
    
    var MotionEvent = Packages.android.view.MotionEvent;
    var Paint = Packages.android.graphics.Paint;
    var Color = Packages.android.graphics.Color;
    var RectF = Packages.android.graphics.RectF;
    var Morph = Packages.com.makewithmoto.apprunner.Morph;
    
    // Local variables ("instance variables")
    
    var color = Color.WHITE;
    var morph = new Morph(Activity);
    var width = 100;
    var height = 100;
    
    // Event handlers
    
    morph.setOnDrawListener(function(canvas)
    { 
        var radius = Math.min(width, height) / 2;
        var centerX = width / 2;
        var centerY = height / 2;
        paint = new Paint();
        paint.setColor(color);
        paint.setStyle(Paint.Style.FILL);
        paint.setAntiAlias(true);
        canvas.drawOval(
            new RectF(
                centerX - radius, 
                centerY - radius,
                centerX + radius, 
                centerY + radius), 
            paint);
    });
    
    morph.setOnSizeChangedListener(function(w, h, oldw, oldh)
    {
        width = w;
        height = h;
        morph.invalidate();
    });
    
    morph.setOnTouchListener(function(view, event)
    {
        var action = event.getAction();
        
        if (action == MotionEvent.ACTION_DOWN)
        {
            function random255() { return Math.random() * 255; }
            color = Color.rgb(random255(), random255(), random255());
            view.invalidate();
        }
        
        return true;
    });
    
    return morph;
}
