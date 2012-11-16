<?php

require_once('Zend/Controller/Request/Http.php');

class Aduyng_Controller_Request_Translator {

    private $_request = null;

    public function Aduyng_Controller_Request_Translator($request) {
        $this->_request = $request;
    }

    public function getString($index, $default = null) {
        $v = $this->_request->getParam($index);

        if (empty($v)) {
            if (!is_null($default)) {
                return $default;
            }
        }

        return $v;
    }

    public function getInteger($index) {
        $v = $this->getString($index);
        if (!is_null($v)) {
            if (is_numeric($v)) {
                return intval($v);
            }
        }
        return $v;
    }

    public function getPositiveInteger($index) {
        $v = $this->getInteger($index);

        if ($v > 0) {
            return $v;
        }

        return null;
    }

    public function getBool($varName, $default=false) {
        $value = $this->getString($varName);

        if (is_null($value) && $default !== null) {
            return $default;
        }
        if (strcasecmp($value, 'false') == 0 || empty($value)) {
            return false;
        }
        return true;
    }

    public function getArray($varName, $default = array()) {
        $value = $this->_request->getParam($varName);

        if (is_null($value)) {
            return $default;
        }

        if (!is_array($value)) {
            return array($value);
        }

        return $value;
    }

    public function getDouble($varName, $default = 0.0) {
        $string = $this->getString($varName);

        return!is_null($string) ? doubleval($string) : $default;
    }
    
    public function getFile(){
    	Zend_Loader::loadClass('Aduyng_File_Transfer');
    	
    	$upload = new Aduyng_File_Transfer();
    	$upload->receive();
    	
    	
    	$uploadedFiles = $upload->getFiles();
    	$files = array_values($uploadedFiles);
    	if (empty($files)) {
    		return null;
    	}
    	$file = $files[0];
    	
    	return file_get_contents($file['tmp_name']);
    }

}