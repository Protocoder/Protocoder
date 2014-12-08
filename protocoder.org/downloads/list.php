<?php
$path    = './';
//$files = scandir($path);
$files = glob('*.{apk}', GLOB_BRACE);

foreach ($files as $file) {
	echo $file . "\n";
}

?>