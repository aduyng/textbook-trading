<?php

include_once 'Aduyng/Db/Table/Row.php';
include_once 'models/College.php';
class AccountRow extends Aduyng_Db_Table_Row {

    public function save() {

        if (Aduyng_Db_Table_Row::isEmpty($this->phoneNumber)) {
            throw new Zend_Db_Table_Row_Exception("phoneNumber is empty");
        }
        
        return parent::save();
    }
    

}
