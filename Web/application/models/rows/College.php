<?php
include_once 'Aduyng/Db/Table/Row.php';
class CollegeRow extends Aduyng_Db_Table_Row{
    
    
    public function save() {
        
        if( Aduyng_Db_Table_Row::isEmpty($this->name) ){
            throw new Zend_Db_Table_Row_Exception("Name is empty");
        }
        
        if( Aduyng_Db_Table_Row::isEmpty($this->logo) ){
            throw new Zend_Db_Table_Row_Exception("Logo is empty");
        }
        
        
        return parent::save();
    }
    
    public function setLogo($base64EncodedString){
    	if( !empty($base64EncodedString)){
    		$this->logo = base64_decode($base64EncodedString);
    		return $this;
    	}
    }

}
