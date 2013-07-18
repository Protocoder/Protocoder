var Vibrator = Packages.android.os.Vibrator;

var Context = Packages.android.content.Context;

var v = Activity.getSystemService(Context.VIBRATOR_SERVICE);

v.vibrate(500);