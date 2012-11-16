<?php

include_once 'Zend/Controller/Action.php';
include_once 'Aduyng/Controller/Request/Translator.php';

class Aduyng_Controller_Action extends Zend_Controller_Action {

    protected $_translator = null;

    public function init() {
    	$this->_translator = $this->view->translator = new Aduyng_Controller_Request_Translator($this->_request);
        $this->view->request = $this->_request;

        $format = $this->_translator->getString('format', 'json');

        $supportedFormats = array(
            'html', 'json'
        );

        if (!in_array($format, $supportedFormats)) {
            throw new Exception("Response format $format is not supported!");
        }
        
        
    }

}