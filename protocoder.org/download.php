<?php 

$fileName = $_GET["filename"]; 
//echo $filename;
$filePath = "./downloads/" . $fileName;


header('Content-Type: application/octect-stream');
header('Content-Disposition: attachment; filename="' . $fileName . '"');
//header("Content-disposition: attachment");
header('Pragma: no-cache');

//echo $file; 
readfile($filePath);

//store in table downloads IP, country, location 
//echo $_SERVER['HTTP_USER_AGENT']; 

?>