var CheckBox = Packages.android.widget.CheckBox;
var TextView = Packages.android.widget.TextView;
var Toast = Packages.android.widget.Toast;
var LinearLayout = Packages.android.widget.LinearLayout;
var LayoutParams = Packages.android.widget.LinearLayout.LayoutParams;

// Called when creating the Activity
function onCreate(icicle)
{
    
    
    var text = new TextView(Activity);
    text.setLayoutParams(new LayoutParams(
        LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT, 1));
    text.setText("Hello World");
    
    var cb = new CheckBox(Activity);
    cb.setLayoutParams(new LayoutParams(
        LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT, 1));
        
    
    
       var mainLayout = new LinearLayout(Activity);
    mainLayout.setOrientation(LinearLayout.VERTICAL);
    mainLayout.setLayoutParams(new LayoutParams(
        LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT));
    mainLayout.addView(text);
    mainLayout.addView(cb);
    
    Activity.setContentView(mainLayout);  
    
}

function showToast(message)
{
    Toast.makeText(
        Activity,
        message,
        Toast.LENGTH_SHORT).show();
}