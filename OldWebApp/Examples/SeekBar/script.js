var SeekBar = Packages.android.widget.SeekBar;
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
    
    var seekbar1 = new SeekBar(Activity);
    seekbar1.setLayoutParams(new LayoutParams(
        LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT, 1));
        
    seekbar1.setMax(100);
    seekbar1.setProgress(50);
    //seekbar1.setOnSeekBarChangeListener(Activity);
    
       var mainLayout = new LinearLayout(Activity);
    mainLayout.setOrientation(LinearLayout.VERTICAL);
    mainLayout.setLayoutParams(new LayoutParams(
        LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT));
    mainLayout.addView(text);
    mainLayout.addView(seekbar1);
    
    Activity.setContentView(mainLayout);  
    
}

function showToast(message)
{
    Toast.makeText(
        Activity,
        message,
        Toast.LENGTH_SHORT).show();
}