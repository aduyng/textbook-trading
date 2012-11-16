<?php

class Aduyng_File_Transfer extends Zend_File_Transfer {

    public static function sizeToString($size) {
        $filesizename = array(" Bytes", " KB", " MB", " GB", " TB", " PB", " EB", " ZB", " YB");
        return $size ? round($size / pow(1024, ($i = floor(log($size, 1024)))), 2) . $filesizename[$i] : '0 Bytes';
    }
    
    public function __construct($adapter = 'Aduyng_File_Transfer_Adapter_Http', $direction = false, $options = array()) {
        $options['useByteString'] = false;
        parent::__construct($adapter, $direction, $options);
    }


}