/* 
*   Webview 
*   adds a webview and load html content 
*   the api can be called within the html 
*
*   TOFIX: editor should understand the
*   mixed HTML / javascript code
*/ 

//html content to load 
var content =
"""<html>
    <head> 
    <style type="text/css">
    body {
        background: rgb(36, 180, 255);
        margin: 12px;
    }

    h1 { 
        font-family: sans-serif;
        font-size: 2em;
        color: rgba(92, 92, 92, 1);
        text-shadow: 2px 2px rgba(0, 0, 0, 0.15);
        width: 100%;
        text-align: center;
        padding-bottom: 125px;
    } 
    button { 
        background: rgb(255, 197, 36);
        padding: 25px;
        border: 0px;
        font-weight: bold;
        border-radius: 2px;
        width: 100%
    } 
    
    </style>
    </head> 
    <body>
        <script>
        function vibrate() {
            activity.eval(
                "android.vibrate(500)"); }
        </script>
        <h1>HTML example!</h1>
        <button onclick="vibrate()">brrrbrbrbr</button>
    </body>
</html>""";

//load the content 
browser.loadData(content);
