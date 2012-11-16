<?php

class Aduyng_File_Transfer_Adapter_Http extends Zend_File_Transfer_Adapter_Http {

    public function getFiles() {
        return $this->_files;
    }

}