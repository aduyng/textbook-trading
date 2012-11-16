<?php

Zend_Loader::loadClass('Aduyng_Controller_Action');
include_once 'Aduyng/Controller/Request/Translator/Fetch.php';
include_once 'Aduyng/Db/Table/FetchInput.php';

class Aduyng_Controller_Action_Rest extends Aduyng_Controller_Action {
	
	
	protected function setMessage($message){
		$this->view->message = $message;
	}
	
	protected function setStatus($status){
		$this->view->status = $status;
	}
	
    public function init() {
    	parent::init();
    	$this->setMessage('Unknown error occurred');
    	$this->setStatus(-1);

        $this->getHelper('layout')->setLayout('json');
        
    }

}