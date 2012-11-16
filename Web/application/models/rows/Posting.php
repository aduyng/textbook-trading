<?php
include_once 'Aduyng/Db/Table/Row.php';
class PostingRow extends Aduyng_Db_Table_Row{
    
    
    public function save() {
        
        if( Aduyng_Db_Table_Row::isEmpty($this->sellerPhoneNumber) ){
            throw new Zend_Db_Table_Row_Exception("Seller phone number is empty");
        }
        
        if( Aduyng_Db_Table_Row::isEmpty($this->title) ){
            throw new Zend_Db_Table_Row_Exception("Book title is empty");
        }
        
        if( Aduyng_Db_Table_Row::isEmpty($this->isbn) ){
            throw new Zend_Db_Table_Row_Exception("Book ISBN is empty");
        }
        
        if( Aduyng_Db_Table_Row::isEmpty($this->authors) ){
            throw new Zend_Db_Table_Row_Exception("Authors is empty");
        }
        
        if( Aduyng_Db_Table_Row::isEmpty($this->edition) ){
            $this->edition = "";
        }
        
        if( is_null($this->price) || $this->price < 0 ){
            throw new Zend_Db_Table_Row_Exception("Price must be a possitive number");
        }
        
        if( Aduyng_Db_Table_Row::isEmpty($this->picture) ){
            throw new Zend_Db_Table_Row_Exception("Picture of the book is required.");
        }
        
        if( Aduyng_Db_Table_Row::isEmpty($this->dateCreated) ){
            $this->dateCreated = new Aduyng_Date();
        }
        
        
        if( Aduyng_Db_Table_Row::isEmpty($this->isSold) ){
            $this->isSold = false;
        }
        
        return parent::save();
    }
    
    public function setPicture($base64EncodedString){
    	if( !empty($base64EncodedString)){
    		$this->picture = base64_decode($base64EncodedString);
    		return $this;
    	}
    }
    
    public function getSeller(){
    	include_once 'models/Account.php';
    	
    	$model = new AccountModel();
    	return $model->fetchRowByPhoneNumber($this->sellerPhoneNumber);
    }
    
    public function getFlagsCount(){
    	include_once 'models/PostingFlag.php';
    	$model = new PostingFlagModel();
    	
    	$select = $model->select();
    	
    	$select->from($model, "COUNT(*) as nbRecords");
    	$select->where('postingId = ?', $this->id);
    	$result = $model->fetchRow($countSelect);
    	return $result->nbRecords;
    }
    
    public function getViewsCount(){
    	include_once 'models/PostingView.php';
    	$model = new PostingViewModel();
    	 
    	$select = $model->select();
    	 
    	$select->from($model, "COUNT(*) as nbRecords");
    	$select->where('postingId = ?', $this->id);
    	$result = $model->fetchRow($countSelect);
    	return $result->nbRecords;
    }

}
