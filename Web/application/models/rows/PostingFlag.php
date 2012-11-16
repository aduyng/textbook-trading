<?php

include_once 'Aduyng/Db/Table/Row.php';
class PostingFlagRow extends Aduyng_Db_Table_Row {

    public function save() {
        if (Aduyng_Db_Table_Row::isEmpty($this->flaggerPhoneNumber)) {
            throw new Zend_Db_Table_Row_Exception("Flagger phone number is empty");
        }
        
        if (Aduyng_Db_Table_Row::isEmpty($this->postingId)) {
        	throw new Zend_Db_Table_Row_Exception("posting id is empty");
        }
        
        return parent::save();
    }
    

}
