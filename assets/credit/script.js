function Vibrate()
{
	AndroidFunction.Vibrate();
}

function SetStyle()
{
	var str= document.getElementById('stylesheet').href;
	var n  = str.split("/");
	

	console.log(n[n.length-1]);
	if(n[n.length-1] == 'style1.css')
	{
		document.getElementById('stylesheet').href = 'style2.css';
	}else
	{
		document.getElementById('stylesheet').href = 'style1.css';
	}
}