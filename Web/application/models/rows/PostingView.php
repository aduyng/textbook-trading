<?php

include_once 'Aduyng/Db/Table/Row.php';
class PostingViewRow extends Aduyng_Db_Table_Row {

    public function save() {
        if (Aduyng_Db_Table_Row::isEmpty($this->viewerPhoneNumber)) {
            throw new Zend_Db_Table_Row_Exception("Viewer phone number is empty");
        }
        
        if (Aduyng_Db_Table_Row::isEmpty($this->postingId)) {
        	throw new Zend_Db_Table_Row_Exception("posting id is empty");
        }
        
        return parent::save();
    }
    

}
